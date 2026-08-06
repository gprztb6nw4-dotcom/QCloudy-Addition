package cloudy.autume.addition.inventory.storage;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.inventory.InventoryDataManager;
import cloudy.autume.addition.inventory.ProfileContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public final class StorageCacheManager {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("qcloudy_addition-storage.nbt");
    private static final ExecutorService IO = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ACA-Storage-IO");
        thread.setDaemon(true);
        return thread;
    });
    private static final Map<String, NavigableMap<StoragePageKey, PageData>> PROFILES = new HashMap<>();
    private static boolean loaded;
    private static boolean dirty;
    private static long dirtyAt;
    private static RegistryAccess lastRegistryAccess;
    private static CompletableFuture<Void> lastWrite = CompletableFuture.completedFuture(null);

    private StorageCacheManager() {
    }

    public static void ensureLoaded(Minecraft client) {
        if (client.level == null) return;
        RegistryAccess activeRegistries = client.level.registryAccess();
        if (loaded) {
            if (lastRegistryAccess != activeRegistries) {
                lastRegistryAccess = activeRegistries;
                rebindCachedStacks(activeRegistries);
            }
            return;
        }
        loaded = true;
        lastRegistryAccess = activeRegistries;
        if (!Files.isRegularFile(FILE)) return;
        try {
            CompoundTag root = NbtIo.readCompressed(FILE, NbtAccounter.create(128L * 1024L * 1024L));
            var ops = activeRegistries.createSerializationContext(NbtOps.INSTANCE);
            int skippedStacks = 0;
            for (CompoundTag profileTag : root.getListOrEmpty("profiles").compoundStream().toList()) {
                String key = profileTag.getStringOr("key", "");
                if (key.isEmpty()) continue;
                NavigableMap<StoragePageKey, PageData> pages = new TreeMap<>();
                for (CompoundTag pageTag : profileTag.getListOrEmpty("pages").compoundStream().toList()) {
                    int index = pageTag.getIntOr("index", -1);
                    if (index < 0 || index >= 27) continue;
                    List<ItemStack> stacks = new ArrayList<>();
                    for (CompoundTag itemTag : pageTag.getListOrEmpty("items").compoundStream().toList()) {
                        try {
                            stacks.add(itemTag.read("stack", ItemStack.OPTIONAL_CODEC, ops).orElse(ItemStack.EMPTY));
                        } catch (RuntimeException exception) {
                            stacks.add(ItemStack.EMPTY);
                            skippedStacks++;
                        }
                    }
                    pages.put(new StoragePageKey(index), PageData.loaded(stacks, client));
                }
                PROFILES.put(key, pages);
            }
            if (skippedStacks > 0) {
                QCloudyAdditionClient.LOGGER.warn(
                        "Ignored {} invalid item stack(s) while loading the Storage cache", skippedStacks);
            }
        } catch (Exception exception) {
            QCloudyAdditionClient.LOGGER.error("Could not load storage cache", exception);
            PROFILES.clear();
        }
    }

    public static NavigableMap<StoragePageKey, PageData> current(Minecraft client) {
        ensureLoaded(client);
        return PROFILES.computeIfAbsent(ProfileContext.key(client), ignored -> new TreeMap<>());
    }

    public static void ensurePage(Minecraft client, StoragePageKey key) {
        current(client).computeIfAbsent(key, ignored -> PageData.unloaded());
    }

    public static boolean updatePage(Minecraft client, StoragePageKey key, List<ItemStack> stacks) {
        List<ItemStack> copies = stacks.stream().map(ItemStack::copy).toList();
        int fingerprint = fingerprint(copies);
        PageData old = current(client).get(key);
        if (old != null && old.loaded && old.fingerprint == fingerprint) return false;
        current(client).put(key, PageData.loaded(copies, client));
        markDirty();
        return true;
    }

    public static void removePage(Minecraft client, StoragePageKey key) {
        if (current(client).remove(key) != null) markDirty();
    }

    public static String displayName(Minecraft client, StoragePageKey key) {
        return InventoryDataManager.current(client).storageNames.getOrDefault(key.index(), key.defaultName());
    }

    public static void rename(Minecraft client, StoragePageKey key, String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty() || trimmed.equals(key.defaultName())) {
            InventoryDataManager.current(client).storageNames.remove(key.index());
        } else {
            InventoryDataManager.current(client).storageNames.put(key.index(), trimmed.substring(0, Math.min(32, trimmed.length())));
        }
        InventoryDataManager.markDirty();
    }

    public static void tick(Minecraft client) {
        ensureLoaded(client);
        if (dirty && System.nanoTime() - dirtyAt >= 1_000_000_000L) saveAsync(client);
    }

    public static void flush(Minecraft client) {
        if (dirty && client.level != null) saveAsync(client);
        try {
            lastWrite.join();
        } catch (RuntimeException ignored) {
        }
    }

    private static void markDirty() {
        dirty = true;
        dirtyAt = System.nanoTime();
    }

    private static void saveAsync(Minecraft client) {
        if (client.level == null) return;
        CompoundTag snapshot;
        try {
            snapshot = encode(client);
        } catch (RuntimeException exception) {
            dirty = false;
            QCloudyAdditionClient.LOGGER.error(
                    "Could not encode the Storage cache; the in-memory cache remains available", exception);
            return;
        }
        dirty = false;
        lastWrite = lastWrite.handle((ignored, failure) -> null).thenRunAsync(() -> write(snapshot), IO);
    }

    private static CompoundTag encode(Minecraft client) {
        CompoundTag root = new CompoundTag();
        root.putInt("version", 1);
        ListTag profiles = new ListTag();
        RegistryAccess activeRegistries = client.level.registryAccess();
        var ops = activeRegistries.createSerializationContext(NbtOps.INSTANCE);
        int[] skippedStacks = {0};
        PROFILES.forEach((profileKey, pages) -> {
            CompoundTag profile = new CompoundTag();
            profile.putString("key", profileKey);
            ListTag pageList = new ListTag();
            pages.forEach((pageKey, pageData) -> {
                if (!pageData.loaded) return;
                CompoundTag page = new CompoundTag();
                page.putInt("index", pageKey.index());
                ListTag items = new ListTag();
                for (ItemStack stack : pageData.stacks) {
                    boolean stored = appendStackSafely(items, stack, (item, value) -> item.store(
                            "stack", ItemStack.OPTIONAL_CODEC, ops,
                            rebindDynamicComponents(value, activeRegistries)));
                    if (!stored) skippedStacks[0]++;
                }
                page.put("items", items);
                pageList.add(page);
            });
            profile.put("pages", pageList);
            profiles.add(profile);
        });
        root.put("profiles", profiles);
        if (skippedStacks[0] > 0) {
            QCloudyAdditionClient.LOGGER.warn(
                    "Saved the Storage cache with {} invalid item stack(s) replaced by empty slots",
                    skippedStacks[0]);
        }
        return root;
    }

    static boolean appendStackSafely(ListTag items, ItemStack stack,
                                     BiConsumer<CompoundTag, ItemStack> encoder) {
        CompoundTag item = new CompoundTag();
        try {
            encoder.accept(item, stack);
            items.add(item);
            return true;
        } catch (RuntimeException exception) {
            // Preserve the slot index. One stale registry-backed component must never
            // abort the render thread or discard every other cached Storage item.
            items.add(new CompoundTag());
            return false;
        }
    }

    private static ItemStack rebindDynamicComponents(ItemStack source, RegistryAccess registries) {
        if (source.isEmpty()) return ItemStack.EMPTY;
        ItemStack rebound = source.copy();
        rebindEnchantments(rebound, DataComponents.ENCHANTMENTS, registries);
        rebindEnchantments(rebound, DataComponents.STORED_ENCHANTMENTS, registries);
        return rebound;
    }

    private static void rebindEnchantments(ItemStack stack,
                                           DataComponentType<ItemEnchantments> component,
                                           RegistryAccess registries) {
        ItemEnchantments existing = stack.get(component);
        if (existing == null || existing.isEmpty()) return;
        var enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        ItemEnchantments.Mutable rebound = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (var entry : existing.entrySet()) {
            entry.getKey().unwrapKey().flatMap(enchantments::get)
                    .ifPresent(holder -> rebound.set(holder, entry.getIntValue()));
        }
        stack.set(component, rebound.toImmutable());
    }

    private static void rebindCachedStacks(RegistryAccess registries) {
        boolean changed = false;
        for (NavigableMap<StoragePageKey, PageData> pages : PROFILES.values()) {
            for (Map.Entry<StoragePageKey, PageData> entry : pages.entrySet()) {
                PageData data = entry.getValue();
                if (!data.loaded) continue;
                entry.setValue(data.rebind(registries));
                changed = true;
            }
        }
        if (changed) markDirty();
    }

    private static void write(CompoundTag root) {
        try {
            Files.createDirectories(FILE.getParent());
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            NbtIo.writeCompressed(root, temporary);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException exception) {
            QCloudyAdditionClient.LOGGER.error("Could not save storage cache", exception);
        }
    }

    private static int fingerprint(List<ItemStack> stacks) {
        int result = 1;
        for (ItemStack stack : stacks) {
            try {
                result = 31 * result + stack.hashCode();
            } catch (RuntimeException ignored) {
                result = 31 * result;
            }
        }
        return result;
    }

    public static final class PageData {
        private final List<ItemStack> stacks;
        private final List<String> searchText;
        private final int fingerprint;
        private final boolean loaded;

        private PageData(List<ItemStack> stacks, List<String> searchText, int fingerprint, boolean loaded) {
            this.stacks = stacks;
            this.searchText = searchText;
            this.fingerprint = fingerprint;
            this.loaded = loaded;
        }

        static PageData unloaded() {
            return new PageData(List.of(), List.of(), 0, false);
        }

        static PageData loaded(List<ItemStack> input, Minecraft client) {
            List<ItemStack> stacks = Collections.unmodifiableList(new ArrayList<>(input));
            List<String> search = new ArrayList<>(stacks.size());
            Item.TooltipContext context = client.level == null ? Item.TooltipContext.EMPTY : Item.TooltipContext.of(client.level);
            for (ItemStack stack : stacks) {
                if (stack.isEmpty()) {
                    search.add("");
                    continue;
                }
                try {
                    StringBuilder builder = new StringBuilder(stack.getHoverName().getString());
                    for (var line : stack.getTooltipLines(context, client.player, TooltipFlag.NORMAL)) {
                        builder.append(' ').append(line.getString());
                    }
                    search.add(builder.toString().toLowerCase(Locale.ROOT));
                } catch (RuntimeException ignored) {
                    search.add("");
                }
            }
            return new PageData(stacks, List.copyOf(search), fingerprint(stacks), true);
        }

        private PageData rebind(RegistryAccess registries) {
            List<ItemStack> rebound = new ArrayList<>(stacks.size());
            for (ItemStack stack : stacks) {
                try {
                    rebound.add(rebindDynamicComponents(stack, registries));
                } catch (RuntimeException exception) {
                    rebound.add(stack);
                }
            }
            List<ItemStack> immutable = Collections.unmodifiableList(rebound);
            return new PageData(immutable, searchText, fingerprint(immutable), true);
        }

        public List<ItemStack> stacks() {
            return stacks;
        }

        public boolean loaded() {
            return loaded;
        }

        public int rows() {
            return Math.max(1, (stacks.size() + 8) / 9);
        }

        public boolean matchesItem(int index, String query) {
            if (query.isBlank()) return true;
            if (index < 0 || index >= searchText.size()) return false;
            String haystack = searchText.get(index);
            for (String word : query.toLowerCase(Locale.ROOT).trim().split("\\s+")) {
                if (!haystack.contains(word)) return false;
            }
            return true;
        }

        public boolean matchesPage(String query) {
            if (query.isBlank() || !loaded) return true;
            for (int index = 0; index < searchText.size(); index++) if (matchesItem(index, query)) return true;
            return false;
        }
    }
}

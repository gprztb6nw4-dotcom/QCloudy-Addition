package cloudy.autume.addition.inventory;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Records only Hunting Box pages the player actually opens. It never sends
 * {@code /hb}, clicks a page, or asks the server for hidden inventory data.
 */
public final class ShardWarehouseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("QCloudy_Addition/ShardWarehouse");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("qcloudy_addition_shard_warehouse.json");
    private static final Pattern TITLE = Pattern.compile("^(?:\\((\\d+)/(\\d+)\\) )?Hunting Box$");
    private static final Pattern OWNED = Pattern.compile("Owned: ([\\d,]+) Shards?");
    private static final int MAX_PROFILES = 64;
    private static WarehouseFile data = new WarehouseFile();
    private static String lastSignature = "";

    private ShardWarehouseManager() {
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        try {
            WarehouseFile loaded = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), WarehouseFile.class);
            if (loaded != null) data = normalize(loaded);
        } catch (Exception exception) {
            LOGGER.warn("Could not read {}; starting with an empty Shard warehouse", FILE, exception);
            data = new WarehouseFile();
        }
    }

    public static void update(Minecraft client) {
        if (client.player == null
                || !(MinecraftClientCompat.screen(client) instanceof AbstractContainerScreen<?> screen)) return;
        Matcher title = TITLE.matcher(screen.getTitle().getString().trim());
        if (!title.matches()) return;

        String page = title.group(1) == null ? "1/1" : title.group(1) + "/" + title.group(2);
        Map<String, Integer> observed = new LinkedHashMap<>();
        ShardFusionCatalog catalog = ShardFusionCatalog.instance();
        ShardItemResolver resolver = new ShardItemResolver(catalog);
        int recognizedEntries = 0;
        for (var slot : client.player.containerMenu.slots) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) continue;
            ShardFusionCatalog.Shard shard = identify(catalog, stack);
            if (shard == null) continue;
            Integer count = ownedCount(stack);
            if (count == null) continue;
            recognizedEntries++;
            if (count > 0) observed.put(shard.id(), count);
            resolver.remember(stack);
        }
        // A partially loaded/transition frame must not erase a good snapshot.
        if (recognizedEntries == 0) return;

        String profile = ProfileContext.key(client);
        String signature = profile + '|' + page + '|' + observed;
        if (signature.equals(lastSignature)) return;
        lastSignature = signature;

        ProfileSnapshot snapshot = data.profiles.computeIfAbsent(profile, ignored -> new ProfileSnapshot());
        snapshot.pages.put(page, new LinkedHashMap<>(observed));
        snapshot.updatedAt = System.currentTimeMillis();
        trimProfiles();
        save();
    }

    public static Map<String, Integer> current(Minecraft client) {
        ProfileSnapshot snapshot = data.profiles.get(ProfileContext.key(client));
        if (snapshot == null) return Map.of();
        Map<String, Integer> merged = new LinkedHashMap<>();
        for (Map<String, Integer> page : snapshot.pages.values()) {
            for (Map.Entry<String, Integer> entry : page.entrySet()) {
                merged.merge(entry.getKey(), entry.getValue(), Math::max);
            }
        }
        return Map.copyOf(merged);
    }

    public static long updatedAt(Minecraft client) {
        ProfileSnapshot snapshot = data.profiles.get(ProfileContext.key(client));
        return snapshot == null ? 0L : snapshot.updatedAt;
    }

    public static void clearCurrent(Minecraft client) {
        if (data.profiles.remove(ProfileContext.key(client)) != null) save();
        lastSignature = "";
    }

    static ShardFusionCatalog.Shard identify(ShardFusionCatalog catalog, ItemStack stack) {
        ShardFusionCatalog.Shard shard = catalog.byItemId(SkyBlockItemData.itemId(stack)).orElse(null);
        if (shard != null) return shard;
        String name = ShardItemResolver.canonicalName(stack.getHoverName().getString());
        return catalog.byName(name).orElse(null);
    }

    static Integer ownedCount(ItemStack stack) {
        ItemLore lore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        for (var line : lore.lines()) {
            Integer count = parseOwnedLine(line.getString());
            if (count != null) return count;
        }
        return null;
    }

    static Integer parseOwnedLine(String value) {
        Matcher matcher = OWNED.matcher(value == null ? "" : value.trim());
        if (!matcher.matches()) return null;
        try {
            return Math.max(0, Integer.parseInt(matcher.group(1).replace(",", "")));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(data), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}", FILE, exception);
        }
    }

    private static WarehouseFile normalize(WarehouseFile source) {
        WarehouseFile result = new WarehouseFile();
        if (source.profiles == null) return result;
        for (Map.Entry<String, ProfileSnapshot> profile : source.profiles.entrySet()) {
            if (profile.getKey() == null || profile.getValue() == null || result.profiles.size() >= MAX_PROFILES) continue;
            ProfileSnapshot repaired = new ProfileSnapshot();
            repaired.updatedAt = Math.max(0L, profile.getValue().updatedAt);
            if (profile.getValue().pages != null) {
                for (Map.Entry<String, Map<String, Integer>> page : profile.getValue().pages.entrySet()) {
                    if (page.getKey() == null || page.getValue() == null) continue;
                    Map<String, Integer> values = new LinkedHashMap<>();
                    for (Map.Entry<String, Integer> entry : page.getValue().entrySet()) {
                        if (entry.getKey() == null || entry.getValue() == null) continue;
                        if (ShardFusionCatalog.instance().byId(entry.getKey()).isPresent() && entry.getValue() > 0) {
                            values.put(entry.getKey(), entry.getValue());
                        }
                    }
                    repaired.pages.put(page.getKey(), values);
                }
            }
            result.profiles.put(profile.getKey(), repaired);
        }
        return result;
    }

    private static void trimProfiles() {
        while (data.profiles.size() > MAX_PROFILES) {
            String oldest = data.profiles.entrySet().stream()
                    .min(Map.Entry.comparingByValue((left, right) -> Long.compare(left.updatedAt, right.updatedAt)))
                    .map(Map.Entry::getKey).orElse(null);
            if (oldest == null) break;
            data.profiles.remove(oldest);
        }
    }

    @SuppressWarnings("unused")
    private static final class WarehouseFile {
        int schemaVersion = 1;
        Map<String, ProfileSnapshot> profiles = new LinkedHashMap<>();
    }

    @SuppressWarnings("unused")
    private static final class ProfileSnapshot {
        long updatedAt;
        Map<String, Map<String, Integer>> pages = new LinkedHashMap<>();
    }
}

package cloudy.autume.addition.inventory;

import cloudy.autume.addition.QCloudyAdditionClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Small profile-aware data store for locks, bindings, and custom storage names. */
public final class InventoryDataManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("qcloudy_addition-inventory.json");
    private static RootData root = new RootData();
    private static boolean dirty;
    private static long dirtyAt;

    private InventoryDataManager() {
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        try {
            RootData loaded = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), RootData.class);
            root = loaded == null ? new RootData() : loaded;
            root.normalize();
        } catch (Exception exception) {
            QCloudyAdditionClient.LOGGER.error("Could not read inventory helper data", exception);
            root = new RootData();
        }
    }

    public static ProfileData current(Minecraft client) {
        root.normalize();
        return root.profiles.computeIfAbsent(ProfileContext.key(client), ignored -> new ProfileData());
    }

    public static DimensionData currentDimension(Minecraft client) {
        ProfileData profile = current(client);
        return profile.dimensions.computeIfAbsent(ProfileContext.dimensionKey(), ignored -> new DimensionData());
    }

    public static void markDirty() {
        dirty = true;
        dirtyAt = System.nanoTime();
    }

    public static void tick() {
        if (dirty && System.nanoTime() - dirtyAt >= 500_000_000L) saveNow();
    }

    public static void saveNow() {
        if (!dirty && Files.isRegularFile(FILE)) return;
        try {
            root.normalize();
            Files.createDirectories(FILE.getParent());
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
            dirty = false;
        } catch (IOException exception) {
            QCloudyAdditionClient.LOGGER.error("Could not save inventory helper data", exception);
        }
    }

    public static final class RootData {
        public int version = 1;
        public Map<String, ProfileData> profiles = new HashMap<>();

        void normalize() {
            if (profiles == null) profiles = new HashMap<>();
            profiles.values().removeIf(Objects::isNull);
            profiles.values().forEach(ProfileData::normalize);
        }
    }

    public static final class ProfileData {
        public Set<String> lockedItemUuids = new HashSet<>();
        public Map<String, DimensionData> dimensions = new HashMap<>();
        public Map<Integer, String> storageNames = new HashMap<>();

        void normalize() {
            if (lockedItemUuids == null) lockedItemUuids = new HashSet<>();
            if (dimensions == null) dimensions = new HashMap<>();
            if (storageNames == null) storageNames = new HashMap<>();
            dimensions.values().removeIf(Objects::isNull);
            dimensions.values().forEach(DimensionData::normalize);
        }
    }

    public static final class DimensionData {
        public Set<Integer> lockedSlots = new HashSet<>();
        public Set<SlotBinding> bindings = new HashSet<>();

        void normalize() {
            if (lockedSlots == null) lockedSlots = new HashSet<>();
            if (bindings == null) bindings = new HashSet<>();
            lockedSlots.removeIf(slot -> slot == null || slot < 0 || slot >= 36);
            bindings.removeIf(binding -> binding == null || !binding.valid());
        }
    }

    public record SlotBinding(int hotbar, int inventory) {
        boolean valid() {
            return hotbar >= 0 && hotbar < 9 && inventory >= 9 && inventory < 36;
        }

        public boolean involves(int slot) {
            return hotbar == slot || inventory == slot;
        }
    }
}

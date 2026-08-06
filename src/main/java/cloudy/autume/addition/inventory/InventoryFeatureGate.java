package cloudy.autume.addition.inventory;

import cloudy.autume.addition.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;

/** Optional duplicate-feature handoff; ACA remains fully available when Firmament is absent. */
public final class InventoryFeatureGate {
    private static final boolean FIRMAMENT_LOADED = FabricLoader.getInstance().isModLoaded("firmament");

    private InventoryFeatureGate() {
    }

    public static boolean available() {
        return !FIRMAMENT_LOADED || !ConfigManager.get().inventory.yieldToFirmament;
    }
}

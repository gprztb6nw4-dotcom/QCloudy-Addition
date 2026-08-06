package cloudy.autume.addition.inventory;

import cloudy.autume.addition.config.ModConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MiddleClickMenusTest {
    @Test
    void defaultsOffAndOnlyConvertsLeftClicksWhenEnabled() {
        ModConfig config = new ModConfig();
        assertFalse(config.inventory.middleClickMenus);
        assertTrue("LEFT".equals(config.inventory.middleClickMode));
    }

    @Test
    void excludesRealStorageContainersFromButtonConversion() {
        assertTrue(MiddleClickMenus.isRealStorage("Storage"));
        assertTrue(MiddleClickMenus.isRealStorage("Ender Chest (1/9)"));
        assertTrue(MiddleClickMenus.isRealStorage("Large Backpack (Slot #12)"));
        assertTrue(MiddleClickMenus.isRealStorage("Personal Vault"));
        assertFalse(MiddleClickMenus.isRealStorage("Pets"));
    }
}

package cloudy.autume.addition.inventory;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

public final class MiddleClickMenus {
    private MiddleClickMenus() {
    }

    public static boolean convert(AbstractContainerScreen<?> screen, Slot slot, int button, ContainerInput action) {
        var config = ConfigManager.get().inventory;
        if (!config.middleClickMenus || !InventoryFeatureGate.available() || !LocationTracker.isSkyBlock()) return false;
        if (!(screen.getMenu() instanceof ChestMenu) || slot == null || slot.container instanceof Inventory
                || slot.getItem().isEmpty() || action != ContainerInput.PICKUP) return false;
        if (button == 0 && "RIGHT".equals(config.middleClickMode)) return false;
        if (button == 1 && "LEFT".equals(config.middleClickMode)) return false;
        if (button != 0 && button != 1) return false;
        if (config.middleClickRequiresEmptyCursor && !screen.getMenu().getCarried().isEmpty()) return false;
        if (isRealStorage(screen.getTitle().getString())) return false;

        Minecraft client = Minecraft.getInstance();
        if (client.gameMode == null || client.player == null) return false;
        client.gameMode.handleContainerInput(screen.getMenu().containerId, slot.index, 2,
                ContainerInput.CLONE, client.player);
        return true;
    }

    static boolean isRealStorage(String title) {
        return title.matches("^Ender Chest (?:✦ )?\\([1-9]/[1-9]\\)$")
                || title.matches("^.+Backpack (?:✦ )?\\(Slot #[0-9]+\\)$")
                || title.equals("Storage")
                || title.contains("Personal Vault");
    }
}

package cloudy.autume.addition.inventory.storage;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.inventory.InventoryFeatureGate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

public final class StorageController {
    private static Screen lastScreen;
    private static Screen vanillaScreen;
    private static boolean skipNextStorageScreen;
    private static int ticks;

    private StorageController() {
    }

    public static void tick(Minecraft client) {
        StorageCacheManager.tick(client);
        ticks++;
        Screen screen = client.screen;
        if (screen instanceof StorageOverlayScreen overlay) {
            if (ticks % 10 == 0) capturePage(client, overlay.getMenu(), overlay.activePage());
            lastScreen = screen;
            return;
        }
        if (!(screen instanceof ContainerScreen container)) {
            if (screen != vanillaScreen) vanillaScreen = null;
            lastScreen = screen;
            return;
        }
        StoragePageKey page = StoragePageKey.fromTitle(container.getTitle().getString());
        boolean overview = "Storage".equals(container.getTitle().getString());
        if (!overview && page == null) {
            lastScreen = screen;
            return;
        }
        if (screen != lastScreen || ticks % 10 == 0) {
            if (overview) captureOverview(client, container);
            else capturePage(client, container.getMenu(), page);
        }
        lastScreen = screen;
        var config = ConfigManager.get().inventory;
        if (!config.storageUi || !config.alwaysReplaceStorage || !InventoryFeatureGate.available()) return;
        if (screen == vanillaScreen) return;
        if (skipNextStorageScreen) {
            skipNextStorageScreen = false;
            vanillaScreen = screen;
            return;
        }
        client.setScreen(new StorageOverlayScreen(container, page));
    }

    public static void showVanilla(ContainerScreen backing) {
        vanillaScreen = backing;
        Minecraft.getInstance().setScreen(backing);
    }

    public static void openVanillaOverview() {
        skipNextStorageScreen = true;
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) connection.sendCommand("storage");
    }

    public static void reset(Minecraft client) {
        StorageCacheManager.flush(client);
        lastScreen = null;
        vanillaScreen = null;
        skipNextStorageScreen = false;
    }

    private static void captureOverview(Minecraft client, ContainerScreen screen) {
        int chestSlots = screen.getMenu().getRowCount() * 9;
        for (int index = 0; index < Math.min(chestSlots, screen.getMenu().slots.size()); index++) {
            StoragePageKey key = StoragePageKey.fromOverviewSlot(index);
            if (key == null) continue;
            var stack = screen.getMenu().slots.get(index).getItem();
            if (stack.isEmpty() || stack.is(Items.RED_STAINED_GLASS_PANE)
                    || stack.is(Items.BROWN_STAINED_GLASS_PANE) || stack.is(Items.GRAY_DYE)) {
                StorageCacheManager.removePage(client, key);
            } else {
                StorageCacheManager.ensurePage(client, key);
            }
        }
    }

    private static void capturePage(Minecraft client, net.minecraft.world.inventory.ChestMenu menu,
                                    StoragePageKey page) {
        if (page == null) return;
        int chestSlots = menu.getRowCount() * 9;
        var stacks = new ArrayList<net.minecraft.world.item.ItemStack>();
        for (int index = 9; index < Math.min(chestSlots, menu.slots.size()); index++) {
            stacks.add(menu.slots.get(index).getItem());
        }
        if (!stacks.isEmpty()) StorageCacheManager.updatePage(client, page, stacks);
    }
}

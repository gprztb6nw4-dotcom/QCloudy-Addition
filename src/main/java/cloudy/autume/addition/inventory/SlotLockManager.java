package cloudy.autume.addition.inventory;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class SlotLockManager {
    private static Slot bindingStart;
    private static AbstractContainerScreen<?> bindingScreen;
    private static long lastFeedback;

    private SlotLockManager() {
    }

    public static boolean enabled() {
        return ConfigManager.get().inventory.slotLocking && InventoryFeatureGate.available()
                && LocationTracker.isSkyBlock();
    }

    public static boolean keyPressed(AbstractContainerScreen<?> screen, Slot hovered, KeyEvent event) {
        if (!enabled() || hovered == null || !(hovered.container instanceof Inventory)) return false;
        if (QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.LOCK_ITEM, event)) {
            toggleItem(hovered.getItem());
            return true;
        }
        if (QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.BIND_SLOT, event)) {
            bindingStart = hovered;
            bindingScreen = screen;
            return true;
        }
        if (QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.LOCK_SLOT, event)) {
            toggleSlot(hovered.getContainerSlot());
            return true;
        }
        return false;
    }

    public static boolean mousePressed(AbstractContainerScreen<?> screen, Slot hovered, MouseButtonEvent event) {
        if (!enabled() || hovered == null || !(hovered.container instanceof Inventory)) return false;
        if (QCloudyAdditionClient.matchesMouseChord(
                QCloudyAdditionClient.ChordAction.LOCK_ITEM, event)) {
            toggleItem(hovered.getItem());
            return true;
        }
        if (QCloudyAdditionClient.matchesMouseChord(
                QCloudyAdditionClient.ChordAction.BIND_SLOT, event)) {
            bindingStart = hovered;
            bindingScreen = screen;
            return true;
        }
        if (QCloudyAdditionClient.matchesMouseChord(
                QCloudyAdditionClient.ChordAction.LOCK_SLOT, event)) {
            toggleSlot(hovered.getContainerSlot());
            return true;
        }
        return false;
    }

    public static boolean keyReleased(AbstractContainerScreen<?> screen, Slot hovered, KeyEvent event) {
        if (bindingStart == null || bindingScreen != screen || !QCloudyAdditionClient.matchesBaseKey(
                QCloudyAdditionClient.ChordAction.BIND_SLOT, event)) {
            return false;
        }
        return finishBinding(hovered);
    }

    public static boolean mouseReleased(AbstractContainerScreen<?> screen, Slot hovered, MouseButtonEvent event) {
        if (bindingStart == null || bindingScreen != screen || !QCloudyAdditionClient.matchesBaseMouse(
                QCloudyAdditionClient.ChordAction.BIND_SLOT, event)) {
            return false;
        }
        return finishBinding(hovered);
    }

    private static boolean finishBinding(Slot hovered) {
        Slot start = bindingStart;
        bindingStart = null;
        bindingScreen = null;
        if (hovered == null || !(hovered.container instanceof Inventory)) return true;
        if (hovered == start || isHotbar(hovered.getContainerSlot()) == isHotbar(start.getContainerSlot())) {
            removeBindings(hovered.getContainerSlot());
            success();
            return true;
        }
        int hotbar = isHotbar(hovered.getContainerSlot()) ? hovered.getContainerSlot() : start.getContainerSlot();
        int inventory = isHotbar(hovered.getContainerSlot()) ? start.getContainerSlot() : hovered.getContainerSlot();
        bind(hotbar, inventory);
        return true;
    }

    public static void resetTransient() {
        bindingStart = null;
        bindingScreen = null;
    }

    public static boolean handleBoundSlotClick(AbstractContainerScreen<?> screen, Slot clicked, ContainerInput action) {
        if (!enabled() || clicked == null || !(clicked.container instanceof Inventory)) return false;
        var config = ConfigManager.get().inventory;
        if (action != ContainerInput.QUICK_MOVE && (config.requireShiftClickForBindings || action != ContainerInput.PICKUP)) {
            return false;
        }
        if (config.bindingsOnlyInInventory && !(screen.getMenu() instanceof net.minecraft.world.inventory.InventoryMenu)) {
            return false;
        }
        List<InventoryDataManager.SlotBinding> matches = bindings(clicked.getContainerSlot());
        if (matches.size() != 1) return false;
        var binding = matches.getFirst();
        Slot inventorySlot = playerSlot(screen, binding.inventory());
        Minecraft client = Minecraft.getInstance();
        if (inventorySlot == null || client.gameMode == null || client.player == null) return false;
        client.gameMode.handleContainerInput(screen.getMenu().containerId, inventorySlot.index,
                binding.hotbar(), ContainerInput.SWAP, client.player);
        return true;
    }

    public static boolean shouldBlock(AbstractContainerScreen<?> screen, Slot slot, ContainerInput action,
                                      ItemStack override, boolean hotbarDrop) {
        if (!enabled()) return false;
        ItemStack stack = override != null ? override : slot == null ? ItemStack.EMPTY : slot.getItem();
        if (hotbarDrop && ConfigManager.get().inventory.allowDungeonAbilityDrop && LocationTracker.isDungeon()) return false;
        if (slot != null && slot.container instanceof Inventory) {
            int index = slot.getContainerSlot();
            var data = InventoryDataManager.currentDimension(Minecraft.getInstance());
            if (data.lockedSlots.contains(index)) return blocked(stack);
            if (ConfigManager.get().inventory.lockBoundSlots && data.bindings.stream().anyMatch(it -> it.involves(index))) {
                return blocked(stack);
            }
        }
        boolean destructive = action == ContainerInput.THROW || hotbarDrop || isSaleTradeOrSalvage(screen);
        if (!destructive || stack.isEmpty()) return false;
        if (ConfigManager.get().inventory.protectHuntingBox && SkyBlockItemData.isHuntingBox(stack)) {
            return blocked(stack);
        }
        String uuid = SkyBlockItemData.uuid(stack);
        if (!uuid.isEmpty() && InventoryDataManager.current(Minecraft.getInstance()).lockedItemUuids.contains(uuid)) {
            return blocked(stack);
        }
        return false;
    }

    public static boolean isSlotLocked(Slot slot) {
        return enabled() && slot.container instanceof Inventory
                && InventoryDataManager.currentDimension(Minecraft.getInstance()).lockedSlots.contains(slot.getContainerSlot());
    }

    public static boolean isItemLocked(ItemStack stack) {
        if (!enabled() || stack.isEmpty()) return false;
        String uuid = SkyBlockItemData.uuid(stack);
        return !uuid.isEmpty() && InventoryDataManager.current(Minecraft.getInstance()).lockedItemUuids.contains(uuid);
    }

    public static void renderSlotOverlay(GuiGraphicsExtractor graphics, Slot slot) {
        if (!enabled()) return;
        if (isItemLocked(slot.getItem())) {
            drawCornerStar(graphics, slot.x, slot.y, ConfigManager.get().inventory.itemLockColor);
            return;
        }
        if (isSlotLocked(slot)) {
            drawCornerLock(graphics, slot.x, slot.y, ConfigManager.get().inventory.slotLockColor);
            return;
        }
        if (slot.container instanceof Inventory && !bindings(slot.getContainerSlot()).isEmpty()) {
            int color = 0xFF000000 | ConfigManager.get().inventory.bindingColor;
            graphics.outline(slot.x, slot.y, 16, 16, color);
        }
    }

    private static void drawCornerStar(GuiGraphicsExtractor graphics, int x, int y, int rgb) {
        drawStarPixels(graphics, x + 1, y + 1, 0xE8000000);
        drawStarPixels(graphics, x, y, 0xFF000000 | rgb);
    }

    private static void drawStarPixels(GuiGraphicsExtractor graphics, int x, int y, int color) {
        graphics.fill(x + 2, y, x + 3, y + 5, color);
        graphics.fill(x, y + 2, x + 5, y + 3, color);
        graphics.fill(x + 1, y + 1, x + 4, y + 4, color);
    }

    private static void drawCornerLock(GuiGraphicsExtractor graphics, int x, int y, int rgb) {
        int shadow = 0xE8000000;
        int color = 0xFF000000 | rgb;
        graphics.outline(x + 1, y + 1, 5, 6, shadow);
        graphics.fill(x + 1, y + 3, x + 6, y + 7, shadow);
        graphics.outline(x, y, 5, 6, color);
        graphics.fill(x, y + 3, x + 5, y + 7, color);
    }

    public static void renderBindingLines(GuiGraphicsExtractor graphics, AbstractContainerScreen<?> screen,
                                          Slot hovered, int left, int top) {
        if (!enabled() || "BOXES".equals(ConfigManager.get().inventory.bindingRenderMode)) return;
        for (var binding : InventoryDataManager.currentDimension(Minecraft.getInstance()).bindings) {
            Slot hotbar = playerSlot(screen, binding.hotbar());
            Slot inventory = playerSlot(screen, binding.inventory());
            if (hotbar == null || inventory == null) continue;
            boolean relevant = hovered == hotbar || hovered == inventory;
            if ("HOVER".equals(ConfigManager.get().inventory.bindingRenderMode) && !relevant) continue;
            int color = 0xFF000000 | ConfigManager.get().inventory.bindingColor;
            drawLine(graphics, left + hotbar.x + 8, top + hotbar.y + 8,
                    left + inventory.x + 8, top + inventory.y + 8, color);
        }
        if (bindingStart != null && bindingScreen == screen) {
            int color = 0xFF000000 | ConfigManager.get().inventory.bindingColor;
            graphics.outline(left + bindingStart.x - 1, top + bindingStart.y - 1, 18, 18, color);
        }
    }

    private static void toggleSlot(int slot) {
        var data = InventoryDataManager.currentDimension(Minecraft.getInstance());
        removeBindings(slot);
        if (!data.lockedSlots.remove(slot)) data.lockedSlots.add(slot);
        InventoryDataManager.markDirty();
        success();
    }

    private static void toggleItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        if (SkyBlockItemData.isHuntingBox(stack)) {
            feedback(ModText.get("inventory.lock.hunting_box_hint"), true);
            return;
        }
        String uuid = SkyBlockItemData.uuid(stack);
        if (uuid.isEmpty()) {
            feedback(ModText.get("inventory.lock.no_uuid"), true);
            return;
        }
        var uuids = InventoryDataManager.current(Minecraft.getInstance()).lockedItemUuids;
        boolean unlocked = uuids.remove(uuid);
        if (!unlocked) uuids.add(uuid);
        InventoryDataManager.markDirty();
        success();
        feedback(ModText.get(unlocked ? "inventory.lock.item_unlocked" : "inventory.lock.item_locked"), false);
    }

    private static void bind(int hotbar, int inventory) {
        var data = InventoryDataManager.currentDimension(Minecraft.getInstance());
        data.lockedSlots.remove(hotbar);
        data.lockedSlots.remove(inventory);
        if (!ConfigManager.get().inventory.allowMultiBinding) {
            data.bindings.removeIf(binding -> binding.involves(hotbar) || binding.involves(inventory));
        } else {
            data.bindings.removeIf(binding -> binding.inventory() == inventory);
        }
        data.bindings.add(new InventoryDataManager.SlotBinding(hotbar, inventory));
        InventoryDataManager.markDirty();
        success();
    }

    private static void removeBindings(int slot) {
        var data = InventoryDataManager.currentDimension(Minecraft.getInstance());
        if (data.bindings.removeIf(binding -> binding.involves(slot))) InventoryDataManager.markDirty();
    }

    private static List<InventoryDataManager.SlotBinding> bindings(int index) {
        var result = new ArrayList<InventoryDataManager.SlotBinding>();
        for (var binding : InventoryDataManager.currentDimension(Minecraft.getInstance()).bindings) {
            if (binding.involves(index)) result.add(binding);
        }
        return result;
    }

    private static Slot playerSlot(AbstractContainerScreen<?> screen, int containerSlot) {
        for (Slot slot : screen.getMenu().slots) {
            if (slot.container instanceof Inventory && slot.getContainerSlot() == containerSlot) return slot;
        }
        return null;
    }

    private static boolean isHotbar(int slot) {
        return slot >= 0 && slot < 9;
    }

    private static boolean isSaleTradeOrSalvage(AbstractContainerScreen<?> screen) {
        if (screen == null) return false;
        String title = screen.getTitle().getString();
        return title.contains("Salvage Item") || title.contains("Trades") || title.contains("Shop")
                || title.contains("Your stuff");
    }

    private static boolean blocked(ItemStack stack) {
        feedback(ModText.get("inventory.lock.protected", stack.getHoverName().getString()), true);
        return true;
    }

    private static void success() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && ConfigManager.get().inventory.protectionSound) {
            client.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.45f, 1.5f);
        }
    }

    private static void feedback(String text, boolean failure) {
        long now = System.nanoTime();
        if (failure && now - lastFeedback < 250_000_000L) return;
        lastFeedback = now;
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && ConfigManager.get().inventory.protectionMessage) {
            client.player.sendOverlayMessage(Component.literal(text)
                    .withStyle(failure ? ChatFormatting.RED : ChatFormatting.GREEN));
        }
        if (failure && client.player != null && ConfigManager.get().inventory.protectionSound) {
            client.player.playSound(SoundEvents.VILLAGER_NO, 0.65f, 1.0f);
        }
    }

    private static void drawLine(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int error = dx + dy;
        while (true) {
            graphics.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) break;
            int twice = error * 2;
            if (twice >= dy) { error += dy; x0 += sx; }
            if (twice <= dx) { error += dx; y0 += sy; }
        }
    }
}

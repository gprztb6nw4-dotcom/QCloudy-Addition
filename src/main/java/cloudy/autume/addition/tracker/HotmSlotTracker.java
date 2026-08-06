package cloudy.autume.addition.tracker;

import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

/** Tracks only the selected name already visible in Hypixel's HOTM slot menu. */
public final class HotmSlotTracker {
    private static final String MENU_TITLE = "Heart of the Mountain Slot";

    private HotmSlotTracker() {
    }

    public static void update(Minecraft client) {
        if (!LocationTracker.isSkyBlock() || client.player == null
                || !(client.screen instanceof AbstractContainerScreen<?> screen)) return;
        String title = screen.getTitle().getString();

        Item.TooltipContext context = client.level == null
                ? Item.TooltipContext.EMPTY : Item.TooltipContext.of(client.level);
        List<MenuEntry> entries = new ArrayList<>();
        for (var slot : screen.getMenu().slots) {
            if (slot.container instanceof Inventory || slot.getItem().isEmpty()) continue;
            List<String> tooltip = new ArrayList<>();
            try {
                for (var line : slot.getItem().getTooltipLines(context, client.player, TooltipFlag.NORMAL)) {
                    tooltip.add(line.getString());
                }
            } catch (RuntimeException ignored) {
                continue;
            }
            entries.add(new MenuEntry(slot.getItem().getHoverName().getString(), tooltip));
        }

        String selected = selectedName(title, entries);
        if (selected.isBlank()) selected = currentSelection(entries);
        if (selected.isBlank() || selected.equals(ConfigManager.get().mining.lastHotmSlotName)) return;
        ConfigManager.get().mining.lastHotmSlotName = selected;
        ConfigManager.save();
    }

    public static String currentName() {
        return ConfigManager.get().mining.lastHotmSlotName;
    }

    static String selectedName(String title, List<MenuEntry> entries) {
        if (title == null || !MENU_TITLE.equalsIgnoreCase(title.trim())) return "";
        for (MenuEntry entry : entries) {
            boolean selected = entry.tooltip().stream().anyMatch(line -> "SELECTED".equalsIgnoreCase(line.trim()));
            if (selected) return entry.name().trim();
        }
        return "";
    }

    static String currentSelection(List<MenuEntry> entries) {
        for (MenuEntry entry : entries) {
            if (!"Heart of the Mountain".equalsIgnoreCase(entry.name().trim())
                    && !MENU_TITLE.equalsIgnoreCase(entry.name().trim())) continue;
            for (String line : entry.tooltip()) {
                String value = line.trim();
                if (value.regionMatches(true, 0, "Current:", 0, "Current:".length())) {
                    return value.substring("Current:".length()).trim();
                }
            }
        }
        return "";
    }

    record MenuEntry(String name, List<String> tooltip) {
    }
}

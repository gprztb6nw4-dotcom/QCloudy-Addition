package cloudy.autume.addition.hud;

import cloudy.autume.addition.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class HudPanel {
    private HudPanel() {
    }

    public static void background(GuiGraphicsExtractor graphics, int x, int y, int width, int height,
                                  ModConfig.PanelStyle style) {
        int opacity = Math.clamp(style.backgroundOpacity, 0, 255);
        if (opacity > 0) {
            graphics.fill(x, y, x + width, y + height, (opacity << 24) | (style.backgroundColor & 0xFFFFFF));
        }
        if (!style.border) return;
        int thickness = Math.clamp(style.borderThickness, 1, 4);
        int color = 0xD0000000 | (style.borderColor & 0xFFFFFF);
        graphics.fill(x, y, x + width, y + thickness, color);
        graphics.fill(x, y + height - thickness, x + width, y + height, color);
        graphics.fill(x, y, x + thickness, y + height, color);
        graphics.fill(x + width - thickness, y, x + width, y + height, color);
    }

    public static void text(GuiGraphicsExtractor graphics, String value, int x, int y, int color,
                            ModConfig.PanelStyle style) {
        graphics.text(Minecraft.getInstance().font, styledText(value, style), x, y, color, style.textShadow);
    }

    public static void title(GuiGraphicsExtractor graphics, String value, int x, int y,
                             ModConfig.PanelStyle style) {
        graphics.text(Minecraft.getInstance().font, styledText(value, style), x, y,
                0xFF000000 | style.titleColor, style.textShadow);
    }

    public static MutableComponent styledText(String value, ModConfig.PanelStyle style) {
        MutableComponent component = Component.literal(value);
        if (style.boldText) component = component.withStyle(ChatFormatting.BOLD);
        return component;
    }
}

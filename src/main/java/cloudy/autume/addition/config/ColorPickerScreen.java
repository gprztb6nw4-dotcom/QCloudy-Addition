package cloudy.autume.addition.config;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.i18n.ModText;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.IntConsumer;

final class ColorPickerScreen extends Screen {
    private static final Identifier WHEEL = Identifier.fromNamespaceAndPath(
            "qcloudy_addition", "textures/gui/color_wheel.png");
    private static final int[] PRESETS = {
            0x3498DB, 0xE74C3C, 0x2ECC71, 0xF1C40F, 0xFFFFFF, 0x000000, 0x19D3DA, 0x9B59B6
    };
    private final Screen parent;
    private final IntConsumer setter;
    private final boolean allowTransparent;
    private final Runnable transparentAction;
    private final Runnable opaqueAction;
    private final long openedAt = System.nanoTime();
    private float hue;
    private float saturation;
    private float brightness;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int wheelX;
    private int wheelY;
    private int wheelSize;
    private int sliderX;
    private int sliderWidth;
    private Drag drag = Drag.NONE;
    private boolean transparent;

    ColorPickerScreen(Screen parent, int initial, IntConsumer setter) {
        this(parent, initial, setter, false, false, () -> { }, () -> { });
    }

    ColorPickerScreen(Screen parent, int initial, IntConsumer setter, boolean allowTransparent,
                      boolean initiallyTransparent, Runnable transparentAction, Runnable opaqueAction) {
        super(ModText.component("config.color_picker"));
        this.parent = parent;
        this.setter = setter;
        this.allowTransparent = allowTransparent;
        this.transparent = allowTransparent && initiallyTransparent;
        this.transparentAction = transparentAction;
        this.opaqueAction = opaqueAction;
        float[] hsv = rgbToHsv(initial);
        hue = hsv[0];
        saturation = hsv[1];
        brightness = hsv[2];
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layout();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        UiAnimation.push(graphics, UiAnimation.scale(openedAt), width / 2.0f, height / 2.0f);
        graphics.fill(windowX + 4, windowY + 5, windowX + windowWidth + 5, windowY + windowHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1, windowY + 34, AcaUiTheme.HEADER);
        int doneWidth = Math.min(58, Math.max(1, windowWidth / 4));
        int doneX = windowX + windowWidth - doneWidth - 12;
        drawFittedText(graphics, ModText.get("config.color_picker"), windowX + 12, windowY + 12,
                Math.max(1, doneX - windowX - 18), AcaUiTheme.TEXT);
        AcaUiTheme.button(graphics, font, ModText.get("config.done"), doneX, windowY + 8,
                doneWidth, 18, AcaUiTheme.contains(mouseX, mouseY, doneX, windowY + 8, doneWidth, 18), true);

        // Destination size is independent from the 160x160 source region. The
        // shorter overload treats the destination size as the sampled region too,
        // so a wheel larger than 160px wraps the texture along its right/bottom edge.
        graphics.enableScissor(wheelX, wheelY, wheelX + wheelSize, wheelY + wheelSize);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WHEEL, wheelX, wheelY, 0, 0,
                wheelSize, wheelSize, 160, 160, 160, 160);
        graphics.disableScissor();
        // Keep the wheel itself at full brightness. Darkening its square bounds
        // also darkens the transparent corners and makes the circular control
        // look like a flat black tile for darker colours. Brightness is shown
        // and edited by the dedicated gradient slider instead.
        double angle = hue * Math.PI * 2.0;
        float radius = saturation * (wheelSize / 2.0f - 3.0f);
        int markerX = Math.round(wheelX + wheelSize / 2.0f + (float) Math.cos(angle) * radius);
        int markerY = Math.round(wheelY + wheelSize / 2.0f + (float) Math.sin(angle) * radius);
        graphics.fill(markerX - 3, markerY - 3, markerX + 4, markerY + 4, 0xFF000000);
        graphics.fill(markerX - 2, markerY - 2, markerX + 3, markerY + 3, 0xFFFFFFFF);

        int rgb = currentRgb();
        int previewY = windowY + 43;
        if (transparent) drawTransparency(graphics, sliderX, previewY, sliderWidth, 24);
        else graphics.fill(sliderX, previewY, sliderX + sliderWidth, previewY + 24, 0xFF000000 | rgb);
        graphics.outline(sliderX, previewY, sliderWidth, 24, AcaUiTheme.BORDER);
        String hex = transparent ? ModText.get("config.transparent") : String.format("#%06X", rgb);
        int contrast = brightness > 0.62f ? 0xFF071014 : 0xFFFFFFFF;
        graphics.text(font, hex, sliderX + (sliderWidth - font.width(hex)) / 2, previewY + 8, contrast, false);

        drawBrightness(graphics, windowY + 80);
        drawRgbSlider(graphics, 0, "R", windowY + 116);
        drawRgbSlider(graphics, 1, "G", windowY + 145);
        drawRgbSlider(graphics, 2, "B", windowY + 174);
        drawPresets(graphics, mouseX, mouseY);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        UiAnimation.pop(graphics);
    }

    private void layout() {
        windowWidth = Math.max(1, Math.min(470, width - Math.min(20, Math.max(0, width - 1))));
        windowHeight = Math.max(1, Math.min(310, height - Math.min(20, Math.max(0, height - 1))));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;
        int horizontalWheelSpace = Math.max(1, windowWidth - 150);
        wheelSize = Math.max(1, Math.min(190, Math.min(windowHeight - 100, horizontalWheelSpace)));
        wheelX = windowX + 18;
        wheelY = windowY + 47;
        sliderX = wheelX + wheelSize + 24;
        sliderWidth = Math.max(1, windowX + windowWidth - sliderX - 18);
    }

    private void drawBrightness(GuiGraphicsExtractor graphics, int y) {
        graphics.text(font, ModText.get("config.brightness"), sliderX, y - 11, AcaUiTheme.TEXT_MUTED, false);
        for (int x = 0; x < sliderWidth; x++) {
            float value = x / (float) Math.max(1, sliderWidth - 1);
            graphics.fill(sliderX + x, y, sliderX + x + 1, y + 12,
                    0xFF000000 | hsvToRgb(hue, saturation, value));
        }
        int marker = sliderX + Math.round(brightness * (sliderWidth - 1));
        graphics.fill(marker - 1, y - 2, marker + 2, y + 14, 0xFFFFFFFF);
        graphics.outline(sliderX, y, sliderWidth, 12, AcaUiTheme.BORDER);
    }

    private void drawRgbSlider(GuiGraphicsExtractor graphics, int channel, String label, int y) {
        int rgb = currentRgb();
        int value = channel(rgb, channel);
        graphics.text(font, label, sliderX, y + 3, AcaUiTheme.TEXT, false);
        int barX = sliderX + 15;
        int barWidth = rgbBarWidth(sliderWidth);
        for (int x = 0; x < barWidth; x++) {
            int component = Math.round(x / (float) Math.max(1, barWidth - 1) * 255.0f);
            graphics.fill(barX + x, y, barX + x + 1, y + 12,
                    0xFF000000 | withChannel(rgb, channel, component));
        }
        int marker = barX + Math.round(value / 255.0f * (barWidth - 1));
        graphics.fill(marker - 1, y - 2, marker + 2, y + 14, 0xFFFFFFFF);
        graphics.outline(barX, y, barWidth, 12, AcaUiTheme.BORDER);
        String number = Integer.toString(value);
        graphics.text(font, number, sliderX + sliderWidth - font.width(number), y + 3, AcaUiTheme.TEXT_MUTED, false);
    }

    private void drawPresets(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int y = windowY + windowHeight - 32;
        PresetLayout layout = presetLayout(windowX, windowWidth, allowTransparent);
        drawFittedText(graphics, ModText.get("config.presets"), windowX + 14, y + 6,
                Math.max(1, layout.startX() - windowX - 18), AcaUiTheme.TEXT_MUTED);
        int x = layout.startX();
        for (int preset : PRESETS) {
            boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, layout.swatchSize(), 20);
            graphics.fill(x, y, x + layout.swatchSize(), y + 20, 0xFF000000 | preset);
            graphics.outline(x, y, layout.swatchSize(), 20, hovered ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
            x += layout.swatchSize() + layout.gap();
        }
        if (allowTransparent) {
            boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, layout.swatchSize(), 20);
            drawTransparency(graphics, x, y, layout.swatchSize(), 20);
            graphics.outline(x, y, layout.swatchSize(), 20,
                    hovered || transparent ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        int doneWidth = Math.min(58, Math.max(1, windowWidth / 4));
        int doneX = windowX + windowWidth - doneWidth - 12;
        if (AcaUiTheme.contains(click.x(), click.y(), doneX, windowY + 8, doneWidth, 18)) {
            onClose();
            return true;
        }
        Drag target = dragAt(click.x(), click.y());
        if (target != Drag.NONE) {
            drag = target;
            updateDrag(click.x(), click.y());
            return true;
        }
        int y = windowY + windowHeight - 32;
        PresetLayout layout = presetLayout(windowX, windowWidth, allowTransparent);
        int x = layout.startX();
        for (int preset : PRESETS) {
            if (AcaUiTheme.contains(click.x(), click.y(), x, y, layout.swatchSize(), 20)) {
                makeOpaque();
                setRgb(preset);
                apply();
                return true;
            }
            x += layout.swatchSize() + layout.gap();
        }
        if (allowTransparent && AcaUiTheme.contains(click.x(), click.y(), x, y, layout.swatchSize(), 20)) {
            transparent = true;
            transparentAction.run();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (drag != Drag.NONE && click.button() == 0) {
            updateDrag(click.x(), click.y());
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (drag != Drag.NONE && click.button() == 0) {
            drag = Drag.NONE;
            apply();
            return true;
        }
        return super.mouseReleased(click);
    }

    private Drag dragAt(double x, double y) {
        double dx = x - (wheelX + wheelSize / 2.0);
        double dy = y - (wheelY + wheelSize / 2.0);
        if (dx * dx + dy * dy <= wheelSize * wheelSize / 4.0) return Drag.WHEEL;
        if (AcaUiTheme.contains(x, y, sliderX, windowY + 77, sliderWidth, 18)) return Drag.BRIGHTNESS;
        for (int channel = 0; channel < 3; channel++) {
            int rowY = windowY + 116 + channel * 29;
            if (AcaUiTheme.contains(x, y, sliderX + 15, rowY - 3, rgbBarWidth(sliderWidth), 18)) {
                return Drag.values()[Drag.RED.ordinal() + channel];
            }
        }
        return Drag.NONE;
    }

    private void updateDrag(double x, double y) {
        makeOpaque();
        if (drag == Drag.WHEEL) {
            double dx = x - (wheelX + wheelSize / 2.0);
            double dy = y - (wheelY + wheelSize / 2.0);
            hue = (float) (Math.atan2(dy, dx) / (Math.PI * 2.0));
            if (hue < 0.0f) hue += 1.0f;
            saturation = Math.clamp((float) (Math.sqrt(dx * dx + dy * dy) / (wheelSize / 2.0)), 0.0f, 1.0f);
        } else if (drag == Drag.BRIGHTNESS) {
            brightness = sliderValue(x, sliderX, sliderWidth);
        } else if (drag == Drag.RED || drag == Drag.GREEN || drag == Drag.BLUE) {
            int channel = drag.ordinal() - Drag.RED.ordinal();
            int barX = sliderX + 15;
            int barWidth = rgbBarWidth(sliderWidth);
            int value = Math.round(sliderValue(x, barX, barWidth) * 255.0f);
            setRgb(withChannel(currentRgb(), channel, value));
        }
        apply();
    }

    private static float sliderValue(double mouseX, int x, int width) {
        return Math.clamp((float) ((mouseX - x) / Math.max(1, width - 1)), 0.0f, 1.0f);
    }

    static int rgbBarWidth(int sliderWidth) {
        return Math.max(1, sliderWidth - 47);
    }

    static PresetLayout presetLayout(int windowX, int windowWidth, boolean transparent) {
        int count = PRESETS.length + (transparent ? 1 : 0);
        int startX = windowX + Math.min(70, Math.max(14, windowWidth / 4));
        int available = Math.max(1, windowX + windowWidth - 14 - startX);
        int gap = count <= 1 ? 0 : Math.min(5, Math.max(0, available / Math.max(1, count * 5)));
        int swatch = Math.max(1, Math.min(20,
                (available - gap * Math.max(0, count - 1)) / Math.max(1, count)));
        if (swatch * count + gap * Math.max(0, count - 1) > available) gap = 0;
        return new PresetLayout(startX, swatch, gap);
    }

    private void drawFittedText(GuiGraphicsExtractor graphics, String text, int x, int y,
                                int availableWidth, int color) {
        if (availableWidth <= 0) return;
        int measured = font.width(text);
        if (measured <= availableWidth) {
            graphics.text(font, text, x, y, color, false);
            return;
        }
        float scale = availableWidth / (float) Math.max(1, measured);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + Math.round((1.0f - scale) * 4.0f));
        graphics.pose().scale(scale, scale);
        graphics.text(font, text, 0, 0, color, false);
        graphics.pose().popMatrix();
    }

    private void apply() {
        setter.accept(currentRgb());
    }

    private void makeOpaque() {
        if (!transparent) return;
        transparent = false;
        opaqueAction.run();
    }

    private static void drawTransparency(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFFE6E6E6);
        int cell = Math.max(3, Math.min(width, height) / 4);
        for (int yy = 0; yy < height; yy += cell) {
            for (int xx = 0; xx < width; xx += cell) {
                if (((xx / cell) + (yy / cell)) % 2 == 0) {
                    graphics.fill(x + xx, y + yy, Math.min(x + width, x + xx + cell),
                            Math.min(y + height, y + yy + cell), 0xFF9A9A9A);
                }
            }
        }
        graphics.fill(x + 2, y + height - 3, x + width - 2, y + height - 1, 0xFFE74C3C);
    }

    private int currentRgb() {
        return hsvToRgb(hue, saturation, brightness);
    }

    private void setRgb(int rgb) {
        float[] hsv = rgbToHsv(rgb);
        hue = hsv[0];
        saturation = hsv[1];
        brightness = hsv[2];
    }

    @Override
    public void onClose() {
        apply();
        MinecraftClientCompat.setScreen(minecraft, parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static int channel(int rgb, int channel) {
        return switch (channel) {
            case 0 -> (rgb >> 16) & 0xFF;
            case 1 -> (rgb >> 8) & 0xFF;
            default -> rgb & 0xFF;
        };
    }

    private static int withChannel(int rgb, int channel, int value) {
        value = Math.clamp(value, 0, 255);
        return switch (channel) {
            case 0 -> (rgb & 0x00FFFF) | (value << 16);
            case 1 -> (rgb & 0xFF00FF) | (value << 8);
            default -> (rgb & 0xFFFF00) | value;
        };
    }

    static int hsvToRgb(float hue, float saturation, float value) {
        float h = (hue - (float) Math.floor(hue)) * 6.0f;
        int sector = (int) h;
        float fraction = h - sector;
        float p = value * (1.0f - saturation);
        float q = value * (1.0f - fraction * saturation);
        float t = value * (1.0f - (1.0f - fraction) * saturation);
        float r;
        float g;
        float b;
        switch (sector % 6) {
            case 0 -> { r = value; g = t; b = p; }
            case 1 -> { r = q; g = value; b = p; }
            case 2 -> { r = p; g = value; b = t; }
            case 3 -> { r = p; g = q; b = value; }
            case 4 -> { r = t; g = p; b = value; }
            default -> { r = value; g = p; b = q; }
        }
        return Math.round(r * 255.0f) << 16 | Math.round(g * 255.0f) << 8 | Math.round(b * 255.0f);
    }

    static float[] rgbToHsv(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float h;
        if (delta == 0.0f) h = 0.0f;
        else if (max == r) h = ((g - b) / delta) % 6.0f;
        else if (max == g) h = (b - r) / delta + 2.0f;
        else h = (r - g) / delta + 4.0f;
        h /= 6.0f;
        if (h < 0.0f) h += 1.0f;
        return new float[]{h, max == 0.0f ? 0.0f : delta / max, max};
    }

    private enum Drag { NONE, WHEEL, BRIGHTNESS, RED, GREEN, BLUE }

    record PresetLayout(int startX, int swatchSize, int gap) {
    }
}

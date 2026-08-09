package cloudy.autume.addition.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;

final class UiAnimation {
    private static final long DURATION_NANOS = 180_000_000L;

    private UiAnimation() {
    }

    static float scale(long openedAt) {
        if (!ConfigManager.get().hudStyle.animations) return 1.0f;
        float linear = Math.clamp((System.nanoTime() - openedAt) / (float) DURATION_NANOS, 0.0f, 1.0f);
        float eased = 1.0f - (1.0f - linear) * (1.0f - linear) * (1.0f - linear);
        return 0.94f + eased * 0.06f;
    }

    static void push(GuiGraphicsExtractor graphics, float scale, float centerX, float centerY) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, centerY);
        graphics.pose().scale(scale, scale);
        graphics.pose().translate(-centerX, -centerY);
    }

    static void pop(GuiGraphicsExtractor graphics) {
        graphics.pose().popMatrix();
    }
}

package cloudy.autume.addition.config;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.i18n.ModText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Explicit second confirmation before any optional provider capability scan begins. */
final class IntegrationScanConfirmScreen extends Screen {
    private static final int BUTTON_WIDTH = 112;
    private static final int BUTTON_HEIGHT = 22;

    private final Screen parent;
    private final UnifiedModIntegration.ScanView view;
    private final Runnable confirmed;
    private final long openedAt = System.nanoTime();
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int confirmX;
    private int cancelX;
    private int buttonWidth;
    private int confirmY;
    private int cancelY;

    IntegrationScanConfirmScreen(Screen parent, UnifiedModIntegration.ScanView view, Runnable confirmed) {
        super(ModText.component("config.integration.scan.confirm.title"));
        this.parent = parent;
        this.view = view;
        this.confirmed = confirmed;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layout();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        UiAnimation.push(graphics, UiAnimation.scale(openedAt), width / 2.0f, height / 2.0f);
        graphics.fill(windowX + 4, windowY + 5, windowX + windowWidth + 5,
                windowY + windowHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1, windowY + 34,
                AcaUiTheme.HEADER);
        graphics.centeredText(font,
                Component.literal(ModText.get("config.integration.scan.confirm.title"))
                        .withStyle(ChatFormatting.BOLD),
                windowX + windowWidth / 2, windowY + 11, AcaUiTheme.TEXT);

        int textX = windowX + 18;
        int textWidth = Math.max(1, windowWidth - 36);
        int y = windowY + 50;
        y = drawWrapped(graphics, ModText.get(detailKey(view)), textX, y, textWidth, AcaUiTheme.TEXT);
        drawWrapped(graphics, ModText.get("config.integration.scan.confirm.warning"),
                textX, y + 8, textWidth, AcaUiTheme.TEXT_MUTED);

        AcaUiTheme.button(graphics, font, ModText.get("config.integration.scan.confirm.action"),
                confirmX, confirmY, buttonWidth, BUTTON_HEIGHT,
                AcaUiTheme.contains(mouseX, mouseY, confirmX, confirmY, buttonWidth, BUTTON_HEIGHT), true);
        AcaUiTheme.button(graphics, font, ModText.get("config.integration.scan.confirm.cancel"),
                cancelX, cancelY, buttonWidth, BUTTON_HEIGHT,
                AcaUiTheme.contains(mouseX, mouseY, cancelX, cancelY, buttonWidth, BUTTON_HEIGHT), false);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        UiAnimation.pop(graphics);
    }

    private void layout() {
        int horizontalMargin = Math.min(24, Math.max(0, width - 1));
        int verticalMargin = Math.min(24, Math.max(0, height - 1));
        windowWidth = Math.max(1, Math.min(460, width - horizontalMargin));
        windowHeight = Math.max(1, Math.min(210, height - verticalMargin));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;
        int gap = 12;
        buttonWidth = Math.max(1, Math.min(BUTTON_WIDTH, windowWidth - 36));
        int controlsWidth = buttonWidth * 2 + gap;
        if (controlsWidth <= windowWidth - 24) {
            confirmX = windowX + (windowWidth - controlsWidth) / 2;
            cancelX = confirmX + buttonWidth + gap;
            confirmY = cancelY = windowY + windowHeight - BUTTON_HEIGHT - 16;
        } else {
            confirmX = cancelX = windowX + (windowWidth - buttonWidth) / 2;
            cancelY = windowY + windowHeight - BUTTON_HEIGHT - 10;
            confirmY = cancelY - BUTTON_HEIGHT - 7;
        }
    }

    private int drawWrapped(GuiGraphicsExtractor graphics, String text, int x, int y,
                            int availableWidth, int color) {
        List<FormattedCharSequence> lines = font.split(Component.literal(text), availableWidth);
        for (FormattedCharSequence line : lines) {
            graphics.text(font, line, x, y, color, false);
            y += 11;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        if (AcaUiTheme.contains(click.x(), click.y(), confirmX, confirmY, buttonWidth, BUTTON_HEIGHT)) {
            confirm();
            return true;
        }
        if (AcaUiTheme.contains(click.x(), click.y(), cancelX, cancelY, buttonWidth, BUTTON_HEIGHT)) {
            onClose();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (confirmsWithKey(event.key())) {
            confirm();
            return true;
        }
        return super.keyPressed(event);
    }

    private void confirm() {
        confirmed.run();
    }

    @Override
    public void onClose() {
        MinecraftClientCompat.setScreen(minecraft, parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static String detailKey(UnifiedModIntegration.ScanView view) {
        return view == UnifiedModIntegration.ScanView.HUD
                ? "config.integration.scan.confirm.hud"
                : "config.integration.scan.confirm.settings";
    }

    static boolean confirmsWithKey(int key) {
        return key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER;
    }
}

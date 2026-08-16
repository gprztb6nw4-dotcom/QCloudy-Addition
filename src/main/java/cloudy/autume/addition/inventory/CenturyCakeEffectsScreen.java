package cloudy.autume.addition.inventory;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.config.AcaUiTheme;
import cloudy.autume.addition.i18n.ModText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** `/effects`-inspired, read-only Century Cake timer screen. */
public final class CenturyCakeEffectsScreen extends Screen {
    private static final int SLOT = 28;
    private static final int COLUMNS = 7;
    private final Screen parent;
    private final List<Hit> hits = new ArrayList<>();
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public CenturyCakeEffectsScreen(Screen parent) {
        super(Component.literal("Century Cake Effects"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        List<CenturyCakeManager.CakeStatus> statuses = CenturyCakeManager.current(minecraft);
        int rows = (statuses.size() + COLUMNS - 1) / COLUMNS;
        panelWidth = COLUMNS * SLOT + 28;
        panelHeight = rows * SLOT + 70;
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;

        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.fill(panelX + 4, panelY + 5, panelX + panelWidth + 5, panelY + panelHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, panelX, panelY, panelWidth, panelHeight, AcaUiTheme.WINDOW);
        graphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + 30, AcaUiTheme.HEADER);
        graphics.text(font, ModText.get("century_cake.screen.title"), panelX + 10, panelY + 10,
                AcaUiTheme.TEXT, false);
        AcaUiTheme.button(graphics, font, "×", panelX + panelWidth - 26, panelY + 6, 18, 18,
                AcaUiTheme.contains(mouseX, mouseY, panelX + panelWidth - 26, panelY + 6, 18, 18), false);

        hits.clear();
        int startX = panelX + 14;
        int startY = panelY + 38;
        for (int index = 0; index < statuses.size(); index++) {
            CenturyCakeManager.CakeStatus status = statuses.get(index);
            int x = startX + index % COLUMNS * SLOT;
            int y = startY + index / COLUMNS * SLOT;
            boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, 24, 24);
            graphics.fill(x, y, x + 24, y + 24, hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CONTROL);
            graphics.outline(x, y, 24, 24, status.active() ? 0xFF55FF55 : AcaUiTheme.BORDER);
            graphics.item(status.cake().icon(), x + 4, y + 4);
            if (!status.active()) graphics.fill(x + 1, y + 1, x + 23, y + 23, 0x88000000);
            hits.add(new Hit(status, x, y, 24, 24));
        }

        for (Hit hit : hits) {
            if (!hit.contains(mouseX, mouseY)) continue;
            CenturyCakeManager.CakeStatus status = hit.status();
            List<Component> tooltip = List.of(
                    Component.literal(status.cake().name()).withStyle(ChatFormatting.GREEN),
                    Component.literal("Furniture").withStyle(ChatFormatting.DARK_GRAY),
                    Component.empty(),
                    Component.literal(status.cake().bonus()).withStyle(ChatFormatting.YELLOW),
                    Component.literal(ModText.get("century_cake.remaining",
                            formatRemaining(status.remainingMs()))).withStyle(ChatFormatting.GRAY),
                    Component.empty(),
                    Component.literal(status.cake().rarity()).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
            break;
        }
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    static String formatRemaining(long milliseconds) {
        if (milliseconds <= 0L) return ModText.get("century_cake.inactive");
        long seconds = milliseconds / 1_000L;
        long days = seconds / 86_400L;
        long hours = seconds % 86_400L / 3_600L;
        long minutes = seconds % 3_600L / 60L;
        if (days > 0L) return days + "d " + hours + "h " + minutes + "m";
        if (hours > 0L) return hours + "h " + minutes + "m";
        return Math.max(1L, minutes) + "m";
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() == 0 && AcaUiTheme.contains(click.x(), click.y(),
                panelX + panelWidth - 26, panelY + 6, 18, 18)) {
            onClose();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void onClose() {
        MinecraftClientCompat.setScreen(minecraft, parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Hit(CenturyCakeManager.CakeStatus status, int x, int y, int width, int height) {
        boolean contains(double mouseX, double mouseY) {
            return AcaUiTheme.contains(mouseX, mouseY, x, y, width, height);
        }
    }
}

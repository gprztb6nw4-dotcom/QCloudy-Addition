package cloudy.autume.addition.inventory.storage;

import cloudy.autume.addition.config.AcaUiTheme;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.mixin.SlotPositionAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class StorageOverlayScreen extends AbstractContainerScreen<ChestMenu> {
    private static final int SLOT = 18;
    private static final int PAGE_WIDTH = 176;
    private static final int HEADER_HEIGHT = 17;
    private static final Identifier CONTAINER_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int PLAYER_SLOT = 22;
    private static final int PLAYER_ITEM_SIZE = 20;
    private static final float PLAYER_ITEM_SCALE = PLAYER_ITEM_SIZE / 16.0f;
    private static final int PLAYER_WIDTH = 9 * PLAYER_SLOT + 14;
    private static final int PLAYER_HEIGHT = 4 * PLAYER_SLOT + 20;

    private final ContainerScreen backing;
    private final StoragePageKey activePage;
    private final List<PageLayout> layouts = new ArrayList<>();
    private EditBox search;
    private EditBox rename;
    private StoragePageKey renamingPage;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int viewportHeight;
    private int playerX;
    private int playerY;
    private int scroll;
    private int maximumScroll;
    private int lastContentHeight;
    private static int retainedScroll;

    public StorageOverlayScreen(ContainerScreen backing, StoragePageKey activePage) {
        super(backing.getMenu(), Minecraft.getInstance().player.getInventory(), backing.getTitle(), 176, 166);
        this.backing = backing;
        this.activePage = activePage;
    }

    public StoragePageKey activePage() {
        return activePage;
    }

    @Override
    protected void init() {
        super.init();
        leftPos = 0;
        topPos = 0;
        computePanel();
        search = new EditBox(font, panelX + 8, 12, Math.min(180, panelWidth - 150), 18,
                ModText.component("storage.search"));
        search.setHint(ModText.component("storage.search"));
        search.setBordered(false);
        search.setTextShadow(false);
        search.setResponder(ignored -> {
            scroll = 0;
            updateLayout();
        });
        addRenderableWidget(search);
        scroll = ConfigManager.get().inventory.retainStorageScroll ? retainedScroll : 0;
        updateLayout();
        repositionSlots();
    }

    private void computePanel() {
        var config = ConfigManager.get().inventory;
        int columns = Math.max(1, Math.min(config.storageColumns,
                Math.max(1, (width - config.storageMargin * 2 + config.storagePadding) / (PAGE_WIDTH + config.storagePadding))));
        panelWidth = columns * PAGE_WIDTH + (columns - 1) * config.storagePadding + 16;
        panelX = (width - panelWidth) / 2;
        panelY = 36;
        viewportHeight = Math.min(config.storageHeight, Math.max(90, height - PLAYER_HEIGHT - panelY - 18));
        playerX = (width - PLAYER_WIDTH) / 2;
        playerY = panelY + viewportHeight + 5;
    }

    private void updateLayout() {
        layouts.clear();
        if (minecraft == null) return;
        var config = ConfigManager.get().inventory;
        int columns = Math.max(1, (panelWidth - 16 + config.storagePadding) / (PAGE_WIDTH + config.storagePadding));
        String query = search == null ? "" : search.getValue().trim().toLowerCase(Locale.ROOT);
        int column = 0;
        int y = panelY + 8 - scroll;
        int rowHeight = 0;
        for (var entry : StorageCacheManager.current(minecraft).entrySet()) {
            String name = StorageCacheManager.displayName(minecraft, entry.getKey());
            if (!query.isBlank() && !name.toLowerCase(Locale.ROOT).contains(query)
                    && !entry.getValue().matchesPage(query)) continue;
            int height = entry.getValue().loaded()
                    ? HEADER_HEIGHT + entry.getValue().rows() * SLOT + 7 : HEADER_HEIGHT + SLOT + 7;
            int x = panelX + 8 + column * (PAGE_WIDTH + config.storagePadding);
            layouts.add(new PageLayout(entry.getKey(), entry.getValue(), x, y, PAGE_WIDTH, height));
            rowHeight = Math.max(rowHeight, height);
            column++;
            if (column >= columns) {
                column = 0;
                y += rowHeight + config.storagePadding;
                rowHeight = 0;
            }
        }
        if (column != 0) y += rowHeight + config.storagePadding;
        lastContentHeight = Math.max(0, y + scroll - (panelY + 8));
        maximumScroll = Math.max(0, lastContentHeight - (viewportHeight - 16));
        scroll = Math.clamp(scroll, 0, maximumScroll);
    }

    private void repositionSlots() {
        PageLayout active = activePage == null ? null : findLayout(activePage);
        int chestSlots = menu.getRowCount() * 9;
        for (int index = 0; index < menu.slots.size(); index++) {
            Slot slot = menu.slots.get(index);
            int x = -10000;
            int y = -10000;
            if (index < chestSlots && active != null && index >= 9) {
                int item = index - 9;
                if (item < active.data.stacks().size()) {
                    x = active.x + 8 + item % 9 * SLOT;
                    y = active.y + HEADER_HEIGHT + item / 9 * SLOT;
                }
            } else if (slot.container instanceof Inventory) {
                int containerSlot = slot.getContainerSlot();
                x = playerX + 8 + (containerSlot % 9) * PLAYER_SLOT;
                y = containerSlot < 9 ? playerY + 8 + 3 * PLAYER_SLOT + 6
                        : playerY + 8 + ((containerSlot - 9) / 9) * PLAYER_SLOT;
            }
            ((SlotPositionAccessor) slot).aca$setX(x);
            ((SlotPositionAccessor) slot).aca$setY(y);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xB0101519);
        graphics.fill(panelX + 3, panelY + 4, panelX + panelWidth + 4, panelY + viewportHeight + 5, 0x66000000);
        graphics.text(font, ModText.get("storage.title"), panelX + panelWidth / 2 - font.width(ModText.get("storage.title")) / 2,
                panelY - 20, AcaUiTheme.TEXT, false);
        drawTopButtons(graphics, mouseX, mouseY);

        graphics.enableScissor(panelX + 5, panelY + 5, panelX + panelWidth - 5, panelY + viewportHeight - 5);
        String query = search == null ? "" : search.getValue();
        for (PageLayout layout : layouts) drawPage(graphics, layout, query, mouseX, mouseY);
        graphics.disableScissor();

        drawPlayerInventoryBackground(graphics);
        graphics.text(font, ModText.get("storage.inventory"), playerX + 8, playerY - 11, AcaUiTheme.TEXT_MUTED, false);
        drawScrollbar(graphics);
    }

    private void drawPlayerInventoryBackground(GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(playerX, playerY);
        graphics.pose().scale(PLAYER_WIDTH / 176.0f, PLAYER_HEIGHT / 90.0f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE,
                0, 0, 0, 132, 176, 90, 256, 256);
        graphics.pose().popMatrix();
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (!(slot.container instanceof Inventory)) {
            super.extractSlot(graphics, slot, mouseX, mouseY);
            return;
        }
        graphics.pose().pushMatrix();
        graphics.pose().translate(slot.x, slot.y);
        graphics.pose().scale(PLAYER_ITEM_SCALE, PLAYER_ITEM_SCALE);
        graphics.pose().translate(-slot.x, -slot.y);
        super.extractSlot(graphics, slot, mouseX, mouseY);
        graphics.pose().popMatrix();
    }

    private void drawPage(GuiGraphicsExtractor graphics, PageLayout layout, String query, int mouseX, int mouseY) {
        boolean active = layout.key.equals(activePage);
        int outline = active && ConfigManager.get().inventory.outlineActiveStoragePage
                ? 0xFF000000 | ConfigManager.get().inventory.storageActiveColor : AcaUiTheme.BORDER_SOFT;
        int rows = layout.data.loaded() ? layout.data.rows() : 1;
        int contentHeight = HEADER_HEIGHT + rows * SLOT;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE,
                layout.x, layout.y, 0, 0, PAGE_WIDTH, contentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE,
                layout.x, layout.y + contentHeight, 0, 215, PAGE_WIDTH, 7, 256, 256);
        graphics.outline(layout.x, layout.y, layout.width, layout.height, outline);
        String name = StorageCacheManager.displayName(minecraft, layout.key);
        graphics.text(font, name, layout.x + 5, layout.y + 5,
                active ? 0xFF000000 | ConfigManager.get().inventory.storageActiveColor : AcaUiTheme.TEXT, false);
        if (!layout.data.loaded()) {
            graphics.text(font, ModText.get("storage.not_loaded"), layout.x + 5, layout.y + 20,
                    AcaUiTheme.TEXT_DIM, false);
            return;
        }
        for (int index = 0; index < layout.data.stacks().size(); index++) {
            ItemStack stack = layout.data.stacks().get(index);
            int x = layout.x + 8 + index % 9 * SLOT;
            int y = layout.y + HEADER_HEIGHT + index / 9 * SLOT;
            if (!query.isBlank() && ConfigManager.get().inventory.highlightStorageSearch
                    && layout.data.matchesItem(index, query)) {
                graphics.fill(x, y, x + 17, y + 17,
                        0x68000000 | ConfigManager.get().inventory.storageSearchColor);
            }
            if (!stack.isEmpty()) {
                graphics.item(stack, x, y, 0);
                graphics.itemDecorations(font, stack, x, y);
            }
        }
    }

    private void drawTopButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int vanillaX = panelX + panelWidth - 98;
        AcaUiTheme.button(graphics, font, ModText.get("storage.vanilla"), vanillaX, 12, 70, 18,
                AcaUiTheme.contains(mouseX, mouseY, vanillaX, 12, 70, 18), false);
        AcaUiTheme.button(graphics, font, "×", panelX + panelWidth - 23, 12, 18, 18,
                AcaUiTheme.contains(mouseX, mouseY, panelX + panelWidth - 23, 12, 18, 18), false);
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics) {
        if (maximumScroll <= 0) return;
        int x = panelX + panelWidth - 7;
        int y = panelY + 7;
        int height = viewportHeight - 14;
        graphics.fill(x, y, x + 3, y + height, AcaUiTheme.CONTROL);
        int thumb = Math.max(18, height * height / (height + maximumScroll));
        int thumbY = y + (height - thumb) * scroll / maximumScroll;
        graphics.fill(x, thumbY, x + 3, thumbY + thumb, AcaUiTheme.ACCENT);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // All labels are placed in absolute screen space by extractBackground.
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (!ConfigManager.get().inventory.inactivePageTooltips) return;
        for (PageLayout layout : layouts) {
            if (layout.key.equals(activePage) || !insideViewport(mouseX, mouseY)) continue;
            int index = itemAt(layout, mouseX, mouseY);
            if (index >= 0 && index < layout.data.stacks().size()) {
                ItemStack stack = layout.data.stacks().get(index);
                if (!stack.isEmpty()) graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int vanillaX = panelX + panelWidth - 98;
        if (AcaUiTheme.contains(click.x(), click.y(), vanillaX, 12, 70, 18)) {
            if (activePage == null) StorageController.showVanilla(backing);
            else StorageController.openVanillaOverview();
            return true;
        }
        if (AcaUiTheme.contains(click.x(), click.y(), panelX + panelWidth - 23, 12, 18, 18)) {
            onClose();
            return true;
        }
        for (PageLayout layout : layouts) {
            if (!insideViewport(click.x(), click.y()) || !layout.contains(click.x(), click.y())) continue;
            if (click.button() == 1 && click.y() < layout.y + HEADER_HEIGHT) {
                beginRename(layout);
                return true;
            }
            if (click.button() == 0 && !layout.key.equals(activePage)) {
                retainedScroll = scroll;
                layout.key.navigate();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private void beginRename(PageLayout layout) {
        if (rename != null) removeWidget(rename);
        renamingPage = layout.key;
        rename = new EditBox(font, layout.x + 3, layout.y + 2, layout.width - 6, 14,
                ModText.component("storage.rename"));
        rename.setMaxLength(32);
        rename.setValue(StorageCacheManager.displayName(minecraft, layout.key));
        rename.setFocused(true);
        addRenderableWidget(rename);
    }

    private void finishRename() {
        if (rename == null || renamingPage == null) return;
        StorageCacheManager.rename(minecraft, renamingPage, rename.getValue());
        removeWidget(rename);
        rename = null;
        renamingPage = null;
        updateLayout();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (rename != null && event.key() == GLFW.GLFW_KEY_ENTER) {
            finishRename();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (!insideViewport(mouseX, mouseY)) return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        int direction = ConfigManager.get().inventory.inverseStorageScroll ? 1 : -1;
        scroll = Math.clamp(scroll + (int) Math.round(vertical * ConfigManager.get().inventory.storageScrollSpeed * direction),
                0, maximumScroll);
        retainedScroll = scroll;
        updateLayout();
        repositionSlots();
        return true;
    }

    private int itemAt(PageLayout layout, double mouseX, double mouseY) {
        int localX = (int) mouseX - (layout.x + 8);
        int localY = (int) mouseY - (layout.y + HEADER_HEIGHT);
        if (localX < 0 || localY < 0 || localX >= 9 * SLOT) return -1;
        int column = localX / SLOT;
        int row = localY / SLOT;
        if (localX % SLOT >= 17 || localY % SLOT >= 17) return -1;
        return row * 9 + column;
    }

    private boolean insideViewport(double x, double y) {
        return x >= panelX + 5 && x <= panelX + panelWidth - 5
                && y >= panelY + 5 && y <= panelY + viewportHeight - 5;
    }

    private PageLayout findLayout(StoragePageKey key) {
        for (PageLayout layout : layouts) if (layout.key.equals(key)) return layout;
        return null;
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
        if (insideViewport(mouseX, mouseY)
                || AcaUiTheme.contains(mouseX, mouseY, playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) return false;
        return super.hasClickedOutside(mouseX, mouseY, left, top);
    }

    @Override
    public void onClose() {
        if (!ConfigManager.get().inventory.retainStorageScroll) retainedScroll = 0;
        super.onClose();
    }

    private record PageLayout(StoragePageKey key, StorageCacheManager.PageData data,
                              int x, int y, int width, int height) {
        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
}

package cloudy.autume.addition.config;

import cloudy.autume.addition.i18n.ModText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public final class ConfigScreen extends Screen {
    private static final Identifier ICON = Identifier.fromNamespaceAndPath(
            "qcloudy_addition", "icon.png");
    private static final int CARD_HEIGHT = 72;
    private static final int CARD_GAP = 8;
    private static final int GROUP_HEADER_HEIGHT = 22;
    private static final int TOP_CONTROL_Y_OFFSET = 37;
    private static final int TOP_CONTROL_HEIGHT = 18;
    private static final int SEARCH_HORIZONTAL_PADDING = 5;
    private static final int SEARCH_NAVIGATION_GAP = 8;

    private final @Nullable Screen parent;
    private final long openedAt = System.nanoTime();
    private final List<Hit<Feature>> featureHits = new ArrayList<>();
    private final List<Hit<FeatureGroup>> groupHits = new ArrayList<>();
    private final EnumSet<FeatureGroup> expandedGroups = EnumSet.noneOf(FeatureGroup.class);
    private Category category = Category.GENERAL;
    private EditBox searchBox;
    private String query = "";
    private int scroll;
    private int maximumScroll;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int sidebarWidth;
    private int contentX;
    private int contentY;
    private int contentWidth;
    private int contentHeight;
    private int navigationX;
    private int navigationY;
    private int navigationTabWidth;
    private int searchFrameX;
    private int searchFrameY;
    private int searchFrameWidth;

    public ConfigScreen(@Nullable Screen parent) {
        this(parent, null);
    }

    public ConfigScreen(@Nullable Screen parent, @Nullable HudFocus focus) {
        super(Component.literal("QCloudy_Addition"));
        this.parent = parent;
        if (focus != null) {
            category = switch (focus) {
                case MAP -> Category.MAPS;
                case MINING -> Category.MINING;
                case FORAGING -> Category.FORAGING;
                case HUNTING, SAFARI -> Category.HUNTING;
                case PET -> Category.INVENTORY;
            };
        }
    }

    @Override
    protected void init() {
        layoutWindow();
        int textY = searchFrameY + Math.max(0, (TOP_CONTROL_HEIGHT - font.lineHeight) / 2);
        searchBox = new EditBox(font, searchFrameX + SEARCH_HORIZONTAL_PADDING, textY,
                searchFrameWidth - SEARCH_HORIZONTAL_PADDING * 2, font.lineHeight,
                ModText.component("config.search"));
        searchBox.setBordered(false);
        searchBox.setTextShadow(false);
        searchBox.setMaxLength(64);
        searchBox.setHint(ModText.component("config.search"));
        searchBox.setTextColor(AcaUiTheme.TEXT);
        searchBox.setValue(query);
        searchBox.setResponder(value -> {
            query = value;
            scroll = 0;
        });
        addRenderableWidget(searchBox);
    }

    private void layoutWindow() {
        windowWidth = Math.min(640, Math.max(320, width - 20));
        windowHeight = Math.min(380, Math.max(220, height - 20));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;
        sidebarWidth = windowWidth >= 460 ? 112 : 88;
        contentX = windowX + sidebarWidth + 10;
        contentY = windowY + 68;
        contentWidth = windowWidth - sidebarWidth - 20;
        contentHeight = windowHeight - 80;

        searchFrameWidth = Math.clamp(contentWidth / 3, 86, 150);
        searchFrameX = contentX + contentWidth - searchFrameWidth;
        searchFrameY = windowY + TOP_CONTROL_Y_OFFSET;
        navigationX = contentX;
        navigationY = searchFrameY;
        int navigationWidth = Math.max(96, searchFrameX - SEARCH_NAVIGATION_GAP - navigationX);
        navigationTabWidth = Math.clamp(navigationWidth, 72, 92);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layoutWindow();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        UiAnimation.push(graphics, UiAnimation.scale(openedAt), width / 2.0f, height / 2.0f);
        graphics.fill(windowX + 4, windowY + 5, windowX + windowWidth + 5, windowY + windowHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1, windowY + 31, AcaUiTheme.HEADER);
        graphics.fill(windowX + 1, windowY + 31, windowX + sidebarWidth, windowY + windowHeight - 1,
                AcaUiTheme.SIDEBAR);
        graphics.fill(windowX + sidebarWidth, windowY + 31, windowX + windowWidth - 1,
                windowY + windowHeight - 1, AcaUiTheme.CONTENT);

        drawBrand(graphics, mouseX, mouseY);
        drawTopNavigation(graphics, mouseX, mouseY);
        drawSidebar(graphics, mouseX, mouseY);
        drawSearchBackground(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        drawContent(graphics, mouseX, mouseY);
        UiAnimation.pop(graphics);
    }

    private void drawBrand(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON, windowX + 9, windowY + 7, 0, 0,
                18, 18, 18, 18);
        graphics.text(font, Component.literal("QCLOUDY").withStyle(ChatFormatting.BOLD),
                windowX + 32, windowY + 7, AcaUiTheme.TEXT, false);
        graphics.text(font, "ADDITION", windowX + 32, windowY + 17, AcaUiTheme.TEXT_DIM, false);
        int closeX = windowX + windowWidth - 22;
        boolean hovered = AcaUiTheme.contains(mouseX, mouseY, closeX, windowY + 7, 14, 14);
        graphics.fill(closeX, windowY + 7, closeX + 14, windowY + 21, hovered ? AcaUiTheme.DANGER : AcaUiTheme.CONTROL);
        graphics.outline(closeX, windowY + 7, 14, 14, AcaUiTheme.BORDER);
        graphics.centeredText(font, "×", closeX + 7, windowY + 9, AcaUiTheme.TEXT);
    }

    private void drawTopNavigation(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        AcaUiTheme.button(graphics, font, ModText.get("config.tab.features"), navigationX, navigationY,
                navigationTabWidth, TOP_CONTROL_HEIGHT,
                AcaUiTheme.contains(mouseX, mouseY, navigationX, navigationY,
                        navigationTabWidth, TOP_CONTROL_HEIGHT), true);
    }

    private void drawSearchBackground(GuiGraphicsExtractor graphics) {
        if (searchBox == null) return;
        graphics.fill(searchFrameX, searchFrameY, searchFrameX + searchFrameWidth,
                searchFrameY + TOP_CONTROL_HEIGHT, AcaUiTheme.CONTROL);
        graphics.outline(searchFrameX, searchFrameY, searchFrameWidth, TOP_CONTROL_HEIGHT,
                searchBox.isFocused() ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
    }

    private void drawSidebar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = windowX + 8;
        int y = windowY + 43;
        int width = sidebarWidth - 16;
        for (Category value : Category.values()) {
            boolean selected = category == value;
            boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, width, 22);
            if (selected) graphics.fill(x, y, x + 3, y + 22, AcaUiTheme.ACCENT);
            graphics.fill(x + 3, y, x + width, y + 22,
                    selected ? 0xFF303A3F : hovered ? 0xFF2B3337 : AcaUiTheme.SIDEBAR);
            graphics.text(font, ModText.get(value.key), x + 10, y + 7,
                    selected ? AcaUiTheme.TEXT : AcaUiTheme.TEXT_MUTED, false);
            y += 24;
        }

        int editY = windowY + windowHeight - 52;
        AcaUiTheme.button(graphics, font, ModText.get("config.layout"), x, editY, width, 20,
                AcaUiTheme.contains(mouseX, mouseY, x, editY, width, 20), false);
        int languageY = windowY + windowHeight - 27;
        AcaUiTheme.button(graphics, font, ModText.get("config.language.short"), x, languageY, width, 18,
                AcaUiTheme.contains(mouseX, mouseY, x, languageY, width, 18), false);
    }

    private void drawContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        featureHits.clear();
        groupHits.clear();
        graphics.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeight);
        drawFeatureCards(graphics, mouseX, mouseY);
        graphics.disableScissor();
        if (maximumScroll > 0) drawScrollbar(graphics);
    }

    private void drawFeatureCards(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<GroupBlock> blocks = groupBlocks();
        if (blocks.isEmpty()) {
            drawEmpty(graphics);
            maximumScroll = 0;
            return;
        }
        int columns = contentWidth >= 360 ? 2 : 1;
        int cardWidth = (contentWidth - 5 - CARD_GAP * (columns - 1)) / columns;
        int totalHeight = 0;
        for (GroupBlock block : blocks) {
            totalHeight += GROUP_HEADER_HEIGHT;
            if (block.expanded()) {
                int rows = (block.features().size() + columns - 1) / columns;
                totalHeight += 5 + rows * (CARD_HEIGHT + CARD_GAP) - CARD_GAP;
            }
            totalHeight += CARD_GAP;
        }
        maximumScroll = Math.max(0, totalHeight - CARD_GAP - contentHeight);
        scroll = Math.clamp(scroll, 0, maximumScroll);
        int y = contentY - scroll;
        for (GroupBlock block : blocks) {
            drawGroupHeader(graphics, block, contentX, y, contentWidth - 5, mouseX, mouseY);
            groupHits.add(new Hit<>(block.group(), contentX, y, contentWidth - 5, GROUP_HEADER_HEIGHT));
            y += GROUP_HEADER_HEIGHT + 5;
            if (block.expanded()) {
                for (int index = 0; index < block.features().size(); index++) {
                    Feature feature = block.features().get(index);
                    int column = index % columns;
                    int row = index / columns;
                    int x = contentX + column * (cardWidth + CARD_GAP);
                    int cardY = y + row * (CARD_HEIGHT + CARD_GAP);
                    drawFeatureCard(graphics, feature, x, cardY, cardWidth, mouseX, mouseY);
                    featureHits.add(new Hit<>(feature, x, cardY, cardWidth, CARD_HEIGHT));
                }
                int rows = (block.features().size() + columns - 1) / columns;
                y += rows * (CARD_HEIGHT + CARD_GAP) - CARD_GAP;
            }
            y += CARD_GAP;
        }
    }

    private List<GroupBlock> groupBlocks() {
        List<GroupBlock> blocks = new ArrayList<>();
        boolean searching = !query.trim().isEmpty();
        for (FeatureGroup group : FeatureGroup.values()) {
            if (group.category != category) continue;
            List<Feature> features = new ArrayList<>();
            for (Feature feature : Feature.values()) {
                if (feature.group == group && matches(feature.titleKey, feature.descriptionKey)) {
                    features.add(feature);
                }
            }
            if (!features.isEmpty()) blocks.add(new GroupBlock(group, features,
                    searching || expandedGroups.contains(group)));
        }
        return blocks;
    }

    private void drawGroupHeader(GuiGraphicsExtractor graphics, GroupBlock block, int x, int y, int width,
                                 int mouseX, int mouseY) {
        boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, width, GROUP_HEADER_HEIGHT);
        graphics.fill(x, y, x + width, y + GROUP_HEADER_HEIGHT,
                hovered ? AcaUiTheme.CARD_HOVER : 0xFF20292D);
        graphics.outline(x, y, width, GROUP_HEADER_HEIGHT,
                hovered ? AcaUiTheme.ACCENT_DARK : AcaUiTheme.BORDER_SOFT);
        graphics.text(font, block.expanded() ? "▾" : "▸", x + 8, y + 7, AcaUiTheme.ACCENT, false);
        graphics.text(font, Component.literal(ModText.get(block.group().key)).withStyle(ChatFormatting.BOLD),
                x + 22, y + 7, AcaUiTheme.TEXT, false);
        String count = Integer.toString(block.features().size());
        graphics.text(font, count, x + width - font.width(count) - 10, y + 7, AcaUiTheme.TEXT_DIM, false);
    }

    private void drawFeatureCard(GuiGraphicsExtractor graphics, Feature feature, int x, int y, int cardWidth,
                                 int mouseX, int mouseY) {
        boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, cardWidth, CARD_HEIGHT);
        boolean enabled = feature.enabled(ConfigManager.get());
        graphics.fill(x, y, x + cardWidth, y + CARD_HEIGHT, hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CARD);
        graphics.outline(x, y, cardWidth, CARD_HEIGHT, hovered ? AcaUiTheme.ACCENT_DARK : AcaUiTheme.BORDER_SOFT);
        graphics.fill(x, y, x + 3, y + CARD_HEIGHT, enabled ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
        Component title = Component.literal(ModText.get(feature.titleKey)).withStyle(ChatFormatting.BOLD);
        drawFittedText(graphics, title, x + 10, y + 8, Math.max(30, cardWidth - 20), AcaUiTheme.TEXT);
        List<FormattedCharSequence> lines = font.split(Component.literal(ModText.get(feature.descriptionKey)),
                Math.max(40, cardWidth - 20));
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            graphics.text(font, lines.get(i), x + 10, y + 24 + i * 10, AcaUiTheme.TEXT_MUTED, false);
        }
        graphics.text(font, ModText.get(feature.group.key), x + 10, y + 57, AcaUiTheme.TEXT_DIM, false);
    }

    private void drawEmpty(GuiGraphicsExtractor graphics) {
        graphics.centeredText(font, ModText.get("config.empty"), contentX + contentWidth / 2,
                contentY + contentHeight / 2 - 5, AcaUiTheme.TEXT_MUTED);
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics) {
        int barX = contentX + contentWidth - 3;
        int thumbHeight = Math.max(20, contentHeight * contentHeight / (contentHeight + maximumScroll));
        int travel = contentHeight - thumbHeight;
        int thumbY = contentY + (maximumScroll == 0 ? 0 : travel * scroll / maximumScroll);
        graphics.fill(barX, contentY, barX + 2, contentY + contentHeight, AcaUiTheme.CONTROL);
        graphics.fill(barX, thumbY, barX + 2, thumbY + thumbHeight, AcaUiTheme.ACCENT);
    }

    private boolean matches(String titleKey, String descriptionKey) {
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) return true;
        return ModText.get(titleKey).toLowerCase(Locale.ROOT).contains(normalized)
                || ModText.get(descriptionKey).toLowerCase(Locale.ROOT).contains(normalized);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() == 1
                && AcaUiTheme.contains(click.x(), click.y(), contentX, contentY, contentWidth, contentHeight)) {
            for (Hit<Feature> hit : featureHits) {
                if (hit.contains(click.x(), click.y()) && hit.value.hasSettings()) {
                    minecraft.setScreen(new FeatureSettingsScreen(this, hit.value));
                    return true;
                }
            }
        }
        if (super.mouseClicked(click, doubled)) return true;
        if (click.button() != 0) return false;
        double mouseX = click.x();
        double mouseY = click.y();
        if (searchBox != null && AcaUiTheme.contains(mouseX, mouseY, searchFrameX, searchFrameY,
                searchFrameWidth, TOP_CONTROL_HEIGHT)) {
            searchBox.setFocused(true);
            return true;
        }
        if (AcaUiTheme.contains(mouseX, mouseY, windowX + windowWidth - 22, windowY + 7, 14, 14)) {
            onClose();
            return true;
        }
        if (AcaUiTheme.contains(mouseX, mouseY, navigationX, navigationY,
                navigationTabWidth, TOP_CONTROL_HEIGHT)) {
            scroll = 0;
            return true;
        }
        int sidebarX = windowX + 8;
        int sidebarY = windowY + 43;
        int sideWidth = sidebarWidth - 16;
        for (Category value : Category.values()) {
            if (AcaUiTheme.contains(mouseX, mouseY, sidebarX, sidebarY, sideWidth, 22)) {
                category = value;
                scroll = 0;
                return true;
            }
            sidebarY += 24;
        }
        int editY = windowY + windowHeight - 52;
        if (AcaUiTheme.contains(mouseX, mouseY, sidebarX, editY, sideWidth, 20)) {
            minecraft.setScreen(new HudLayoutScreen(this));
            return true;
        }
        int languageY = windowY + windowHeight - 27;
        if (AcaUiTheme.contains(mouseX, mouseY, sidebarX, languageY, sideWidth, 18)) {
            ModConfig config = ConfigManager.get();
            config.language = "zh_cn".equals(config.language) ? "en_us" : "zh_cn";
            ConfigManager.save();
            rebuildWidgets();
            return true;
        }
        if (!AcaUiTheme.contains(mouseX, mouseY, contentX, contentY, contentWidth, contentHeight)) return false;
        for (Hit<FeatureGroup> hit : groupHits) {
            if (!hit.contains(mouseX, mouseY)) continue;
            if (!expandedGroups.remove(hit.value)) expandedGroups.add(hit.value);
            scroll = Math.clamp(scroll, 0, maximumScroll);
            return true;
        }
        for (Hit<Feature> hit : featureHits) {
            if (!hit.contains(mouseX, mouseY)) continue;
            hit.value.toggle(ConfigManager.get());
            ConfigManager.save();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (AcaUiTheme.contains(mouseX, mouseY, contentX, contentY, contentWidth, contentHeight)) {
            scroll = Math.clamp(scroll - (int) Math.round(vertical * 24), 0, maximumScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawFittedText(GuiGraphicsExtractor graphics, Component value, int x, int y,
                                int maximumWidth, int color) {
        int measured = font.width(value);
        if (measured <= maximumWidth) {
            graphics.text(font, value, x, y, color, false);
            return;
        }
        float scale = maximumWidth / (float) measured;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + Math.round((1.0f - scale) * 4.0f));
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, color, false);
        graphics.pose().popMatrix();
    }

    public enum HudFocus { MAP, MINING, FORAGING, HUNTING, SAFARI, PET }

    enum Category {
        GENERAL("config.category.general"),
        MAPS("config.category.maps"),
        INVENTORY("config.category.inventory"),
        COMBAT("config.category.combat"),
        MINING("config.category.mining"),
        FORAGING("config.category.foraging"),
        HUNTING("config.category.hunting");

        private final String key;

        Category(String key) {
            this.key = key;
        }
    }

    enum FeatureGroup {
        HUD(Category.GENERAL, "config.group.hud"),
        CONNECTION(Category.GENERAL, "config.group.connection"),
        FISHING(Category.GENERAL, "config.group.fishing"),
        MAPS(Category.MAPS, "config.group.maps"),
        WAYPOINTS(Category.MAPS, "config.group.waypoints"),
        MINING_OBJECTIVES(Category.MINING, "config.group.mining_objectives"),
        FORAGING_TORRHUS(Category.FORAGING, "config.group.torrhus"),
        FORAGING_GALATEA(Category.FORAGING, "config.group.galatea"),
        HUNTING_CORE(Category.HUNTING, "config.group.hunting_core"),
        HUNTING_SAFARI(Category.HUNTING, "config.group.safari"),
        CRIMSON_OBJECTIVES(Category.COMBAT, "config.group.crimson_objectives"),
        COMBAT_VISIBILITY(Category.COMBAT, "config.group.combat_visibility"),
        PET_DISPLAY(Category.INVENTORY, "config.group.pet_display"),
        SHARD_FUSION(Category.INVENTORY, "config.group.shard_fusion"),
        CHAT_UI(Category.GENERAL, "config.group.chat_ui"),
        INVENTORY_TOOLS(Category.INVENTORY, "config.group.inventory_tools");

        private final Category category;
        private final String key;

        FeatureGroup(Category category, String key) {
            this.category = category;
            this.key = key;
        }
    }

    enum Feature {
        HUD_ANIMATIONS(FeatureGroup.HUD, "config.setting.animations", "config.desc.animations"),
        HUNTING_ALERT_SOUND(FeatureGroup.HUD, "config.hunting.alert_sound", "config.desc.hunting.alert_sound"),
        MANUAL_RECONNECT(FeatureGroup.CONNECTION, "config.manual_reconnect", "config.desc.manual_reconnect"),
        FISHING_BITE_ALERT(FeatureGroup.FISHING, "config.fishing.bite_alert", "config.desc.fishing.bite_alert"),
        DWARVEN_MAP(FeatureGroup.MAPS, "config.dwarven_map", "config.desc.dwarven_map"),
        GLACITE_MAP(FeatureGroup.MAPS, "config.glacite_map", "config.desc.glacite_map"),
        FAIRY_SOUL_WAYPOINTS(FeatureGroup.WAYPOINTS, "config.hunting.fairy_souls", "config.desc.hunting.fairy_souls"),
        MINING_TRACKER(FeatureGroup.MINING_OBJECTIVES, "config.mining_tracker", "config.desc.mining_tracker"),
        TORRHUS_TRACKER(FeatureGroup.FORAGING_TORRHUS, "config.hunting.torrhus_tracker", "config.desc.hunting.torrhus_tracker"),
        GALATEA_TRACKER(FeatureGroup.FORAGING_GALATEA, "config.hunting.galatea_tracker", "config.desc.hunting.galatea_tracker"),
        TREE_CRITTER_TIMER(FeatureGroup.FORAGING_TORRHUS, "config.hunting.tree_critter_timer", "config.desc.hunting.tree_critter_timer"),
        MIRIA_CONTEST(FeatureGroup.FORAGING_TORRHUS, "config.hunting.miria_contest", "config.desc.hunting.miria_contest"),
        AGATHA_CONTEST(FeatureGroup.FORAGING_GALATEA, "config.hunting.agatha_contest", "config.desc.hunting.agatha_contest"),
        BENEFACTOR_HUD(FeatureGroup.FORAGING_TORRHUS, "config.hunting.benefactor", "config.desc.hunting.benefactor"),
        TREE_GIFT_ALERTS(FeatureGroup.FORAGING_TORRHUS, "config.hunting.tree_gift", "config.desc.hunting.tree_gift"),
        BEEHEEMOTH_HELPER(FeatureGroup.HUNTING_CORE, "config.hunting.beeheemoth", "config.desc.hunting.beeheemoth"),
        LASSO_REEL_SOUND(FeatureGroup.HUNTING_CORE, "config.hunting.lasso_reel_sound", "config.desc.hunting.lasso_reel_sound"),
        CRITTER_BEHAVIOR(FeatureGroup.HUNTING_CORE, "config.hunting.critter_behavior", "config.desc.hunting.critter_behavior"),
        COLD_SAFETY(FeatureGroup.HUNTING_SAFARI, "config.hunting.cold_safety", "config.desc.hunting.cold_safety"),
        DOOMSPIRAL_READY(FeatureGroup.HUNTING_SAFARI, "config.hunting.doomspiral_ready", "config.desc.hunting.doomspiral_ready"),
        WARDEN_READY_ALERT(FeatureGroup.HUNTING_SAFARI, "config.hunting.warden_ready", "config.desc.hunting.warden_ready"),
        SAFARI_CRITTER_HIGHLIGHT(FeatureGroup.HUNTING_SAFARI, "config.hunting.critter_highlight", "config.desc.hunting.critter_highlight"),
        SAFARI_DASHBOARD(FeatureGroup.HUNTING_SAFARI, "config.hunting.safari_dashboard", "config.desc.hunting.safari_dashboard"),
        SAFARI_SHARD_STATS(FeatureGroup.HUNTING_SAFARI, "config.hunting.safari_shard_stats", "config.desc.hunting.safari_shard_stats"),
        SAFARI_CRITTERDEX(FeatureGroup.HUNTING_SAFARI, "config.hunting.safari_critterdex", "config.desc.hunting.safari_critterdex"),
        SPARKLING_ALERT(FeatureGroup.HUNTING_SAFARI, "config.hunting.sparkling", "config.desc.hunting.sparkling"),
        FLOOR_QUEST_ASSISTANT(FeatureGroup.HUNTING_SAFARI, "config.hunting.floor_quest", "config.desc.hunting.floor_quest"),
        WUMPA_HUD(FeatureGroup.HUNTING_SAFARI, "config.hunting.wumpa", "config.desc.hunting.wumpa"),
        SNOOZLE_WALL_OVERLAY(FeatureGroup.HUNTING_SAFARI, "config.hunting.snoozle_wall", "config.desc.hunting.snoozle_wall"),
        SAFARI_BELT(FeatureGroup.HUNTING_SAFARI, "config.hunting.safari_belt", "config.desc.hunting.safari_belt"),
        CRIMSON_TASKS(FeatureGroup.CRIMSON_OBJECTIVES, "config.crimson_tasks", "config.desc.crimson_tasks"),
        DRAGON_HIGHLIGHT(FeatureGroup.COMBAT_VISIBILITY, "config.dragon_highlight", "config.desc.dragon_highlight"),
        PET_HUD(FeatureGroup.PET_DISPLAY, "config.pet_hud", "config.desc.pet_hud"),
        SHARD_FUSION_HELPER(FeatureGroup.SHARD_FUSION, "config.shard_fusion", "config.desc.shard_fusion"),
        CHAT_PEEK(FeatureGroup.CHAT_UI, "config.chat_peek", "config.desc.chat_peek"),
        ITEM_TIMESTAMPS(FeatureGroup.INVENTORY_TOOLS, "config.item_timestamps", "config.desc.item_timestamps"),
        CURSOR_MEMORY(FeatureGroup.INVENTORY_TOOLS, "config.cursor_memory", "config.desc.cursor_memory"),
        TELEPORT_SOUNDS(FeatureGroup.INVENTORY_TOOLS, "config.teleport_sounds", "config.desc.teleport_sounds");

        final FeatureGroup group;
        final Category category;
        final String titleKey;
        final String descriptionKey;

        Feature(FeatureGroup group, String titleKey, String descriptionKey) {
            this.group = group;
            this.category = group.category;
            this.titleKey = titleKey;
            this.descriptionKey = descriptionKey;
        }

        boolean enabled(ModConfig config) {
            return switch (this) {
                case HUD_ANIMATIONS -> config.hudStyle.animations;
                case MANUAL_RECONNECT -> config.manualReconnectButton;
                case FISHING_BITE_ALERT -> config.fishing.biteAlert;
                case DWARVEN_MAP -> config.maps.dwarvenMines;
                case GLACITE_MAP -> config.maps.glaciteTunnels;
                case MINING_TRACKER -> config.mining.taskAndPowderTracker;
                case HUNTING_ALERT_SOUND -> config.hunting.alertSound;
                case COLD_SAFETY -> config.hunting.coldSafety;
                case DOOMSPIRAL_READY -> config.hunting.doomspiralReadyAlert;
                case WARDEN_READY_ALERT -> config.hunting.wardenReadyAlert;
                case FAIRY_SOUL_WAYPOINTS -> config.hunting.fairySoulWaypoints;
                case SAFARI_CRITTER_HIGHLIGHT -> config.hunting.safariCritterHighlight;
                case TORRHUS_TRACKER -> config.hunting.torrhusTracker;
                case GALATEA_TRACKER -> config.hunting.galateaTracker;
                case TREE_CRITTER_TIMER -> config.hunting.treeCritterTimer;
                case BEEHEEMOTH_HELPER -> config.hunting.beeheemothHelper;
                case LASSO_REEL_SOUND -> config.hunting.lassoReelAudio.sound;
                case MIRIA_CONTEST -> config.hunting.miriaContest;
                case AGATHA_CONTEST -> config.hunting.agathaContest;
                case CRITTER_BEHAVIOR -> config.hunting.critterBehavior;
                case BENEFACTOR_HUD -> config.hunting.benefactorHud;
                case TREE_GIFT_ALERTS -> config.hunting.treeGiftAlerts;
                case SAFARI_DASHBOARD -> config.hunting.safariDashboard;
                case SAFARI_SHARD_STATS -> config.hunting.safariShards;
                case SAFARI_CRITTERDEX -> config.hunting.safariCritterdex;
                case SPARKLING_ALERT -> config.hunting.sparklingAlert;
                case FLOOR_QUEST_ASSISTANT -> config.hunting.floorDropAssistant || config.hunting.questItemTracker;
                case WUMPA_HUD -> config.hunting.wumpaHud;
                case SNOOZLE_WALL_OVERLAY -> config.hunting.snoozleWallOverlay;
                case SAFARI_BELT -> config.hunting.safariBeltTooltip;
                case CRIMSON_TASKS -> config.crimsonIsle.taskTracker;
                case DRAGON_HIGHLIGHT -> config.combat.enderDragonHighlight;
                case PET_HUD -> config.pets.equippedPetHud;
                case SHARD_FUSION_HELPER -> config.inventory.shardFusionHelper;
                case CHAT_PEEK -> config.chat.chatPeek;
                case ITEM_TIMESTAMPS -> config.inventory.itemTimestamps;
                case CURSOR_MEMORY -> config.inventory.saveCursorPosition;
                case TELEPORT_SOUNDS -> config.inventory.teleportSoundCustomization;
            };
        }

        void toggle(ModConfig config) {
            switch (this) {
                case HUD_ANIMATIONS -> config.hudStyle.animations = !config.hudStyle.animations;
                case MANUAL_RECONNECT -> config.manualReconnectButton = !config.manualReconnectButton;
                case FISHING_BITE_ALERT -> config.fishing.biteAlert = !config.fishing.biteAlert;
                case DWARVEN_MAP -> config.maps.dwarvenMines = !config.maps.dwarvenMines;
                case GLACITE_MAP -> config.maps.glaciteTunnels = !config.maps.glaciteTunnels;
                case MINING_TRACKER -> config.mining.taskAndPowderTracker = !config.mining.taskAndPowderTracker;
                case HUNTING_ALERT_SOUND -> config.hunting.alertSound = !config.hunting.alertSound;
                case COLD_SAFETY -> config.hunting.coldSafety = !config.hunting.coldSafety;
                case DOOMSPIRAL_READY -> config.hunting.doomspiralReadyAlert = !config.hunting.doomspiralReadyAlert;
                case WARDEN_READY_ALERT -> config.hunting.wardenReadyAlert = !config.hunting.wardenReadyAlert;
                case FAIRY_SOUL_WAYPOINTS -> config.hunting.fairySoulWaypoints = !config.hunting.fairySoulWaypoints;
                case SAFARI_CRITTER_HIGHLIGHT -> config.hunting.safariCritterHighlight = !config.hunting.safariCritterHighlight;
                case TORRHUS_TRACKER -> config.hunting.torrhusTracker = !config.hunting.torrhusTracker;
                case GALATEA_TRACKER -> config.hunting.galateaTracker = !config.hunting.galateaTracker;
                case TREE_CRITTER_TIMER -> config.hunting.treeCritterTimer = !config.hunting.treeCritterTimer;
                case BEEHEEMOTH_HELPER -> config.hunting.beeheemothHelper = !config.hunting.beeheemothHelper;
                case LASSO_REEL_SOUND -> config.hunting.lassoReelAudio.sound = !config.hunting.lassoReelAudio.sound;
                case MIRIA_CONTEST -> config.hunting.miriaContest = !config.hunting.miriaContest;
                case AGATHA_CONTEST -> config.hunting.agathaContest = !config.hunting.agathaContest;
                case CRITTER_BEHAVIOR -> config.hunting.critterBehavior = !config.hunting.critterBehavior;
                case BENEFACTOR_HUD -> config.hunting.benefactorHud = !config.hunting.benefactorHud;
                case TREE_GIFT_ALERTS -> config.hunting.treeGiftAlerts = !config.hunting.treeGiftAlerts;
                case SAFARI_DASHBOARD -> config.hunting.safariDashboard = !config.hunting.safariDashboard;
                case SAFARI_SHARD_STATS -> config.hunting.safariShards = !config.hunting.safariShards;
                case SAFARI_CRITTERDEX -> config.hunting.safariCritterdex = !config.hunting.safariCritterdex;
                case SPARKLING_ALERT -> config.hunting.sparklingAlert = !config.hunting.sparklingAlert;
                case FLOOR_QUEST_ASSISTANT -> {
                    boolean enabled = config.hunting.floorDropAssistant || config.hunting.questItemTracker;
                    config.hunting.floorDropAssistant = !enabled;
                    config.hunting.questItemTracker = !enabled;
                }
                case WUMPA_HUD -> config.hunting.wumpaHud = !config.hunting.wumpaHud;
                case SNOOZLE_WALL_OVERLAY -> config.hunting.snoozleWallOverlay = !config.hunting.snoozleWallOverlay;
                case SAFARI_BELT -> config.hunting.safariBeltTooltip = !config.hunting.safariBeltTooltip;
                case CRIMSON_TASKS -> config.crimsonIsle.taskTracker = !config.crimsonIsle.taskTracker;
                case DRAGON_HIGHLIGHT -> config.combat.enderDragonHighlight = !config.combat.enderDragonHighlight;
                case PET_HUD -> config.pets.equippedPetHud = !config.pets.equippedPetHud;
                case SHARD_FUSION_HELPER -> config.inventory.shardFusionHelper = !config.inventory.shardFusionHelper;
                case CHAT_PEEK -> config.chat.chatPeek = !config.chat.chatPeek;
                case ITEM_TIMESTAMPS -> config.inventory.itemTimestamps = !config.inventory.itemTimestamps;
                case CURSOR_MEMORY -> config.inventory.saveCursorPosition = !config.inventory.saveCursorPosition;
                case TELEPORT_SOUNDS -> config.inventory.teleportSoundCustomization = !config.inventory.teleportSoundCustomization;
            }
        }

        ModConfig.HudType hudType() {
            return switch (this) {
                case DWARVEN_MAP, GLACITE_MAP -> ModConfig.HudType.MAP;
                case MINING_TRACKER, CRIMSON_TASKS -> ModConfig.HudType.MINING;
                case TORRHUS_TRACKER, GALATEA_TRACKER, TREE_CRITTER_TIMER, MIRIA_CONTEST, AGATHA_CONTEST,
                        CRITTER_BEHAVIOR, BENEFACTOR_HUD,
                        SAFARI_DASHBOARD, SAFARI_SHARD_STATS, SAFARI_CRITTERDEX,
                        FLOOR_QUEST_ASSISTANT, WUMPA_HUD -> ModConfig.HudType.HUNTING;
                case PET_HUD -> ModConfig.HudType.PET;
                case HUD_ANIMATIONS, HUNTING_ALERT_SOUND, MANUAL_RECONNECT, FISHING_BITE_ALERT,
                        COLD_SAFETY, DOOMSPIRAL_READY, WARDEN_READY_ALERT,
                        FAIRY_SOUL_WAYPOINTS, SAFARI_CRITTER_HIGHLIGHT, BEEHEEMOTH_HELPER,
                        LASSO_REEL_SOUND, TREE_GIFT_ALERTS,
                        SPARKLING_ALERT, SNOOZLE_WALL_OVERLAY, SAFARI_BELT, DRAGON_HIGHLIGHT, CHAT_PEEK -> null;
                case SHARD_FUSION_HELPER, ITEM_TIMESTAMPS, CURSOR_MEMORY, TELEPORT_SOUNDS -> null;
            };
        }

        boolean inventoryFeature() {
            return group.category == Category.INVENTORY;
        }

        boolean huntingFeature() {
            return group == FeatureGroup.FORAGING_TORRHUS || group == FeatureGroup.FORAGING_GALATEA
                    || group == FeatureGroup.HUNTING_CORE
                    || group == FeatureGroup.HUNTING_SAFARI;
        }

        boolean hasSettings() {
            if (this == HUD_ANIMATIONS || this == HUNTING_ALERT_SOUND || this == MANUAL_RECONNECT) return false;
            if (this == FAIRY_SOUL_WAYPOINTS) return false;
            if (huntingFeature()) return hudType() != null || !HuntingOption.forFeature(this).isEmpty();
            return true;
        }
    }

    private record GroupBlock(FeatureGroup group, List<Feature> features, boolean expanded) { }

    private record Hit<T>(T value, int x, int y, int width, int height) {
        boolean contains(double mouseX, double mouseY) {
            return AcaUiTheme.contains(mouseX, mouseY, x, y, width, height);
        }
    }
}

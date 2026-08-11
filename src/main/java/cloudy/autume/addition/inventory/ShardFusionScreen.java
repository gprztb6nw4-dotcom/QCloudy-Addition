package cloudy.autume.addition.inventory;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.config.AcaUiTheme;
import cloudy.autume.addition.i18n.ModText;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Standalone JEI-inspired browser for Attribute Shard fusion recipes. */
public final class ShardFusionScreen extends Screen {
    private static final int HEADER_HEIGHT = 32;
    private static final int CONTROL_HEIGHT = 18;
    private static final int RESULT_ROW_HEIGHT = 22;
    private static final int RECIPE_CARD_HEIGHT = 94;
    private static final int STACKED_RECIPE_CARD_HEIGHT = 132;
    private static final int MAX_HISTORY = 64;
    private static final CompletableFuture<Void> INDEX_PREPARATION = CompletableFuture
            .runAsync(ShardFusionCatalog.instance()::prepareIndex)
            .whenComplete((ignored, error) -> {
                if (error != null) {
                    QCloudyAdditionClient.LOGGER.warn("Could not prebuild the Shard Fusion recipe index", error);
                }
            });

    private final @Nullable Screen parent;
    private final String initialQuery;
    private final ShardFusionCatalog catalog;
    private final ShardItemResolver itemResolver;
    private final List<Hit> hits = new ArrayList<>();
    private final List<ShardFusionCatalog.Shard> history = new ArrayList<>();
    private EditBox searchBox;
    private List<ShardFusionCatalog.Shard> searchResults = List.of();
    private List<ShardFusionCatalog.Recipe> visibleRecipes = List.of();
    private ShardFusionCatalog.Shard selected;
    private Mode mode = Mode.INFO;
    private PairPreview pairPreview;
    private int historyIndex = -1;
    private int resultScroll;
    private int recipePage;
    private int infoScroll;
    private int maximumInfoScroll;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int searchX;
    private int searchY;
    private int searchWidth;
    private int contentY;
    private int contentHeight;
    private int listX;
    private int listWidth;
    private int detailX;
    private int detailWidth;
    private int recipeCardsY;
    private int recipeCardsHeight;
    private int recipesPerPage;
    private int recipeCardHeight;
    private boolean compactDetails;
    private boolean singleColumn;
    private boolean singleColumnListVisible;
    private boolean stackedOutputs;
    private boolean searchLabelVisible;
    private boolean searchVisible;
    private boolean twoRowControls;

    public ShardFusionScreen(@Nullable Screen parent) {
        this(parent, "");
    }

    public ShardFusionScreen(@Nullable Screen parent, String initialQuery) {
        super(ModText.component("shard.title"));
        this.parent = parent;
        this.initialQuery = initialQuery == null ? "" : initialQuery.trim();
        catalog = ShardFusionCatalog.instance();
        itemResolver = new ShardItemResolver(catalog);
    }

    @Override
    protected void init() {
        boolean firstInitialization = searchBox == null;
        boolean previouslyFocused = !firstInitialization && searchBox.isFocused();
        String query = firstInitialization ? initialQuery : searchBox.getValue();
        layout();
        int searchInset = Math.min(5, Math.max(0, (searchWidth - 1) / 2));
        searchBox = new EditBox(font, searchX + searchInset,
                searchY + Math.max(0, (CONTROL_HEIGHT - font.lineHeight) / 2),
                Math.max(1, searchWidth - searchInset * 2), font.lineHeight, ModText.component("shard.search"));
        searchBox.setBordered(false);
        searchBox.setTextShadow(false);
        searchBox.setMaxLength(80);
        searchBox.setHint(ModText.component("shard.search.hint"));
        searchBox.setTextColor(AcaUiTheme.TEXT);
        searchBox.visible = searchVisible;
        addRenderableWidget(searchBox);
        searchBox.setValue(query);
        searchBox.setResponder(this::updateSearch);
        searchResults = catalog.search(query);
        if (firstInitialization) {
            ShardFusionCatalog.Shard first = initialSelection(query);
            if (first != null) select(first, true);
        }
        if (shouldFocusSearch(firstInitialization, previouslyFocused, searchBox.visible)) {
            setInitialFocus(searchBox);
        } else {
            setFocused(null);
        }
    }

    static boolean shouldFocusSearch(boolean firstInitialization, boolean previouslyFocused,
                                     boolean searchVisible) {
        return searchVisible && (firstInitialization || previouslyFocused);
    }

    private ShardFusionCatalog.Shard initialSelection(String query) {
        if (!query.isBlank()) {
            var exact = catalog.byId(query).or(() -> catalog.byName(query));
            if (exact.isPresent()) return exact.get();
            return searchResults.isEmpty() ? null : searchResults.getFirst();
        }
        // An empty query is a browser state, not an implicit request for every C1 recipe/use.
        return null;
    }

    private void layout() {
        int horizontalMargin = width >= 20 ? 9 : 0;
        int verticalMargin = height >= 20 ? 9 : 0;
        windowWidth = Math.max(0, Math.min(820, width - horizontalMargin * 2));
        windowHeight = Math.max(0, Math.min(460, height - verticalMargin * 2));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int innerX = windowX + Math.min(10, Math.max(0, windowWidth / 2));
        int innerWidth = Math.max(1, windowWidth - (innerX - windowX) * 2);
        int searchLabelWidth = font.width(ModText.get("shard.search.label")) + 9;
        searchLabelVisible = innerWidth >= searchLabelWidth + 92;
        searchX = searchLabelVisible ? innerX + searchLabelWidth : innerX;
        searchWidth = Math.max(1, innerWidth - (searchX - innerX));
        searchVisible = windowWidth >= 40 && windowHeight >= 76;
        searchY = Math.max(windowY, Math.min(windowY + 38,
                windowY + Math.max(0, windowHeight - CONTROL_HEIGHT)));
        contentY = searchVisible
                ? Math.min(windowY + windowHeight, searchY + CONTROL_HEIGHT + 7)
                : Math.min(windowY + windowHeight, windowY + Math.min(36, windowHeight));
        int contentBottom = Math.max(contentY, windowY + windowHeight - Math.min(23, windowHeight));
        contentHeight = Math.max(0, contentBottom - contentY);
        listX = windowX + 8;
        singleColumn = windowWidth < 520;
        if (singleColumn) {
            listWidth = Math.max(0, windowWidth - 16);
            detailX = listX;
            detailWidth = listWidth;
        } else {
            listWidth = Math.clamp(windowWidth / 3, 142, 218);
            detailX = listX + listWidth + 8;
            detailWidth = Math.max(0, windowX + windowWidth - 8 - detailX);
        }
        twoRowControls = singleColumn && detailWidth - 16 < 176;
        // A second control row needs 20 extra pixels. Keep the compact header
        // until a full normal header and one card can fit so increasing the
        // GUI height never makes a previously visible recipe disappear.
        compactDetails = contentHeight < (twoRowControls ? 210 : 190);
        recipeCardsY = contentY + (compactDetails ? 47 : 72) + (twoRowControls ? 20 : 0);
        // The card viewport may shrink, but never extends outside the content panel.
        recipeCardsHeight = Math.max(0, contentY + contentHeight - recipeCardsY - 23);
        stackedOutputs = detailWidth < 310 && recipeCardsHeight >= STACKED_RECIPE_CARD_HEIGHT;
        recipeCardHeight = stackedOutputs ? STACKED_RECIPE_CARD_HEIGHT : RECIPE_CARD_HEIGHT;
        recipesPerPage = Math.max(1, recipeCardsHeight / recipeCardHeight);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layout();
        itemResolver.refresh(minecraft);
        hits.clear();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        graphics.fill(Math.min(width, windowX + 4), Math.min(height, windowY + 5),
                Math.min(width, windowX + windowWidth + 5), Math.min(height, windowY + windowHeight + 6),
                0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        if (windowWidth > 2 && windowHeight > 1) {
            graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1,
                    Math.min(windowY + windowHeight, windowY + HEADER_HEIGHT), AcaUiTheme.HEADER);
        }
        drawHeader(graphics, mouseX, mouseY);
        if (searchBox != null && searchBox.visible) drawSearchFrame(graphics);
        boolean drawList = !singleColumn || singleColumnListVisible || selected == null;
        boolean drawDetail = !singleColumn || !drawList;
        if (drawList && listWidth > 0 && contentHeight > 0) {
            graphics.fill(listX, contentY, listX + listWidth, contentY + contentHeight, AcaUiTheme.SIDEBAR);
            graphics.outline(listX, contentY, listWidth, contentHeight, AcaUiTheme.BORDER_SOFT);
        }
        if (drawDetail && detailWidth > 0 && contentHeight > 0) {
            graphics.fill(detailX, contentY, detailX + detailWidth, contentY + contentHeight, AcaUiTheme.CONTENT);
            graphics.outline(detailX, contentY, detailWidth, contentHeight, AcaUiTheme.BORDER_SOFT);
        }
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        if (drawList) drawShardList(graphics, mouseX, mouseY);
        if (drawDetail) {
            graphics.enableScissor(detailX, contentY, detailX + detailWidth, contentY + contentHeight);
            drawDetails(graphics, mouseX, mouseY);
            graphics.disableScissor();
        }
        drawFooter(graphics);
    }

    private void drawHeader(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (windowWidth <= 0 || windowHeight <= 0) return;
        String title = ModText.get("shard.title");
        int closeSize = Math.min(15, Math.max(1, Math.min(windowWidth, windowHeight)));
        int closeX = Math.max(windowX, windowX + windowWidth - closeSize - 8);
        int closeY = Math.min(windowY + 7, windowY + windowHeight - closeSize);
        boolean showHistory = windowWidth >= 190 && windowHeight >= 25;
        int historyX = closeX - 45;
        boolean showPlanner = windowWidth >= 330 && windowHeight >= 25;
        int plannerWidth = Math.min(94, Math.max(58, windowWidth / 6));
        int plannerX = historyX - plannerWidth - 7;
        int titleX = windowX + Math.min(12, Math.max(0, windowWidth - 1));
        int titleMaximum = Math.max(1, (showPlanner ? plannerX : showHistory ? historyX : closeX) - titleX - 5);
        int titleY = Math.max(windowY, Math.min(windowY + 11,
                windowY + Math.max(0, windowHeight - font.lineHeight)));
        drawFitted(graphics, title, titleX, titleY, titleMaximum, AcaUiTheme.TEXT);
        if (showHistory) {
            drawSmallButton(graphics, ModText.get("shard.history.back"), historyX, closeY,
                    18, 18, mouseX, mouseY, historyIndex > 0, Action.HISTORY_BACK, null, HitRegion.WINDOW);
            drawSmallButton(graphics, ModText.get("shard.history.forward"), historyX + 21, closeY,
                    18, 18, mouseX, mouseY, historyIndex >= 0 && historyIndex + 1 < history.size(),
                    Action.HISTORY_FORWARD, null, HitRegion.WINDOW);
        }
        if (showPlanner) {
            drawSmallButton(graphics, ModText.get("shard.planner.open"), plannerX, closeY,
                    plannerWidth, 18, mouseX, mouseY, true, Action.PLANNER, null, HitRegion.WINDOW);
        }
        boolean hovered = isHovered(mouseX, mouseY, closeX, closeY, closeSize, closeSize, HitRegion.WINDOW);
        graphics.fill(closeX, closeY, closeX + closeSize, closeY + closeSize,
                hovered ? AcaUiTheme.DANGER : AcaUiTheme.CONTROL);
        graphics.outline(closeX, closeY, closeSize, closeSize, AcaUiTheme.BORDER);
        drawCenteredFitted(graphics, "×", closeX, closeY, closeSize, closeSize, AcaUiTheme.TEXT);
        addHit(new Hit(Action.CLOSE, null, null, closeX, closeY, closeSize, closeSize), HitRegion.WINDOW);
    }

    private void drawSearchFrame(GuiGraphicsExtractor graphics) {
        if (searchLabelVisible) {
            graphics.text(font, ModText.get("shard.search.label"), windowX + 10, searchY + 5,
                    AcaUiTheme.TEXT_MUTED, false);
        }
        graphics.fill(searchX, searchY, searchX + searchWidth, searchY + CONTROL_HEIGHT, AcaUiTheme.CONTROL);
        graphics.outline(searchX, searchY, searchWidth, CONTROL_HEIGHT,
                searchBox != null && searchBox.isFocused() ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
    }

    private void drawShardList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (listWidth <= 0 || contentHeight <= 0) return;
        int headerY = contentY + 6;
        String count = ModText.get("shard.results", searchResults.size());
        drawFitted(graphics, count, listX + 7, headerY, Math.max(1, listWidth - 14), AcaUiTheme.TEXT_MUTED);
        int rowsY = contentY + 20;
        int rowsHeight = Math.max(0, contentHeight - 24);
        if (rowsHeight <= 0 || listWidth <= 2) return;
        if (searchResults.isEmpty()) {
            int returnHeight = selected != null && singleColumn ? Math.min(18, rowsHeight) : 0;
            int messageHeight = Math.max(1, rowsHeight - returnHeight - (returnHeight > 0 ? 4 : 0));
            drawCenteredFitted(graphics, ModText.get("shard.results", 0), listX + 7, rowsY,
                    Math.max(1, listWidth - 14), messageHeight, AcaUiTheme.TEXT_MUTED);
            if (returnHeight > 0) {
                int buttonY = rowsY + rowsHeight - returnHeight;
                drawSmallButton(graphics, selected.displayName(), listX + 7, buttonY,
                        Math.max(1, listWidth - 14), returnHeight, mouseX, mouseY, true,
                        Action.SHOW_DETAIL, null, HitRegion.LIST);
            }
            return;
        }
        int visibleRows = Math.max(1, rowsHeight / RESULT_ROW_HEIGHT);
        int maximum = Math.max(0, searchResults.size() - visibleRows);
        resultScroll = Math.clamp(resultScroll, 0, maximum);
        graphics.enableScissor(listX + 1, rowsY, listX + listWidth - 1, contentY + contentHeight - 2);
        int end = Math.min(searchResults.size(), resultScroll + visibleRows + 1);
        for (int index = resultScroll; index < end; index++) {
            ShardFusionCatalog.Shard shard = searchResults.get(index);
            int y = rowsY + (index - resultScroll) * RESULT_ROW_HEIGHT;
            boolean active = selected == shard;
            boolean hovered = isHovered(mouseX, mouseY, listX + 3, y, listWidth - 6,
                    RESULT_ROW_HEIGHT - 2, HitRegion.LIST);
            if (active) graphics.fill(listX + 3, y, listX + 6, y + RESULT_ROW_HEIGHT - 2, AcaUiTheme.ACCENT);
            graphics.fill(listX + 6, y, listX + listWidth - 3, y + RESULT_ROW_HEIGHT - 2,
                    active ? 0xFF303A3F : hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.SIDEBAR);
            graphics.item(itemResolver.item(shard), listX + 9, y + 2);
            drawShardLink(graphics, shard.name(), listX + 29, y + 3, listWidth - 67,
                    shard.rarity().color(), mouseX, mouseY);
            drawFitted(graphics, shard.id(), listX + listWidth - 34, y + 3, 28, AcaUiTheme.TEXT_DIM);
            addHit(new Hit(Action.SELECT_SHARD, shard, null, listX + 3, y, listWidth - 6,
                    RESULT_ROW_HEIGHT - 2), HitRegion.LIST);
        }
        graphics.disableScissor();
        if (maximum > 0) {
            int trackY = rowsY;
            int trackHeight = Math.max(12, rowsHeight - 2);
            int thumbHeight = Math.max(12, trackHeight * visibleRows / searchResults.size());
            int thumbY = trackY + (trackHeight - thumbHeight) * resultScroll / maximum;
            graphics.fill(listX + listWidth - 3, trackY, listX + listWidth - 1, trackY + trackHeight,
                    AcaUiTheme.CONTROL);
            graphics.fill(listX + listWidth - 3, thumbY, listX + listWidth - 1, thumbY + thumbHeight,
                    AcaUiTheme.ACCENT);
        }
    }

    private void drawDetails(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (detailWidth <= 0 || contentHeight <= 0) return;
        if (selected == null) {
            drawCenteredFitted(graphics, ModText.get("shard.no_selection"), detailX + 8, contentY,
                    Math.max(1, detailWidth - 16), contentHeight, AcaUiTheme.TEXT_MUTED);
            return;
        }
        int titleX = detailX + 10;
        int titleY = contentY + (compactDetails ? 4 : 8);
        graphics.item(itemResolver.item(selected), titleX, titleY);
        drawFitted(graphics, selected.displayName(), titleX + 21, titleY - 1, detailWidth - 42,
                selected.rarity().color());
        MutableComponent attributeLine = Component.literal(selected.id() + " · ")
                .withStyle(style -> style.withColor(AcaUiTheme.TEXT_MUTED))
                .append(Component.literal(selected.attributeName())
                        .withStyle(style -> style.withColor(effectPrimaryColor(selected))));
        drawFittedComponent(graphics, attributeLine, titleX + 21, titleY + 11,
                detailWidth - 42, AcaUiTheme.TEXT_MUTED);
        String metadata = selected.rarity().displayName() + " · " + selected.category().displayName()
                + (selected.skill().isBlank() ? "" : " · " + selected.skill());
        if (!compactDetails) {
            drawFitted(graphics, metadata, titleX, contentY + 37, detailWidth - 20,
                    selected.category().color());
        }

        int tabY = contentY + (compactDetails ? 27 : 51);
        int innerTabsWidth = Math.max(1, detailWidth - 16);
        int tabStartX = detailX + 8;
        if (singleColumn) {
            int browseWidth = twoRowControls ? innerTabsWidth
                    : Math.min(82, Math.max(48, innerTabsWidth / 4));
            drawSmallButton(graphics, ModText.get("shard.results", searchResults.size()), tabStartX, tabY,
                    browseWidth, 18, mouseX, mouseY, true, Action.SHOW_LIST, null, HitRegion.DETAIL);
            if (twoRowControls) {
                tabY += 20;
            } else {
                tabStartX += browseWidth + 4;
            }
        }
        int tabsRight = detailX + detailWidth - 8;
        int tabGap = tabsRight - tabStartX >= 5 ? 3 : 0;
        int tabWidth = Math.min(92, Math.max(1, (tabsRight - tabStartX - tabGap * 2) / 3));
        if (pairPreview == null) {
            drawTab(graphics, ModText.get("shard.tab.info"), tabStartX, tabY, tabWidth,
                    mode == Mode.INFO, mouseX, mouseY, Action.INFO);
            drawTab(graphics, ModText.get("shard.tab.recipes"), tabStartX + tabWidth + tabGap, tabY, tabWidth,
                    mode == Mode.RECIPES, mouseX, mouseY, Action.RECIPES);
            drawTab(graphics, ModText.get("shard.tab.uses"), tabStartX + (tabWidth + tabGap) * 2, tabY, tabWidth,
                    mode == Mode.USES, mouseX, mouseY, Action.USES);
        } else {
            drawSmallButton(graphics, ModText.get("shard.back_to_list"), tabStartX, tabY,
                    Math.max(1, detailX + detailWidth - 8 - tabStartX), 18,
                    mouseX, mouseY, true, Action.BACK_TO_LIST, null, HitRegion.DETAIL);
        }

        drawRecipeArea(graphics, mouseX, mouseY);
    }

    private void drawRecipeArea(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (pairPreview == null && mode == Mode.INFO) {
            drawInfoArea(graphics);
            return;
        }
        List<ShardFusionCatalog.Recipe> rows = pairPreview == null
                ? visibleRecipes
                : pairPreview.recipe() == null ? List.of() : List.of(pairPreview.recipe());
        if (pairPreview != null && pairPreview.recipe() == null) {
            if (recipeCardsHeight < 84) {
                drawInsufficientSpace(graphics);
                return;
            }
            drawEmptyPair(graphics, pairPreview, mouseX, mouseY);
            return;
        }
        if (rows.isEmpty()) {
            drawCenteredFitted(graphics,
                    ModText.get(mode == Mode.RECIPES ? "shard.no_recipes" : "shard.no_uses"),
                    detailX + 8, recipeCardsY, Math.max(1, detailWidth - 16),
                    Math.max(1, recipeCardsHeight), AcaUiTheme.TEXT_MUTED);
            return;
        }
        if (recipeCardsHeight < recipeCardHeight) {
            drawInsufficientSpace(graphics);
            return;
        }
        int pages = pairPreview == null ? Math.max(1, (rows.size() + recipesPerPage - 1) / recipesPerPage) : 1;
        recipePage = Math.clamp(recipePage, 0, pages - 1);
        int start = pairPreview == null ? recipePage * recipesPerPage : 0;
        int end = Math.min(rows.size(), start + recipesPerPage);
        for (int index = start; index < end; index++) {
            int y = recipeCardsY + (index - start) * recipeCardHeight;
            drawRecipeCard(graphics, rows.get(index), detailX + 6, y, detailWidth - 12, mouseX, mouseY);
        }
        if (pairPreview == null) drawPager(graphics, rows.size(), pages, mouseX, mouseY);
    }

    private void drawInsufficientSpace(GuiGraphicsExtractor graphics) {
        int availableHeight = Math.max(1, contentY + contentHeight - recipeCardsY);
        drawCenteredFitted(graphics, ModText.get("shard.insufficient_space"), detailX + 8, recipeCardsY,
                Math.max(1, detailWidth - 16), availableHeight, AcaUiTheme.TEXT_MUTED);
    }

    private void drawInfoArea(GuiGraphicsExtractor graphics) {
        int viewportX = detailX + 9;
        int viewportY = recipeCardsY;
        int viewportWidth = Math.max(1, detailWidth - 18);
        int viewportHeight = Math.max(1, contentY + contentHeight - viewportY - 4);
        List<InfoLine> lines = infoLines(viewportWidth);
        int totalHeight = 0;
        for (InfoLine line : lines) totalHeight += line.height();
        maximumInfoScroll = Math.max(0, totalHeight - viewportHeight);
        infoScroll = Math.clamp(infoScroll, 0, maximumInfoScroll);

        graphics.enableScissor(viewportX, viewportY, viewportX + viewportWidth, viewportY + viewportHeight);
        int y = viewportY - infoScroll;
        for (InfoLine line : lines) {
            if (y + line.height() >= viewportY && y < viewportY + viewportHeight) {
                graphics.text(font, line.text(), viewportX + line.indent(), y + line.topPadding(),
                        0xFFFFFFFF, false);
            }
            y += line.height();
        }
        graphics.disableScissor();

        if (maximumInfoScroll > 0) {
            int trackX = detailX + detailWidth - 4;
            int thumbHeight = Math.max(12, viewportHeight * viewportHeight / (viewportHeight + maximumInfoScroll));
            int travel = Math.max(0, viewportHeight - thumbHeight);
            int thumbY = viewportY + (maximumInfoScroll == 0 ? 0 : travel * infoScroll / maximumInfoScroll);
            graphics.fill(trackX, viewportY, trackX + 2, viewportY + viewportHeight, AcaUiTheme.CONTROL);
            graphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, AcaUiTheme.ACCENT);
        }
    }

    private List<InfoLine> infoLines(int width) {
        List<InfoLine> result = new ArrayList<>();
        addInfoHeading(result, ModText.get("shard.info.effect"), ShardFusionCatalog.TextTone.YELLOW.color(), 0);
        addWrappedInfo(result, effectComponent(selected), width, 0, 1);

        addInfoHeading(result, ModText.get("shard.info.classification"),
                selected.category().color(), 5);
        addWrappedInfo(result, labeledComponent(ModText.get("shard.info.rarity"),
                selected.rarity().displayName(), selected.rarity().color()), width, 0, 0);
        addWrappedInfo(result, labeledComponent(ModText.get("shard.info.category"),
                selected.category().displayName(), selected.category().color()), width, 0, 0);
        if (!selected.skill().isBlank()) {
            addWrappedInfo(result, labeledComponent(ModText.get("shard.info.skill"),
                    selected.skill(), skillColor(selected.skill())), width, 0, 0);
        }
        if (!selected.families().isEmpty()) {
            addWrappedInfo(result, labeledComponent(ModText.get("shard.info.family"),
                    String.join(", ", selected.families()), selected.category().color()), width, 0, 0);
        }
        for (String mobType : selected.mobTypes()) {
            int color = mobTypeColor(mobType);
            addWrappedInfo(result, labeledComponent(ModText.get("shard.info.mob_type"), mobType, color),
                    width, 0, 0);
        }

        addInfoHeading(result, ModText.get("shard.info.obtain"),
                ShardFusionCatalog.TextTone.GOLD.color(), 5);
        int fusionRecipeCount = catalog.recipesForOutput(selected.id()).size();
        if (selected.fusionOnly()) {
            addWrappedInfo(result, Component.literal(ModText.get("shard.info.fusion_only"))
                    .withStyle(style -> style.withColor(ShardFusionCatalog.TextTone.DARK_PURPLE.color())),
                    width, 0, 1);
        }
        for (ShardFusionCatalog.Acquisition method : selected.acquisition()) {
            Component line = Component.literal("• " + method.text())
                    .withStyle(style -> style.withColor(method.kind().color()));
            addWrappedInfo(result, line, width, 4, 1);
        }
        if (fusionRecipeCount > 0) {
            Component line = Component.literal("• " + ModText.get("shard.info.fusion_available", fusionRecipeCount))
                    .withStyle(style -> style.withColor(ShardFusionCatalog.TextTone.DARK_PURPLE.color()));
            addWrappedInfo(result, line, width, 4, 1);
        }
        return List.copyOf(result);
    }

    private void addInfoHeading(List<InfoLine> lines, String text, int color, int topPadding) {
        lines.add(new InfoLine(Component.literal(text)
                .withStyle(style -> style.withColor(color).withBold(true)).getVisualOrderText(),
                0, topPadding, font.lineHeight + topPadding + 1));
    }

    private void addWrappedInfo(List<InfoLine> lines, Component component, int width,
                                int indent, int topPadding) {
        int available = Math.max(1, width - indent);
        List<FormattedCharSequence> wrapped = font.split(component, available);
        if (wrapped.isEmpty()) return;
        for (int index = 0; index < wrapped.size(); index++) {
            int padding = index == 0 ? topPadding : 0;
            lines.add(new InfoLine(wrapped.get(index), indent, padding,
                    font.lineHeight + padding + 1));
        }
    }

    private Component effectComponent(ShardFusionCatalog.Shard shard) {
        MutableComponent result = Component.empty();
        for (ShardFusionCatalog.TextSpan span : shard.effect()) {
            result.append(Component.literal(span.text())
                    .withStyle(style -> style.withColor(span.tone().color())));
        }
        return result;
    }

    private static int effectPrimaryColor(ShardFusionCatalog.Shard shard) {
        return shard.effect().stream()
                .filter(span -> span.tone() != ShardFusionCatalog.TextTone.TEXT
                        && span.tone() != ShardFusionCatalog.TextTone.GRAY)
                .map(span -> span.tone().color())
                .findFirst()
                .orElse(AcaUiTheme.TEXT_MUTED);
    }

    private static Component labeledComponent(String label, String value, int valueColor) {
        return Component.literal(label + ": ")
                .withStyle(style -> style.withColor(ShardFusionCatalog.TextTone.GRAY.color()))
                .append(Component.literal(value).withStyle(style -> style.withColor(valueColor)));
    }

    private static int skillColor(String skill) {
        return switch (skill.toLowerCase(java.util.Locale.ROOT)) {
            case "combat" -> ShardFusionCatalog.TextTone.RED.color();
            case "mining" -> ShardFusionCatalog.TextTone.AQUA.color();
            case "foraging" -> ShardFusionCatalog.TextTone.DARK_GREEN.color();
            case "fishing" -> ShardFusionCatalog.TextTone.BLUE.color();
            case "farming" -> ShardFusionCatalog.TextTone.GREEN.color();
            case "enchanting" -> ShardFusionCatalog.TextTone.DARK_PURPLE.color();
            case "alchemy", "taming", "hunting" -> ShardFusionCatalog.TextTone.LIGHT_PURPLE.color();
            default -> ShardFusionCatalog.TextTone.YELLOW.color();
        };
    }

    private static int mobTypeColor(String mobType) {
        return switch (mobType.toLowerCase(java.util.Locale.ROOT)) {
            case "animal", "critter", "cubic" -> ShardFusionCatalog.TextTone.GREEN.color();
            case "aquatic" -> ShardFusionCatalog.TextTone.BLUE.color();
            case "arcane", "ender" -> ShardFusionCatalog.TextTone.DARK_PURPLE.color();
            case "arthropod", "infernal" -> ShardFusionCatalog.TextTone.DARK_RED.color();
            case "elusive" -> ShardFusionCatalog.TextTone.LIGHT_PURPLE.color();
            case "glacial" -> ShardFusionCatalog.TextTone.AQUA.color();
            case "humanoid", "shielded" -> ShardFusionCatalog.TextTone.YELLOW.color();
            case "magmatic" -> ShardFusionCatalog.TextTone.RED.color();
            case "mythological", "pest", "undead", "woodland" ->
                    ShardFusionCatalog.TextTone.DARK_GREEN.color();
            case "spooky", "subterranean" -> ShardFusionCatalog.TextTone.GOLD.color();
            case "wither" -> ShardFusionCatalog.TextTone.DARK_GRAY.color();
            case "frozen" -> ShardFusionCatalog.TextTone.WHITE.color();
            default -> ShardFusionCatalog.TextTone.GRAY.color();
        };
    }

    private void drawEmptyPair(GuiGraphicsExtractor graphics, PairPreview preview, int mouseX, int mouseY) {
        int x = detailX + 8;
        int y = recipeCardsY + 4;
        int cardWidth = Math.max(0, detailWidth - 16);
        int cardHeight = 84;
        graphics.fill(x, y, x + cardWidth, y + cardHeight, AcaUiTheme.CARD);
        graphics.outline(x, y, cardWidth, cardHeight, AcaUiTheme.BORDER);
        int swapWidth = Math.min(56, Math.max(1, cardWidth - 16));
        int swapX = x + Math.max(8, cardWidth - swapWidth - 8);
        drawFitted(graphics, ModText.get("shard.input_count", preview.left().inputCount()), x + 8, y + 7,
                Math.max(1, swapX - x - 12), AcaUiTheme.TEXT_DIM);
        drawSmallButton(graphics, ModText.get("shard.swap"), swapX,
                y + 5, swapWidth, 18,
                mouseX, mouseY, true, Action.SWAP, null, HitRegion.RECIPE);
        drawInputPair(graphics, preview.left(), preview.right(), x + 8, y + 27,
                Math.max(1, cardWidth - 16), mouseX, mouseY);
        drawFitted(graphics, ModText.get("shard.no_output"), x + 8, y + 49,
                Math.max(1, cardWidth - 16), AcaUiTheme.TEXT_MUTED);
        drawFitted(graphics, ModText.get("shard.order_note"), x + 8, y + 62,
                Math.max(1, cardWidth - 16), AcaUiTheme.TEXT_MUTED);
    }

    private void drawRecipeCard(GuiGraphicsExtractor graphics, ShardFusionCatalog.Recipe recipe,
                                int x, int y, int cardWidth, int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, cardWidth, recipeCardHeight - 4,
                HitRegion.RECIPE);
        graphics.fill(x, y, x + cardWidth, y + recipeCardHeight - 4,
                hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CARD);
        graphics.outline(x, y, cardWidth, recipeCardHeight - 4, AcaUiTheme.BORDER);
        int swapWidth = Math.min(52, Math.max(1, cardWidth - 16));
        int swapX = x + Math.max(8, cardWidth - swapWidth - 8);
        String countLabel = ModText.get("shard.input_count", recipe.inputCount());
        drawFitted(graphics, countLabel, x + 8, y + 6, Math.max(1, swapX - x - 12), AcaUiTheme.TEXT_DIM);
        drawSmallButton(graphics, ModText.get("shard.swap"), swapX, y + 4, swapWidth, 16,
                mouseX, mouseY, true, Action.SWAP, recipe, HitRegion.RECIPE);
        int inputWidth = Math.max(1, cardWidth - 16);
        InputPairLayout inputLayout = drawInputPair(
                graphics, recipe.left(), recipe.right(), x + 8, y + 20, inputWidth, mouseX, mouseY);
        addHit(new Hit(Action.SELECT_SHARD, recipe.left(), null,
                x + 8 + inputLayout.leftX(), y + 20,
                inputLayout.leftWidth(), 17), HitRegion.RECIPE);
        addHit(new Hit(Action.SELECT_SHARD, recipe.right(), null,
                x + 8 + inputLayout.rightX(), y + 20,
                inputLayout.rightWidth(), 17), HitRegion.RECIPE);

        int outputY = y + 40;
        int outputWidth = Math.max(1, cardWidth - 16);
        if (stackedOutputs) {
            for (int index = 0; index < recipe.outputs().size(); index++) {
                ShardFusionCatalog.Output output = recipe.outputs().get(index);
                int rowY = outputY + index * 18;
                String label = output.shard().name() + " ×" + output.count();
                String kind = kindLabel(output.kind());
                int naturalWidth = 19 + font.width(label) + 6 + font.width(kind);
                int rowWidth = Math.min(outputWidth, naturalWidth);
                int outputX = x + 8 + (outputWidth - rowWidth) / 2;
                graphics.item(itemResolver.item(output.shard()), outputX, rowY);
                CompactRowLayout textLayout = compactRowLayout(
                        Math.max(1, rowWidth - 19), 6,
                        List.of(font.width(label), font.width(kind)));
                drawShardLink(graphics, label,
                        outputX + 19 + textLayout.starts().get(0), rowY + 3,
                        textLayout.widths().get(0), output.shard().rarity().color(),
                        mouseX, mouseY);
                drawFitted(graphics, kind,
                        outputX + 19 + textLayout.starts().get(1), rowY + 3,
                        textLayout.widths().get(1), AcaUiTheme.TEXT_DIM);
                addHit(new Hit(Action.SELECT_SHARD, output.shard(), null, outputX, rowY,
                        rowWidth, 17), HitRegion.RECIPE);
            }
        } else {
            List<Integer> naturalWidths = recipe.outputs().stream()
                    .map(output -> 19 + Math.max(
                            font.width(output.shard().name() + " ×" + output.count()),
                            font.width(kindLabel(output.kind()))))
                    .toList();
            CompactRowLayout outputLayout = compactRowLayout(outputWidth, 9, naturalWidths);
            for (int index = 0; index < recipe.outputs().size(); index++) {
                ShardFusionCatalog.Output output = recipe.outputs().get(index);
                int outputX = x + 8 + outputLayout.starts().get(index);
                int available = outputLayout.widths().get(index);
                graphics.item(itemResolver.item(output.shard()), outputX, outputY);
                String label = output.shard().name() + " ×" + output.count();
                drawShardLink(graphics, label, outputX + 19, outputY + 1,
                        Math.max(1, available - 19), output.shard().rarity().color(),
                        mouseX, mouseY);
                drawFitted(graphics, kindLabel(output.kind()), outputX + 19, outputY + 11,
                        Math.max(1, available - 19), AcaUiTheme.TEXT_DIM);
                addHit(new Hit(Action.SELECT_SHARD, output.shard(), null, outputX, outputY,
                        Math.max(1, available), 25), HitRegion.RECIPE);
            }
        }
        int noteY = stackedOutputs ? y + 99 : y + 69;
        drawFitted(graphics, ModText.get("shard.order_note"), x + 8, noteY,
                Math.max(1, cardWidth - 16), AcaUiTheme.TEXT_MUTED);
        if (recipe.pureReptilePossible()) {
            drawFitted(graphics, ModText.get("shard.pure_reptile"), x + 8, noteY + 11,
                    cardWidth - 16, 0xFF55FF55);
        }
    }

    private InputPairLayout drawInputPair(GuiGraphicsExtractor graphics, ShardFusionCatalog.Shard left,
                                          ShardFusionCatalog.Shard right, int x, int y,
                                          int availableWidth, int mouseX, int mouseY) {
        InputPairLayout layout = inputPairLayout(availableWidth,
                font.width(left.name()), font.width(right.name()), font.width("+"));
        int textOffset = layout.showItems() ? 19 : 0;
        if (layout.showItems()) graphics.item(itemResolver.item(left), x + layout.leftX(), y);
        drawShardLink(graphics, left.name(), x + layout.leftX() + textOffset, y + 3,
                Math.max(1, layout.leftWidth() - textOffset), left.rarity().color(),
                mouseX, mouseY);
        graphics.text(font, "+", x + layout.plusX(), y + 4, AcaUiTheme.TEXT_MUTED, false);
        if (layout.showItems()) graphics.item(itemResolver.item(right), x + layout.rightX(), y);
        drawShardLink(graphics, right.name(), x + layout.rightX() + textOffset, y + 3,
                Math.max(1, layout.rightWidth() - textOffset), right.rarity().color(),
                mouseX, mouseY);
        return layout;
    }

    static InputPairLayout inputPairLayout(int availableWidth, int leftTextWidth,
                                           int rightTextWidth, int plusWidth) {
        int safeWidth = Math.max(1, availableWidth);
        int safePlusWidth = Math.max(1, plusWidth);
        boolean showItems = safeWidth >= 2 * 20 + safePlusWidth + 8;
        int textOffset = showItems ? 19 : 0;
        int leftNatural = Math.max(1, textOffset + Math.max(1, leftTextWidth));
        int rightNatural = Math.max(1, textOffset + Math.max(1, rightTextWidth));
        int gap = safeWidth >= safePlusWidth + 10 ? 5 : 1;
        int separatorWidth = Math.min(safeWidth - 2,
                Math.max(1, safePlusWidth + gap * 2));
        int naturalWidth = leftNatural + separatorWidth + rightNatural;
        int groupWidth = Math.min(safeWidth, naturalWidth);
        int segmentBudget = Math.max(2, groupWidth - separatorWidth);
        List<Integer> segmentWidths = distributeWidths(segmentBudget,
                List.of(leftNatural, rightNatural));
        int leftWidth = segmentWidths.get(0);
        int rightWidth = segmentWidths.get(1);
        int groupX = Math.max(0, (safeWidth - groupWidth) / 2);
        int plusX = groupX + leftWidth + gap;
        int rightX = groupX + leftWidth + separatorWidth;
        return new InputPairLayout(groupX, leftWidth, plusX, safePlusWidth,
                rightX, rightWidth, groupWidth, showItems);
    }

    static CompactRowLayout compactRowLayout(int availableWidth, int preferredGap,
                                              List<Integer> naturalWidths) {
        int safeWidth = Math.max(1, availableWidth);
        if (naturalWidths == null || naturalWidths.isEmpty()) {
            return new CompactRowLayout(0, List.of(), List.of(), 0);
        }
        int count = naturalWidths.size();
        int gap = count == 1 ? 0 : Math.min(Math.max(0, preferredGap),
                Math.max(0, (safeWidth - count) / (count - 1)));
        int totalGap = gap * (count - 1);
        int naturalTotal = naturalWidths.stream().mapToInt(width -> Math.max(1, width)).sum();
        int groupWidth = Math.min(safeWidth, naturalTotal + totalGap);
        int usedCellWidth = groupWidth - totalGap;
        List<Integer> widths = distributeWidths(usedCellWidth, naturalWidths);
        int groupX = Math.max(0, (safeWidth - groupWidth) / 2);
        List<Integer> starts = new ArrayList<>(count);
        int cursor = groupX;
        for (int width : widths) {
            starts.add(cursor);
            cursor += width + gap;
        }
        return new CompactRowLayout(groupX, List.copyOf(starts), widths, groupWidth);
    }

    private static List<Integer> distributeWidths(int availableWidth, List<Integer> naturalWidths) {
        int count = naturalWidths.size();
        if (count == 0) return List.of();
        int safeWidth = Math.max(count, availableWidth);
        List<Integer> widths = new ArrayList<>(java.util.Collections.nCopies(count, 1));
        int remaining = safeWidth - count;
        while (remaining > 0) {
            boolean grew = false;
            for (int index = 0; index < count && remaining > 0; index++) {
                int natural = Math.max(1, naturalWidths.get(index));
                if (widths.get(index) >= natural) continue;
                widths.set(index, widths.get(index) + 1);
                remaining--;
                grew = true;
            }
            if (!grew) break;
        }
        return List.copyOf(widths);
    }

    private void drawPager(GuiGraphicsExtractor graphics, int recipeCount, int pages,
                           int mouseX, int mouseY) {
        int y = contentY + contentHeight - 19;
        String page = ModText.get("shard.page", recipePage + 1, pages);
        int innerWidth = Math.max(1, detailWidth - 16);
        if (innerWidth < 40) {
            drawFitted(graphics, page, detailX + 8, y + 4, innerWidth, AcaUiTheme.TEXT_MUTED);
            return;
        }
        int pagerWidth = Math.min(112, innerWidth);
        int buttonWidth = Math.min(18, Math.max(1, (pagerWidth - 1) / 2));
        int pageWidth = Math.max(1, pagerWidth - buttonWidth * 2 - 4);
        int x = detailX + detailWidth - 8 - pagerWidth;
        drawSmallButton(graphics, "‹", x, y, buttonWidth, 16, mouseX, mouseY, recipePage > 0,
                Action.PREVIOUS_PAGE, null, HitRegion.DETAIL);
        drawFitted(graphics, page, x + buttonWidth + 2, y + 4, pageWidth, AcaUiTheme.TEXT_MUTED);
        drawSmallButton(graphics, "›", x + buttonWidth + pageWidth + 4, y, buttonWidth, 16, mouseX, mouseY,
                recipePage + 1 < pages, Action.NEXT_PAGE, null, HitRegion.DETAIL);
        int countWidth = x - (detailX + 8) - 4;
        if (countWidth > 0) {
            drawFitted(graphics, ModText.get("shard.recipe_count", recipeCount), detailX + 8, y + 4,
                    countWidth, AcaUiTheme.TEXT_DIM);
        }
    }

    private void drawFooter(GuiGraphicsExtractor graphics) {
        String source = ModText.get("shard.data_footer", catalog.dataVersion(), catalog.verifiedAt());
        drawFitted(graphics, source, windowX + Math.min(9, Math.max(0, windowWidth / 2)),
                Math.max(windowY, windowY + windowHeight - 15), Math.max(1, windowWidth - 18),
                AcaUiTheme.TEXT_DIM);
    }

    private void drawTab(GuiGraphicsExtractor graphics, String label, int x, int y, int width,
                         boolean selectedTab, int mouseX, int mouseY, Action action) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, 18, HitRegion.DETAIL);
        graphics.fill(x, y, x + width, y + 18,
                selectedTab ? AcaUiTheme.ACCENT : hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CONTROL);
        graphics.outline(x, y, width, 18, selectedTab ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
        drawCenteredFitted(graphics, label, x, y, width, 18,
                selectedTab ? 0xFF071014 : AcaUiTheme.TEXT);
        addHit(new Hit(action, null, null, x, y, width, 18), HitRegion.DETAIL);
    }

    private void drawSmallButton(GuiGraphicsExtractor graphics, String label, int x, int y, int width, int height,
                                 int mouseX, int mouseY, boolean enabled, Action action,
                                 ShardFusionCatalog.@Nullable Recipe recipe, HitRegion hitRegion) {
        int fill = !enabled ? 0xFF1B2023
                : isHovered(mouseX, mouseY, x, y, width, height, hitRegion)
                ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CONTROL;
        graphics.fill(x, y, x + width, y + height, fill);
        graphics.outline(x, y, width, height, enabled ? AcaUiTheme.BORDER : AcaUiTheme.BORDER_SOFT);
        drawCenteredFitted(graphics, label, x, y, width, height, enabled ? AcaUiTheme.TEXT : AcaUiTheme.TEXT_DIM);
        if (enabled) addHit(new Hit(action, null, recipe, x, y, width, height), hitRegion);
    }

    private boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height,
                              HitRegion region) {
        Bounds bounds = clippedBounds(x, y, width, height, region);
        return bounds != null && bounds.contains(mouseX, mouseY);
    }

    private void addHit(Hit hit, HitRegion region) {
        Bounds bounds = clippedBounds(hit.x(), hit.y(), hit.width(), hit.height(), region);
        if (bounds == null) return;
        hits.add(new Hit(hit.action(), hit.shard(), hit.recipe(),
                bounds.x(), bounds.y(), bounds.width(), bounds.height()));
    }

    private @Nullable Bounds clippedBounds(int x, int y, int width, int height, HitRegion region) {
        int clipLeft;
        int clipTop;
        int clipRight;
        int clipBottom;
        switch (region) {
            case WINDOW -> {
                clipLeft = windowX;
                clipTop = windowY;
                clipRight = windowX + windowWidth;
                clipBottom = windowY + windowHeight;
            }
            case LIST -> {
                clipLeft = listX + 1;
                clipTop = contentY + 20;
                clipRight = listX + listWidth - 1;
                clipBottom = contentY + contentHeight - 2;
            }
            case DETAIL -> {
                clipLeft = detailX;
                clipTop = contentY;
                clipRight = detailX + detailWidth;
                clipBottom = contentY + contentHeight;
            }
            case RECIPE -> {
                clipLeft = detailX;
                clipTop = recipeCardsY;
                clipRight = detailX + detailWidth;
                clipBottom = Math.min(contentY + contentHeight - 23,
                        recipeCardsY + recipeCardsHeight);
            }
            default -> throw new IllegalStateException("Unhandled hit region " + region);
        }
        int left = Math.max(x, clipLeft);
        int top = Math.max(y, clipTop);
        int right = Math.min(x + width, clipRight);
        int bottom = Math.min(y + height, clipBottom);
        if (right <= left || bottom <= top) return null;
        return new Bounds(left, top, right - left, bottom - top);
    }

    private void updateSearch(String value) {
        searchResults = catalog.search(value);
        resultScroll = 0;
        if (singleColumn) singleColumnListVisible = true;
    }

    private void select(ShardFusionCatalog.Shard shard, boolean recordHistory) {
        if (shard == null) return;
        selected = shard;
        pairPreview = null;
        recipePage = 0;
        infoScroll = 0;
        if (singleColumn) singleColumnListVisible = false;
        if (recordHistory) {
            while (history.size() > historyIndex + 1) history.removeLast();
            if (history.isEmpty() || history.getLast() != shard) {
                history.add(shard);
                if (history.size() > MAX_HISTORY) history.removeFirst();
                historyIndex = history.size() - 1;
            }
        }
        refreshRecipes();
    }

    private void refreshRecipes() {
        if (selected == null) {
            visibleRecipes = List.of();
            return;
        }
        visibleRecipes = switch (mode) {
            case INFO -> List.of();
            case RECIPES -> catalog.recipesForOutput(selected.id());
            case USES -> catalog.usesForInput(selected.id());
        };
        recipePage = 0;
    }

    private void navigateHistory(int direction) {
        int next = historyIndex + direction;
        if (next < 0 || next >= history.size()) return;
        historyIndex = next;
        select(history.get(next), false);
    }

    private void swap(ShardFusionCatalog.@Nullable Recipe recipe) {
        if (recipe == null && pairPreview == null) return;
        ShardFusionCatalog.Shard left = recipe != null ? recipe.right() : pairPreview.right();
        ShardFusionCatalog.Shard right = recipe != null ? recipe.left() : pairPreview.left();
        pairPreview = new PairPreview(left, right,
                catalog.fuse(left.id(), right.id()).orElse(null));
        recipePage = 0;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        boolean insideSearch = searchBox != null && searchBox.visible
                && AcaUiTheme.contains(click.x(), click.y(), searchX, searchY,
                searchWidth, CONTROL_HEIGHT);
        if (!insideSearch && searchBox != null && searchBox.isFocused()) {
            releaseSearchFocus();
        }
        if (super.mouseClicked(click, doubled)) return true;
        if (insideSearch && click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && searchBox != null) {
            setFocused(searchBox);
            return true;
        }
        for (int index = hits.size() - 1; index >= 0; index--) {
            Hit hit = hits.get(index);
            if (!hit.contains(click.x(), click.y())) continue;
            if (hit.action == Action.SELECT_SHARD && hit.shard != null) {
                mode = click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? Mode.USES : Mode.INFO;
                select(hit.shard, true);
                return true;
            }
            if (click.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return true;
            return activate(hit);
        }
        return false;
    }

    private boolean activate(Hit hit) {
        switch (hit.action) {
            case CLOSE -> onClose();
            case HISTORY_BACK -> navigateHistory(-1);
            case HISTORY_FORWARD -> navigateHistory(1);
            case INFO -> {
                mode = Mode.INFO;
                pairPreview = null;
                refreshRecipes();
            }
            case RECIPES -> {
                mode = Mode.RECIPES;
                pairPreview = null;
                refreshRecipes();
            }
            case USES -> {
                mode = Mode.USES;
                pairPreview = null;
                refreshRecipes();
            }
            case PREVIOUS_PAGE -> recipePage = Math.max(0, recipePage - 1);
            case NEXT_PAGE -> recipePage++;
            case SWAP -> swap(hit.recipe);
            case BACK_TO_LIST -> pairPreview = null;
            case SHOW_LIST -> singleColumnListVisible = true;
            case SHOW_DETAIL -> singleColumnListVisible = false;
            case PLANNER -> minecraft.setScreen(new ShardPlanningScreen(this,
                    selected == null ? "" : selected.id()));
            case SELECT_SHARD -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        boolean listIsVisible = !singleColumn || singleColumnListVisible || selected == null;
        if (listIsVisible && AcaUiTheme.contains(mouseX, mouseY, listX, contentY, listWidth, contentHeight)) {
            int visibleRows = Math.max(1, (contentHeight - 24) / RESULT_ROW_HEIGHT);
            int maximum = Math.max(0, searchResults.size() - visibleRows);
            resultScroll = Math.clamp(resultScroll - (int) Math.round(vertical * 3), 0, maximum);
            return true;
        }
        if (recipeCardsHeight >= recipeCardHeight && !visibleRecipes.isEmpty()
                && AcaUiTheme.contains(mouseX, mouseY, detailX, recipeCardsY, detailWidth, recipeCardsHeight)
                && pairPreview == null) {
            int pages = Math.max(1, (visibleRecipes.size() + recipesPerPage - 1) / recipesPerPage);
            recipePage = Math.clamp(recipePage - (int) Math.signum(vertical), 0, pages - 1);
            return true;
        }
        if (mode == Mode.INFO && pairPreview == null && maximumInfoScroll > 0
                && AcaUiTheme.contains(mouseX, mouseY, detailX, recipeCardsY,
                detailWidth, Math.max(1, contentY + contentHeight - recipeCardsY))) {
            infoScroll = Math.clamp(infoScroll - (int) Math.round(vertical * 18), 0, maximumInfoScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (searchBox != null && searchBox.isFocused() && isSearchFocusExitKey(event.key())) {
            releaseSearchFocus();
            return true;
        }
        if (searchBox != null && !searchBox.isFocused()) {
            if (event.key() == GLFW.GLFW_KEY_I) {
                mode = Mode.INFO;
                pairPreview = null;
                refreshRecipes();
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_R) {
                mode = Mode.RECIPES;
                pairPreview = null;
                refreshRecipes();
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_U) {
                mode = Mode.USES;
                pairPreview = null;
                refreshRecipes();
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_LEFT) {
                navigateHistory(-1);
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_RIGHT) {
                navigateHistory(1);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    static boolean isSearchFocusExitKey(int key) {
        return key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_TAB;
    }

    private void releaseSearchFocus() {
        setFocused(null);
        // Normally setFocused(null) also updates the child. Keep this guard so
        // the edit box cannot retain IME/text focus if a future screen focus
        // implementation changes that relationship.
        if (searchBox != null && searchBox.isFocused()) searchBox.setFocused(false);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private String kindLabel(ShardFusionCatalog.FusionKind kind) {
        return ModText.get(switch (kind) {
            case ID -> "shard.kind.id";
            case SPECIAL -> "shard.kind.special";
            case CHAMELEON -> "shard.kind.chameleon";
        });
    }

    private void drawFitted(GuiGraphicsExtractor graphics, String value, int x, int y,
                            int maximumWidth, int color) {
        if (maximumWidth <= 0 || value == null || value.isEmpty()) return;
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

    private void drawShardLink(GuiGraphicsExtractor graphics, String value, int x, int y,
                               int maximumWidth, int color, int mouseX, int mouseY) {
        int renderedTextWidth = Math.min(maximumWidth, Math.max(1, font.width(value)));
        boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y,
                renderedTextWidth, font.lineHeight);
        int renderedColor = hovered ? darken(color) : color;
        Component component = Component.literal(value).withStyle(style -> style
                .withColor(renderedColor).withUnderlined(hovered));
        drawFittedComponent(graphics, component, x, y, maximumWidth, renderedColor);
    }

    private void drawFittedComponent(GuiGraphicsExtractor graphics, Component value, int x, int y,
                                     int maximumWidth, int fallbackColor) {
        if (maximumWidth <= 0 || value == null) return;
        int measured = font.width(value);
        if (measured <= maximumWidth) {
            graphics.text(font, value, x, y, fallbackColor, false);
            return;
        }
        float scale = maximumWidth / (float) measured;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + Math.round((1.0f - scale) * 4.0f));
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, fallbackColor, false);
        graphics.pose().popMatrix();
    }

    static int darken(int color) {
        int alpha = color & 0xFF000000;
        int red = Math.round(((color >>> 16) & 0xFF) * 0.68f);
        int green = Math.round(((color >>> 8) & 0xFF) * 0.68f);
        int blue = Math.round((color & 0xFF) * 0.68f);
        return alpha | (red << 16) | (green << 8) | blue;
    }

    private void drawCenteredFitted(GuiGraphicsExtractor graphics, String value, int x, int y,
                                    int width, int height, int color) {
        if (width <= 0 || height <= 0 || value == null || value.isEmpty()) return;
        int measured = font.width(value);
        int innerWidth = Math.max(1, width - 4);
        if (measured <= innerWidth && font.lineHeight <= height) {
            graphics.text(font, value, x + (width - measured) / 2,
                    y + (height - font.lineHeight) / 2, color, false);
            return;
        }
        float scale = Math.min(innerWidth / (float) measured,
                Math.max(1, height - 2) / (float) font.lineHeight);
        float scaledWidth = measured * scale;
        float scaledHeight = font.lineHeight * scale;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + (width - scaledWidth) / 2.0f, y + (height - scaledHeight) / 2.0f);
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, color, false);
        graphics.pose().popMatrix();
    }

    private enum Mode {
        INFO,
        RECIPES,
        USES
    }

    private enum Action {
        CLOSE,
        HISTORY_BACK,
        HISTORY_FORWARD,
        SELECT_SHARD,
        INFO,
        RECIPES,
        USES,
        PREVIOUS_PAGE,
        NEXT_PAGE,
        SWAP,
        BACK_TO_LIST,
        SHOW_LIST,
        SHOW_DETAIL,
        PLANNER
    }

    private enum HitRegion {
        WINDOW,
        LIST,
        DETAIL,
        RECIPE
    }

    private record PairPreview(ShardFusionCatalog.Shard left, ShardFusionCatalog.Shard right,
                               ShardFusionCatalog.@Nullable Recipe recipe) {
    }

    private record Hit(Action action, ShardFusionCatalog.@Nullable Shard shard,
                       ShardFusionCatalog.@Nullable Recipe recipe,
                       int x, int y, int width, int height) {
        private boolean contains(double mouseX, double mouseY) {
            return AcaUiTheme.contains(mouseX, mouseY, x, y, width, height);
        }
    }

    private record Bounds(int x, int y, int width, int height) {
        private boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }

    private record InfoLine(FormattedCharSequence text, int indent, int topPadding, int height) {
    }

    record InputPairLayout(int leftX, int leftWidth, int plusX, int plusWidth,
                           int rightX, int rightWidth, int groupWidth, boolean showItems) {
    }

    record CompactRowLayout(int groupX, List<Integer> starts,
                            List<Integer> widths, int groupWidth) {
    }
}

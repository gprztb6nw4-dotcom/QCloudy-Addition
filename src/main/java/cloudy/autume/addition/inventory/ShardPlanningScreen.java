package cloudy.autume.addition.inventory;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.config.AcaUiTheme;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.i18n.ModText;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Local multi-step planner layered on top of the original Shard Fusion guide.
 * The screen never clicks a menu, sends a command, or performs a price request.
 */
public final class ShardPlanningScreen extends Screen {
    private static final int HEADER = 31;
    private static final int TAB_HEIGHT = 20;
    private static final int FIELD_HEIGHT = 18;
    private static final int ROW_HEIGHT = 15;
    private static final int NODE_WIDTH = 102;
    private static final int NODE_HEIGHT = 27;
    private static final DateTimeFormatter SNAPSHOT_TIME = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT).withZone(ZoneId.systemDefault());

    private final @Nullable Screen parent;
    private final ShardFusionCatalog catalog = ShardFusionCatalog.instance();
    private final ShardFusionPlanner planner = new ShardFusionPlanner(catalog);
    private final ShardItemResolver itemResolver = new ShardItemResolver(catalog);
    private final List<Hit> hits = new ArrayList<>();
    private Page page = Page.PLAN;
    private EditBox primaryBox;
    private EditBox secondaryBox;
    private EditBox tertiaryBox;
    private final List<FieldFrame> fieldFrames = new ArrayList<>();
    private String recipeInput = "";
    private String recipeOutput = "";
    private String shardQuery;
    private String lineQuery;
    private String selectedShardId;
    private String selectedLineId;
    private ShardFusionPlanner.@Nullable Plan currentPlan;
    private ShardFusionPlanner.@Nullable Node selectedPlanNode;
    private Map<String, Double> prices = Map.of();
    private boolean priceLoading;
    private boolean priceLoaded;
    private int scroll;
    private int maximumScroll;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int bodyX;
    private int bodyY;
    private int bodyWidth;
    private int bodyHeight;
    private @Nullable String draggingNode;
    private int dragOffsetX;
    private int dragOffsetY;

    public ShardPlanningScreen(@Nullable Screen parent, String initialTarget) {
        super(ModText.component("shard.planner.title"));
        this.parent = parent;
        String target = resolve(initialTarget) == null
                ? ConfigManager.get().inventory.shardPlannerTarget : resolve(initialTarget).id();
        shardQuery = target;
        lineQuery = target;
        selectedShardId = target;
        selectedLineId = target;
    }

    @Override
    protected void init() {
        layout();
        fieldFrames.clear();
        switch (page) {
            case PLAN -> initPlanFields();
            case RECIPES -> initRecipeFields();
            case SHARDS -> initShardFields();
            case LINES -> initLineFields();
            case WAREHOUSE -> {
            }
            case SETTINGS -> initSettingFields();
        }
        if (!priceLoaded && !priceLoading) loadPrices();
        if (page == Page.PLAN && currentPlan == null) calculatePlan();
    }

    private void layout() {
        windowWidth = Math.max(1, Math.min(940, width - Math.min(18, Math.max(0, width - 1))));
        windowHeight = Math.max(1, Math.min(520, height - Math.min(18, Math.max(0, height - 1))));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;
        bodyX = windowX + 8;
        bodyY = windowY + HEADER + TAB_HEIGHT + 10;
        bodyWidth = Math.max(1, windowWidth - 16);
        bodyHeight = Math.max(1, windowY + windowHeight - 23 - bodyY);
    }

    private void initPlanFields() {
        var config = ConfigManager.get().inventory;
        int y = bodyY + 8;
        primaryBox = addField(bodyX + 8, y + 11, Math.min(190, bodyWidth / 3),
                config.shardPlannerTarget, value -> {
                    ShardFusionCatalog.Shard shard = resolve(value);
                    if (shard != null) config.shardPlannerTarget = shard.id();
                });
        secondaryBox = addField(bodyX + 206, y + 11, 68,
                Integer.toString(config.shardPlannerQuantity), value -> {
                    config.shardPlannerQuantity = parseInt(value, config.shardPlannerQuantity, 1, 1_000_000);
                });
    }

    private void initRecipeFields() {
        int y = bodyY + 19;
        int fieldWidth = Math.max(70, Math.min(220, (bodyWidth - 40) / 2));
        primaryBox = addField(bodyX + 8, y, fieldWidth, recipeInput, value -> recipeInput = value);
        secondaryBox = addField(bodyX + 20 + fieldWidth, y, fieldWidth,
                recipeOutput, value -> recipeOutput = value);
    }

    private void initShardFields() {
        primaryBox = addField(bodyX + 8, bodyY + 19, Math.min(250, Math.max(70, bodyWidth / 3)),
                shardQuery, value -> {
                    shardQuery = value;
                    ShardFusionCatalog.Shard shard = resolve(value);
                    if (shard != null) selectedShardId = shard.id();
                    scroll = 0;
                });
        ShardFusionCatalog.Shard shard = selectedShard();
        String rate = shard == null ? "0" : displayDecimal(rate(shard));
        tertiaryBox = addField(bodyX + Math.min(270, bodyWidth / 2), bodyY + 19, 90,
                rate, ignored -> {
                });
    }

    private void initLineFields() {
        primaryBox = addField(bodyX + 8, bodyY + 19, Math.min(250, Math.max(70, bodyWidth / 3)),
                lineQuery, value -> {
                    lineQuery = value;
                    ShardFusionCatalog.Shard shard = resolve(value);
                    if (shard != null) selectedLineId = shard.id();
                });
    }

    private void initSettingFields() {
        var config = ConfigManager.get().inventory;
        int left = bodyX + 154;
        int right = bodyX + Math.max(330, bodyWidth / 2 + 90);
        int y = bodyY + 29;
        primaryBox = addField(left, y, 92, Integer.toString(config.shardPlannerHunterFortune), value ->
                config.shardPlannerHunterFortune = parseInt(value, config.shardPlannerHunterFortune, 0, 10_000));
        secondaryBox = addField(left, y + 28, 92, Integer.toString(config.shardPlannerCrocodileLevel), value ->
                config.shardPlannerCrocodileLevel = parseInt(value, config.shardPlannerCrocodileLevel, 0, 10));
        tertiaryBox = addField(left, y + 56, 92, displayDecimal(config.shardPlannerCoinsPerHour), value ->
                config.shardPlannerCoinsPerHour = parseDouble(value, config.shardPlannerCoinsPerHour, 0, 1.0E15));
        addField(right, y, 92, Integer.toString(config.shardPlannerCraftSeconds), value ->
                config.shardPlannerCraftSeconds = parseInt(value, config.shardPlannerCraftSeconds, 0, 600));
        addField(right, y + 28, 92, Integer.toString(config.shardPlannerKuudraSeconds), value ->
                config.shardPlannerKuudraSeconds = parseInt(value, config.shardPlannerKuudraSeconds, 10, 1800));
    }

    private EditBox addField(int x, int y, int width, String value, Consumer<String> responder) {
        int safeX = Math.clamp(x, bodyX, bodyX + bodyWidth - 1);
        int safeWidth = Math.max(1, Math.min(width, bodyX + bodyWidth - safeX));
        EditBox box = new EditBox(font, safeX + 5, y + 4, Math.max(1, safeWidth - 10),
                font.lineHeight, Component.empty());
        box.setBordered(false);
        box.setTextShadow(false);
        box.setMaxLength(80);
        box.setTextColor(AcaUiTheme.TEXT);
        box.setValue(value == null ? "" : value);
        box.setResponder(responder);
        addRenderableWidget(box);
        fieldFrames.add(new FieldFrame(box, safeX, y, safeWidth, FIELD_HEIGHT));
        return box;
    }

    private void loadPrices() {
        priceLoading = true;
        boolean instantBuy = ConfigManager.get().inventory.shardPlannerInstantBuy;
        CompletableFuture.supplyAsync(() -> ShardPriceService.instance().snapshot(instantBuy))
                .whenComplete((snapshot, error) -> {
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        if (error != null) {
                            QCloudyAdditionClient.LOGGER.warn("Could not read the optional Shard price cache", error);
                            prices = Map.of();
                        } else {
                            prices = snapshot == null ? Map.of() : snapshot;
                        }
                        priceLoading = false;
                        priceLoaded = true;
                        if (minecraft.screen == this && page == Page.PLAN) calculatePlan();
                    });
                });
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layout();
        itemResolver.refresh(minecraft);
        hits.clear();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        graphics.fill(windowX + 4, windowY + 5, windowX + windowWidth + 5,
                windowY + windowHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1,
                windowY + HEADER, AcaUiTheme.HEADER);
        drawHeader(graphics, mouseX, mouseY);
        drawTabs(graphics, mouseX, mouseY);
        graphics.fill(bodyX, bodyY, bodyX + bodyWidth, bodyY + bodyHeight, AcaUiTheme.CONTENT);
        graphics.outline(bodyX, bodyY, bodyWidth, bodyHeight, AcaUiTheme.BORDER_SOFT);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        for (FieldFrame frame : fieldFrames) {
            graphics.outline(frame.x(), frame.y(), frame.width(), frame.height(),
                    frame.box().isFocused() ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
        }
        graphics.enableScissor(bodyX + 1, bodyY + 1, bodyX + bodyWidth - 1, bodyY + bodyHeight - 1);
        switch (page) {
            case PLAN -> drawPlan(graphics, mouseX, mouseY);
            case RECIPES -> drawRecipes(graphics, mouseX, mouseY);
            case SHARDS -> drawShards(graphics, mouseX, mouseY);
            case LINES -> drawLines(graphics, mouseX, mouseY);
            case WAREHOUSE -> drawWarehouse(graphics, mouseX, mouseY);
            case SETTINGS -> drawSettings(graphics, mouseX, mouseY);
        }
        graphics.disableScissor();
        drawFooter(graphics);
    }

    private void drawHeader(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, ModText.get("shard.planner.title"), windowX + 11, windowY + 11,
                AcaUiTheme.TEXT, false);
        int closeX = windowX + windowWidth - 23;
        drawButton(graphics, "×", closeX, windowY + 7, 15, 15, mouseX, mouseY,
                false, true, Action.CLOSE, null);
        int guideWidth = Math.min(112, Math.max(50, windowWidth / 6));
        drawButton(graphics, ModText.get("shard.planner.guide"), closeX - guideWidth - 7,
                windowY + 7, guideWidth, 15, mouseX, mouseY, false, true, Action.GUIDE, null);
    }

    private void drawTabs(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = windowX + 8;
        int y = windowY + HEADER + 3;
        int gap = 3;
        int tabWidth = Math.max(24, (windowWidth - 16 - gap * (Page.values().length - 1)) / Page.values().length);
        for (Page value : Page.values()) {
            drawButton(graphics, ModText.get(value.key), x, y, tabWidth, TAB_HEIGHT,
                    mouseX, mouseY, value == page, true, Action.TAB, value);
            x += tabWidth + gap;
        }
    }

    private void drawPlan(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        var config = ConfigManager.get().inventory;
        int top = bodyY + 5;
        drawLabel(graphics, ModText.get("shard.planner.target"), bodyX + 8, top);
        drawLabel(graphics, ModText.get("shard.planner.quantity"), bodyX + 206, top);
        int controlsX = bodyX + Math.min(284, Math.max(8, bodyWidth / 3));
        int available = bodyX + bodyWidth - 8 - controlsX;
        int buttonWidth = Math.max(36, Math.min(86, (available - 12) / 4));
        drawButton(graphics, ModText.get("shard.planner.mode." + config.shardPlannerMode.toLowerCase(Locale.ROOT)),
                controlsX, top + 11, buttonWidth, 18, mouseX, mouseY, false, true, Action.MODE, null);
        drawButton(graphics, ModText.get("shard.planner.objective."
                        + config.shardPlannerObjective.toLowerCase(Locale.ROOT)),
                controlsX + buttonWidth + 4, top + 11, buttonWidth, 18,
                mouseX, mouseY, false, true, Action.OBJECTIVE, null);
        drawButton(graphics, ModText.get("shard.planner.materials"),
                controlsX + (buttonWidth + 4) * 2, top + 11, buttonWidth, 18,
                mouseX, mouseY, config.shardPlannerMaterialsOnly, true, Action.MATERIALS_ONLY, null);
        drawButton(graphics, ModText.get("shard.planner.calculate"),
                controlsX + (buttonWidth + 4) * 3, top + 11, buttonWidth, 18,
                mouseX, mouseY, true, true, Action.CALCULATE, null);

        int contentTop = bodyY + 41;
        if (currentPlan == null) return;
        if (!currentPlan.possible()) {
            drawWrapped(graphics, ModText.get("shard.planner.problem", currentPlan.problem()),
                    bodyX + 12, contentTop + 8, bodyWidth - 24, AcaUiTheme.DANGER, 5);
            drawPriceStatus(graphics, bodyX + 12, contentTop + 55, bodyWidth - 24);
            return;
        }
        if (config.shardPlannerMaterialsOnly) {
            drawMaterialSummary(graphics, bodyX + 8, contentTop, bodyWidth - 16,
                    bodyY + bodyHeight - contentTop - 4, mouseX, mouseY);
            return;
        }
        int leftWidth = Math.max(100, bodyWidth * 62 / 100);
        int rightX = bodyX + leftWidth + 5;
        graphics.fill(bodyX + 5, contentTop, bodyX + leftWidth, bodyY + bodyHeight - 4, AcaUiTheme.CARD);
        graphics.outline(bodyX + 5, contentTop, leftWidth - 5,
                bodyY + bodyHeight - contentTop - 4, AcaUiTheme.BORDER_SOFT);
        graphics.fill(rightX, contentTop, bodyX + bodyWidth - 5, bodyY + bodyHeight - 4, AcaUiTheme.CARD);
        graphics.outline(rightX, contentTop, bodyX + bodyWidth - 5 - rightX,
                bodyY + bodyHeight - contentTop - 4, AcaUiTheme.BORDER_SOFT);
        List<TreeLine> tree = new ArrayList<>();
        flatten(currentPlan.root(), 0, tree);
        int visible = Math.max(1, (bodyY + bodyHeight - contentTop - 24) / ROW_HEIGHT);
        maximumScroll = Math.max(0, tree.size() - visible);
        scroll = Math.clamp(scroll, 0, maximumScroll);
        drawLabel(graphics, ModText.get("shard.planner.tree"), bodyX + 12, contentTop + 6);
        for (int index = scroll; index < Math.min(tree.size(), scroll + visible); index++) {
            TreeLine line = tree.get(index);
            int y = contentTop + 20 + (index - scroll) * ROW_HEIGHT;
            boolean active = selectedPlanNode == line.node();
            if (active) graphics.fill(bodyX + 9, y - 2, bodyX + leftWidth - 4, y + 11, 0xFF303A3F);
            String prefix = "  ".repeat(Math.min(10, line.depth())) + (line.depth() == 0 ? "" : "↳ ");
            String text = prefix + line.node().shard().name() + " ×" + line.node().requested()
                    + " · " + methodLabel(line.node().method());
            drawFitted(graphics, text, bodyX + 12, y, leftWidth - 20,
                    line.node().shard().rarity().color());
            hits.add(new Hit(Action.SELECT_NODE, line.node(), bodyX + 9, y - 2, leftWidth - 13, 14));
        }
        drawPlanSummary(graphics, rightX + 7, contentTop + 6,
                bodyX + bodyWidth - rightX - 16, mouseX, mouseY);
    }

    private void drawPlanSummary(GuiGraphicsExtractor graphics, int x, int y, int width,
                                 int mouseX, int mouseY) {
        if (currentPlan == null) return;
        var config = ConfigManager.get().inventory;
        drawLabel(graphics, ModText.get("shard.planner.summary"), x, y);
        String unit = "CHEAPEST".equals(config.shardPlannerObjective)
                ? ModText.get("shard.planner.coins") : ModText.get("shard.planner.hours");
        String[] lines = {
                ModText.get("shard.planner.cost", formatNumber(currentPlan.estimatedCost()), unit),
                ModText.get("shard.planner.crafts", currentPlan.crafts()),
                ModText.get("shard.planner.hunt_count", sum(currentPlan.huntMaterials())),
                ModText.get("shard.planner.buy_count", sum(currentPlan.buyMaterials())),
                ModText.get("shard.planner.owned_count", sum(currentPlan.inventoryUsed()))
        };
        int cursor = y + 15;
        for (String line : lines) {
            drawFitted(graphics, line, x, cursor, width, AcaUiTheme.TEXT_MUTED);
            cursor += 13;
        }
        drawPriceStatus(graphics, x, cursor + 2, width);
        cursor += 31;
        ShardFusionPlanner.Node node = selectedPlanNode == null ? currentPlan.root() : selectedPlanNode;
        drawFitted(graphics, ModText.get("shard.planner.alternatives", node.shard().name()),
                x, cursor, width, node.shard().rarity().color());
        cursor += 14;
        int shown = 0;
        for (ShardFusionPlanner.Alternative alternative : node.alternatives()) {
            if (shown++ >= 9) break;
            String label;
            if (alternative.recipe() == null) {
                label = methodLabel(alternative.method()) + " · " + formatNumber(alternative.unitCost());
            } else {
                label = alternative.recipe().left().name() + " + " + alternative.recipe().right().name()
                        + " → ×" + alternative.outputPerCraft() + " · "
                        + formatNumber(alternative.unitCost());
            }
            drawFitted(graphics, label, x, cursor, width, AcaUiTheme.TEXT_MUTED);
            cursor += 13;
        }
        drawButton(graphics, ModText.get("shard.planner.materials.open"), x,
                Math.min(bodyY + bodyHeight - 24, cursor + 4), Math.min(128, width), 18,
                mouseX, mouseY, false, true, Action.MATERIALS_ONLY, null);
    }

    private void drawMaterialSummary(GuiGraphicsExtractor graphics, int x, int y, int width,
                                     int height, int mouseX, int mouseY) {
        if (currentPlan == null) return;
        drawLabel(graphics, ModText.get("shard.planner.materials.title"), x + 5, y + 5);
        int columnWidth = Math.max(1, (width - 16) / 3);
        drawMaterialColumn(graphics, ModText.get("shard.planner.hunt"), currentPlan.huntMaterials(),
                x + 5, y + 22, columnWidth, height - 28, ShardFusionPlanner.Acquisition.HUNT);
        drawMaterialColumn(graphics, ModText.get("shard.planner.buy"), currentPlan.buyMaterials(),
                x + 10 + columnWidth, y + 22, columnWidth, height - 28, ShardFusionPlanner.Acquisition.BUY);
        drawMaterialColumn(graphics, ModText.get("shard.planner.inventory"), currentPlan.inventoryUsed(),
                x + 15 + columnWidth * 2, y + 22, columnWidth, height - 28,
                ShardFusionPlanner.Acquisition.INVENTORY);
        drawButton(graphics, ModText.get("shard.planner.tree.open"), x + width - 130,
                y + 3, 125, 18, mouseX, mouseY, false, true, Action.MATERIALS_ONLY, null);
    }

    private void drawMaterialColumn(GuiGraphicsExtractor graphics, String title,
                                    Map<String, Integer> materials, int x, int y, int width, int height,
                                    ShardFusionPlanner.Acquisition type) {
        graphics.fill(x, y, x + width, y + height, AcaUiTheme.CARD);
        graphics.outline(x, y, width, height, AcaUiTheme.BORDER_SOFT);
        drawFitted(graphics, title + " · " + sum(materials), x + 6, y + 6,
                width - 12, methodColor(type));
        int cursor = y + 21;
        for (Map.Entry<String, Integer> entry : materials.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).toList()) {
            if (cursor + 12 > y + height) break;
            ShardFusionCatalog.Shard shard = catalog.byId(entry.getKey()).orElse(null);
            if (shard == null) continue;
            graphics.item(itemResolver.item(shard), x + 5, cursor - 2);
            drawFitted(graphics, shard.name() + " ×" + entry.getValue(), x + 24, cursor,
                    width - 29, shard.rarity().color());
            cursor += 17;
        }
    }

    private void drawRecipes(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        drawLabel(graphics, ModText.get("shard.planner.recipe.input"), bodyX + 8, bodyY + 7);
        int fieldWidth = Math.max(70, Math.min(220, (bodyWidth - 40) / 2));
        drawLabel(graphics, ModText.get("shard.planner.recipe.output"), bodyX + 20 + fieldWidth, bodyY + 7);
        List<ShardFusionCatalog.Recipe> recipes = filteredRecipes();
        drawFitted(graphics, ModText.get("shard.planner.recipe.matches", recipes.size()),
                bodyX + bodyWidth - 180, bodyY + 22, 170, AcaUiTheme.TEXT_DIM);
        int startY = bodyY + 45;
        int rowHeight = 37;
        int visible = Math.max(1, (bodyY + bodyHeight - startY - 4) / rowHeight);
        maximumScroll = Math.max(0, recipes.size() - visible);
        scroll = Math.clamp(scroll, 0, maximumScroll);
        for (int index = scroll; index < Math.min(recipes.size(), scroll + visible); index++) {
            ShardFusionCatalog.Recipe recipe = recipes.get(index);
            int y = startY + (index - scroll) * rowHeight;
            graphics.fill(bodyX + 6, y, bodyX + bodyWidth - 6, y + rowHeight - 4, AcaUiTheme.CARD);
            graphics.outline(bodyX + 6, y, bodyWidth - 12, rowHeight - 4, AcaUiTheme.BORDER_SOFT);
            String inputs = recipe.left().name() + " ×" + recipe.inputCount() + " + "
                    + recipe.right().name() + " ×" + recipe.inputCount();
            String outputs = recipe.outputs().stream().map(output -> output.shard().name()
                    + " ×" + output.count()).reduce((a, b) -> a + " / " + b).orElse("-");
            drawFitted(graphics, inputs, bodyX + 13, y + 5, bodyWidth / 2 - 20,
                    AcaUiTheme.TEXT);
            drawFitted(graphics, "→ " + outputs, bodyX + bodyWidth / 2, y + 5,
                    bodyWidth / 2 - 15, AcaUiTheme.SUCCESS);
            drawFitted(graphics, ModText.get("shard.input_count", recipe.inputCount()),
                    bodyX + 13, y + 18, bodyWidth - 26, AcaUiTheme.TEXT_DIM);
            hits.add(new Hit(Action.OPEN_RECIPE, recipe, bodyX + 6, y, bodyWidth - 12, rowHeight - 4));
        }
    }

    private void drawShards(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        drawLabel(graphics, ModText.get("shard.planner.shard.search"), bodyX + 8, bodyY + 7);
        int listWidth = Math.min(250, Math.max(110, bodyWidth / 3));
        List<ShardFusionCatalog.Shard> results = catalog.search(shardQuery);
        int y = bodyY + 43;
        int visible = Math.max(1, (bodyHeight - 48) / 22);
        for (int index = 0; index < Math.min(visible, results.size()); index++) {
            ShardFusionCatalog.Shard shard = results.get(index);
            int rowY = y + index * 22;
            boolean active = shard.id().equals(selectedShardId);
            graphics.fill(bodyX + 6, rowY, bodyX + listWidth, rowY + 20,
                    active ? 0xFF303A3F : AcaUiTheme.CARD);
            graphics.item(itemResolver.item(shard), bodyX + 9, rowY + 2);
            drawFitted(graphics, shard.name(), bodyX + 29, rowY + 5,
                    listWidth - 35, shard.rarity().color());
            hits.add(new Hit(Action.SELECT_SHARD, shard, bodyX + 6, rowY, listWidth - 6, 20));
        }
        ShardFusionCatalog.Shard shard = selectedShard();
        if (shard == null) return;
        int x = bodyX + listWidth + 10;
        int width = bodyX + bodyWidth - x - 8;
        graphics.item(itemResolver.item(shard), x, bodyY + 7);
        drawFitted(graphics, shard.displayName(), x + 21, bodyY + 7, width - 21, shard.rarity().color());
        drawFitted(graphics, shard.id() + " · " + shard.attributeName(), x + 21, bodyY + 19,
                width - 21, shard.category().color());
        int cursor = bodyY + 47;
        cursor = drawMetadata(graphics, ModText.get("shard.info.effect"), effectText(shard),
                x, cursor, width, effectColor(shard));
        cursor = drawMetadata(graphics, ModText.get("shard.info.family"),
                String.join(", ", shard.families()), x, cursor, width, shard.category().color());
        cursor = drawMetadata(graphics, ModText.get("shard.info.skill"), shard.skill(),
                x, cursor, width, AcaUiTheme.SUCCESS);
        cursor = drawMetadata(graphics, ModText.get("shard.info.mob_type"),
                String.join(", ", shard.mobTypes()), x, cursor, width, AcaUiTheme.TEXT);
        drawLabel(graphics, ModText.get("shard.info.obtain"), x, cursor + 3);
        cursor += 18;
        for (ShardFusionCatalog.Acquisition acquisition : shard.acquisition()) {
            cursor += drawWrapped(graphics, "• " + acquisition.text(), x, cursor, width,
                    acquisition.kind().color(), 5);
        }
        int rateY = Math.min(bodyY + bodyHeight - 44, Math.max(cursor + 7, bodyY + 120));
        drawLabel(graphics, ModText.get("shard.planner.rate"), x, rateY);
        int saveX = bodyX + Math.min(270, bodyWidth / 2) + 96;
        drawButton(graphics, ModText.get("shard.planner.save"), saveX, bodyY + 19,
                54, 18, mouseX, mouseY, false, true, Action.SAVE_RATE, shard);
        drawButton(graphics, ModText.get("shard.planner.reset"), saveX + 58, bodyY + 19,
                54, 18, mouseX, mouseY, false, true, Action.RESET_RATE, shard);
        drawFitted(graphics, ModText.get("shard.planner.rate.baseline",
                        displayDecimal(ShardAcquisitionRates.instance().rate(shard.id()))),
                x, rateY + 15, width, AcaUiTheme.TEXT_DIM);
    }

    private void drawLines(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        drawLabel(graphics, ModText.get("shard.planner.lines.search"), bodyX + 8, bodyY + 7);
        drawFitted(graphics, ModText.get("shard.planner.lines.help"),
                bodyX + Math.min(270, bodyWidth / 2), bodyY + 24,
                bodyWidth - Math.min(278, bodyWidth / 2), AcaUiTheme.TEXT_DIM);
        ShardFusionCatalog.Shard focus = catalog.byId(selectedLineId).orElse(null);
        if (focus == null) return;
        Graph graph = graph(focus);
        int graphTop = bodyY + 46;
        int graphHeight = bodyY + bodyHeight - graphTop - 5;
        for (GraphEdge edge : graph.edges()) {
            GraphPosition from = position(edge.from(), graph.nodes(), graphTop, graphHeight);
            GraphPosition to = position(edge.to(), graph.nodes(), graphTop, graphHeight);
            int x1 = bodyX + 8 + from.x() + NODE_WIDTH / 2;
            int y1 = graphTop + from.y() + NODE_HEIGHT / 2;
            int x2 = bodyX + 8 + to.x() + NODE_WIDTH / 2;
            int y2 = graphTop + to.y() + NODE_HEIGHT / 2;
            int color = edge.kind() == ShardFusionCatalog.FusionKind.SPECIAL
                    ? ShardFusionCatalog.TextTone.DARK_PURPLE.color()
                    : edge.kind() == ShardFusionCatalog.FusionKind.CHAMELEON
                    ? ShardFusionCatalog.TextTone.GREEN.color() : AcaUiTheme.ACCENT_DARK;
            int mid = (x1 + x2) / 2;
            graphics.fill(Math.min(x1, mid), y1, Math.max(x1, mid) + 1, y1 + 1, color);
            graphics.fill(mid, Math.min(y1, y2), mid + 1, Math.max(y1, y2) + 1, color);
            graphics.fill(Math.min(mid, x2), y2, Math.max(mid, x2) + 1, y2 + 1, color);
        }
        for (ShardFusionCatalog.Shard shard : graph.nodes()) {
            GraphPosition position = position(shard.id(), graph.nodes(), graphTop, graphHeight);
            int x = bodyX + 8 + position.x();
            int y = graphTop + position.y();
            boolean active = shard.id().equals(selectedLineId);
            graphics.fill(x, y, x + NODE_WIDTH, y + NODE_HEIGHT,
                    active ? 0xFF303A3F : AcaUiTheme.CARD);
            graphics.outline(x, y, NODE_WIDTH, NODE_HEIGHT,
                    active ? shard.rarity().color() : AcaUiTheme.BORDER);
            graphics.item(itemResolver.item(shard), x + 4, y + 5);
            drawFitted(graphics, shard.name(), x + 24, y + 5, NODE_WIDTH - 28, shard.rarity().color());
            drawFitted(graphics, shard.id(), x + 24, y + 16, NODE_WIDTH - 28, AcaUiTheme.TEXT_DIM);
            hits.add(new Hit(Action.DRAG_NODE, shard, x, y, NODE_WIDTH, NODE_HEIGHT));
        }
    }

    private void drawWarehouse(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Map<String, Integer> warehouse = ShardWarehouseManager.current(minecraft);
        long updated = ShardWarehouseManager.updatedAt(minecraft);
        drawLabel(graphics, ModText.get("shard.planner.warehouse.title"), bodyX + 9, bodyY + 8);
        drawWrapped(graphics, ModText.get("shard.planner.warehouse.help"), bodyX + 9,
                bodyY + 25, bodyWidth - 130, AcaUiTheme.TEXT_MUTED, 3);
        drawButton(graphics, ModText.get("shard.planner.warehouse.clear"),
                bodyX + bodyWidth - 112, bodyY + 8, 102, 18, mouseX, mouseY,
                false, !warehouse.isEmpty(), Action.CLEAR_WAREHOUSE, null);
        String time = updated <= 0 ? ModText.get("shard.planner.never")
                : SNAPSHOT_TIME.format(Instant.ofEpochMilli(updated));
        drawFitted(graphics, ModText.get("shard.planner.warehouse.updated", time),
                bodyX + 9, bodyY + 54, bodyWidth - 18, AcaUiTheme.TEXT_DIM);
        List<Map.Entry<String, Integer>> entries = warehouse.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey)).toList();
        int columns = Math.max(1, bodyWidth / 210);
        int cellWidth = bodyWidth / columns;
        int visibleRows = Math.max(1, (bodyHeight - 78) / 22);
        int maximum = Math.max(0, (entries.size() + columns - 1) / columns - visibleRows);
        maximumScroll = maximum;
        scroll = Math.clamp(scroll, 0, maximum);
        for (int index = scroll * columns; index < Math.min(entries.size(), (scroll + visibleRows) * columns); index++) {
            int relative = index - scroll * columns;
            int x = bodyX + (relative % columns) * cellWidth + 7;
            int y = bodyY + 74 + (relative / columns) * 22;
            Map.Entry<String, Integer> entry = entries.get(index);
            ShardFusionCatalog.Shard shard = catalog.byId(entry.getKey()).orElse(null);
            if (shard == null) continue;
            graphics.item(itemResolver.item(shard), x, y);
            drawFitted(graphics, shard.name() + " ×" + entry.getValue(), x + 20, y + 4,
                    cellWidth - 28, shard.rarity().color());
        }
        if (warehouse.isEmpty()) {
            drawWrapped(graphics, ModText.get("shard.planner.warehouse.empty"), bodyX + 10,
                    bodyY + 85, bodyWidth - 20, AcaUiTheme.TEXT_DIM, 4);
        }
    }

    private void drawSettings(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        var config = ConfigManager.get().inventory;
        int y = bodyY + 8;
        drawLabel(graphics, ModText.get("shard.planner.settings.title"), bodyX + 9, y);
        int left = bodyX + 9;
        int right = bodyX + Math.max(260, bodyWidth / 2);
        String[] leftLabels = {
                ModText.get("shard.planner.hunter_fortune"),
                ModText.get("shard.planner.crocodile"),
                ModText.get("shard.planner.coins_hour")
        };
        String[] rightLabels = {
                ModText.get("shard.planner.craft_seconds"),
                ModText.get("shard.planner.kuudra_seconds")
        };
        for (int index = 0; index < leftLabels.length; index++) {
            drawFitted(graphics, leftLabels[index], left, bodyY + 34 + index * 28,
                    140, AcaUiTheme.TEXT_MUTED);
        }
        for (int index = 0; index < rightLabels.length; index++) {
            drawFitted(graphics, rightLabels[index], right, bodyY + 34 + index * 28,
                    150, AcaUiTheme.TEXT_MUTED);
        }
        int toggleY = bodyY + 132;
        drawToggleRow(graphics, ModText.get("shard.planner.use_warehouse"),
                config.shardPlannerUseWarehouse, left, toggleY, mouseX, mouseY,
                Action.USE_WAREHOUSE);
        drawToggleRow(graphics, ModText.get("shard.planner.instant_buy"),
                config.shardPlannerInstantBuy, left, toggleY + 27, mouseX, mouseY,
                Action.INSTANT_BUY);
        drawFitted(graphics, ModText.get("shard.planner.kuudra_tier"), right,
                toggleY, 145, AcaUiTheme.TEXT_MUTED);
        drawButton(graphics, config.shardPlannerKuudraTier, right + 154, toggleY - 3,
                64, 18, mouseX, mouseY, false, true, Action.KUUDRA_TIER, null);
        drawPriceStatus(graphics, left, toggleY + 64, bodyWidth - 24);
        drawWrapped(graphics, ModText.get("shard.planner.settings.persistence"), left,
                toggleY + 96, bodyWidth - 24, AcaUiTheme.TEXT_DIM, 4);
        drawWrapped(graphics, ModText.get("shard.planner.settings.client_only"), left,
                toggleY + 139, bodyWidth - 24, AcaUiTheme.SUCCESS, 4);
    }

    private void drawToggleRow(GuiGraphicsExtractor graphics, String label, boolean enabled,
                               int x, int y, int mouseX, int mouseY, Action action) {
        drawFitted(graphics, label, x, y, 180, AcaUiTheme.TEXT_MUTED);
        AcaUiTheme.toggle(graphics, x + 188, y - 3, enabled);
        hits.add(new Hit(action, null, x, y - 4, 222, 20));
    }

    private void drawPriceStatus(GuiGraphicsExtractor graphics, int x, int y, int width) {
        String status;
        int color;
        if (priceLoading) {
            status = ModText.get("shard.planner.price.loading");
            color = AcaUiTheme.TEXT_DIM;
        } else if (!prices.isEmpty()) {
            status = ModText.get("shard.planner.price.available",
                    ShardPriceService.instance().sourceName(), prices.size());
            color = AcaUiTheme.SUCCESS;
        } else {
            status = ModText.get("shard.planner.price.unavailable",
                    ShardPriceService.instance().sourceName());
            color = AcaUiTheme.DANGER;
        }
        drawWrapped(graphics, status, x, y, width, color, 3);
    }

    private int drawMetadata(GuiGraphicsExtractor graphics, String label, String value,
                             int x, int y, int width, int color) {
        if (value == null || value.isBlank()) return y;
        drawFitted(graphics, label + ":", x, y, width, AcaUiTheme.TEXT_DIM);
        int lines = drawWrapped(graphics, value, x + 10, y + 13, width - 10, color, 5);
        return y + 13 + lines + 5;
    }

    private void drawFooter(GuiGraphicsExtractor graphics) {
        drawFitted(graphics, ModText.get("shard.planner.footer"), windowX + 9,
                windowY + windowHeight - 15, windowWidth - 18, AcaUiTheme.TEXT_DIM);
    }

    private void drawLabel(GuiGraphicsExtractor graphics, String label, int x, int y) {
        graphics.text(font, label, x, y, AcaUiTheme.TEXT_MUTED, false);
    }

    private void drawButton(GuiGraphicsExtractor graphics, String label, int x, int y, int width,
                            int height, int mouseX, int mouseY, boolean selected, boolean enabled,
                            Action action, @Nullable Object value) {
        boolean hovered = enabled && contains(mouseX, mouseY, x, y, width, height);
        int fill = selected ? AcaUiTheme.ACCENT
                : !enabled ? 0xFF1B2023 : hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CONTROL;
        graphics.fill(x, y, x + width, y + height, fill);
        graphics.outline(x, y, width, height, selected ? AcaUiTheme.ACCENT : AcaUiTheme.BORDER);
        drawCenteredFitted(graphics, label, x, y, width, height,
                selected ? 0xFF071014 : enabled ? AcaUiTheme.TEXT : AcaUiTheme.TEXT_DIM);
        if (enabled) hits.add(new Hit(action, value, x, y, width, height));
    }

    private void calculatePlan() {
        var config = ConfigManager.get().inventory;
        ShardFusionCatalog.Shard target = resolve(primaryBox == null
                ? config.shardPlannerTarget : primaryBox.getValue());
        if (target != null) config.shardPlannerTarget = target.id();
        config.shardPlannerQuantity = parseInt(secondaryBox == null ? "" : secondaryBox.getValue(),
                config.shardPlannerQuantity, 1, 1_000_000);
        ConfigManager.save();
        Map<String, Integer> warehouse = config.shardPlannerUseWarehouse
                ? ShardWarehouseManager.current(minecraft) : Map.of();
        ShardFusionPlanner.Parameters parameters = new ShardFusionPlanner.Parameters(
                ShardFusionPlanner.Mode.valueOf(config.shardPlannerMode),
                ShardFusionPlanner.Objective.valueOf(config.shardPlannerObjective),
                config.shardPlannerRates, prices, warehouse, config.shardPlannerUseWarehouse,
                config.shardPlannerHunterFortune, config.shardPlannerCrocodileLevel,
                config.shardPlannerCoinsPerHour, config.shardPlannerCraftSeconds,
                ShardFusionPlanner.KuudraTier.valueOf(config.shardPlannerKuudraTier),
                config.shardPlannerKuudraSeconds);
        currentPlan = planner.plan(config.shardPlannerTarget, config.shardPlannerQuantity, parameters);
        selectedPlanNode = currentPlan.possible() ? currentPlan.root() : null;
        scroll = 0;
    }

    private List<ShardFusionCatalog.Recipe> filteredRecipes() {
        ShardFusionCatalog.Shard input = resolve(recipeInput);
        ShardFusionCatalog.Shard output = resolve(recipeOutput);
        if (input == null && output == null) return List.of();
        List<ShardFusionCatalog.Recipe> source = input != null
                ? catalog.usesForInput(input.id()) : catalog.recipesForOutput(output.id());
        if (output == null) return source;
        return source.stream().filter(recipe -> recipe.outputs().stream()
                .anyMatch(value -> value.shard().id().equals(output.id()))).toList();
    }

    private Graph graph(ShardFusionCatalog.Shard focus) {
        Map<String, ShardFusionCatalog.Shard> nodes = new LinkedHashMap<>();
        List<GraphEdge> edges = new ArrayList<>();
        nodes.put(focus.id(), focus);
        for (ShardFusionCatalog.Recipe recipe : catalog.recipesForOutput(focus.id())) {
            if (nodes.size() >= 24) break;
            nodes.put(recipe.left().id(), recipe.left());
            nodes.put(recipe.right().id(), recipe.right());
            for (ShardFusionCatalog.Output output : recipe.outputs()) {
                if (!output.shard().id().equals(focus.id())) continue;
                edges.add(new GraphEdge(recipe.left().id(), focus.id(), output.kind()));
                edges.add(new GraphEdge(recipe.right().id(), focus.id(), output.kind()));
            }
        }
        for (ShardFusionCatalog.Recipe recipe : catalog.usesForInput(focus.id())) {
            if (nodes.size() >= 24) break;
            for (ShardFusionCatalog.Output output : recipe.outputs()) {
                if (nodes.size() >= 24) break;
                nodes.put(output.shard().id(), output.shard());
                edges.add(new GraphEdge(focus.id(), output.shard().id(), output.kind()));
            }
        }
        return new Graph(List.copyOf(nodes.values()), dedupeEdges(edges));
    }

    private List<GraphEdge> dedupeEdges(List<GraphEdge> source) {
        Set<GraphEdge> values = new LinkedHashSet<>(source);
        return List.copyOf(values);
    }

    private GraphPosition position(String id, List<ShardFusionCatalog.Shard> nodes,
                                   int graphTop, int graphHeight) {
        String saved = ConfigManager.get().inventory.shardFusionLinePositions.get(id);
        int maxX = Math.max(0, bodyWidth - NODE_WIDTH - 16);
        int maxY = Math.max(0, graphHeight - NODE_HEIGHT);
        if (saved != null) {
            String[] parts = saved.split(",", 2);
            try {
                return new GraphPosition(Math.clamp(Integer.parseInt(parts[0]), 0, maxX),
                        Math.clamp(Integer.parseInt(parts[1]), 0, maxY));
            } catch (RuntimeException ignored) {
            }
        }
        int index = 0;
        for (; index < nodes.size(); index++) if (nodes.get(index).id().equals(id)) break;
        int columns = Math.max(1, Math.max(1, bodyWidth - 16) / (NODE_WIDTH + 18));
        return new GraphPosition(Math.clamp((index % columns) * (NODE_WIDTH + 18), 0, maxX),
                Math.clamp((index / columns) * (NODE_HEIGHT + 16), 0, maxY));
    }

    private ShardFusionCatalog.@Nullable Shard selectedShard() {
        return catalog.byId(selectedShardId).orElse(null);
    }

    private ShardFusionCatalog.@Nullable Shard resolve(String value) {
        if (value == null || value.isBlank()) return null;
        return catalog.byId(value).or(() -> catalog.byName(value))
                .orElseGet(() -> {
                    List<ShardFusionCatalog.Shard> matches = catalog.search(value);
                    return matches.isEmpty() ? null : matches.getFirst();
                });
    }

    private double rate(ShardFusionCatalog.Shard shard) {
        return ConfigManager.get().inventory.shardPlannerRates.getOrDefault(shard.id(),
                ShardAcquisitionRates.instance().rate(shard.id()));
    }

    private void saveRate(ShardFusionCatalog.Shard shard) {
        double value = parseDouble(tertiaryBox == null ? "" : tertiaryBox.getValue(),
                rate(shard), 0.0, 1.0E12);
        ConfigManager.get().inventory.shardPlannerRates.put(shard.id(), value);
        ConfigManager.save();
        if (tertiaryBox != null) tertiaryBox.setValue(displayDecimal(value));
        currentPlan = null;
    }

    private void resetRate(ShardFusionCatalog.Shard shard) {
        ConfigManager.get().inventory.shardPlannerRates.remove(shard.id());
        ConfigManager.save();
        if (tertiaryBox != null) tertiaryBox.setValue(displayDecimal(rate(shard)));
        currentPlan = null;
    }

    private void setPage(Page next) {
        if (page == next) return;
        ConfigManager.save();
        page = next;
        scroll = 0;
        rebuildWidgets();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (!insideField(click.x(), click.y())) setFocused(null);
        if (super.mouseClicked(click, doubled)) return true;
        for (int index = hits.size() - 1; index >= 0; index--) {
            Hit hit = hits.get(index);
            if (!hit.contains(click.x(), click.y())) continue;
            return activate(hit, click);
        }
        return false;
    }

    private boolean activate(Hit hit, MouseButtonEvent click) {
        switch (hit.action()) {
            case CLOSE -> onClose();
            case GUIDE -> minecraft.setScreen(new ShardFusionScreen(this,
                    ConfigManager.get().inventory.shardPlannerTarget));
            case TAB -> setPage((Page) hit.value());
            case CALCULATE -> calculatePlan();
            case MODE -> {
                var config = ConfigManager.get().inventory;
                config.shardPlannerMode = "IRONMAN".equals(config.shardPlannerMode) ? "NORMAL" : "IRONMAN";
                if ("IRONMAN".equals(config.shardPlannerMode)
                        && "CHEAPEST".equals(config.shardPlannerObjective)) {
                    config.shardPlannerObjective = "FASTEST";
                }
                calculatePlan();
            }
            case OBJECTIVE -> {
                var config = ConfigManager.get().inventory;
                if ("IRONMAN".equals(config.shardPlannerMode)) {
                    config.shardPlannerObjective = "FASTEST";
                } else {
                    config.shardPlannerObjective = "FASTEST".equals(config.shardPlannerObjective)
                            ? "CHEAPEST" : "FASTEST";
                }
                calculatePlan();
            }
            case MATERIALS_ONLY -> {
                var config = ConfigManager.get().inventory;
                config.shardPlannerMaterialsOnly = !config.shardPlannerMaterialsOnly;
                ConfigManager.save();
            }
            case SELECT_NODE -> selectedPlanNode = (ShardFusionPlanner.Node) hit.value();
            case OPEN_RECIPE -> {
                ShardFusionCatalog.Recipe recipe = (ShardFusionCatalog.Recipe) hit.value();
                minecraft.setScreen(new ShardFusionScreen(this, recipe.outputs().getFirst().shard().id()));
            }
            case SELECT_SHARD -> {
                ShardFusionCatalog.Shard shard = (ShardFusionCatalog.Shard) hit.value();
                selectedShardId = shard.id();
                shardQuery = shard.id();
                scroll = 0;
                rebuildWidgets();
            }
            case SAVE_RATE -> saveRate((ShardFusionCatalog.Shard) hit.value());
            case RESET_RATE -> resetRate((ShardFusionCatalog.Shard) hit.value());
            case DRAG_NODE -> {
                ShardFusionCatalog.Shard shard = (ShardFusionCatalog.Shard) hit.value();
                selectedLineId = shard.id();
                lineQuery = shard.id();
                if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    draggingNode = shard.id();
                    dragOffsetX = (int) click.x() - hit.x();
                    dragOffsetY = (int) click.y() - hit.y();
                }
            }
            case CLEAR_WAREHOUSE -> {
                ShardWarehouseManager.clearCurrent(minecraft);
                currentPlan = null;
            }
            case USE_WAREHOUSE -> {
                var config = ConfigManager.get().inventory;
                config.shardPlannerUseWarehouse = !config.shardPlannerUseWarehouse;
                ConfigManager.save();
                currentPlan = null;
            }
            case INSTANT_BUY -> {
                var config = ConfigManager.get().inventory;
                config.shardPlannerInstantBuy = !config.shardPlannerInstantBuy;
                ConfigManager.save();
                priceLoaded = false;
                prices = Map.of();
                loadPrices();
                currentPlan = null;
            }
            case KUUDRA_TIER -> {
                var config = ConfigManager.get().inventory;
                ShardFusionPlanner.KuudraTier current = ShardFusionPlanner.KuudraTier
                        .valueOf(config.shardPlannerKuudraTier);
                ShardFusionPlanner.KuudraTier[] values = ShardFusionPlanner.KuudraTier.values();
                config.shardPlannerKuudraTier = values[(current.ordinal() + 1) % values.length].name();
                ConfigManager.save();
                currentPlan = null;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (draggingNode != null && page == Page.LINES) {
            int graphTop = bodyY + 46;
            int graphHeight = bodyY + bodyHeight - graphTop - 5;
            int maxX = Math.max(0, bodyWidth - NODE_WIDTH - 16);
            int maxY = Math.max(0, graphHeight - NODE_HEIGHT);
            int x = Math.clamp((int) click.x() - (bodyX + 8) - dragOffsetX, 0, maxX);
            int y = Math.clamp((int) click.y() - graphTop - dragOffsetY, 0, maxY);
            ConfigManager.get().inventory.shardFusionLinePositions.put(draggingNode, x + "," + y);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (draggingNode != null) {
            draggingNode = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (contains(mouseX, mouseY, bodyX, bodyY, bodyWidth, bodyHeight)) {
            scroll = Math.clamp(scroll - (int) Math.signum(vertical), 0, maximumScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ENTER && page == Page.PLAN) {
            calculatePlan();
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE && focusedField()) {
            setFocused(null);
            return true;
        }
        return super.keyPressed(event);
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

    private boolean insideField(double mouseX, double mouseY) {
        return fieldFrames.stream().anyMatch(frame -> contains(mouseX, mouseY,
                frame.x(), frame.y(), frame.width(), frame.height()));
    }

    private boolean focusedField() {
        return fieldFrames.stream().anyMatch(frame -> frame.box().isFocused());
    }

    private void flatten(ShardFusionPlanner.Node node, int depth, List<TreeLine> result) {
        if (node == null || result.size() >= 1_000) return;
        result.add(new TreeLine(node, depth));
        for (ShardFusionPlanner.Node input : node.inputs()) flatten(input, depth + 1, result);
    }

    private static int sum(Map<String, Integer> values) {
        long result = 0;
        for (int value : values.values()) result += value;
        return (int) Math.min(Integer.MAX_VALUE, result);
    }

    private String methodLabel(ShardFusionPlanner.Acquisition method) {
        return ModText.get("shard.planner.method." + method.name().toLowerCase(Locale.ROOT));
    }

    private static int methodColor(ShardFusionPlanner.Acquisition method) {
        return switch (method) {
            case INVENTORY -> AcaUiTheme.ACCENT;
            case HUNT -> AcaUiTheme.SUCCESS;
            case BUY -> ShardFusionCatalog.TextTone.GOLD.color();
            case FUSION -> ShardFusionCatalog.TextTone.DARK_PURPLE.color();
            case UNAVAILABLE -> AcaUiTheme.DANGER;
        };
    }

    private static String effectText(ShardFusionCatalog.Shard shard) {
        return shard.effect().stream().map(ShardFusionCatalog.TextSpan::text)
                .reduce((a, b) -> a + b).orElse("");
    }

    private static int effectColor(ShardFusionCatalog.Shard shard) {
        return shard.effect().stream().filter(span -> span.tone() != ShardFusionCatalog.TextTone.TEXT
                        && span.tone() != ShardFusionCatalog.TextTone.GRAY)
                .findFirst().map(span -> span.tone().color()).orElse(AcaUiTheme.TEXT_MUTED);
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

    private void drawCenteredFitted(GuiGraphicsExtractor graphics, String value, int x, int y,
                                    int width, int height, int color) {
        if (width <= 0 || height <= 0 || value == null || value.isEmpty()) return;
        int measured = Math.max(1, font.width(value));
        int available = Math.max(1, width - 4);
        float scale = Math.min(1.0f, available / (float) measured);
        float rendered = measured * scale;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + (width - rendered) / 2.0f,
                y + (height - font.lineHeight * scale) / 2.0f);
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, color, false);
        graphics.pose().popMatrix();
    }

    private int drawWrapped(GuiGraphicsExtractor graphics, String value, int x, int y,
                            int width, int color, int maximumLines) {
        if (value == null || value.isBlank() || width <= 0) return 0;
        List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.literal(value), width);
        int count = Math.min(maximumLines, lines.size());
        for (int index = 0; index < count; index++) {
            graphics.text(font, lines.get(index), x, y + index * (font.lineHeight + 2), color, false);
        }
        return count * (font.lineHeight + 2);
    }

    private static int parseInt(String value, int fallback, int minimum, int maximum) {
        try {
            return Math.clamp(Integer.parseInt(value.trim()), minimum, maximum);
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static double parseDouble(String value, double fallback, double minimum, double maximum) {
        try {
            return Math.clamp(Double.parseDouble(value.trim().replace(",", "")), minimum, maximum);
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String displayDecimal(double value) {
        if (!Double.isFinite(value)) return "0";
        if (Math.rint(value) == value) return Long.toString((long) value);
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static String formatNumber(double value) {
        if (!Double.isFinite(value)) return "-";
        double absolute = Math.abs(value);
        if (absolute >= 1_000_000_000) return String.format(Locale.ROOT, "%.1fb", value / 1_000_000_000);
        if (absolute >= 1_000_000) return String.format(Locale.ROOT, "%.1fm", value / 1_000_000);
        if (absolute >= 1_000) return String.format(Locale.ROOT, "%.1fk", value / 1_000);
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static boolean contains(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private enum Page {
        PLAN("shard.planner.tab.plan"),
        RECIPES("shard.planner.tab.recipes"),
        SHARDS("shard.planner.tab.shards"),
        LINES("shard.planner.tab.lines"),
        WAREHOUSE("shard.planner.tab.warehouse"),
        SETTINGS("shard.planner.tab.settings");

        private final String key;

        Page(String key) {
            this.key = key;
        }
    }

    private enum Action {
        CLOSE, GUIDE, TAB, CALCULATE, MODE, OBJECTIVE, MATERIALS_ONLY,
        SELECT_NODE, OPEN_RECIPE, SELECT_SHARD, SAVE_RATE, RESET_RATE,
        DRAG_NODE, CLEAR_WAREHOUSE, USE_WAREHOUSE, INSTANT_BUY, KUUDRA_TIER
    }

    private record FieldFrame(EditBox box, int x, int y, int width, int height) {
    }

    private record Hit(Action action, @Nullable Object value, int x, int y, int width, int height) {
        private boolean contains(double mouseX, double mouseY) {
            return ShardPlanningScreen.contains(mouseX, mouseY, x, y, width, height);
        }
    }

    private record TreeLine(ShardFusionPlanner.Node node, int depth) {
    }

    private record Graph(List<ShardFusionCatalog.Shard> nodes, List<GraphEdge> edges) {
    }

    private record GraphEdge(String from, String to, ShardFusionCatalog.FusionKind kind) {
    }

    private record GraphPosition(int x, int y) {
    }

}

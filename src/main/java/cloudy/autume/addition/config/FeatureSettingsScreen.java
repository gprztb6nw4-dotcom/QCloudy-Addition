package cloudy.autume.addition.config;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.input.HotkeyInputs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

final class FeatureSettingsScreen extends Screen {
    private static final int ROW_HEIGHT = 27;
    private final Screen parent;
    private final ConfigScreen.Feature feature;
    private final long openedAt = System.nanoTime();
    private final List<Hit> hits = new ArrayList<>();
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int contentX;
    private int contentY;
    private int contentWidth;
    private int scroll;
    private int maxScroll;
    private Hit draggingSlider;
    private QCloudyAdditionClient.ChordAction listeningChord;

    FeatureSettingsScreen(Screen parent, ConfigScreen.Feature feature) {
        super(ModText.component(feature.titleKey));
        this.parent = parent;
        this.feature = feature;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        layout();
        graphics.fill(0, 0, width, height, AcaUiTheme.SCRIM);
        UiAnimation.push(graphics, UiAnimation.scale(openedAt), width / 2.0f, height / 2.0f);
        graphics.fill(windowX + 4, windowY + 5, windowX + windowWidth + 5, windowY + windowHeight + 6, 0x66000000);
        AcaUiTheme.surface(graphics, windowX, windowY, windowWidth, windowHeight, AcaUiTheme.WINDOW);
        graphics.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1, windowY + 34, AcaUiTheme.HEADER);
        graphics.text(font, Component.literal(ModText.get(feature.titleKey)).withStyle(ChatFormatting.BOLD),
                windowX + 42, windowY + 10, AcaUiTheme.TEXT, false);
        AcaUiTheme.button(graphics, font, "‹", windowX + 10, windowY + 8, 24, 18,
                AcaUiTheme.contains(mouseX, mouseY, windowX + 10, windowY + 8, 24, 18), false);
        String hint = ModText.get("config.feature_settings_hint");
        graphics.text(font, hint, windowX + 12, windowY + 43, AcaUiTheme.TEXT_DIM, false);

        hits.clear();
        List<Setting> settings = settings();
        int viewportY = contentY;
        int viewportHeight = windowHeight - (contentY - windowY) - 12;
        maxScroll = Math.max(0, settings.size() * (ROW_HEIGHT + 4) - 4 - viewportHeight);
        scroll = Math.clamp(scroll, 0, maxScroll);
        graphics.enableScissor(contentX, viewportY, contentX + contentWidth, viewportY + viewportHeight);
        int y = contentY - scroll;
        for (Setting setting : settings) {
            drawRow(graphics, setting, contentX, y, contentWidth, mouseX, mouseY);
            hits.add(new Hit(setting, contentX, y, contentWidth, ROW_HEIGHT));
            y += ROW_HEIGHT + 4;
        }
        graphics.disableScissor();
        if (maxScroll > 0) {
            int thumbHeight = Math.max(18, viewportHeight * viewportHeight / (viewportHeight + maxScroll));
            int thumbY = viewportY + (viewportHeight - thumbHeight) * scroll / maxScroll;
            graphics.fill(contentX + contentWidth + 3, viewportY, contentX + contentWidth + 5,
                    viewportY + viewportHeight, AcaUiTheme.CONTROL);
            graphics.fill(contentX + contentWidth + 3, thumbY, contentX + contentWidth + 5,
                    thumbY + thumbHeight, AcaUiTheme.ACCENT);
        }
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        UiAnimation.pop(graphics);
    }

    private void layout() {
        windowWidth = Math.min(520, Math.max(310, width - 24));
        windowHeight = Math.min(390, Math.max(230, height - 24));
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;
        contentX = windowX + 12;
        contentY = windowY + 60;
        contentWidth = windowWidth - 29;
    }

    private void drawRow(GuiGraphicsExtractor graphics, Setting setting, int x, int y, int rowWidth,
                         int mouseX, int mouseY) {
        boolean hovered = AcaUiTheme.contains(mouseX, mouseY, x, y, rowWidth, ROW_HEIGHT);
        boolean listening = setting.chordAction() != null && setting.chordAction() == listeningChord;
        graphics.fill(x, y, x + rowWidth, y + ROW_HEIGHT,
                listening ? 0xFF263D47 : hovered ? AcaUiTheme.CARD_HOVER : AcaUiTheme.CARD);
        graphics.outline(x, y, rowWidth, ROW_HEIGHT,
                listening ? AcaUiTheme.ACCENT : hovered ? AcaUiTheme.ACCENT_DARK : AcaUiTheme.BORDER_SOFT);
        String value = setting.value();
        int valueX = x + rowWidth - font.width(value) - 12;
        if (setting.chordAction() != null) {
            int maximumValueWidth = Math.max(72, rowWidth / 2 - 14);
            int labelWidth = Math.max(42, rowWidth - maximumValueWidth - 30);
            drawFittedText(graphics, setting.label(), x + 10, y + 9, labelWidth);
            drawFittedTextRight(graphics, value, x + rowWidth - 10, y + 9, maximumValueWidth,
                    listening ? AcaUiTheme.ACCENT : AcaUiTheme.TEXT_MUTED);
            return;
        }
        if (setting.slider()) {
            int trackEnd = x + rowWidth - 58;
            int trackWidth = Math.min(150, Math.max(72, rowWidth / 3));
            int trackX = trackEnd - trackWidth;
            int labelWidth = Math.max(36, trackX - x - 18);
            drawFittedText(graphics, setting.label(), x + 10, y + 9, labelWidth);
            int trackY = y + 12;
            int knobX = trackX + (int) Math.round(setting.sliderFraction() * trackWidth);
            graphics.fill(trackX, trackY, trackEnd, trackY + 3, 0xFF69747A);
            graphics.fill(trackX, trackY, knobX, trackY + 3, AcaUiTheme.ACCENT);
            graphics.fill(knobX - 4, y + 7, knobX + 5, y + 21, 0xFFBCEEFF);
            graphics.outline(knobX - 4, y + 7, 9, 14, AcaUiTheme.ACCENT_DARK);
            graphics.text(font, value, x + rowWidth - font.width(value) - 10, y + 9,
                    AcaUiTheme.TEXT_MUTED, false);
            return;
        }
        graphics.text(font, setting.label(), x + 10, y + 9, AcaUiTheme.TEXT, false);
        if (setting.color()) {
            int swatchX = valueX - 18;
            if (setting.kind == Kind.BACKGROUND_COLOR && panelStyle().backgroundOpacity == 0) {
                graphics.fill(swatchX, y + 7, swatchX + 13, y + 20, 0xFFE6E6E6);
                graphics.fill(swatchX, y + 7, swatchX + 6, y + 13, 0xFF999999);
                graphics.fill(swatchX + 6, y + 13, swatchX + 13, y + 20, 0xFF999999);
            } else {
                graphics.fill(swatchX, y + 7, swatchX + 13, y + 20, 0xFF000000 | setting.colorValue());
            }
            graphics.outline(swatchX, y + 7, 13, 13, AcaUiTheme.BORDER);
        }
        graphics.text(font, value, valueX, y + 9, AcaUiTheme.TEXT_MUTED, false);
    }

    private void drawFittedText(GuiGraphicsExtractor graphics, String text, int x, int y, int availableWidth) {
        int textWidth = font.width(text);
        if (textWidth <= availableWidth) {
            graphics.text(font, text, x, y, AcaUiTheme.TEXT, false);
            return;
        }
        float scale = Math.min(1.0f, availableWidth / (float) textWidth);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + Math.round((1.0f - scale) * 4.0f));
        graphics.pose().scale(scale, scale);
        graphics.text(font, text, 0, 0, AcaUiTheme.TEXT, false);
        graphics.pose().popMatrix();
    }

    private void drawFittedTextRight(GuiGraphicsExtractor graphics, String text, int rightX, int y,
                                     int availableWidth, int color) {
        int textWidth = font.width(text);
        float scale = textWidth <= availableWidth ? 1.0f : availableWidth / (float) textWidth;
        graphics.pose().pushMatrix();
        graphics.pose().translate(rightX - textWidth * scale, y + Math.round((1.0f - scale) * 4.0f));
        graphics.pose().scale(scale, scale);
        graphics.text(font, text, 0, 0, color, false);
        graphics.pose().popMatrix();
    }

    private List<Setting> settings() {
        List<Setting> rows = new ArrayList<>();
        if (feature.huntingFeature()) {
            for (HuntingOption option : HuntingOption.forFeature(feature)) rows.add(new Setting(option));
            if (feature.hudType() != null) {
                rows.add(new Setting(Kind.OPACITY, "config.setting.opacity"));
                rows.add(new Setting(Kind.BACKGROUND_COLOR, "config.setting.background_color"));
                rows.add(new Setting(Kind.BORDER, "config.setting.border"));
                rows.add(new Setting(Kind.BORDER_SIZE, "config.setting.border_size"));
                rows.add(new Setting(Kind.BORDER_COLOR, "config.setting.border_color"));
                rows.add(new Setting(Kind.TITLE_COLOR, "config.setting.title_color"));
                rows.add(new Setting(Kind.BOLD, "config.setting.bold"));
                rows.add(new Setting(Kind.SHADOW, "config.setting.shadow"));
                rows.add(new Setting(Kind.SCALE, "config.setting.scale"));
                rows.add(new Setting(Kind.EDIT_LAYOUT, "config.layout"));
            }
            return rows;
        }
        if (feature.inventoryFeature()) {
            rows.add(new Setting(Kind.YIELD_FIRMAMENT, "config.setting.yield_firmament"));
            switch (feature) {
                case ITEM_TIMESTAMPS -> {
                    rows.add(new Setting(Kind.SHOW_CREATION, "config.setting.show_creation"));
                    rows.add(new Setting(Kind.SHOW_COUNTDOWNS, "config.setting.show_countdowns"));
                    rows.add(new Setting(Kind.TIMESTAMP_FORMAT, "config.setting.timestamp_format"));
                }
                case CURSOR_MEMORY -> rows.add(new Setting(Kind.CURSOR_TOLERANCE, "config.setting.cursor_tolerance"));
                case TELEPORT_SOUNDS -> {
                    ModConfig.Inventory inventory = ConfigManager.get().inventory;
                    rows.add(new Setting(Kind.INSTANT_SOUND_MODE, "config.setting.instant_sound_mode"));
                    if ("CUSTOM".equals(inventory.instantTransmissionSoundMode)) {
                        rows.add(new Setting(Kind.INSTANT_CUSTOM_SOUND, "config.setting.instant_custom_sound"));
                        rows.add(new Setting(Kind.INSTANT_SOUND_VOLUME, "config.setting.instant_sound_volume"));
                    }
                    rows.add(new Setting(Kind.ETHERWARP_SOUND_MODE, "config.setting.etherwarp_sound_mode"));
                    if ("CUSTOM".equals(inventory.etherwarpSoundMode)) {
                        rows.add(new Setting(Kind.ETHERWARP_CUSTOM_SOUND, "config.setting.etherwarp_custom_sound"));
                        rows.add(new Setting(Kind.ETHERWARP_SOUND_VOLUME, "config.setting.etherwarp_sound_volume"));
                    }
                }
                default -> { }
            }
            return rows;
        }
        if (feature == ConfigScreen.Feature.DRAGON_HIGHLIGHT) {
            rows.add(new Setting(Kind.DRAGON_COLOR, "config.setting.highlight_color"));
            return rows;
        }
        if (feature == ConfigScreen.Feature.CHAT_PEEK) {
            rows.add(new Setting(Kind.CHAT_PEEK_KEY, "config.setting.chat_peek_key"));
            rows.add(new Setting(Kind.CHAT_SCROLL_TARGET, "config.setting.chat_scroll_target"));
            return rows;
        }
        rows.add(new Setting(Kind.OPACITY, "config.setting.opacity"));
        rows.add(new Setting(Kind.BACKGROUND_COLOR, "config.setting.background_color"));
        rows.add(new Setting(Kind.BORDER, "config.setting.border"));
        rows.add(new Setting(Kind.BORDER_SIZE, "config.setting.border_size"));
        rows.add(new Setting(Kind.BORDER_COLOR, "config.setting.border_color"));
        rows.add(new Setting(Kind.TITLE_COLOR, "config.setting.title_color"));
        rows.add(new Setting(Kind.BOLD, "config.setting.bold"));
        rows.add(new Setting(Kind.SHADOW, "config.setting.shadow"));
        rows.add(new Setting(Kind.SCALE, "config.setting.scale"));
        if (feature == ConfigScreen.Feature.MINING_TRACKER) {
            rows.add(new Setting(Kind.COMMISSION_PROGRESS, "config.setting.commission_progress"));
            rows.add(new Setting(Kind.HOTM_SLOT, "config.setting.hotm_slot"));
        }
        if (feature == ConfigScreen.Feature.PET_HUD) {
            rows.add(new Setting(Kind.PET_ICON, "config.setting.pet_icon"));
            rows.add(new Setting(Kind.PET_LEVEL_XP, "config.setting.pet_level_xp"));
            rows.add(new Setting(Kind.PET_MAX_XP, "config.setting.pet_max_xp"));
            rows.add(new Setting(Kind.PET_OVERFLOW_LEVEL, "config.setting.pet_overflow_level"));
            rows.add(new Setting(Kind.PET_SKIN_NAME, "config.setting.pet_skin_name"));
            rows.add(new Setting(Kind.PET_ACCESSORY, "config.setting.pet_accessory"));
        }
        rows.add(new Setting(Kind.EDIT_LAYOUT, "config.layout"));
        return rows;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (listeningChord != null) {
            if (HotkeyInputs.supportedMouseButton(click.button())) {
                QCloudyAdditionClient.setMouseChord(listeningChord, click.button(), click.modifiers());
                saveVanillaOptions();
                listeningChord = null;
            }
            return true;
        }
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        if (AcaUiTheme.contains(click.x(), click.y(), windowX + 10, windowY + 8, 24, 18)) {
            onClose();
            return true;
        }
        int viewportHeight = windowHeight - (contentY - windowY) - 12;
        if (!AcaUiTheme.contains(click.x(), click.y(), contentX, contentY, contentWidth, viewportHeight)) {
            return super.mouseClicked(click, doubled);
        }
        for (Hit hit : hits) {
            if (hit.contains(click.x(), click.y())) {
                if (hit.setting.slider() && hit.sliderContains(click.x(), click.y())) {
                    draggingSlider = hit;
                    updateSlider(hit, click.x());
                    return true;
                }
                if (hit.setting.slider()) return true;
                activate(hit.setting);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listeningChord == null) return super.keyPressed(event);
        if (event.key() == GLFW.GLFW_KEY_ESCAPE
                || event.key() == GLFW.GLFW_KEY_BACKSPACE
                || event.key() == GLFW.GLFW_KEY_DELETE) {
            QCloudyAdditionClient.clearChord(listeningChord);
            saveVanillaOptions();
            listeningChord = null;
            return true;
        }
        if (isModifier(event.key())) return true;
        QCloudyAdditionClient.setKeyboardChord(listeningChord, event.key(), event.modifiers());
        saveVanillaOptions();
        listeningChord = null;
        return true;
    }

    private void saveVanillaOptions() {
        if (minecraft != null) minecraft.options.save();
    }

    private static boolean isModifier(int key) {
        return key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT
                || key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL
                || key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT
                || key == GLFW.GLFW_KEY_LEFT_SUPER || key == GLFW.GLFW_KEY_RIGHT_SUPER;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (draggingSlider != null && click.button() == 0) {
            updateSlider(draggingSlider, click.x());
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (draggingSlider != null && click.button() == 0) {
            updateSlider(draggingSlider, click.x());
            draggingSlider = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    private void updateSlider(Hit hit, double mouseX) {
        int trackEnd = hit.x + hit.width - 58;
        int trackWidth = Math.min(150, Math.max(72, hit.width / 3));
        int trackX = trackEnd - trackWidth;
        hit.setting.setSliderFraction(Math.clamp((mouseX - trackX) / trackWidth, 0.0, 1.0));
    }

    private void activate(Setting setting) {
        ModConfig config = ConfigManager.get();
        if (setting.huntingOption != null) {
            HuntingOption option = setting.huntingOption;
            if (option.type == HuntingOption.Type.BOOLEAN) option.toggle(config.hunting);
            else if (option.type == HuntingOption.Type.COLOR) {
                openColor(option.intValue(config.hunting), value -> option.setInt(config.hunting, value));
            }
            ConfigManager.save();
            return;
        }
        ModConfig.PanelStyle style = panelStyle();
        switch (setting.kind) {
            case YIELD_FIRMAMENT -> config.inventory.yieldToFirmament = !config.inventory.yieldToFirmament;
            case OPEN_CONFIG_KEY -> listeningChord = QCloudyAdditionClient.ChordAction.OPEN_CONFIG;
            case SHOW_CREATION -> config.inventory.showCreationTimestamp = !config.inventory.showCreationTimestamp;
            case SHOW_COUNTDOWNS -> config.inventory.showCountdownCompletion = !config.inventory.showCountdownCompletion;
            case TIMESTAMP_FORMAT -> config.inventory.timestampFormat = next(config.inventory.timestampFormat,
                    "LOCAL_24H", "LOCAL_12H", "ISO", "RFC");
            case CURSOR_TOLERANCE -> config.inventory.cursorToleranceMs = config.inventory.cursorToleranceMs >= 5000
                    ? 50 : Math.min(5000, config.inventory.cursorToleranceMs + 50);
            case INSTANT_SOUND_MODE -> config.inventory.instantTransmissionSoundMode = next(
                    config.inventory.instantTransmissionSoundMode, "VANILLA", "CUSTOM");
            case INSTANT_CUSTOM_SOUND -> config.inventory.instantTransmissionCustomSound = nextTeleportSound(
                    config.inventory.instantTransmissionCustomSound);
            case INSTANT_SOUND_VOLUME -> config.inventory.instantTransmissionSoundVolume = nextPercent(
                    config.inventory.instantTransmissionSoundVolume, 0, 100);
            case ETHERWARP_SOUND_MODE -> config.inventory.etherwarpSoundMode = next(
                    config.inventory.etherwarpSoundMode, "VANILLA", "CUSTOM");
            case ETHERWARP_CUSTOM_SOUND -> config.inventory.etherwarpCustomSound = nextTeleportSound(
                    config.inventory.etherwarpCustomSound);
            case ETHERWARP_SOUND_VOLUME -> config.inventory.etherwarpSoundVolume = nextPercent(
                    config.inventory.etherwarpSoundVolume, 0, 100);
            case DRAGON_COLOR -> openColor(config.combat.enderDragonHighlightColor,
                    color -> config.combat.enderDragonHighlightColor = color);
            case CHAT_PEEK_KEY -> listeningChord = QCloudyAdditionClient.ChordAction.PEEK_CHAT;
            case CHAT_SCROLL_TARGET -> config.chat.peekScrollTarget = next(
                    config.chat.peekScrollTarget, "CHAT", "HOTBAR");
            case OPACITY -> style.backgroundOpacity = style.backgroundOpacity >= 255
                    ? 0 : Math.min(255, (style.backgroundOpacity / 32 + 1) * 32);
            case BACKGROUND_COLOR -> openBackgroundColor(style);
            case BORDER -> style.border = !style.border;
            case BORDER_SIZE -> style.borderThickness = style.borderThickness % 4 + 1;
            case BORDER_COLOR -> openColor(style.borderColor, color -> style.borderColor = color);
            case TITLE_COLOR -> openColor(style.titleColor, color -> style.titleColor = color);
            case BOLD -> style.boldText = !style.boldText;
            case SHADOW -> style.textShadow = !style.textShadow;
            case SCALE -> {
                style.scale = Math.round((style.scale + 0.1f) * 10.0f) / 10.0f;
                if (style.scale > 2.0f) style.scale = 0.5f;
            }
            case COMMISSION_PROGRESS -> config.mining.commissionProgressMode = next(
                    config.mining.commissionProgressMode, "PERCENT", "NUMERIC");
            case HOTM_SLOT -> config.mining.showHotmSlot = !config.mining.showHotmSlot;
            case PET_ICON -> config.pets.showPetIcon = !config.pets.showPetIcon;
            case PET_LEVEL_XP -> config.pets.showLevelProgress = !config.pets.showLevelProgress;
            case PET_MAX_XP -> config.pets.showMaxProgress = !config.pets.showMaxProgress;
            case PET_OVERFLOW_LEVEL -> config.pets.showOverflowLevel = !config.pets.showOverflowLevel;
            case PET_SKIN_NAME -> config.pets.showSkinName = !config.pets.showSkinName;
            case PET_ACCESSORY -> config.pets.petAccessoryDisplay = next(config.pets.petAccessoryDisplay,
                    "ICON_AND_NAME", "ICON_ONLY", "NAME_ONLY");
            case EDIT_LAYOUT -> minecraft.setScreen(new HudLayoutScreen(this));
        }
        ConfigManager.save();
    }

    private void openColor(int initial, IntConsumer setter) {
        minecraft.setScreen(new ColorPickerScreen(this, initial, value -> {
            setter.accept(value);
            ConfigManager.save();
        }));
    }

    private void openBackgroundColor(ModConfig.PanelStyle style) {
        int restoredOpacity = style.backgroundOpacity > 0 ? style.backgroundOpacity : 120;
        minecraft.setScreen(new ColorPickerScreen(this, style.backgroundColor, value -> {
            style.backgroundColor = value;
            ConfigManager.save();
        }, true, style.backgroundOpacity == 0, () -> {
            style.backgroundOpacity = 0;
            ConfigManager.save();
        }, () -> {
            style.backgroundOpacity = restoredOpacity;
            ConfigManager.save();
        }));
    }

    private static String next(String current, String... values) {
        for (int index = 0; index < values.length; index++) {
            if (values[index].equals(current)) return values[(index + 1) % values.length];
        }
        return values[0];
    }

    private static String nextTeleportSound(String current) {
        return next(current, "CHORUS", "ENDERMAN", "AMETHYST", "ORB", "PORTAL", "SHULKER");
    }

    private static int nextPercent(int current, int minimum, int maximum) {
        return current >= maximum ? minimum : Math.min(maximum, current + 10);
    }

    private ModConfig.PanelStyle panelStyle() {
        ModConfig.HudType type = feature.hudType();
        return type == null ? ConfigManager.get().hudStyle.map : ConfigManager.get().hudStyle.style(type);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (AcaUiTheme.contains(mouseX, mouseY, contentX, contentY, contentWidth, windowHeight - 72)) {
            scroll = Math.clamp(scroll - (int) Math.round(vertical * 22), 0, maxScroll);
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

    private enum Kind {
        DRAGON_COLOR, OPACITY, BACKGROUND_COLOR, BORDER, BORDER_SIZE, BORDER_COLOR,
        TITLE_COLOR, BOLD, SHADOW, SCALE, PET_ICON, PET_LEVEL_XP, PET_MAX_XP, PET_OVERFLOW_LEVEL,
        PET_SKIN_NAME, PET_ACCESSORY, COMMISSION_PROGRESS, HOTM_SLOT, EDIT_LAYOUT,
        YIELD_FIRMAMENT, OPEN_CONFIG_KEY, SHOW_CREATION, SHOW_COUNTDOWNS,
        TIMESTAMP_FORMAT, CURSOR_TOLERANCE,
        INSTANT_SOUND_MODE, INSTANT_CUSTOM_SOUND, INSTANT_SOUND_VOLUME,
        ETHERWARP_SOUND_MODE, ETHERWARP_CUSTOM_SOUND, ETHERWARP_SOUND_VOLUME,
        CHAT_PEEK_KEY, CHAT_SCROLL_TARGET
    }

    private final class Setting {
        private final Kind kind;
        private final String labelKey;
        private final HuntingOption huntingOption;

        private Setting(Kind kind, String labelKey) {
            this.kind = kind;
            this.labelKey = labelKey;
            this.huntingOption = null;
        }

        private Setting(HuntingOption huntingOption) {
            this.kind = null;
            this.labelKey = huntingOption.labelKey;
            this.huntingOption = huntingOption;
        }

        String label() {
            return ModText.get(labelKey);
        }

        String value() {
            ModConfig config = ConfigManager.get();
            if (huntingOption != null) {
                return switch (huntingOption.type) {
                    case BOOLEAN -> onOff(huntingOption.booleanValue(config.hunting));
                    case SLIDER -> huntingOption.intValue(config.hunting) + huntingOption.suffix;
                    case COLOR -> String.format("#%06X", huntingOption.intValue(config.hunting) & 0xFFFFFF);
                };
            }
            ModConfig.PanelStyle style = panelStyle();
            return switch (kind) {
                case YIELD_FIRMAMENT -> onOff(config.inventory.yieldToFirmament);
                case OPEN_CONFIG_KEY, CHAT_PEEK_KEY -> {
                    QCloudyAdditionClient.ChordAction action = chordAction();
                    yield action == listeningChord ? ModText.get("config.key.waiting")
                            : QCloudyAdditionClient.chordName(action);
                }
                case SHOW_CREATION -> onOff(config.inventory.showCreationTimestamp);
                case SHOW_COUNTDOWNS -> onOff(config.inventory.showCountdownCompletion);
                case TIMESTAMP_FORMAT -> config.inventory.timestampFormat.replace('_', ' ');
                case CURSOR_TOLERANCE -> config.inventory.cursorToleranceMs + " ms";
                case INSTANT_SOUND_MODE -> ModText.get("config.value."
                        + config.inventory.instantTransmissionSoundMode.toLowerCase());
                case INSTANT_CUSTOM_SOUND -> ModText.get("config.value.sound."
                        + config.inventory.instantTransmissionCustomSound.toLowerCase());
                case INSTANT_SOUND_VOLUME -> config.inventory.instantTransmissionSoundVolume + "%";
                case ETHERWARP_SOUND_MODE -> ModText.get("config.value."
                        + config.inventory.etherwarpSoundMode.toLowerCase());
                case ETHERWARP_CUSTOM_SOUND -> ModText.get("config.value.sound."
                        + config.inventory.etherwarpCustomSound.toLowerCase());
                case ETHERWARP_SOUND_VOLUME -> config.inventory.etherwarpSoundVolume + "%";
                case CHAT_SCROLL_TARGET -> ModText.get("config.value."
                        + config.chat.peekScrollTarget.toLowerCase());
                case BACKGROUND_COLOR -> style.backgroundOpacity == 0
                        ? ModText.get("config.transparent") : String.format("#%06X", colorValue());
                case DRAGON_COLOR, BORDER_COLOR, TITLE_COLOR -> String.format("#%06X", colorValue());
                case OPACITY -> Math.round(style.backgroundOpacity / 255.0f * 100.0f) + "%";
                case BORDER -> onOff(style.border);
                case BORDER_SIZE -> style.borderThickness + " px";
                case BOLD -> onOff(style.boldText);
                case SHADOW -> onOff(style.textShadow);
                case SCALE -> Math.round(style.scale * 100) + "%";
                case COMMISSION_PROGRESS -> ModText.get("config.value."
                        + config.mining.commissionProgressMode.toLowerCase());
                case HOTM_SLOT -> onOff(config.mining.showHotmSlot);
                case PET_ICON -> onOff(config.pets.showPetIcon);
                case PET_LEVEL_XP -> onOff(config.pets.showLevelProgress);
                case PET_MAX_XP -> onOff(config.pets.showMaxProgress);
                case PET_OVERFLOW_LEVEL -> onOff(config.pets.showOverflowLevel);
                case PET_SKIN_NAME -> onOff(config.pets.showSkinName);
                case PET_ACCESSORY -> ModText.get("config.value." + config.pets.petAccessoryDisplay.toLowerCase());
                case EDIT_LAYOUT -> ModText.get("config.open");
            };
        }

        boolean color() {
            if (huntingOption != null) return huntingOption.type == HuntingOption.Type.COLOR;
            return kind == Kind.DRAGON_COLOR || kind == Kind.BACKGROUND_COLOR
                    || kind == Kind.BORDER_COLOR || kind == Kind.TITLE_COLOR;
        }

        QCloudyAdditionClient.ChordAction chordAction() {
            if (huntingOption != null) return null;
            return switch (kind) {
                case OPEN_CONFIG_KEY -> QCloudyAdditionClient.ChordAction.OPEN_CONFIG;
                case CHAT_PEEK_KEY -> QCloudyAdditionClient.ChordAction.PEEK_CHAT;
                default -> null;
            };
        }

        int colorValue() {
            ModConfig config = ConfigManager.get();
            if (huntingOption != null) return huntingOption.intValue(config.hunting) & 0xFFFFFF;
            ModConfig.PanelStyle style = panelStyle();
            return switch (kind) {
                case DRAGON_COLOR -> config.combat.enderDragonHighlightColor;
                case BACKGROUND_COLOR -> style.backgroundColor;
                case BORDER_COLOR -> style.borderColor;
                case TITLE_COLOR -> style.titleColor;
                default -> 0xFFFFFF;
            } & 0xFFFFFF;
        }

        boolean slider() {
            if (huntingOption != null) return huntingOption.type == HuntingOption.Type.SLIDER;
            return switch (kind) {
                case OPACITY, SCALE, CURSOR_TOLERANCE, INSTANT_SOUND_VOLUME,
                        ETHERWARP_SOUND_VOLUME -> true;
                default -> false;
            };
        }

        double sliderFraction() {
            ModConfig config = ConfigManager.get();
            if (huntingOption != null) {
                return fraction(huntingOption.intValue(config.hunting), huntingOption.minimum, huntingOption.maximum);
            }
            ModConfig.PanelStyle style = panelStyle();
            return switch (kind) {
                case OPACITY -> style.backgroundOpacity / 255.0;
                case SCALE -> (style.scale - 0.5) / 1.5;
                case CURSOR_TOLERANCE -> fraction(config.inventory.cursorToleranceMs, 50, 5000);
                case INSTANT_SOUND_VOLUME -> fraction(config.inventory.instantTransmissionSoundVolume, 0, 100);
                case ETHERWARP_SOUND_VOLUME -> fraction(config.inventory.etherwarpSoundVolume, 0, 100);
                default -> 0.0;
            };
        }

        void setSliderFraction(double fraction) {
            ModConfig config = ConfigManager.get();
            if (huntingOption != null) {
                huntingOption.setInt(config.hunting,
                        ranged(Math.clamp(fraction, 0.0, 1.0), huntingOption.minimum, huntingOption.maximum));
                return;
            }
            ModConfig.PanelStyle style = panelStyle();
            double clamped = Math.clamp(fraction, 0.0, 1.0);
            switch (kind) {
                case OPACITY -> style.backgroundOpacity = ranged(clamped, 0, 255);
                case SCALE -> style.scale = Math.round((0.5f + (float) clamped * 1.5f) * 100.0f) / 100.0f;
                case CURSOR_TOLERANCE -> config.inventory.cursorToleranceMs =
                        Math.round(ranged(clamped, 50, 5000) / 10.0f) * 10;
                case INSTANT_SOUND_VOLUME -> config.inventory.instantTransmissionSoundVolume = ranged(clamped, 0, 100);
                case ETHERWARP_SOUND_VOLUME -> config.inventory.etherwarpSoundVolume = ranged(clamped, 0, 100);
                default -> { }
            }
        }

        private double fraction(int value, int minimum, int maximum) {
            return (value - minimum) / (double) (maximum - minimum);
        }

        private int ranged(double fraction, int minimum, int maximum) {
            return minimum + (int) Math.round(fraction * (maximum - minimum));
        }

        private String onOff(boolean enabled) {
            return ModText.get(enabled ? "config.enabled" : "config.disabled");
        }
    }

    private record Hit(Setting setting, int x, int y, int width, int height) {
        boolean contains(double mouseX, double mouseY) {
            return AcaUiTheme.contains(mouseX, mouseY, x, y, width, height);
        }

        boolean sliderContains(double mouseX, double mouseY) {
            int trackEnd = x + width - 58;
            int trackWidth = Math.min(150, Math.max(72, width / 3));
            return AcaUiTheme.contains(mouseX, mouseY, trackEnd - trackWidth - 5, y + 4,
                    trackWidth + 10, height - 8);
        }
    }
}

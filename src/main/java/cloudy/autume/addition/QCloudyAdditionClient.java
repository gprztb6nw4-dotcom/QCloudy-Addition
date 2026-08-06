package cloudy.autume.addition;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.config.ConfigScreen;
import cloudy.autume.addition.hud.HudRenderer;
import cloudy.autume.addition.input.HotkeyInputs;
import cloudy.autume.addition.inventory.InventoryDataManager;
import cloudy.autume.addition.inventory.ItemTimestampTooltip;
import cloudy.autume.addition.hunting.HuntingTracker;
import cloudy.autume.addition.hunting.HuntingWorldRenderer;
import cloudy.autume.addition.inventory.SlotLockManager;
import cloudy.autume.addition.inventory.SafariBeltTooltip;
import cloudy.autume.addition.inventory.storage.StorageController;
import cloudy.autume.addition.tracker.LocationTracker;
import cloudy.autume.addition.tracker.HotmSlotTracker;
import cloudy.autume.addition.tracker.PetTracker;
import cloudy.autume.addition.tracker.PetSkinTracker;
import cloudy.autume.addition.tracker.TabListTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.MacosUtil;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QCloudyAdditionClient implements ClientModInitializer {
    public static final String MOD_ID = "qcloudy_addition";
    public static final Logger LOGGER = LoggerFactory.getLogger("QCloudy_Addition");
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls"));
    private static final KeyMapping OPEN_CONFIG = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.qcloudy_addition.open_config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KEY_CATEGORY));
    private static final KeyMapping LOCK_SLOT = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.qcloudy_addition.lock_slot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY));
    private static final KeyMapping LOCK_ITEM = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.qcloudy_addition.lock_item", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY));
    private static final KeyMapping BIND_SLOT = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.qcloudy_addition.bind_slot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KEY_CATEGORY));
    private static final KeyMapping PEEK_CHAT = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.qcloudy_addition.peek_chat", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private static final String[] COMMAND_ALIASES = {"aca", "qca", "ca", "qc"};
    private int ticks;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        InventoryDataManager.load();
        ItemTimestampTooltip.register();
        SafariBeltTooltip.register();
        HuntingWorldRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ticks++;
            if (ticks % 20 == 0) {
                LocationTracker.update(client);
                TabListTracker.update(client);
                HuntingTracker.updateReceivedText(TabListTracker.lines(), LocationTracker.scoreboardLines());
                HotmSlotTracker.update(client);
                PetSkinTracker.update(client);
            }
            InventoryDataManager.tick();
            StorageController.tick(client);
            HuntingTracker.tick(client);
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            PetTracker.onChat(message.getString(), overlay);
            HuntingTracker.onMessage(message, overlay);
            if (!overlay) PetSkinTracker.onChat(message.getString());
        });
        // Compatibility path for chat compactors (for example SkyHanni): GAME
        // and GAME_CANCELED are mutually exclusive for one received message.
        ClientReceiveMessageEvents.GAME_CANCELED.register(HuntingTracker::onMessage);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            StorageController.reset(client);
            InventoryDataManager.saveNow();
            resetTrackers();
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.OVERLAY_MESSAGE,
                Identifier.fromNamespaceAndPath(MOD_ID, "main_hud"), (graphics, tickCounter) -> HudRenderer.render(graphics));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String alias : COMMAND_ALIASES) {
                if (dispatcher.getRoot().getChild(alias) != null) {
                    LOGGER.warn("Skipping client command /{} because another mod already registered it", alias);
                    continue;
                }
                dispatcher.register(ClientCommands.literal(alias).executes(context -> {
                    var client = context.getSource().getClient();
                    client.execute(() -> client.setScreen(new ConfigScreen(client.screen)));
                    return 1;
                }));
            }
            if (dispatcher.getRoot().getChild("acastorage") == null) {
                dispatcher.register(ClientCommands.literal("acastorage").executes(context -> {
                    var connection = context.getSource().getClient().getConnection();
                    if (connection != null) connection.sendCommand("storage");
                    return 1;
                }));
            } else {
                LOGGER.warn("Skipping client command /acastorage because another mod already registered it");
            }
            if (dispatcher.getRoot().getChild("th") == null) {
                dispatcher.register(ClientCommands.literal("th").executes(context -> {
                    var connection = context.getSource().getClient().getConnection();
                    if (connection != null) connection.sendCommand("warp torrhus");
                    return 1;
                }));
            } else {
                LOGGER.warn("Skipping client command /th because another mod already registered it");
            }
            if (dispatcher.getRoot().getChild("helia") == null) {
                dispatcher.register(ClientCommands.literal("helia").executes(context -> {
                    var connection = context.getSource().getClient().getConnection();
                    if (connection != null) connection.sendCommand("chapter torrhus");
                    return 1;
                }));
            } else {
                LOGGER.warn("Skipping client command /helia because another mod already registered it");
            }
        });

        LOGGER.info("QCloudy_Addition initialized in client-side mode");
    }

    public static KeyMapping lockSlotKey() {
        return LOCK_SLOT;
    }

    public static KeyMapping lockItemKey() {
        return LOCK_ITEM;
    }

    public static KeyMapping bindSlotKey() {
        return BIND_SLOT;
    }

    public static boolean matchesChord(ChordAction action, KeyEvent event) {
        return key(action).matches(event)
                && modifierMask(event.modifiers()) == modifiers(action);
    }

    public static boolean matchesBaseKey(ChordAction action, KeyEvent event) {
        return key(action).matches(event);
    }

    public static boolean matchesMouseChord(ChordAction action, MouseButtonEvent event) {
        return key(action).matchesMouse(event)
                && modifierMask(event.modifiers()) == modifiers(action);
    }

    public static boolean matchesBaseMouse(ChordAction action, MouseButtonEvent event) {
        return key(action).matchesMouse(event);
    }

    public static boolean isChordDown(ChordAction action) {
        KeyMapping mapping = key(action);
        return !mapping.isUnbound() && mapping.isDown() && activeModifierMask() == modifiers(action);
    }

    public static String chordName(ChordAction action) {
        if (key(action).isUnbound()) return cloudy.autume.addition.i18n.ModText.get("config.key.unbound");
        int modifiers = modifiers(action);
        StringBuilder result = new StringBuilder();
        appendModifier(result, modifiers, GLFW.GLFW_MOD_CONTROL, "Ctrl");
        appendModifier(result, modifiers, GLFW.GLFW_MOD_SHIFT, "Shift");
        appendModifier(result, modifiers, GLFW.GLFW_MOD_ALT, "Alt");
        appendModifier(result, modifiers, GLFW.GLFW_MOD_SUPER, MacosUtil.IS_MACOS ? "Cmd" : "Super");
        if (!result.isEmpty()) result.append('+');
        result.append(key(action).getTranslatedKeyMessage().getString());
        return result.toString();
    }

    public static void setKeyboardChord(ChordAction action, int keyCode, int modifiers) {
        setChord(action, InputConstants.Type.KEYSYM.getOrCreate(keyCode), modifiers);
    }

    public static void setMouseChord(ChordAction action, int button, int modifiers) {
        if (!HotkeyInputs.supportedMouseButton(button)) return;
        setChord(action, InputConstants.Type.MOUSE.getOrCreate(button), modifiers);
    }

    public static void clearChord(ChordAction action) {
        setChord(action, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_UNKNOWN), 0);
    }

    private static void setChord(ChordAction action, InputConstants.Key input, int modifiers) {
        key(action).setKey(input);
        setModifiers(action, modifierMask(modifiers));
        KeyMapping.resetMapping();
        ConfigManager.save();
    }

    private static KeyMapping key(ChordAction action) {
        return switch (action) {
            case OPEN_CONFIG -> OPEN_CONFIG;
            case LOCK_SLOT -> LOCK_SLOT;
            case LOCK_ITEM -> LOCK_ITEM;
            case BIND_SLOT -> BIND_SLOT;
            case PEEK_CHAT -> PEEK_CHAT;
        };
    }

    private static int modifiers(ChordAction action) {
        var keybinds = ConfigManager.get().keybinds;
        return switch (action) {
            case OPEN_CONFIG -> keybinds.openConfigModifiers;
            case LOCK_SLOT -> keybinds.lockSlotModifiers;
            case LOCK_ITEM -> keybinds.lockItemModifiers;
            case BIND_SLOT -> keybinds.bindSlotModifiers;
            case PEEK_CHAT -> keybinds.peekChatModifiers;
        };
    }

    private static void setModifiers(ChordAction action, int modifiers) {
        var keybinds = ConfigManager.get().keybinds;
        switch (action) {
            case OPEN_CONFIG -> keybinds.openConfigModifiers = modifiers;
            case LOCK_SLOT -> keybinds.lockSlotModifiers = modifiers;
            case LOCK_ITEM -> keybinds.lockItemModifiers = modifiers;
            case BIND_SLOT -> keybinds.bindSlotModifiers = modifiers;
            case PEEK_CHAT -> keybinds.peekChatModifiers = modifiers;
        }
    }

    private static int modifierMask(int modifiers) {
        return modifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_SHIFT
                | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER);
    }

    private static int activeModifierMask() {
        long window = net.minecraft.client.Minecraft.getInstance().getWindow().handle();
        int result = 0;
        if (pressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || pressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            result |= GLFW.GLFW_MOD_CONTROL;
        }
        if (pressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || pressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            result |= GLFW.GLFW_MOD_SHIFT;
        }
        if (pressed(window, GLFW.GLFW_KEY_LEFT_ALT) || pressed(window, GLFW.GLFW_KEY_RIGHT_ALT)) {
            result |= GLFW.GLFW_MOD_ALT;
        }
        if (pressed(window, GLFW.GLFW_KEY_LEFT_SUPER) || pressed(window, GLFW.GLFW_KEY_RIGHT_SUPER)) {
            result |= GLFW.GLFW_MOD_SUPER;
        }
        return result;
    }

    private static boolean pressed(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    private static void appendModifier(StringBuilder result, int value, int flag, String label) {
        if ((value & flag) == 0) return;
        if (!result.isEmpty()) result.append('+');
        result.append(label);
    }

    public enum ChordAction {
        OPEN_CONFIG,
        LOCK_SLOT,
        LOCK_ITEM,
        BIND_SLOT,
        PEEK_CHAT
    }

    private static void resetTrackers() {
        LocationTracker.reset();
        TabListTracker.reset();
        PetTracker.reset();
        PetSkinTracker.reset();
        HuntingTracker.reset();
        SlotLockManager.resetTransient();
    }
}

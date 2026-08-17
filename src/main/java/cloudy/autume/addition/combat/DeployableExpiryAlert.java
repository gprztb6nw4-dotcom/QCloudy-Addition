package cloudy.autume.addition.combat;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.inventory.SkyBlockItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

/** Local Power Orb chat and player-placed Flare lifecycle reminders. */
public final class DeployableExpiryAlert {
    private static final long DUPLICATE_WINDOW_NANOS = 2_000_000_000L;
    private static final Identifier FLARE_PLACEMENT_SOUND =
            Identifier.withDefaultNamespace("entity.firework_rocket.launch");
    private static final DeployableExpirySession FLARE_SESSION = new DeployableExpirySession();
    private static String lastTitle = "";
    private static long lastAlertAt;
    private static ClientLevel trackedLevel;

    private DeployableExpiryAlert() {
    }

    public static void onMessage(Component message, boolean overlay) {
        if (overlay || message == null) return;
        var config = ConfigManager.get().combat;
        if (!config.deployableExpiryAlert || !config.deployablePowerOrbAlerts) return;

        String title = DeployableExpiryParser.alertTitle(message.getString());
        if (title == null) return;
        showAlert(Minecraft.getInstance(), title);
    }

    public static void onItemUse(Minecraft client, ItemStack stack) {
        if (client == null || client.level == null || stack == null) return;
        var config = ConfigManager.get().combat;
        if (!config.deployableExpiryAlert || !config.deployableFlareAlerts) return;
        if (trackedLevel != client.level) {
            FLARE_SESSION.clear();
            trackedLevel = client.level;
        }
        FLARE_SESSION.beginFlarePlacement(SkyBlockItemData.itemId(stack), System.nanoTime());
    }

    /** Confirms a successful Flare placement; an attempted use alone never starts its lifetime. */
    public static void onSound(SoundInstance sound) {
        if (sound == null || !FLARE_PLACEMENT_SOUND.equals(sound.getIdentifier())) return;
        if (Math.abs(sound.getPitch() - 1.0f) > 0.001f || Math.abs(sound.getVolume() - 3.0f) > 0.001f) return;
        var config = ConfigManager.get().combat;
        if (!config.deployableExpiryAlert || !config.deployableFlareAlerts) return;
        FLARE_SESSION.confirmFlarePlacement(System.nanoTime());
    }

    public static void tick(Minecraft client) {
        if (client == null) return;
        if (trackedLevel != client.level) {
            FLARE_SESSION.clear();
            trackedLevel = client.level;
            return;
        }
        var config = ConfigManager.get().combat;
        if (!config.deployableExpiryAlert || !config.deployableFlareAlerts) {
            FLARE_SESSION.clear();
            return;
        }
        String title = FLARE_SESSION.pollExpired(System.nanoTime());
        if (title != null) showAlert(client, title);
    }

    private static void showAlert(Minecraft client, String title) {
        if (client.player == null) return;
        long now = System.nanoTime();
        if (title.equals(lastTitle) && now - lastAlertAt < DUPLICATE_WINDOW_NANOS) return;
        lastTitle = title;
        lastAlertAt = now;

        var config = ConfigManager.get().combat;
        if (config.deployableExpiryCenterText) {
            MinecraftClientCompat.showTitle(client,
                    Component.literal(title).withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                    Component.empty(), 6, 42, 10);
        }
        var audio = config.deployableExpiryAudio;
        if (audio.sound && audio.volume > 0) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), audio.volume / 100.0f, 1.15f);
        }
    }

    public static void reset() {
        FLARE_SESSION.clear();
        trackedLevel = null;
        lastTitle = "";
        lastAlertAt = 0L;
    }
}

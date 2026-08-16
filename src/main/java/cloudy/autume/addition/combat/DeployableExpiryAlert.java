package cloudy.autume.addition.combat;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

/** Center-screen reminder driven only by received player-owned despawn chat. */
public final class DeployableExpiryAlert {
    private static final long DUPLICATE_WINDOW_MS = 2_000L;
    private static String lastTitle = "";
    private static long lastAlertAt;

    private DeployableExpiryAlert() {
    }

    public static void onMessage(Component message, boolean overlay) {
        if (overlay || message == null) return;
        var config = ConfigManager.get().combat;
        if (!config.deployableExpiryAlert) return;

        String title = DeployableExpiryParser.alertTitle(message.getString());
        if (title == null) return;
        long now = System.currentTimeMillis();
        if (title.equals(lastTitle) && now - lastAlertAt < DUPLICATE_WINDOW_MS) return;
        lastTitle = title;
        lastAlertAt = now;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        MinecraftClientCompat.showTitle(client,
                Component.literal(title).withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                Component.empty(), 6, 42, 10);
        var audio = config.deployableExpiryAudio;
        if (audio.sound && audio.volume > 0) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), audio.volume / 100.0f, 1.15f);
        }
    }

    public static void reset() {
        lastTitle = "";
        lastAlertAt = 0L;
    }
}

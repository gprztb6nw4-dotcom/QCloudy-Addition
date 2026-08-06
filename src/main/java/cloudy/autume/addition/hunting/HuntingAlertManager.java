package cloudy.autume.addition.hunting;

import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.HashMap;
import java.util.Map;

/** Central title notifications shared by every Hunting warning and prompt. */
public final class HuntingAlertManager {
    private static final long DEFAULT_COOLDOWN_MS = 2_000L;
    private static final Map<String, Long> LAST_ALERTS = new HashMap<>();

    private HuntingAlertManager() {
    }

    public static void show(Channel channel, String key, String title, String subtitle) {
        show(channel, key, title, subtitle, DEFAULT_COOLDOWN_MS);
    }

    public static void show(Channel channel, String key, String title, String subtitle, long cooldownMs) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        long now = System.currentTimeMillis();
        Long previous = LAST_ALERTS.get(key);
        if (previous != null && now - previous < cooldownMs) return;
        LAST_ALERTS.put(key, now);

        client.gui.setTimes(6, 42, 10);
        client.gui.setTitle(Component.literal(title).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
        client.gui.setSubtitle(Component.literal(subtitle).withStyle(ChatFormatting.YELLOW));
        var config = ConfigManager.get().hunting;
        var audio = channel.audio(config);
        if (config.alertSound && audio.sound && audio.volume > 0) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), audio.volume / 100.0f, 1.15f);
        }
    }

    public static void playSound(Channel channel, float pitch) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        var config = ConfigManager.get().hunting;
        var audio = channel.audio(config);
        if (config.alertSound && audio.sound && audio.volume > 0) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), audio.volume / 100.0f, pitch);
        }
    }

    public enum Channel {
        TREE_GIFT, SPARKLING, CRITTER_BEHAVIOR, WUMPA, DOOMSPIRAL, COLD, FLOOR_DROP, BENEFACTOR,
        LASSO_REEL, WARDEN_READY;

        private cloudy.autume.addition.config.ModConfig.AlertAudio audio(
                cloudy.autume.addition.config.ModConfig.Hunting config) {
            return switch (this) {
                case TREE_GIFT -> config.treeGiftAudio;
                case SPARKLING -> config.sparklingAudio;
                case CRITTER_BEHAVIOR -> config.critterBehaviorAudio;
                case WUMPA -> config.wumpaAudio;
                case DOOMSPIRAL -> config.doomspiralAudio;
                case COLD -> config.coldAudio;
                case FLOOR_DROP -> config.floorDropAudio;
                case BENEFACTOR -> config.benefactorAudio;
                case LASSO_REEL -> config.lassoReelAudio;
                case WARDEN_READY -> config.wardenReadyAudio;
            };
        }
    }

    public static void reset() {
        LAST_ALERTS.clear();
    }
}

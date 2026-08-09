package cloudy.autume.addition.inventory;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.config.ModConfig;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;

/** Replaces only the local teleport sound that belongs to the held AOTE/AOTV. */
public final class TeleportSoundCustomizer {
    private static final Set<String> TELEPORT_SWORDS = Set.of("ASPECT_OF_THE_END", "ASPECT_OF_THE_VOID");
    private static final ThreadLocal<Boolean> PLAYING_REPLACEMENT = ThreadLocal.withInitial(() -> false);

    private TeleportSoundCustomizer() {
    }

    /**
     * @return true when the original sound must be cancelled because a custom replacement was played.
     */
    public static boolean customize(SoundInstance sound) {
        ModConfig.Inventory config = ConfigManager.get().inventory;
        if (PLAYING_REPLACEMENT.get() || !config.teleportSoundCustomization || !LocationTracker.isSkyBlock()) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !TELEPORT_SWORDS.contains(SkyBlockItemData.itemId(client.player.getMainHandItem()))) {
            return false;
        }

        double dx = sound.getX() - client.player.getX();
        double dy = sound.getY() - client.player.getY();
        double dz = sound.getZ() - client.player.getZ();
        if (!sound.isRelative() && dx * dx + dy * dy + dz * dz > 9.0) return false;

        TeleportKind kind = identify(sound);
        if (kind == null || "VANILLA".equals(mode(config, kind))) return false;

        SoundEvent replacement = soundEvent(customSound(config, kind));
        float volume = volume(config, kind) / 100.0f;
        SimpleSoundInstance custom = new SimpleSoundInstance(replacement, sound.getSource(), volume, 1.0f,
                SoundInstance.createUnseededRandom(), sound.getX(), sound.getY(), sound.getZ());
        PLAYING_REPLACEMENT.set(true);
        try {
            client.getSoundManager().play(custom);
        } finally {
            PLAYING_REPLACEMENT.set(false);
        }
        return true;
    }

    private static TeleportKind identify(SoundInstance sound) {
        String path = sound.getIdentifier().getPath();
        var resolved = sound.getSound();
        String resolvedPath = resolved == null ? "" : resolved.getLocation().getPath();
        if (matches(path, resolvedPath, "enderman.teleport", "endermen/portal")) {
            return TeleportKind.INSTANT_TRANSMISSION;
        }
        if (matches(path, resolvedPath, "ender_dragon.hurt", "enderdragon/hit")) {
            return TeleportKind.ETHERWARP;
        }
        return null;
    }

    private static boolean matches(String path, String resolvedPath, String first, String second) {
        return path.contains(first) || path.contains(second)
                || resolvedPath.contains(first) || resolvedPath.contains(second);
    }

    private static String mode(ModConfig.Inventory config, TeleportKind kind) {
        return kind == TeleportKind.INSTANT_TRANSMISSION
                ? config.instantTransmissionSoundMode : config.etherwarpSoundMode;
    }

    private static String customSound(ModConfig.Inventory config, TeleportKind kind) {
        return kind == TeleportKind.INSTANT_TRANSMISSION
                ? config.instantTransmissionCustomSound : config.etherwarpCustomSound;
    }

    private static int volume(ModConfig.Inventory config, TeleportKind kind) {
        return kind == TeleportKind.INSTANT_TRANSMISSION
                ? config.instantTransmissionSoundVolume : config.etherwarpSoundVolume;
    }

    private static SoundEvent soundEvent(String name) {
        return switch (name) {
            case "ENDERMAN" -> SoundEvents.ENDERMAN_TELEPORT;
            case "AMETHYST" -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case "ORB" -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case "PORTAL" -> SoundEvents.END_PORTAL_FRAME_FILL;
            case "SHULKER" -> SoundEvents.SHULKER_TELEPORT;
            default -> SoundEvents.CHORUS_FRUIT_TELEPORT;
        };
    }

    private enum TeleportKind {
        INSTANT_TRANSMISSION,
        ETHERWARP
    }
}

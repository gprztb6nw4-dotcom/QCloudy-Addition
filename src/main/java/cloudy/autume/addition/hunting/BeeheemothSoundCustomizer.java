package cloudy.autume.addition.hunting;

import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.RandomSource;

import java.util.Locale;

/** Scales only Bee sounds spatially associated with the loaded scale-9 Beeheemoth. */
public final class BeeheemothSoundCustomizer {
    private BeeheemothSoundCustomizer() {
    }

    public static SoundInstance customize(SoundInstance original) {
        if (original == null || original.isRelative()) return original;
        var config = ConfigManager.get().hunting;
        if (!config.beeheemothHelper || !beeSound(original)) return original;
        if (!HuntingTracker.beeheemothSoundSourceNear(original.getX(), original.getY(), original.getZ())) {
            return original;
        }

        float factor = config.beeheemothSound ? config.beeheemothSoundVolume / 100.0f : 0.0f;
        if (factor == 1.0f) return original;
        float volume = original.getVolume() * factor;
        var resolved = original.getSound();
        return new SimpleSoundInstance(
                original.getIdentifier(), original.getSource(), volume, original.getPitch(),
                RandomSource.create(), original.isLooping(), original.getDelay(), original.getAttenuation(),
                original.getX(), original.getY(), original.getZ(), false
        ) {
            {
                this.sound = resolved;
            }

            @Override
            public float getVolume() {
                return volume;
            }
        };
    }

    static boolean beeSoundPaths(String eventPath, String resolvedPath) {
        String event = eventPath == null ? "" : eventPath.toLowerCase(Locale.ROOT);
        String resolved = resolvedPath == null ? "" : resolvedPath.toLowerCase(Locale.ROOT);
        return event.startsWith("entity.bee.")
                || resolved.startsWith("mob/bee/")
                || resolved.contains("/bee/");
    }

    private static boolean beeSound(SoundInstance sound) {
        String eventPath = sound.getIdentifier().getPath();
        var resolved = sound.getSound();
        String resolvedPath = resolved == null ? "" : resolved.getLocation().getPath();
        return beeSoundPaths(eventPath, resolvedPath);
    }
}

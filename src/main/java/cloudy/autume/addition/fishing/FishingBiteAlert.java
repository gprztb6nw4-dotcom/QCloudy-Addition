package cloudy.autume.addition.fishing;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.FishingHook;

/** Plays a local sound once when the local player's hook receives Hypixel's bite marker. */
public final class FishingBiteAlert {
    private static final SoundEvent BITE_SOUND = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath(QCloudyAdditionClient.MOD_ID, "fishing_bite"));
    private static final FishingBiteSession SESSION = new FishingBiteSession();
    private static final double MARKER_SEARCH_RADIUS = 4.0;

    private FishingBiteAlert() {
    }

    public static void tick(Minecraft client) {
        var config = ConfigManager.get().fishing;
        if (!config.biteAlert || !LocationTracker.isSkyBlock()
                || client.player == null || client.level == null) {
            SESSION.reset();
            return;
        }

        FishingHook hook = client.player.fishing;
        if (hook == null) {
            SESSION.reset();
            return;
        }

        boolean biteMarkerVisible = !client.level.getEntities(hook,
                hook.getBoundingBox().inflate(MARKER_SEARCH_RADIUS), FishingBiteAlert::isBiteMarker).isEmpty();
        if (!SESSION.shouldPlay(hook.getId(), biteMarkerVisible)) return;

        float volume = config.biteAlertVolume / 100.0F;
        if (volume <= 0.0F) return;
        client.getSoundManager().play(SimpleSoundInstance.forUI(BITE_SOUND, 1.0F, volume));
    }

    static boolean isBiteMarker(Entity entity) {
        return entity instanceof ArmorStand
                && entity.isInvisible()
                && entity.hasCustomName()
                && entity.isCustomNameVisible()
                && "!!!".equals(entity.getCustomName().getString());
    }

    public static void reset() {
        SESSION.reset();
    }
}

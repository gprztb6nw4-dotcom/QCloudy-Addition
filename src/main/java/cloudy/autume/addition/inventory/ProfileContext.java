package cloudy.autume.addition.inventory;

import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.Minecraft;

import java.util.Locale;

public final class ProfileContext {
    private ProfileContext() {
    }

    public static String key(Minecraft client) {
        String account = client.player == null ? "offline" : client.player.getUUID().toString();
        String profile = normalize(LocationTracker.profileName());
        return account + "_" + profile;
    }

    static String dimensionKey() {
        return LocationTracker.isRift() ? "rift" : "overworld";
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "_");
    }
}

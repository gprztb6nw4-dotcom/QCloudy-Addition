package cloudy.autume.addition.hunting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Pose;

/** Pure client-side rules for the Doomspiral Warden's visible capture cooldown. */
public final class WardenCooldownSupport {
    public static final int CAPTURE_COOLDOWN_TICKS = 140;

    private static final int ARENA_X_MIN = -18;
    private static final int ARENA_X_MAX = 24;
    private static final int ARENA_Y_MIN = 45;
    private static final int ARENA_Y_MAX = 62;
    private static final int ARENA_Z_MIN = -39;
    private static final int ARENA_Z_MAX = -13;

    private WardenCooldownSupport() {
    }

    static boolean inArena(BlockPos position) {
        return position.getX() >= ARENA_X_MIN && position.getX() <= ARENA_X_MAX
                && position.getY() >= ARENA_Y_MIN && position.getY() <= ARENA_Y_MAX
                && position.getZ() >= ARENA_Z_MIN && position.getZ() <= ARENA_Z_MAX;
    }

    static boolean captureReady(int entityTicks, int latencyMs, Pose pose) {
        if (pose == Pose.EMERGING || pose == Pose.DIGGING) return false;
        return Math.max(0, entityTicks) + latencyTicks(latencyMs) >= CAPTURE_COOLDOWN_TICKS;
    }

    static int latencyTicks(int latencyMs) {
        return latencyMs <= 0 ? 0 : (int) Math.ceil(latencyMs / 50.0);
    }
}

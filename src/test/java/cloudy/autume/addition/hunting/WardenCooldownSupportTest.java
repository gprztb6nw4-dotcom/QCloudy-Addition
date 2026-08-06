package cloudy.autume.addition.hunting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Pose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class WardenCooldownSupportTest {
    @Test
    void compensatesReceivedEntityAgeWithPlayerLatency() {
        assertFalse(WardenCooldownSupport.captureReady(139, 0, Pose.STANDING));
        assertTrue(WardenCooldownSupport.captureReady(140, 0, Pose.STANDING));
        assertEquals(2, WardenCooldownSupport.latencyTicks(100));
        assertTrue(WardenCooldownSupport.captureReady(138, 100, Pose.STANDING));
        assertEquals(0, WardenCooldownSupport.latencyTicks(-1));
    }

    @Test
    void emergenceAndDiggingNeverReportCaptureReady() {
        assertFalse(WardenCooldownSupport.captureReady(500, 500, Pose.EMERGING));
        assertFalse(WardenCooldownSupport.captureReady(500, 500, Pose.DIGGING));
    }

    @Test
    void acceptsOnlyTheSuppliedReferenceModsSafariWardenArena() {
        assertTrue(WardenCooldownSupport.inArena(new BlockPos(-18, 45, -39)));
        assertTrue(WardenCooldownSupport.inArena(new BlockPos(24, 62, -13)));
        assertFalse(WardenCooldownSupport.inArena(new BlockPos(25, 62, -13)));
        assertFalse(WardenCooldownSupport.inArena(new BlockPos(0, 63, -20)));
    }
}

package cloudy.autume.addition.hunting;

import cloudy.autume.addition.tracker.IslandArea;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class HuntingWorldRendererTest {
    @Test
    void containsEveryOfficialFairySoulCoordinate() {
        assertEquals(12, HuntingWorldRenderer.TORRHUS_FAIRY_SOULS.size());
        assertEquals(4, HuntingWorldRenderer.SAFARI_FAIRY_SOULS.size());
        assertEquals(16, HuntingWorldRenderer.TORRHUS_FAIRY_SOULS.stream().distinct().count()
                + HuntingWorldRenderer.SAFARI_FAIRY_SOULS.stream().distinct().count());
        assertTrue(HuntingWorldRenderer.SAFARI_FAIRY_SOULS.stream()
                .anyMatch(pos -> pos.getX() == -162 && pos.getY() == 60 && pos.getZ() == 63));
    }

    @Test
    void fairySoulStorageKeysSeparateIslands() {
        BlockPos pos = new BlockPos(5, 106, 18);
        assertEquals("CRITTER_SAFARI:5,106,18", HuntingTracker.fairySoulKey(IslandArea.CRITTER_SAFARI, pos));
        assertEquals("TORRHUS_CANYON:5,106,18", HuntingTracker.fairySoulKey(IslandArea.TORRHUS_CANYON, pos));
    }
}

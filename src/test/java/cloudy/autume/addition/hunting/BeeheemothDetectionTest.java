package cloudy.autume.addition.hunting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class BeeheemothDetectionTest {
    @Test
    void acceptsOnlyTheReferenceModsNineScaleSignature() {
        assertTrue(HuntingTracker.beeheemothScale(9.0f));
        assertTrue(HuntingTracker.beeheemothScale(9.005f));
        assertFalse(HuntingTracker.beeheemothScale(8.98f));
        assertFalse(HuntingTracker.beeheemothScale(1.0f));
    }

    @Test
    void recognizesOnlyBeeSoundIdentifiers() {
        assertTrue(BeeheemothSoundCustomizer.beeSoundPaths("entity.bee.hurt", ""));
        assertTrue(BeeheemothSoundCustomizer.beeSoundPaths("unknown", "mob/bee/say1"));
        assertFalse(BeeheemothSoundCustomizer.beeSoundPaths("entity.enderman.teleport", "mob/endermen/portal"));
    }
}

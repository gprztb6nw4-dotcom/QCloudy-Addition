package cloudy.autume.addition.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PetLevelingTest {
    @Test
    void calculatesAncientGoldenDragonCosmeticOverflowLevels() {
        var pet = new PetTracker.PetSnapshot("Golden Dragon", "200", "", "", "", true, 0xFFAA00);
        double total = PetLeveling.maximumXp(pet) + 3 * 1_886_700.0 + 100.0;
        assertEquals(203, PetLeveling.cosmeticLevel(pet, total));
    }

    @Test
    void standardPetUsesRarityAdjustedLevelCurve() {
        var pet = new PetTracker.PetSnapshot("Enderman", "50", "12,000", "13,300", "90.2",
                false, 0xAA00AA);
        PetLeveling.Progress progress = PetLeveling.progress(pet);
        assertEquals(100, progress.maxLevel());
        assertTrue(progress.current() > 0);
        assertTrue(progress.current() < progress.maximum());
    }

    @Test
    void dragonPetsUseLevelTwoHundredCurve() {
        var pet = new PetTracker.PetSnapshot("Golden Dragon", "101", "0", "5,555", "0.0",
                false, 0xFFAA00);
        PetLeveling.Progress progress = PetLeveling.progress(pet);
        assertEquals(200, progress.maxLevel());
        assertTrue(progress.maximum() > 100_000_000);
        assertTrue(progress.current() < progress.maximum());
    }
}

package cloudy.autume.addition.hud;

import cloudy.autume.addition.tracker.PetTracker;
import cloudy.autume.addition.tracker.PetLeveling;
import cloudy.autume.addition.tracker.TabListTracker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class HudRendererTest {
    @Test
    void preservesRightMarginAcrossScaleChanges() {
        assertEquals(1724, HudRenderer.resolveX(-196, 188, 1920, 1.0f));
        assertEquals(1630, HudRenderer.resolveX(-196, 188, 1920, 1.5f));
    }

    @Test
    void clampsAbsolutePanelsInsideTheScreen() {
        assertEquals(720, HudRenderer.resolveX(900, 200, 920, 1.0f));
        assertEquals(0, HudRenderer.resolveX(40, 200, 150, 1.0f));
    }

    @Test
    void clampsPanelsVerticallyAfterScaleChanges() {
        assertEquals(720, HudRenderer.resolveY(720, 200, 920, 1.0f));
        assertEquals(620, HudRenderer.resolveY(720, 200, 920, 1.5f));
        assertEquals(0, HudRenderer.resolveY(40, 200, 150, 1.0f));
        assertEquals(0, HudRenderer.resolveY(-20, 43, 240, 1.0f));
    }

    @Test
    void miningHeightTracksCommissionCount() {
        assertEquals(115, HudRenderer.miningHeightForCommissionCount(0));
        assertEquals(235, HudRenderer.miningHeightForCommissionCount(6));
        assertEquals(104, HudRenderer.miningHeightForCommissionCount(0, false));
    }

    @Test
    void formatsCommissionBarsInPercentAndNumericModes() {
        var commission = new TabListTracker.CommissionProgress("Lava Springs Mithril", 80.0, 200, 250);
        assertEquals("80.0%", HudRenderer.commissionProgressText(commission, "PERCENT"));
        assertEquals("200/250", HudRenderer.commissionProgressText(commission, "NUMERIC"));
        var unknown = new TabListTracker.CommissionProgress("Unknown", 33.3, -1, -1);
        assertEquals("33.3%", HudRenderer.commissionProgressText(unknown, "NUMERIC"));
    }

    @Test
    void sizesCommissionBarsToTheWidestFullyRenderedTask() {
        assertEquals(146, HudRenderer.fittedCommissionBarWidth(146, 31));
        assertEquals(80, HudRenderer.fittedCommissionBarWidth(30, 31));
        assertEquals(208, HudRenderer.fittedCommissionBarWidth(260, 31));
        assertEquals(98, HudRenderer.fittedCommissionBarWidth(60, 90));
    }

    @Test
    void crimsonTaskHeightTracksReceivedRows() {
        assertEquals(47, HudRenderer.crimsonTaskHeight(0));
        assertEquals(95, HudRenderer.crimsonTaskHeight(5));
    }

    @Test
    void mapsCoordinatesToTheInsetTextureBounds() {
        assertEquals(12.0f, HudRenderer.mapCoordinate(-230, -230, 210));
        assertEquals(100.0f, HudRenderer.mapCoordinate(-10, -230, 210));
        assertEquals(188.0f, HudRenderer.mapCoordinate(210, -230, 210));
    }

    @Test
    void hidesProgressToMaxWhenPetIsAlreadyMaxLevel() {
        var maxed = new PetTracker.PetSnapshot("Bee", "100", "25.4m", "25.4m", "100", true, 0xFF55FF);
        assertEquals(false, HudRenderer.shouldShowMaxProgress(true, maxed));
        var leveling = new PetTracker.PetSnapshot("Bee", "99", "1.0m", "1.2m", "83", false, 0xFF55FF);
        assertEquals(true, HudRenderer.shouldShowMaxProgress(true, leveling));
    }

    @Test
    void onlyShowsCosmeticOverflowLevelForSupportedSkinWhenEnabled() {
        var pet = new PetTracker.PetSnapshot("Golden Dragon", "200", "", "", "", true, 0xFFAA00);
        double total = PetLeveling.maximumXp(pet) + 5 * 1_886_700.0;
        assertEquals("205", HudRenderer.displayedLevel(true, pet, "golden_dragon_ancient", total));
        assertEquals("200", HudRenderer.displayedLevel(false, pet, "golden_dragon_ancient", total));
        assertEquals("200", HudRenderer.displayedLevel(true, pet, "golden_dragon_anubis", total));
    }

    @Test
    void ancientSkinDoesNotForceAnUnmaxedGoldenDragonToLevelTwoHundred() {
        var pet = new PetTracker.PetSnapshot("Golden Dragon", "141", "1.8m", "1.9m", "95.6",
                false, 0xFFAA00);
        assertEquals("141", HudRenderer.displayedLevel(true, pet, "golden_dragon_ancient", 130_700_000.0));
    }
}

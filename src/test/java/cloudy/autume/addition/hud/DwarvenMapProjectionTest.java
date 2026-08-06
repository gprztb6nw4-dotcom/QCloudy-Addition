package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DwarvenMapProjectionTest {
    @Test
    void visibleLocationSelectsTheMatchingSchematicRegion() {
        var village = DwarvenMapProjection.project(9.5, 211, -111, "⏣ Dwarven Village");
        assertEquals(104.5f, village.x(), 0.6f);
        assertEquals(30.0f, village.y(), 0.6f);

        var mist = DwarvenMapProjection.project(54.5, 87, 91.5, "⏣ The Mist");
        assertEquals(114.5f, mist.x(), 0.6f);
        assertEquals(132.0f, mist.y(), 0.6f);
    }

    @Test
    void heightDisambiguatesRoyalPalaceFromLowerRoyalMines() {
        var palace = DwarvenMapProjection.project(125, 196, 140, "⏣ Dwarven Mines");
        assertTrue(palace.y() >= 145, "Palace should project to the detached lower-right block");

        var mines = DwarvenMapProjection.project(125, 136, 90, "⏣ Dwarven Mines");
        assertTrue(mines.x() >= 155 && mines.y() <= 120,
                "Royal Mines should remain in the gold right-side block");
    }

    @Test
    void projectionClampsMarkersInsideTheirRegion() {
        var forge = DwarvenMapProjection.project(1_000, 150, -1_000, "⏣ The Forge");
        assertEquals(119.0f, forge.x());
        assertEquals(55.0f, forge.y());
    }
}

package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DwarvenMapProjectionTest {
    @Test
    void usesOneContinuousApproximateTransformAcrossTheWholeMap() {
        var bridgeApproach = DwarvenMapProjection.project(54.5, 91.5);
        var oneBlockEast = DwarvenMapProjection.project(55.5, 91.5);
        var oneBlockSouth = DwarvenMapProjection.project(54.5, 92.5);

        assertTrue(oneBlockEast.x() > bridgeApproach.x());
        assertEquals(bridgeApproach.y(), oneBlockEast.y());
        assertEquals(bridgeApproach.x(), oneBlockSouth.x());
        assertTrue(oneBlockSouth.y() > bridgeApproach.y());
    }

    @Test
    void mistAndItsOverheadBridgesShareTheSameXzPoint() {
        // There is deliberately no height or sub-location parameter. The bridge and The Mist below
        // it therefore remain on the same approximate background point instead of jumping regions.
        var mist = DwarvenMapProjection.project(54.5, 91.5);
        var overheadBridge = DwarvenMapProjection.project(54.5, 91.5);

        assertEquals(mist, overheadBridge);
        assertProjectedInside(mist, 71, 141, 106, 130, "The Mist / overhead bridge");
    }

    @Test
    void representativeLocationsRemainOnTheirApproximateBackgroundAreas() {
        assertProjectedInside(DwarvenMapProjection.project(9.5, -111), 82, 126, 9, 29, "Village");
        assertProjectedInside(DwarvenMapProjection.project(-109, -46), 28, 55, 36, 55, "Upper Mines");
        assertProjectedInside(DwarvenMapProjection.project(-75.5, -20), 47, 77, 48, 84, "Rampart Quarry");
        assertProjectedInside(DwarvenMapProjection.project(1.5, -4), 92, 105, 62, 73, "Forge");
        assertProjectedInside(DwarvenMapProjection.project(45, -7), 110, 135, 60, 78, "Lava Springs");
        assertProjectedInside(DwarvenMapProjection.project(32, 33.5), 94, 136, 82, 98, "Cliffside");
        assertProjectedInside(DwarvenMapProjection.project(-126, 68), 23, 45, 74, 125, "Far Reserve");
        assertProjectedInside(DwarvenMapProjection.project(-108, 123), 31, 62, 124, 141, "Goblin Burrows");
        assertProjectedInside(DwarvenMapProjection.project(27.5, 148.5), 79, 122, 136, 150, "Ice Wall");
        assertProjectedInside(DwarvenMapProjection.project(124, 91.5), 145, 176, 88, 120, "Royal Mines");
        assertProjectedInside(DwarvenMapProjection.project(129, 159), 141, 181, 138, 166, "Royal Palace");
    }

    @Test
    void outsideCoordinatesClampToAVisibleSafeInset() {
        assertEquals(new DwarvenMapProjection.Point(18.0f, 7.0f),
                DwarvenMapProjection.project(-10_000, -10_000));
        assertEquals(new DwarvenMapProjection.Point(181.0f, 161.0f),
                DwarvenMapProjection.project(10_000, 10_000));
    }

    private static void assertProjectedInside(DwarvenMapProjection.Point point,
                                              float minX, float maxX, float minY, float maxY,
                                              String label) {
        assertTrue(point.x() >= minX && point.x() <= maxX, label + " X: " + point.x());
        assertTrue(point.y() >= minY && point.y() <= maxY, label + " Z: " + point.y());
    }
}

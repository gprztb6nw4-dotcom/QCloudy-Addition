package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DwarvenMapProjectionTest {
    @Test
    void visibleLocationSelectsTheMatchingSchematicRegion() {
        var village = DwarvenMapProjection.project(9.5, -111, "⏣ Dwarven Village");
        assertEquals(104.0f, village.x(), 0.6f);
        assertEquals(19.0f, village.y(), 0.6f);

        var mist = DwarvenMapProjection.project(54.5, 91.5, "⏣ The Mist");
        assertEquals(106.0f, mist.x(), 0.6f);
        assertEquals(118.0f, mist.y(), 0.6f);
    }

    @Test
    void xzFallbackSeparatesRoyalPalaceFromRoyalMinesWithoutUsingHeight() {
        var palace = DwarvenMapProjection.project(125, 140, "⏣ Dwarven Mines");
        assertTrue(palace.y() >= 136, "Palace should project to the detached lower-right block");

        var mines = DwarvenMapProjection.project(125, 90, "⏣ Dwarven Mines");
        assertTrue(mines.x() >= 151 && mines.y() <= 115,
                "Royal Mines should remain in the gold right-side block");
    }

    @Test
    void projectionClampsMarkersInsideTheirRegion() {
        var forge = DwarvenMapProjection.project(1_000, -1_000, "⏣ The Forge");
        assertEquals(105.0f, forge.x());
        assertEquals(62.0f, forge.y());
    }

    @Test
    void everyNamedRegionUsesTheReplacementMapCalibration() {
        assertProjectedInside("Upper Mines", -109, -46, 28, 55, 36, 55);
        assertProjectedInside("Rampart's Quarry", -75.5, -20, 47, 77, 48, 84);
        assertProjectedInside("The Forge", 1.5, -4, 92, 105, 62, 73);
        assertProjectedInside("Lava Springs", 45, -7, 120, 133, 64, 74);
        assertProjectedInside("Cliffside Veins", 32, 33.5, 94, 136, 86, 96);
        assertProjectedInside("Far Reserve", -126, 68, 23, 35, 74, 125);
        assertProjectedInside("Goblin Burrows", -108, 123, 31, 62, 128, 139);
        assertProjectedInside("The Mist", 54.5, 91.5, 71, 141, 106, 130);
        assertProjectedInside("Great Ice Wall", 27.5, 148.5, 79, 122, 138, 148);
        assertProjectedInside("Royal Mines", 124, 91.5, 152, 176, 88, 113);
        assertProjectedInside("Royal Palace", 129, 159, 141, 181, 143, 163);
        assertProjectedInside("Dwarven Village", 9.5, -111, 82, 126, 9, 29);
    }

    @Test
    void xzCalibrationNeverPlacesMarkerCentreInTransparentMapSpace() throws IOException {
        try (var stream = getClass().getResourceAsStream(
                "/assets/qcloudy_addition/textures/gui/dwarven_mines.png")) {
            var image = ImageIO.read(stream);
            assertTrue(image != null, "Dwarven map resource");
            Calibration[] calibrations = {
                    new Calibration("Royal Palace", 99, 159, 115, 203),
                    new Calibration("Dwarven Village", -45, 64, -154, -68),
                    new Calibration("Upper Mines", -155, -63, -84, -8),
                    new Calibration("Rampart's Quarry", -124, -27, -70, 30),
                    new Calibration("The Forge", -23, 26, -32, 24),
                    new Calibration("Lava Springs", 8, 82, -20, 6),
                    new Calibration("Cliffside Veins", -33, 97, 7, 60),
                    new Calibration("Far Reserve", -157, -95, 5, 131),
                    new Calibration("Goblin Burrows", -159, -57, 91, 155),
                    new Calibration("The Mist", -19, 128, 54, 129),
                    new Calibration("Great Ice Wall", -29, 84, 130, 167),
                    new Calibration("Royal Mines", 92, 156, 29, 154)
            };
            for (Calibration calibration : calibrations) {
                for (int zStep = 0; zStep <= 8; zStep++) {
                    for (int xStep = 0; xStep <= 8; xStep++) {
                        double x = lerp(calibration.minX, calibration.maxX, xStep / 8.0);
                        double z = lerp(calibration.minZ, calibration.maxZ, zStep / 8.0);
                        var point = DwarvenMapProjection.project(x, z, "⏣ " + calibration.location);
                        int alpha = image.getRGB(Math.round(point.x()), Math.round(point.y())) >>> 24;
                        assertTrue(alpha >= 200, calibration.location + " at " + x + ", " + z);
                    }
                }
            }
        }
    }

    private static void assertProjectedInside(String location, double x, double z,
                                              float minX, float maxX, float minY, float maxY) {
        var point = DwarvenMapProjection.project(x, z, "⏣ " + location);
        assertTrue(point.x() >= minX && point.x() <= maxX, location + " X");
        assertTrue(point.y() >= minY && point.y() <= maxY, location + " Y");
    }

    private static double lerp(double start, double end, double amount) {
        return start + (end - start) * amount;
    }

    private record Calibration(String location, double minX, double maxX, double minZ, double maxZ) {
    }
}

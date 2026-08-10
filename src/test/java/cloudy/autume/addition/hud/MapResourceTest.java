package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MapResourceTest {
    private static final String[] MAPS = {
            "dwarven_mines.png",
            "glacite_tunnels_low.png",
            "glacite_tunnels_middle.png",
            "glacite_tunnels_high.png"
    };

    @Test
    void englishMapsExistAndLeaveBackgroundOpacityToTheHud() throws IOException {
        for (String name : MAPS) {
            try (var stream = getClass().getResourceAsStream(
                    "/assets/qcloudy_addition/textures/gui/" + name)) {
                assertNotNull(stream, name);
                var image = ImageIO.read(stream);
                assertNotNull(image, name);
                assertEquals(200, image.getWidth(), name);
                assertEquals(200, image.getHeight(), name);
                assertEquals(0, image.getRGB(199, 199) >>> 24, name);
            }
        }
    }

    @Test
    void replacementDwarvenMapContainsEveryCalibratedRegionColour() throws IOException {
        try (var stream = getClass().getResourceAsStream(
                "/assets/qcloudy_addition/textures/gui/dwarven_mines.png")) {
            assertNotNull(stream);
            var image = ImageIO.read(stream);
            assertNotNull(image);
            int[] expectedColours = {
                    0xFF00A113, // Village
                    0xFF946800, // Upper Mines
                    0xFF004F87, // Rampart Quarry
                    0xFF808080, // Forge
                    0xFF7A201C, // Lava Springs
                    0xFF797563, // Cliffside
                    0xFF8F876D, // Far Reserve
                    0xFF008409, // Goblin Burrows
                    0xFFBCF3FF, // The Mist
                    0xFF00EAFF, // Ice Wall
                    0xFFF5CA00, // Royal Mines
                    0xFF6B6B6B  // Royal Palace
            };
            for (int expected : expectedColours) {
                boolean found = false;
                for (int y = 0; y < image.getHeight() && !found; y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (image.getRGB(x, y) == expected) {
                            found = true;
                            break;
                        }
                    }
                }
                assertTrue(found, "Missing calibrated region colour: " + Integer.toHexString(expected));
            }
        }
    }
}

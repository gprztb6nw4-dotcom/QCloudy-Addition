package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}

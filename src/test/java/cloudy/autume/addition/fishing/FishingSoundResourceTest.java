package cloudy.autume.addition.fishing;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FishingSoundResourceTest {
    @Test
    void bundledSoundDefinitionReferencesTheFishingCue() throws IOException {
        try (var stream = getClass().getResourceAsStream("/assets/qcloudy_addition/sounds.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("\"fishing_bite\""));
            assertTrue(json.contains("\"qcloudy_addition:fishing/ciallo\""));
        }
    }

    @Test
    void fishingCueIsARealBundledOggVorbisResource() throws IOException {
        try (var stream = getClass().getResourceAsStream(
                "/assets/qcloudy_addition/sounds/fishing/ciallo.ogg")) {
            assertNotNull(stream);
            byte[] sound = stream.readAllBytes();
            assertTrue(sound.length > 4_096);
            assertEquals("OggS", new String(sound, 0, 4, StandardCharsets.US_ASCII));
        }
    }
}

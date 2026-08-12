package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ColorPickerTest {
    @Test
    void rgbHsvRoundTripKeepsPresetColors() {
        for (int color : new int[]{0xFF0000, 0x00FF00, 0x0000FF, 0xFFFFFF, 0x000000, 0x50C8FF}) {
            float[] hsv = ColorPickerScreen.rgbToHsv(color);
            assertEquals(color, ColorPickerScreen.hsvToRgb(hsv[0], hsv[1], hsv[2]));
        }
    }

    @Test
    void compactControlsStayInsideTheirAvailableWidth() {
        assertEquals(1, ColorPickerScreen.rgbBarWidth(32));
        var presets = ColorPickerScreen.presetLayout(10, 120, true);
        int count = 9;
        int right = presets.startX() + presets.swatchSize() * count + presets.gap() * (count - 1);
        assertTrue(right <= 10 + 120 - 14);
    }
}

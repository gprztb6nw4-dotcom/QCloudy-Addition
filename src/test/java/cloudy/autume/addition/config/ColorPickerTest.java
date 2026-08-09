package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ColorPickerTest {
    @Test
    void rgbHsvRoundTripKeepsPresetColors() {
        for (int color : new int[]{0xFF0000, 0x00FF00, 0x0000FF, 0xFFFFFF, 0x000000, 0x50C8FF}) {
            float[] hsv = ColorPickerScreen.rgbToHsv(color);
            assertEquals(color, ColorPickerScreen.hsvToRgb(hsv[0], hsv[1], hsv[2]));
        }
    }
}

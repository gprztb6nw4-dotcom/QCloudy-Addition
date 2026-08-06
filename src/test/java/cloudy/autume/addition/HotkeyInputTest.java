package cloudy.autume.addition;

import cloudy.autume.addition.input.HotkeyInputs;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class HotkeyInputTest {
    @Test
    void acceptsStandardAndSideMouseButtons() {
        for (int button = GLFW.GLFW_MOUSE_BUTTON_1; button <= GLFW.GLFW_MOUSE_BUTTON_5; button++) {
            assertTrue(HotkeyInputs.supportedMouseButton(button));
        }
        assertTrue(HotkeyInputs.supportedMouseButton(GLFW.GLFW_MOUSE_BUTTON_LAST));
        assertFalse(HotkeyInputs.supportedMouseButton(-1));
        assertFalse(HotkeyInputs.supportedMouseButton(GLFW.GLFW_MOUSE_BUTTON_LAST + 1));
    }
}

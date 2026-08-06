package cloudy.autume.addition.input;

import org.lwjgl.glfw.GLFW;

public final class HotkeyInputs {
    private HotkeyInputs() {
    }

    public static boolean supportedMouseButton(int button) {
        return button >= GLFW.GLFW_MOUSE_BUTTON_1 && button <= GLFW.GLFW_MOUSE_BUTTON_LAST;
    }
}

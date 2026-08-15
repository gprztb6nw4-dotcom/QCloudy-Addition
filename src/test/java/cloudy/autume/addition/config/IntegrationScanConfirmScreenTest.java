package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class IntegrationScanConfirmScreenTest {
    @Test
    void confirmationCopyMatchesTheIndependentScanScope() {
        assertEquals("config.integration.scan.confirm.settings",
                IntegrationScanConfirmScreen.detailKey(UnifiedModIntegration.ScanView.SETTINGS));
        assertEquals("config.integration.scan.confirm.hud",
                IntegrationScanConfirmScreen.detailKey(UnifiedModIntegration.ScanView.HUD));
    }

    @Test
    void onlyEnterKeysConfirmFromTheKeyboard() {
        assertTrue(IntegrationScanConfirmScreen.confirmsWithKey(GLFW.GLFW_KEY_ENTER));
        assertTrue(IntegrationScanConfirmScreen.confirmsWithKey(GLFW.GLFW_KEY_KP_ENTER));
        assertFalse(IntegrationScanConfirmScreen.confirmsWithKey(GLFW.GLFW_KEY_ESCAPE));
        assertFalse(IntegrationScanConfirmScreen.confirmsWithKey(GLFW.GLFW_KEY_SPACE));
    }
}

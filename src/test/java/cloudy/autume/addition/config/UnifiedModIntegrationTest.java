package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class UnifiedModIntegrationTest {
    @Test
    void providerHudDiscoveryIsEmptyWhileItsMasterSwitchIsOff() {
        assertFalse(ConfigManager.get().integrations.unifiedHudEditor);
        assertTrue(UnifiedModIntegration.externalHuds().isEmpty());
    }

    @Test
    void prefixedFutureVersionTogglesUseReadableFeatureNamesAndStableStems() {
        assertEquals("Commissions", UnifiedModIntegration.featureTitle("enabledCommissions"));
        assertEquals("Price Display", UnifiedModIntegration.featureTitle("showPriceDisplay"));
        assertEquals("commissions", UnifiedModIntegration.semanticStem("enabledCommissions"));
        assertEquals("pricedisplay", UnifiedModIntegration.semanticStem("showPriceDisplay"));
    }

    @Test
    void capabilityDiscoveryAssociatesOnlySettingsFromTheSameFunctionStem() {
        assertTrue(UnifiedModIntegration.relatedToStem("commissionsX", "commissions"));
        assertTrue(UnifiedModIntegration.relatedToStem("commissionScale", "commission"));
        assertTrue(UnifiedModIntegration.relatedToStem("priceDisplayMode", "pricedisplay"));
        assertFalse(UnifiedModIntegration.relatedToStem("powderX", "commissions"));
    }

    @Test
    void prefixedHudCoordinatesAreRecognisedWithoutVersionSpecificFieldLists() {
        assertEquals("x", UnifiedModIntegration.coordinateRole("commissionsX"));
        assertEquals("y", UnifiedModIntegration.coordinateRole("powderY"));
        assertEquals("scale", UnifiedModIntegration.coordinateRole("priceDisplayScale"));
        assertNull(UnifiedModIntegration.coordinateRole("maximumPrice"));
    }
}

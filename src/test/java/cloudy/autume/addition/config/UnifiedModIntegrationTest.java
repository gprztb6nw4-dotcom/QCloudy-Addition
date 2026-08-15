package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void compatibilityReportMergesOnlyUnavailableCapabilitiesPerProviderFeature() {
        List<UnifiedModIntegration.CompatibilityGap> result = UnifiedModIntegration.mergeCompatibilityGaps(List.of(
                new UnifiedModIntegration.CompatibilityGap(
                        UnifiedModIntegration.Provider.SKYHANNI, "Price Display", true, false),
                new UnifiedModIntegration.CompatibilityGap(
                        UnifiedModIntegration.Provider.SKYHANNI, "price display", false, true),
                new UnifiedModIntegration.CompatibilityGap(
                        UnifiedModIntegration.Provider.SKYBLOCKER, "Commissions", false, true),
                new UnifiedModIntegration.CompatibilityGap(
                        UnifiedModIntegration.Provider.FIRMAMENT, "Working Feature", false, false),
                new UnifiedModIntegration.CompatibilityGap(
                        UnifiedModIntegration.Provider.QCLOUDY, "Local Feature", true, true)
        ));

        assertEquals(2, result.size());
        assertEquals(UnifiedModIntegration.Provider.SKYHANNI, result.get(0).provider());
        assertEquals("Price Display", result.get(0).feature());
        assertTrue(result.get(0).settings());
        assertTrue(result.get(0).hud());
        assertEquals(UnifiedModIntegration.Provider.SKYBLOCKER, result.get(1).provider());
        assertFalse(result.get(1).settings());
        assertTrue(result.get(1).hud());
    }

    @Test
    void feeshDelegatedPropertiesGroupOnlyDeterministicallyRelatedSettings() {
        assertTrue(UnifiedModIntegration.feeshRelationScore(
                "alertOnRareDrops", "alertOnRareDropsSource") > 0);
        assertTrue(UnifiedModIntegration.feeshRelationScore(
                "alertOnRareDrops", "rareDropAlertShowPriceFor") > 0);
        assertTrue(UnifiedModIntegration.feeshRelationScore(
                "fishingProfitTrackerOverlay", "fishingProfitTrackerHideCheaperThan") > 0);
        assertEquals(0, UnifiedModIntegration.feeshRelationScore(
                "fishingProfitTrackerOverlay", "seaCreaturesTrackerShowTop"));
        assertTrue(UnifiedModIntegration.feeshRootPriority("fishingProfitTrackerOverlay")
                > UnifiedModIntegration.feeshRootPriority("fishingProfitTrackerCustomStyle"));
    }

    @Test
    void feeshOverlayCoordinatesRoundTripAllNativeAlignmentAnchors() {
        for (String alignment : List.of("LEFT", "CENTER", "RIGHT")) {
            int anchor = UnifiedModIntegration.feeshAnchorX(120, 80, alignment);
            assertEquals(120, UnifiedModIntegration.feeshLeftEdge(anchor, 80, alignment));
        }
    }

    @Test
    void feeshOverlayFeatureNamesDoNotExposeImplementationSuffixes() {
        assertEquals("Fishing Profit Tracker",
                UnifiedModIntegration.feeshFeatureTitle("fishingProfitTrackerOverlay"));
        assertEquals("Alert On Rare Drops",
                UnifiedModIntegration.feeshFeatureTitle("alertOnRareDrops"));
    }
}

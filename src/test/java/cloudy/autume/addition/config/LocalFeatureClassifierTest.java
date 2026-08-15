package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class LocalFeatureClassifierTest {
    @Test
    void verifiedProviderPathAlwaysWinsBeforeTheLocalClassifier() {
        UnifiedModIntegration.Classification result = UnifiedModIntegration.classify(
                "garden.visitors", "Bobber Timer", "Fishing-looking description");

        assertEquals(ConfigScreen.Category.FARMING, result.category());
        assertEquals(UnifiedModIntegration.ClassificationSource.VERIFIED_RULE, result.source());
    }

    @Test
    void localClassifierHandlesOnlyPreviouslyUnknownMetadata() {
        UnifiedModIntegration.Classification result = UnifiedModIntegration.classify(
                "future.module", "Bobber Timer", "Displays the active hook timer");

        assertEquals(ConfigScreen.Category.FISHING, result.category());
        assertEquals(UnifiedModIntegration.ClassificationSource.LOCAL_CLASSIFIER, result.source());
    }

    @Test
    void lowConfidenceMetadataIsNotForcedIntoAnUnrelatedCategory() {
        UnifiedModIntegration.Classification result = UnifiedModIntegration.classify(
                "future.module", "Sparkle Widget", "Shows a configurable widget");

        assertEquals(ConfigScreen.Category.GENERAL, result.category());
        assertEquals(UnifiedModIntegration.ClassificationSource.UNCLASSIFIED, result.source());
        assertNull(LocalFeatureClassifier.classify("future.module", "Sparkle Widget",
                "Shows a configurable widget"));
    }
}

package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShardPlanningScreenLayoutTest {
    @Test
    void wideShardPageKeepsRateControlsBelowTheDetailViewport() {
        var layout = ShardPlanningScreen.shardPageLayout(12, 70, 924, 420);

        assertEquals(250, layout.listWidth());
        assertTrue(layout.detailViewportY() + layout.detailViewportHeight() < layout.rateY());
        assertTrue(layout.rateInputX() >= layout.detailX());
        assertTrue(layout.rateInputX() + layout.rateInputWidth() <= layout.saveX());
        assertTrue(layout.saveX() + layout.buttonWidth() < layout.resetX() + layout.buttonWidth());
        assertTrue(layout.resetX() + layout.buttonWidth() <= layout.detailX() + layout.detailWidth());
        assertTrue(layout.rateControlY() > 70 + 19);
    }

    @Test
    void narrowShardPageNeverLetsRateControlsEscapeTheDetailColumn() {
        var layout = ShardPlanningScreen.shardPageLayout(4, 60, 304, 160);

        assertTrue(layout.detailWidth() > 0);
        assertTrue(layout.rateInputWidth() > 0);
        assertTrue(layout.rateInputX() >= layout.detailX());
        assertTrue(layout.saveX() >= layout.rateInputX() + layout.rateInputWidth());
        assertTrue(layout.resetX() >= layout.saveX() + layout.buttonWidth());
        assertTrue(layout.resetX() + layout.buttonWidth() <= layout.detailX() + layout.detailWidth());
    }

    @Test
    void planControlsUseSeparateRowsBeforeTheyWouldOverlap() {
        var wide = ShardPlanningScreen.planControlsLayout(10, 50, 924);
        var narrow = ShardPlanningScreen.planControlsLayout(10, 50, 304);

        assertEquals(wide.fieldY(), wide.controlsY());
        assertTrue(narrow.controlsY() > narrow.fieldY());
        assertTrue(narrow.contentTop() > narrow.controlsY());
        assertTrue(narrow.controlsX() + narrow.buttonWidth() * 4 + 12 <= 10 + 304 - 8);
        assertTrue(narrow.quantityX() + narrow.quantityWidth() <= 10 + 304 - 8);
    }

    @Test
    void settingsUseOneColumnWhenTwoColumnsWouldOverlap() {
        var wide = ShardPlanningScreen.settingsLayout(10, 50, 924);
        var narrow = ShardPlanningScreen.settingsLayout(10, 50, 304);

        assertEquals(wide.leftFieldY(), wide.rightFieldY());
        assertTrue(narrow.rightFieldY() > narrow.leftFieldY());
        assertTrue(narrow.leftFieldX() + narrow.fieldWidth() <= 10 + 304 - 9);
        assertTrue(narrow.rightFieldX() + narrow.fieldWidth() <= 10 + 304 - 9);
        assertTrue(narrow.toggleY() > narrow.rightFieldY());
        assertTrue(narrow.tierY() > narrow.toggleY() + 27);
        assertTrue(narrow.priceY() > narrow.tierY());
        assertTrue(narrow.persistenceY() > narrow.priceY());
        assertTrue(narrow.clientOnlyY() > narrow.persistenceY());
        assertEquals(wide.toggleY(), wide.tierY());
    }

    @Test
    void shortShardSettingsPageShowsSafeFallbackInsteadOfOverflowingFields() {
        assertTrue(ShardPlanningScreen.settingsContentFits(250));
        assertTrue(!ShardPlanningScreen.settingsContentFits(249));
    }

    @Test
    void fusionLineCanvasGrowsInsteadOfStackingNodesOnItsLastRow() {
        int viewportHeight = 150;

        assertEquals(viewportHeight, ShardPlanningScreen.graphCanvasHeight(4, 924, viewportHeight));
        assertTrue(ShardPlanningScreen.graphCanvasHeight(24, 304, viewportHeight) > viewportHeight);
    }

    @Test
    void recipeFieldsUseTheSameNonOverflowingWidthAtEveryCallSite() {
        assertEquals(220, ShardPlanningScreen.recipeFieldWidth(924));
        assertEquals(132, ShardPlanningScreen.recipeFieldWidth(304));
        assertEquals(1, ShardPlanningScreen.recipeFieldWidth(40));
    }
}

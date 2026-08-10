package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShardFusionScreenLayoutTest {
    @Test
    void wideInputPairUsesItsContentWidthAndStaysCentered() {
        var layout = ShardFusionScreen.inputPairLayout(500, 54, 57, 6);

        assertTrue(layout.showItems());
        assertEquals((500 - layout.groupWidth()) / 2, layout.leftX());
        assertTrue(layout.groupWidth() < 200);
        assertTrue(layout.plusX() >= layout.leftX() + layout.leftWidth());
        assertTrue(layout.rightX() > layout.plusX());
        assertTrue(layout.rightX() + layout.rightWidth() <= 500);
    }

    @Test
    void constrainedInputPairNeverOverlapsOrEscapesItsBounds() {
        var layout = ShardFusionScreen.inputPairLayout(86, 110, 75, 6);

        assertTrue(layout.leftWidth() > 0);
        assertTrue(layout.rightWidth() > 0);
        assertTrue(layout.plusX() >= layout.leftX() + layout.leftWidth());
        assertTrue(layout.rightX() >= layout.plusX() + layout.plusWidth());
        assertTrue(layout.rightX() + layout.rightWidth() <= 86);
    }

    @Test
    void outputCellsRemainCompactAndCenteredWhenTheyFit() {
        var layout = ShardFusionScreen.compactRowLayout(500, 9, List.of(64, 83, 71));

        assertEquals(List.of(64, 83, 71), layout.widths());
        assertEquals(236, layout.groupWidth());
        assertEquals((500 - 236) / 2, layout.groupX());
        assertEquals(layout.groupX(), layout.starts().getFirst());
        assertEquals(9, layout.starts().get(1)
                - layout.starts().get(0) - layout.widths().get(0));
    }

    @Test
    void outputCellsShrinkFairlyWithoutCrossingTheAvailableWidth() {
        var layout = ShardFusionScreen.compactRowLayout(120, 9, List.of(90, 90, 90));

        assertEquals(120, layout.groupWidth());
        assertEquals(0, layout.groupX());
        assertEquals(3, layout.widths().size());
        int last = layout.starts().getLast() + layout.widths().getLast();
        assertTrue(last <= 120);
        assertTrue(layout.widths().stream().allMatch(width -> width > 0));
    }

    @Test
    void escapeAndTabReleaseSearchWhileRecipeShortcutsDoNot() {
        assertTrue(ShardFusionScreen.isSearchFocusExitKey(GLFW.GLFW_KEY_ESCAPE));
        assertTrue(ShardFusionScreen.isSearchFocusExitKey(GLFW.GLFW_KEY_TAB));
        assertFalse(ShardFusionScreen.isSearchFocusExitKey(GLFW.GLFW_KEY_R));
        assertFalse(ShardFusionScreen.isSearchFocusExitKey(GLFW.GLFW_KEY_U));
    }

    @Test
    void resizeDoesNotRestoreSearchFocusAfterThePlayerReleasedIt() {
        assertTrue(ShardFusionScreen.shouldFocusSearch(true, false, true));
        assertTrue(ShardFusionScreen.shouldFocusSearch(false, true, true));
        assertFalse(ShardFusionScreen.shouldFocusSearch(false, false, true));
        assertFalse(ShardFusionScreen.shouldFocusSearch(true, true, false));
    }

    @Test
    void hoveredShardLinksDarkenWithoutChangingAlpha() {
        assertEquals(0xFF740074, ShardFusionScreen.darken(0xFFAA00AA));
        assertEquals(0x80505050, ShardFusionScreen.darken(0x80767676));
    }
}

package cloudy.autume.addition.fishing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FishingBiteSessionTest {
    @Test
    void playsOnlyOnceForEachCastAndResetsAfterTheHookIsGone() {
        FishingBiteSession session = new FishingBiteSession();

        assertFalse(session.shouldPlay(41, false));
        assertTrue(session.shouldPlay(41, true));
        assertFalse(session.shouldPlay(41, true));
        assertFalse(session.shouldPlay(41, false));
        assertFalse(session.shouldPlay(41, true));

        session.reset();
        assertTrue(session.shouldPlay(41, true));
        assertTrue(session.shouldPlay(42, true));
        assertFalse(session.shouldPlay(42, true));
    }

    @Test
    void reelUseDoesNotRearmWhileTheBiteMarkerIsStillVisible() {
        FishingBiteSession session = new FishingBiteSession();

        assertTrue(session.shouldPlay(51, true));
        session.onRodUse(false);
        assertFalse(session.shouldPlay(51, true));

        session.onRodUse(true);
        assertTrue(session.shouldPlay(52, true));
    }
}

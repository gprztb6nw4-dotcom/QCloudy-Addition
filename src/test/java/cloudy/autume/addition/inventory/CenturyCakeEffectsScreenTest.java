package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CenturyCakeEffectsScreenTest {
    @Test
    void remainingTimeUsesReadableDayHourMinuteFormat() {
        assertEquals("1d 2h 3m", CenturyCakeEffectsScreen.formatRemaining(
                (24L * 60L + 2L * 60L + 3L) * 60L * 1_000L));
        assertEquals("2h 3m", CenturyCakeEffectsScreen.formatRemaining(
                (2L * 60L + 3L) * 60L * 1_000L));
        assertEquals("1m", CenturyCakeEffectsScreen.formatRemaining(30_000L));
    }
}

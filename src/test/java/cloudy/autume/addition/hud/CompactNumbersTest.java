package cloudy.autume.addition.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CompactNumbersTest {
    @Test
    void formatsLargeValuesWithOneDecimal() {
        assertEquals("45.1k", CompactNumbers.format("45,132"));
        assertEquals("2.2m", CompactNumbers.format("2,220,348"));
        assertEquals("1.4m", CompactNumbers.format("1.4M"));
        assertEquals("999", CompactNumbers.format("999"));
    }

    @Test
    void parsesExistingSkyBlockSuffixes() {
        assertEquals(1_400_000.0, CompactNumbers.parse("1.4M"));
        assertEquals(931_886.2, CompactNumbers.parse("931,886.2"));
    }
}

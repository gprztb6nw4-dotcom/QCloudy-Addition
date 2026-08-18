package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class CenturyCakeParserTest {
    @Test
    void parsesExactReceivedInitialGainAndRefreshMessages() {
        assertEquals("Hunting Fortune", CenturyCakeParser.parse(
                "Big Yum! You refresh +1\uE05B Hunting Fortune for 48 hours!").effect());
        assertEquals("Hunting Fortune", CenturyCakeParser.parse(
                "§dBig Yum! §eYou refresh §d+1\uE05B Hunting Fortune §efor §a48 §ehours!").effect());
        assertEquals("Hunting Fortune", CenturyCakeParser.parse(
                "Yum! You gain +1\uE05B Hunting Fortune for 48 hours!").effect());
        assertEquals("Sweep", CenturyCakeParser.parse(
                "Big Yum! You refresh +5 Sweep for 48 hours!").effect());
        assertEquals("Farming Fortune", CenturyCakeParser.parse(
                "§dBig Yum! §eYou refresh §6+5 Farming Fortune §efor §a48 §ehours!").effect());
    }

    @Test
    void rejectsInventedMessageCombinationsAndUnrelatedLines() {
        assertNull(CenturyCakeParser.parse("Big Yum! You gain +1 Hunting Fortune for 48 hours!"));
        assertNull(CenturyCakeParser.parse("Yum! You refresh +1 Hunting Fortune for 48 hours!"));
        assertNull(CenturyCakeParser.parse("Big Yum! You refresh +1 Hunter Fortune for 48 hours!"));
        assertNull(CenturyCakeParser.parse("Big Yum! You refresh +5 Sweep for 47 hours!"));
        assertNull(CenturyCakeParser.parse("Your Sweep expired."));
        assertNull(CenturyCakeParser.parse("Big Yum! You refresh +5 Unknown Stat for 48 hours!"));
    }
}

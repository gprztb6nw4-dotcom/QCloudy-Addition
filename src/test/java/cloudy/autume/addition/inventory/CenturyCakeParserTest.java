package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class CenturyCakeParserTest {
    @Test
    void parsesExactReceivedInitialGainAndRefreshMessages() {
        assertEquals("Hunter Fortune", CenturyCakeParser.parse(
                "Yum! You gain +1\uE05B Hunter Fortune for 48 hours!").effect());
        assertEquals("Hunter Fortune", CenturyCakeParser.parse(
                "§dYum! §eYou gain §d+1\uE05B Hunter Fortune §efor §a48 §ehours!").effect());
        assertEquals("Sweep", CenturyCakeParser.parse(
                "Big Yum! You refresh +5 Sweep for 48 hours!").effect());
        assertEquals("Farming Fortune", CenturyCakeParser.parse(
                "§dBig Yum! §eYou refresh §6+5 Farming Fortune §efor §a48 §ehours!").effect());
    }

    @Test
    void rejectsInventedMessageCombinationsAndUnrelatedLines() {
        assertNull(CenturyCakeParser.parse("Big Yum! You gain +1 Hunter Fortune for 48 hours!"));
        assertNull(CenturyCakeParser.parse("Yum! You refresh +1 Hunter Fortune for 48 hours!"));
        assertNull(CenturyCakeParser.parse("Big Yum! You refresh +5 Sweep for 47 hours!"));
        assertNull(CenturyCakeParser.parse("Your Sweep expired."));
        assertNull(CenturyCakeParser.parse("Big Yum! You refresh +5 Unknown Stat for 48 hours!"));
    }
}

package cloudy.autume.addition.hunting;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SafariMilestoneParserTest {
    @Test
    void readsAllFourMilestonesAcrossMenuTitleAndLoreLayouts() {
        SafariMilestoneParser.Levels levels = SafariMilestoneParser.parse(List.of(
                "Cavern Milestone\nCurrent Level: IV",
                "Forest Milestone V\nCOMPLETED",
                "Haunted Milestone: 3",
                "Icy Biome\nCurrent Milestone: X"));

        assertEquals(new SafariMilestoneParser.Levels(4, 5, 3, 10), levels);
    }

    @Test
    void ignoresLockedFutureMilestonesAndCapturedCountFractions() {
        SafariMilestoneParser.Levels levels = SafariMilestoneParser.parse(List.of(
                "Forest Milestone X\nLOCKED\nRequires 3,000 captures",
                "Cavern Milestone\n1,250 / 3,000"));

        assertEquals(SafariMilestoneParser.Levels.EMPTY, levels);
    }

    @Test
    void keepsHighestConfirmedValueWhenTheMenuRepeatsABiome() {
        SafariMilestoneParser.Levels levels = SafariMilestoneParser.parse(List.of(
                "Haunted Milestone II\nREACHED",
                "Haunted Milestone VII\nCLAIMED",
                "Haunted Milestone IV\nCOMPLETED"));

        assertEquals(7, levels.haunted());
    }
}

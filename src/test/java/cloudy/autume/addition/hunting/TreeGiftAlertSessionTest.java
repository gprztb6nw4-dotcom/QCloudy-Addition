package cloudy.autume.addition.hunting;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class TreeGiftAlertSessionTest {
    private static final String BORDER = "▬".repeat(64);

    @Test
    void acceptsHoverAndSeparateBonusRowsFromThePlayersBoundedBlock() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        long now = 1_000L;

        assertEquals(List.of(), session.accept(Component.literal(BORDER), enabled, now++));
        assertEquals(List.of(), session.accept(Component.literal("TREE GIFT"), enabled, now++));
        assertEquals(List.of(), session.accept(
                Component.literal("You helped cut 33.0% of the Fig Tree."), enabled, now++));
        Component summary = Component.literal("+5 rewards gained! ").append(
                Component.literal("(hover)").withStyle(style -> style.withHoverEvent(
                        new HoverEvent.ShowText(Component.literal("Signal Enhancer (0.4%)")))));
        assertEquals(List.of("Signal Enhancer"), session.accept(summary, enabled, now++));
        assertEquals(List.of(), session.accept(Component.literal("BONUS GIFT"), enabled, now++));
        assertEquals(List.of("Chameleon Shard"),
                session.accept(Component.literal("Chameleon (0.08%)"), enabled, now++));
        assertEquals(List.of("Dreadwing"),
                session.accept(Component.literal("A Dreadwing fell from the Tree!"), enabled, now++));
        assertEquals(List.of("Groundhog"),
                session.accept(Component.literal("-A wild Groundhog appeared!"), enabled, now++));
        assertEquals(List.of(),
                session.accept(Component.literal("A Dreadwing fell from the Tree!"), enabled, now++));
    }

    @Test
    void neverArmsOnNearbyPublicDropsOrUnrelatedCaptureMessages() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();

        assertEquals(List.of(), session.accept(
                Component.literal("A Dreadwing fell from the Tree!"), enabled, 1_000L));
        session.accept(Component.literal(BORDER), enabled, 1_001L);
        session.accept(Component.literal("TREE GIFT"), enabled, 1_002L);
        session.accept(Component.literal("BONUS GIFT"), enabled, 1_003L);
        assertEquals(List.of(), session.accept(
                Component.literal("A Dreadwing fell from the Tree!"), enabled, 1_004L));
        assertEquals(List.of(), session.accept(
                Component.literal("-A wild Groundhog appeared!"), enabled, 1_005L));
        assertEquals(List.of(), session.accept(
                Component.literal("CAPTURE! You caught a Dreadwing!"), enabled, 1_006L));
    }

    @Test
    void buffersBonusUntilPersonalSummaryProvesOwnership() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        session.accept(Component.literal(BORDER), enabled, 1_000L);
        session.accept(Component.literal("TREE GIFT"), enabled, 1_001L);
        session.accept(Component.literal("You helped cut 100% of the Mangrove Tree."), enabled, 1_002L);
        session.accept(Component.literal("BONUS GIFT"), enabled, 1_003L);
        assertEquals(List.of(), session.accept(
                Component.literal("A Dreadwing fell from the Tree!"), enabled, 1_004L));
        assertEquals(List.of("Dreadwing"), session.accept(
                Component.literal("+0 rewards gained!"), enabled, 1_005L));
    }

    @Test
    void acceptsWildCreatureImmediatelyAfterAProvenGiftCloses() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        session.accept(Component.literal(BORDER), enabled, 1_000L);
        session.accept(Component.literal("TREE GIFT"), enabled, 1_001L);
        session.accept(Component.literal("+0 rewards gained!"), enabled, 1_002L);
        session.accept(Component.literal(BORDER), enabled, 1_003L);

        assertEquals(List.of("Groundhog"), session.accept(
                Component.literal("-A wild Groundhog appeared!"), enabled, 1_004L));
        assertEquals(List.of(), session.accept(
                Component.literal("-A wild Groundhog appeared!"), enabled, 1_005L));
    }

    @Test
    void postGiftCreatureWindowExpiresAndNeverArmsForAPublicGift() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        session.accept(Component.literal(BORDER), enabled, 1_000L);
        session.accept(Component.literal("TREE GIFT"), enabled, 1_001L);
        session.accept(Component.literal("+0 rewards gained!"), enabled, 1_002L);
        session.accept(Component.literal(BORDER), enabled, 1_003L);
        assertEquals(List.of(), session.accept(
                Component.literal("-A wild Groundhog appeared!"), enabled, 6_004L));

        session.accept(Component.literal(BORDER), enabled, 7_000L);
        session.accept(Component.literal("TREE GIFT"), enabled, 7_001L);
        session.accept(Component.literal(BORDER), enabled, 7_002L);
        assertEquals(List.of(), session.accept(
                Component.literal("-A wild Groundhog appeared!"), enabled, 7_003L));
    }

    @Test
    void personalSummaryOwnsPendingCreatureWithoutLegacyContributionSentence() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        session.accept(Component.literal(BORDER), enabled, 1_000L);
        session.accept(Component.literal("TREE GIFT"), enabled, 1_001L);
        session.accept(Component.literal("-A wild Groundhog appeared!"), enabled, 1_002L);

        assertEquals(List.of("Groundhog"), session.accept(
                Component.literal("+0 rewards gained!"), enabled, 1_003L));
    }

    @Test
    void parsesACompleteMultilineGiftComponentInOneEvent() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        Component block = Component.literal(String.join("\n",
                BORDER,
                "TREE GIFT",
                "-A wild Groundhog appeared!",
                "+0 rewards gained!",
                BORDER));

        assertEquals(List.of("Groundhog"), session.accept(block, enabled, 1_000L));
    }

    @Test
    void personalSummaryOwnsAnEarlierCreatureInTheSameBorderlessComponent() {
        TreeGiftAlertSession session = new TreeGiftAlertSession();
        Map<String, Boolean> enabled = enabledLoot();
        Component compacted = Component.literal(String.join("\n",
                "-A wild Groundhog appeared!",
                "+0 rewards gained!"));

        assertEquals(List.of("Groundhog"), session.accept(compacted, enabled, 1_000L));
    }

    private static Map<String, Boolean> enabledLoot() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put("Signal Enhancer", true);
        result.put("Groundhog", true);
        result.put("Chameleon Shard", true);
        result.put("Dreadwing", true);
        return result;
    }
}

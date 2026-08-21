package cloudy.autume.addition.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DeployableExpirySessionTest {
    @Test
    void tracksEveryFlareForExactlyThreeMinutesAndAlertsOnlyOnce() {
        for (var entry : java.util.Map.of(
                "WARNING_FLARE", "Warning Flare Despawned!!!",
                "ALERT_FLARE", "Alert Flare Despawned!!!",
                "SOS_FLARE", "SOS Flare Despawned!!!").entrySet()) {
            DeployableExpirySession session = new DeployableExpirySession();
            long placedAt = 50L;
            assertTrue(session.beginFlarePlacement(entry.getKey(), placedAt));
            assertFalse(session.hasActiveFlare());
            assertTrue(session.confirmFlarePlacement(placedAt + 1));
            assertTrue(session.hasActiveFlare());
            assertNull(session.pollExpired(placedAt + DeployableExpirySession.FLARE_LIFETIME_NANOS));
            assertEquals(entry.getValue(),
                    session.pollExpired(placedAt + DeployableExpirySession.FLARE_LIFETIME_NANOS + 1));
            assertNull(session.pollExpired(Long.MAX_VALUE));
        }
    }

    @Test
    void aNewFlareSilentlyReplacesThePreviousLifecycle() {
        DeployableExpirySession session = new DeployableExpirySession();
        long firstPlacement = 1_000L;
        long secondPlacement = firstPlacement + 10_000L;
        assertTrue(session.beginFlarePlacement("WARNING_FLARE", firstPlacement));
        assertTrue(session.confirmFlarePlacement(firstPlacement + 1));
        assertTrue(session.beginFlarePlacement("SOS_FLARE", secondPlacement));
        assertTrue(session.confirmFlarePlacement(secondPlacement + 1));

        assertNull(session.pollExpired(firstPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS));
        assertEquals("SOS Flare Despawned!!!",
                session.pollExpired(secondPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 1));
    }

    @Test
    void replacingTheSameSosFlareResetsTheFullThreeMinuteTimer() {
        DeployableExpirySession session = new DeployableExpirySession();
        long firstPlacement = 1_000L;
        long replacement = firstPlacement + java.time.Duration.ofMinutes(2).toNanos();

        assertTrue(session.beginFlarePlacement("SOS_FLARE", firstPlacement));
        assertTrue(session.confirmFlarePlacement(firstPlacement + 1));
        assertTrue(session.beginFlarePlacement("SOS_FLARE", replacement));
        assertTrue(session.confirmFlarePlacement(replacement + 1));

        assertNull(session.pollExpired(firstPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 2));
        assertEquals("SOS Flare Despawned!!!",
                session.pollExpired(replacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 1));
    }

    @Test
    void replacingSosResetsImmediatelyWhenTheReplacementConfirmationIsMissing() {
        DeployableExpirySession session = new DeployableExpirySession();
        long firstPlacement = 1_000L;
        long replacement = firstPlacement + java.time.Duration.ofMinutes(2).toNanos();

        assertTrue(session.beginFlarePlacement("SOS_FLARE", firstPlacement));
        assertTrue(session.confirmFlarePlacement(firstPlacement + 1));
        assertTrue(session.beginFlarePlacement("SOS_FLARE", replacement));

        assertNull(session.pollExpired(firstPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 2));
        assertEquals("SOS Flare Despawned!!!",
                session.pollExpired(replacement + DeployableExpirySession.FLARE_LIFETIME_NANOS));
    }

    @Test
    void confirmedPlacementSoundCanRecoverWhenTheSecondUseCallbackWasMissed() {
        DeployableExpirySession session = new DeployableExpirySession();
        long firstPlacement = 1_000L;
        long replacement = firstPlacement + java.time.Duration.ofMinutes(2).toNanos();

        assertTrue(session.beginFlarePlacement("SOS_FLARE", firstPlacement));
        assertTrue(session.confirmFlarePlacement(firstPlacement + 1));
        assertTrue(session.confirmFlarePlacement("SOS_FLARE", replacement));

        assertNull(session.pollExpired(firstPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 2));
        assertEquals("SOS Flare Despawned!!!",
                session.pollExpired(replacement + DeployableExpirySession.FLARE_LIFETIME_NANOS));
    }

    @Test
    void aPlacementSoundWhileHoldingAnotherItemDoesNotResetTheActiveSos() {
        DeployableExpirySession session = new DeployableExpirySession();
        long firstPlacement = 1_000L;
        long unrelatedSound = firstPlacement + java.time.Duration.ofMinutes(2).toNanos();

        assertTrue(session.beginFlarePlacement("SOS_FLARE", firstPlacement));
        assertTrue(session.confirmFlarePlacement(firstPlacement + 1));
        assertFalse(session.confirmFlarePlacement("FIREWORK_ROCKET", unrelatedSound));

        assertEquals("SOS Flare Despawned!!!",
                session.pollExpired(firstPlacement + DeployableExpirySession.FLARE_LIFETIME_NANOS + 1));
    }

    @Test
    void unrelatedItemsAndSilentClearNeverCreateAnAlert() {
        DeployableExpirySession session = new DeployableExpirySession();
        assertFalse(session.beginFlarePlacement("PLASMAFLUX_POWER_ORB", 0L));
        assertFalse(session.hasActiveFlare());
        assertNull(session.pollExpired(Long.MAX_VALUE));

        assertTrue(session.beginFlarePlacement("SOS_FLARE", 20L));
        assertTrue(session.confirmFlarePlacement(21L));
        session.clear();
        assertFalse(session.hasActiveFlare());
        assertNull(session.pollExpired(Long.MAX_VALUE));
    }

    @Test
    void attemptedUseNeedsPlacementConfirmationAndCannotStartALateLifecycle() {
        DeployableExpirySession session = new DeployableExpirySession();
        long attemptedAt = 1_000L;
        assertTrue(session.beginFlarePlacement("SOS_FLARE", attemptedAt));
        assertFalse(session.hasActiveFlare());
        assertFalse(session.confirmFlarePlacement(
                attemptedAt + DeployableExpirySession.PLACEMENT_CONFIRM_WINDOW_NANOS + 1));
        assertFalse(session.hasActiveFlare());
        assertNull(session.pollExpired(Long.MAX_VALUE));
    }
}

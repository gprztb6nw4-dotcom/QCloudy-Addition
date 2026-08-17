package cloudy.autume.addition.combat;

import java.time.Duration;
import java.util.Map;

/** Pure local lifecycle state for the flare placed by this client. */
final class DeployableExpirySession {
    static final long FLARE_LIFETIME_NANOS = Duration.ofMinutes(3).toNanos();
    static final long PLACEMENT_CONFIRM_WINDOW_NANOS = Duration.ofSeconds(2).toNanos();
    private static final Map<String, String> FLARES = Map.of(
            "WARNING_FLARE", "Warning Flare",
            "ALERT_FLARE", "Alert Flare",
            "SOS_FLARE", "SOS Flare");

    private String activeFlare = "";
    private long expiresAtNanos;
    private String pendingFlare = "";
    private long pendingUntilNanos;

    boolean beginFlarePlacement(String skyBlockItemId, long nowNanos) {
        String flare = FLARES.get(skyBlockItemId);
        if (flare == null) return false;
        pendingFlare = flare;
        pendingUntilNanos = nowNanos + PLACEMENT_CONFIRM_WINDOW_NANOS;
        return true;
    }

    boolean confirmFlarePlacement(long nowNanos) {
        if (pendingFlare.isEmpty() || nowNanos - pendingUntilNanos > 0) {
            clearPending();
            return false;
        }
        activeFlare = pendingFlare;
        expiresAtNanos = nowNanos + FLARE_LIFETIME_NANOS;
        clearPending();
        return true;
    }

    String pollExpired(long nowNanos) {
        if (!pendingFlare.isEmpty() && nowNanos - pendingUntilNanos > 0) clearPending();
        if (activeFlare.isEmpty() || nowNanos - expiresAtNanos < 0) return null;
        String title = activeFlare + " Despawned!!!";
        clear();
        return title;
    }

    void clear() {
        activeFlare = "";
        expiresAtNanos = 0L;
        clearPending();
    }

    private void clearPending() {
        pendingFlare = "";
        pendingUntilNanos = 0L;
    }

    boolean hasActiveFlare() {
        return !activeFlare.isEmpty();
    }
}

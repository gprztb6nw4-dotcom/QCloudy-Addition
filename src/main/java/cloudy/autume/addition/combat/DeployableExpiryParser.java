package cloudy.autume.addition.combat;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Exact parser for the player-owned Power Orb and Flare despawn chat lines. */
final class DeployableExpiryParser {
    private static final Set<String> DEPLOYABLES = Set.of(
            "Radiant Power Orb",
            "Mana Flux Power Orb",
            "Overflux Power Orb",
            "Plasmaflux Power Orb",
            "Warning Flare",
            "Alert Flare",
            "SOS Flare");
    private static final Pattern FORMATTING_CODE = Pattern.compile("§.");
    private static final Pattern MESSAGE = Pattern.compile("^Your (.+) despawned\\.$");

    private DeployableExpiryParser() {
    }

    static String alertTitle(String raw) {
        if (raw == null) return null;
        String plain = FORMATTING_CODE.matcher(raw).replaceAll("").trim();
        Matcher matcher = MESSAGE.matcher(plain);
        if (!matcher.matches() || !DEPLOYABLES.contains(matcher.group(1))) return null;
        return matcher.group(1) + " Despawned!!!";
    }
}

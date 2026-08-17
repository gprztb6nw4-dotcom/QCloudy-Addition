package cloudy.autume.addition.combat;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class DeployableExpiryParserTest {
    @Test
    void acceptsEverySupportedPlayerOwnedPowerOrbDespawnMessage() {
        Map<String, String> cases = Map.of(
                "Radiant Power Orb", "Radiant Power Orb Despawned!!!",
                "Mana Flux Power Orb", "Mana Flux Power Orb Despawned!!!",
                "Overflux Power Orb", "Overflux Power Orb Despawned!!!",
                "Plasmaflux Power Orb", "Plasmaflux Power Orb Despawned!!!");

        cases.forEach((name, expected) -> assertEquals(expected,
                DeployableExpiryParser.alertTitle("Your " + name + " despawned.")));
    }

    @Test
    void removesFormattingButKeepsTheMatchExact() {
        assertEquals("Plasmaflux Power Orb Despawned!!!",
                DeployableExpiryParser.alertTitle("  §6Your Plasmaflux Power Orb despawned.§r  "));
        assertNull(DeployableExpiryParser.alertTitle("Someone's Plasmaflux Power Orb despawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your Sheep despawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your Warning Flare despawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your Alert Flare despawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your SOS Flare despawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your Plasmaflux Power Orb spawned."));
        assertNull(DeployableExpiryParser.alertTitle("Your Plasmaflux Power Orb despawned!"));
    }
}

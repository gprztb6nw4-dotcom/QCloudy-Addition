package cloudy.autume.addition.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ManualReconnectManagerTest {
    @Test
    void formatsDefaultCustomAndIpv6Targets() {
        assertEquals("mc.hypixel.net", ManualReconnectManager.formatAddress("mc.hypixel.net", 25565));
        assertEquals("example.net:25566", ManualReconnectManager.formatAddress("example.net", 25566));
        assertEquals("[2001:db8::1]:25566", ManualReconnectManager.formatAddress("2001:db8::1", 25566));
    }
}

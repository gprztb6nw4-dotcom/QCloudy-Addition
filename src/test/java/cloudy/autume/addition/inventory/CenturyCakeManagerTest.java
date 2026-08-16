package cloudy.autume.addition.inventory;

import net.minecraft.network.chat.ClickEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CenturyCakeManagerTest {
    @Test
    void cakeDurationUsesFortyEightRealWorldHours() {
        assertEquals(48L * 60L * 60L * 1_000L, CenturyCakeManager.DURATION_MS);
    }

    @Test
    void oneTickCollectsAllExpiredEffectsExactlyOnce() {
        var profile = new CenturyCakeManager.ProfileState();
        profile.effects.put("EPOCH_CAKE_EXPIRED", new CenturyCakeManager.EffectState(900L, false));
        profile.effects.put("EPOCH_CAKE_WHITE", new CenturyCakeManager.EffectState(950L, false));
        profile.effects.put("EPOCH_CAKE_RED", new CenturyCakeManager.EffectState(1_500L, false));

        var expired = CenturyCakeManager.collectExpired(profile, 1_000L);

        assertEquals(2, expired.size());
        assertTrue(CenturyCakeManager.collectExpired(profile, 1_000L).isEmpty());
        assertFalse(profile.effects.get("EPOCH_CAKE_RED").alerted);
    }

    @Test
    void chatUsesSingleOrBatchTextAndOneUnderlinedVisitCommand() {
        var catalog = CenturyCakeCatalog.instance();
        var sweep = catalog.byId("EPOCH_CAKE_EXPIRED").orElseThrow();
        var foraging = catalog.byId("EPOCH_CAKE_WHITE").orElseThrow();

        var single = CenturyCakeManager.chatMessage(List.of(sweep));
        assertEquals("[QC] Century Cake Sweep Expired! Click Here For Cake Eating", single.getString());
        assertVisitLink(single);

        var batch = CenturyCakeManager.chatMessage(List.of(sweep, foraging));
        assertEquals("[QC] 2 Century Cake Effect Expired! Click Here For Cake Eating", batch.getString());
        assertVisitLink(batch);
    }

    private static void assertVisitLink(net.minecraft.network.chat.Component message) {
        var visit = message.getSiblings().getLast();
        assertTrue(visit.getStyle().isUnderlined());
        ClickEvent.RunCommand click = assertInstanceOf(ClickEvent.RunCommand.class,
                visit.getStyle().getClickEvent());
        assertEquals("/visit northwestcloudy", click.command());
    }
}

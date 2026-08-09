package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConfigScreenFeatureTest {
    @Test
    void hudAnimationsIsTheFirstFeatureAndUsesTheSharedAnimationSetting() {
        ConfigScreen.Feature feature = ConfigScreen.Feature.values()[0];
        assertEquals(ConfigScreen.Feature.HUD_ANIMATIONS, feature);

        ModConfig config = new ModConfig();
        assertTrue(feature.enabled(config));
        feature.toggle(config);
        assertFalse(config.hudStyle.animations);
        assertFalse(feature.enabled(config));
        assertFalse(feature.hasSettings());
    }

    @Test
    void newHuntingFeaturesUseRequestedDefaultsAndSafariHasNoEssenceOption() {
        ModConfig config = new ModConfig();
        assertTrue(ConfigScreen.Feature.COLD_SAFETY.enabled(config));
        assertTrue(ConfigScreen.Feature.DOOMSPIRAL_READY.enabled(config));
        assertTrue(ConfigScreen.Feature.WARDEN_READY_ALERT.enabled(config));
        assertTrue(ConfigScreen.Feature.SAFARI_CRITTER_HIGHLIGHT.enabled(config));
        assertFalse(ConfigScreen.Feature.SAFARI_SHARD_STATS.enabled(config));
        assertTrue(ConfigScreen.Feature.SNOOZLE_WALL_OVERLAY.enabled(config));
        assertTrue(ConfigScreen.Feature.TREE_CRITTER_TIMER.enabled(config));
        assertTrue(ConfigScreen.Feature.GALATEA_TRACKER.enabled(config));
        assertTrue(ConfigScreen.Feature.AGATHA_CONTEST.enabled(config));
        assertTrue(ConfigScreen.Feature.BEEHEEMOTH_HELPER.enabled(config));
        assertTrue(ConfigScreen.Feature.LASSO_REEL_SOUND.enabled(config));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.BEEHEEMOTH_HELPER).stream()
                .anyMatch(option -> option == HuntingOption.BEEHEEMOTH_COLOR));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.BEEHEEMOTH_HELPER).stream()
                .anyMatch(option -> option == HuntingOption.BEEHEEMOTH_SOUND));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.BEEHEEMOTH_HELPER).stream()
                .anyMatch(option -> option == HuntingOption.BEEHEEMOTH_VOLUME));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.LASSO_REEL_SOUND).stream()
                .anyMatch(option -> option == HuntingOption.LASSO_REEL_VOLUME));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.WARDEN_READY_ALERT).stream()
                .anyMatch(option -> option == HuntingOption.WARDEN_READY_SOUND));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.WARDEN_READY_ALERT).stream()
                .anyMatch(option -> option == HuntingOption.WARDEN_READY_VOLUME));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.WUMPA_HUD).stream()
                .anyMatch(option -> option == HuntingOption.WUMPA_REQUIREMENTS));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.SNOOZLE_WALL_OVERLAY).stream()
                .anyMatch(option -> option == HuntingOption.SNOOZLE_WALL_COLOR));
        assertEquals(ModConfig.HudType.HUNTING, ConfigScreen.Feature.TREE_CRITTER_TIMER.hudType());
        assertFalse(ConfigScreen.Feature.FAIRY_SOUL_WAYPOINTS.enabled(config));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.SAFARI_DASHBOARD).stream()
                .noneMatch(option -> option.name().contains("ESSENCE")));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.SAFARI_CRITTERDEX).stream()
                .noneMatch(option -> option.name().contains("SHARDS")));
        ConfigScreen.Feature.SAFARI_SHARD_STATS.toggle(config);
        assertTrue(config.hunting.safariShards);
        assertEquals(ModConfig.HudType.HUNTING, ConfigScreen.Feature.SAFARI_SHARD_STATS.hudType());
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.TORRHUS_TRACKER).stream()
                .anyMatch(option -> option == HuntingOption.SAFARI_ESSENCE_TORRHUS));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.TREE_GIFT_ALERTS).stream()
                .anyMatch(option -> option == HuntingOption.TREE_GIFT_VOLUME));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.MIRIA_CONTEST).stream()
                .noneMatch(option -> option.name().contains("SCOREBOARD")));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.GALATEA_TRACKER).stream()
                .anyMatch(option -> option == HuntingOption.GALATEA_SWEEP));
        assertTrue(HuntingOption.forFeature(ConfigScreen.Feature.AGATHA_CONTEST).stream()
                .anyMatch(option -> option == HuntingOption.AGATHA_NEXT_BRACKET));
    }

    @Test
    void manualReconnectIsASettingsFreeGeneralToggle() {
        ModConfig config = new ModConfig();
        assertEquals(ConfigScreen.Category.GENERAL, ConfigScreen.Feature.MANUAL_RECONNECT.category);
        assertTrue(ConfigScreen.Feature.MANUAL_RECONNECT.enabled(config));
        assertFalse(ConfigScreen.Feature.MANUAL_RECONNECT.hasSettings());
        ConfigScreen.Feature.MANUAL_RECONNECT.toggle(config);
        assertFalse(config.manualReconnectButton);
    }

    @Test
    void requestedSidebarCategoriesOwnEachFeatureOnce() {
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.TORRHUS_TRACKER.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.GALATEA_TRACKER.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.TREE_CRITTER_TIMER.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.MIRIA_CONTEST.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.AGATHA_CONTEST.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.BENEFACTOR_HUD.category);
        assertEquals(ConfigScreen.Category.FORAGING, ConfigScreen.Feature.TREE_GIFT_ALERTS.category);

        assertEquals(ConfigScreen.Category.MAPS, ConfigScreen.Feature.FAIRY_SOUL_WAYPOINTS.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.BEEHEEMOTH_HELPER.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.LASSO_REEL_SOUND.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.CRITTER_BEHAVIOR.category);

        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.COLD_SAFETY.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.DOOMSPIRAL_READY.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.WARDEN_READY_ALERT.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SAFARI_CRITTER_HIGHLIGHT.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SAFARI_DASHBOARD.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SAFARI_SHARD_STATS.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SAFARI_CRITTERDEX.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SPARKLING_ALERT.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.FLOOR_QUEST_ASSISTANT.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.WUMPA_HUD.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SNOOZLE_WALL_OVERLAY.category);
        assertEquals(ConfigScreen.Category.HUNTING, ConfigScreen.Feature.SAFARI_BELT.category);
    }
}

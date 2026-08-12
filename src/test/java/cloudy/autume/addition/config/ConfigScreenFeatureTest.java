package cloudy.autume.addition.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
    void fishingBiteSoundIsAnOptInTopLevelFishingFeatureWithItsOwnSettings() {
        ModConfig config = new ModConfig();
        ConfigScreen.Feature feature = ConfigScreen.Feature.FISHING_BITE_ALERT;

        assertEquals(ConfigScreen.Category.FISHING, feature.category);
        assertEquals(ConfigScreen.FeatureGroup.FISHING, feature.group);
        assertFalse(feature.enabled(config));
        assertTrue(feature.hasSettings());
        assertEquals(64, config.fishing.biteAlertVolume);

        feature.toggle(config);
        assertTrue(feature.enabled(config));
    }

    @Test
    void requestedTopLevelCategoriesUseTheExactPublishedOrder() {
        assertArrayEquals(new ConfigScreen.Category[]{
                        ConfigScreen.Category.GENERAL,
                        ConfigScreen.Category.MAPS,
                        ConfigScreen.Category.ITEMS_AND_MENUS,
                        ConfigScreen.Category.COMBAT,
                        ConfigScreen.Category.DUNGEONS,
                        ConfigScreen.Category.SLAYER,
                        ConfigScreen.Category.MINING,
                        ConfigScreen.Category.FARMING,
                        ConfigScreen.Category.FORAGING,
                        ConfigScreen.Category.FISHING,
                        ConfigScreen.Category.HUNTING,
                        ConfigScreen.Category.RIFT,
                        ConfigScreen.Category.EVENTS
                },
                ConfigScreen.Category.values());
        int slotHeight = ConfigScreen.sidebarCategorySlotHeight(220);
        assertTrue(slotHeight >= 18);
    }

    @Test
    void shardFusionIsAnEnabledInventoryFeatureInItsOwnGroup() {
        ModConfig config = new ModConfig();
        ConfigScreen.Feature feature = ConfigScreen.Feature.SHARD_FUSION_HELPER;

        assertEquals(ConfigScreen.Category.ITEMS_AND_MENUS, feature.category);
        assertEquals(ConfigScreen.FeatureGroup.SHARD_FUSION, feature.group);
        assertTrue(feature.enabled(config));
        assertTrue(feature.hasSettings());
        assertEquals(null, feature.hudType());
        assertTrue(FeatureSettingsScreen.shardGuideEntryEnabled(config));

        feature.toggle(config);
        assertFalse(config.inventory.shardFusionHelper);
        assertFalse(feature.enabled(config));
        assertFalse(FeatureSettingsScreen.shardGuideEntryEnabled(config));
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

    @Test
    void secondarySettingSlidersShrinkInsteadOfEscapingNarrowRows() {
        var narrow = FeatureSettingsScreen.sliderLayout(20, 96);
        var wide = FeatureSettingsScreen.sliderLayout(20, 480);

        assertTrue(narrow.trackX() >= 20);
        assertTrue(narrow.trackX() + narrow.trackWidth() <= 20 + 96);
        assertTrue(wide.trackX() >= 20);
        assertTrue(wide.trackX() + wide.trackWidth() <= 20 + 480);
        assertTrue(wide.trackWidth() > narrow.trackWidth());
    }
}

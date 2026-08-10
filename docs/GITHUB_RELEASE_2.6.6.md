# QCloudy_Addition Beta 2.6.6 for Minecraft 26.1.2

Beta 2.6.6 promotes the reviewed Alpha 2.5.6 feature set to the Beta channel. QCloudy_Addition remains a standalone, client-only Fabric mod and does not require JEI, Firmament, SkyHanni, Skyblocker, or BabyzombieAddons.

## Highlights since the 1.5.3 release baseline

- Added a JEI-inspired, completely offline Attribute Shard Fusion Guide for the official 320-Shard set.
- Search by original English name, ID, effect, rarity, category, family, skill, mob type, or acquisition text.
- Browse **Details**, **Recipes**, and **Uses**, including ordered inputs, quantities, one-to-three selectable outputs, Special Fusion yields, Chameleon behavior, and Pure Reptile information.
- Added a Shard-specific bundled icon for every catalog ID while keeping a native client-received `ItemStack` as the session-priority presentation.
- Added documented effects, semantic classification, natural acquisition information, Fusion-only labels, and reverse Fusion recipe counts. Shards such as Queen Bee expose both their natural source and verified Fusion recipes.

## Fixes included from Alpha 2.5.4–2.5.6

- Replaced the generic amethyst fallback with Shard-specific icons.
- Fixed search focus so outside click, `Esc`, and `Tab` release typing; clicking the search field restores it.
- Compactly centered each input pair and output group so related Shards no longer appear misleadingly far apart.
- Corrected Epic Shards to Minecraft dark purple (`§5`) and applied reviewed semantic colours to stats, categories, mob types, acquisition methods, and rarities.
- Added hover-only darkening and underlining to clickable Shard text.
- Preserved spaces between coloured effect fragments and removed residual Wiki formatting markers.
- Preserved separate output slots when ID Fusion and Special Fusion produce the same Shard with different yields.

## Removed

- Slot locking.
- Storage Overlay.
- Menu middle-click conversion.

## Safety and compatibility

- Client-only Fabric mod for Minecraft 26.1.2; Java 25 is required.
- Required: Fabric API `0.155.2+26.1.2` or newer. Mod Menu is optional.
- The Shard guide uses bundled offline data and performs no runtime Wiki/API request, packet send, menu click, Fusion, chat send, server command, or automation.
- `/th` and `/helia` remain explicit user-triggered shortcuts and are fully documented; they never run without direct player input.
- Static/build validation cannot replace authenticated Hypixel testing or visual acceptance with every resource pack and GUI scale. Use all mods at your own risk under Hypixel's current rules.

## Files

- Playable: `QCloudy_Addition-Beta-2.6.6+26.1.2.jar`
- Sources: `QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`

The playable JAR is the file normal players should install. The Sources JAR is for source browsing and development only.

# QCloudy_Addition Beta 2.7.17 for Minecraft 26.1.2 and 26.2

Beta 2.7.17 consolidates the completed work since Beta 2.6.6. QCloudy_Addition remains a standalone, client-only Fabric mod; use the JAR matching the exact Minecraft version.

## Major additions since 2.6.6

### Shard Planner and local warehouse

- Generate complete multi-step Fusion trees for a target Shard and quantity.
- Compare alternative direct recipes or show Materials Only totals.
- Plan Fastest routes from editable Shards-per-hour rates.
- Plan Cheapest routes from an optional compatible Skyblocker cached-price API; QCA never downloads Bazaar prices itself.
- Use Ironman mode without Bazaar data.
- Configure Kraken/Kuudra tier, completion time, coins/hour, Hunter Fortune, Crocodile level, and Fusion handling time.
- Record a profile-scoped Shard warehouse only from Hunting Box pages the player physically opens.
- Browse detailed Shard effects, families, Skills, acquisition methods, rates, recipes, uses, and draggable Fusion Lines.

### Unified SkyBlock-mod controls

- Added a function-first catalog spanning QCA and the reviewed SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and BabyZombieAddons 3.4.1 builds.
- Exact-equivalent features select one provider; enabling the shared card disables only equivalent implementations from other detected providers.
- Reviewed Boolean, enum, bounded numeric, HUD position, and HUD scale values are read/written through the provider's own live config and save path.
- Enabled provider HUDs can appear in QCA's HUD editor and commit position/scale on mouse release.
- Integrations are optional, version-locked, reflection-based, and fail closed. QCA starts and works without any provider mod.

### Fishing Bite Sound

- Added an opt-in local Ciallo bite cue with an independent 0–100% volume slider, default 64%.
- Supports directly owned water hooks and Hypixel's bounded ownerless lava-hook presentation.
- Requires the exact nearby `!!!` marker and plays at most once per confirmed bite.
- Fishing is now its own top-level settings category.

### Dual Minecraft-version builds

- The QCA-owned feature set now builds separately for Minecraft 26.1.2 and 26.2.
- Each target uses its own matching Fabric API and optional Mod Menu version.
- Third-party settings/HUD adapters remain reviewed for 26.1.2 only and deliberately fail closed on 26.2 until matching provider builds are audited.

## Improvements

- Rebuilt Shard details, Planner controls, Fusion Lines, settings fields, the RGB picker, feature pages, and the HUD editor for narrow windows, long bilingual text, independent scrolling, clipping, and correct visible hitboxes.
- Replaced the bundled Dwarven Mines artwork and changed the marker to a continuous approximate X/Z-only transform across the full single-layer overview.
- Added `C&C Minecarts Co.` to Dwarven Mines recognition.

## Bug fixes

- Fixed missing lava-fishing bite audio and stopped the cue from replaying while reeling.
- Fixed owned Tree Gift creature lines such as `A wild Groundhog appeared!` being discarded, without allowing an unrelated player's public line to trigger an alert.
- Fixed an unmaxed Ancient Golden Dragon with Ancient skin being shown as `[Lvl 200]`.
- Fixed overlap and invisible-hitbox issues across compact Shard, Planner, Settings, Fusion Lines, RGB, feature-settings, and HUD-editor layouts.
- Fixed Dwarven map marker jumps/disappearance on bridges above The Mist and other vertically overlapping paths. Y and scoreboard sub-locations no longer influence marker placement.

## Replaced logic

- The old Dwarven scoreboard sub-location snapping and per-region clamping were removed and replaced by continuous real-time X/Z projection. No user-facing feature from Beta 2.6.6 was removed in this Beta.

## Safety and compatibility

- Client-only Fabric mod requiring Java 25 and Fabric Loader 0.19.3+.
- MC 26.1.2 requires Fabric API `0.155.2+26.1.2`; MC 26.2 requires `0.154.2+26.2`.
- Mod Menu is optional. SkyHanni, Skyblocker, Firmament, BabyZombieAddons, and JEI are not required.
- No automatic click, Fusion, cast/reel, movement, combat, capture, packet, chat, command, HTTP request, telemetry, or hidden server-data request was added.
- `/th` and `/helia` remain explicit user-triggered shortcuts documented in the compliance notes.
- Automated builds and static checks do not replace authenticated Hypixel, every-resource-pack, every-GUI-scale, or full-modpack testing. Use all mods at your own risk under Hypixel's current rules.

## Files

- Playable for MC 26.1.2: `QCloudy_Addition-Beta-2.7.17+26.1.2.jar`
- Sources for MC 26.1.2: `QCloudy_Addition-Beta-2.7.17+26.1.2-sources.jar`
- Playable for MC 26.2: `QCloudy_Addition-Beta-2.7.17+26.2.jar`
- Sources for MC 26.2: `QCloudy_Addition-Beta-2.7.17+26.2-sources.jar`

Install only the playable JAR matching the exact Minecraft version. Sources JARs are for source browsing and development.

# QCloudy_Addition 1.5.1 for Minecraft 26.1.2

This is the first publication-ready QCloudy_Addition build. It is a client-only Fabric mod focused on readable SkyBlock HUDs, passive visual helpers, and configurable inventory/UI quality-of-life features.

## Requirements

- Minecraft 26.1.2
- Fabric Loader 0.19.3+
- Fabric API 0.155.2+26.1.2+
- Java 25
- Mod Menu 18.0.0 is optional

## Release highlights

- Added original Dwarven Mines and layer-aware Glacite Tunnels maps.
- Added Mining, Crimson Isle, Torrhus, Hunting, and Critter Safari trackers.
- Added the full configurable Pet HUD and corrected dynamic pet-skin/head handling.
- Added BLC-inspired settings, per-HUD drag/resize, RGB/HSV colors, transparent backgrounds, sliders, animations, and inline keyboard/mouse chord editing.
- Added Firmament-inspired—but independently implemented—slot locking, timestamps, Storage Overlay, and cursor memory without a Firmament dependency.
- Added configurable Ender Dragon, Critter, Sparkling, and Beeheemoth outlines.
- Added Wumpa prerequisite tracking, Ravager-body route prediction, Snoozle wall surfaces, Cold/campfire safety, Warden readiness, Lasso REEL audio, and Fairy Soul waypoints.
- Added personal Tree Gift rare-loot parsing with per-loot controls, canceled-chat compatibility, and nearby-player rejection.
- Added a default-on, 64% Beeheemoth sound control that does not alter ordinary Bee sounds.
- Added a player-clicked reconnect button with no automatic retry loop.
- Added `/th` → `warp torrhus` and `/helia` → `chapter torrhus` client shortcuts.

## Defaults worth knowing

- English UI; Simplified Chinese is selectable.
- Alert sounds are enabled at 64% and configured per feature.
- Wumpa route prediction and Fairy Soul waypoints are off by default.
- Storage Overlay and menu middle-click conversion are off by default.
- AOTE/AOTV sounds remain vanilla until the player selects a replacement.
- Chat Peek is enabled, but its hold key is unbound by default.

## Validation

- 98 JUnit tests across 23 suites: all passed.
- Two clean Java 25 builds produced byte-identical binary and Sources JARs.
- Both archives passed `jar --validate` and `unzip -t`.
- Fabric metadata reports Minecraft 26.1.2, Java 25, and client-only environment.
- Standalone 51-module initialization passed.
- A 94-mod compatibility instance with BabyzombieAddons 3.4.1, SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and Mod Menu 18.0 reached resource and sound-engine initialization without a QCA or QCA-mixin exception.

The local instance was not authenticated to Hypixel, so live Torrhus/Safari message variants, exact entity timing, every GUI scale, the user's resource pack, and visual feel still require live player regression. See `docs/VALIDATION.md` for the exact boundary.

## Files

- `QCloudy_Addition-1.5.1+26.1.2.jar`
  - SHA-256: `e3d3131d4f1d40e7859b655aed56aa72ef9a5dae2bd045710d4bde9daf705536`
- `QCloudy_Addition-1.5.1+26.1.2-sources.jar`
  - SHA-256: `ab825c382b6f672cfc6ce2381db0a904ea60b23e593fa5254bd7e87722442ada`

## Safety note

QCA is passive and client-only, but that is not an official Hypixel approval. Review `docs/COMPLIANCE.md` and Hypixel's current rules before use. The exact user-triggered server command payloads are documented there.

# QCloudy_Addition Alpha 2.8.25 for Minecraft 26.1.2

Alpha 2.8.25 adds a complete, client-side Century Cake expiry tracker and keeps the Power Orb/Flare expiry work introduced in the previous Alpha.

## Added

- One default-on **Century Cake Effect Expiry Alert** master switch for all 20 Century Cake bonuses; there are no per-effect switches.
- Absolute 48-hour real-world timers that continue while the player is offline.
- `/cake` and `/centurycakeeffect`, both local commands that open an effects-style timer screen with cake heads, bonuses, rarity, status, and remaining time.
- Center-screen and local-chat expiry alerts. Expiries detected together are combined into one message.
- An underlined `Click Here For Cake Eating` chat action. It runs exactly `/visit northwestcloudy` only after the player clicks it.
- A feature-specific local sound, enabled by default at 64% volume.

## Preserved and refined

- Power Orb and Flare expiry alerts remain client-side and depend on exact received despawn chat lines.
- Alert settings remain grouped by feature rather than sharing one global per-effect list.
- All timers and saved state are local and profile-scoped.

## Safety boundary

QCA never automatically sends the cake-renewal command. `/cake` and `/centurycakeeffect` send no server payload; only a direct click on the underlined renewal component executes `/visit northwestcloudy`. The feature does not eat a cake, click a menu, move the player, or query the server.

## Installation

- Install `QCloudy_Addition-Alpha-2.8.25+26.1.2.jar` as the playable mod.
- Requires Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2, and Java 25.
- Do not install the `-sources.jar` as the playable mod.
- This is an Alpha build and should be marked as a pre-release.

Automated build and archive results are recorded in [VALIDATION.md](VALIDATION.md). Live Hypixel wording and the final in-game screen/chat presentation still require normal in-game regression testing.

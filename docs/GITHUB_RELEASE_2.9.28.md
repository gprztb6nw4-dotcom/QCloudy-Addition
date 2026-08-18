# QCloudy_Addition Beta 2.9.28 for Minecraft 26.1.2 and 26.2

Beta 2.9.28 consolidates the work completed in Alpha 2.8.18–2.8.28 since the previous Beta 2.8.17. It is a client-only Fabric build for Minecraft 26.1.2 and 26.2.

## Highlights

- Added optional unified settings and HUD discovery for installed SkyHanni, Skyblocker, Firmament, BabyZombieAddons, and Feesh builds. Settings and HUD scanning remain independently gated, default off, require confirmation, show progress, and fail closed for unknown provider branches.
- Added Power Orb and Warning/Alert/SOS Flare despawn alerts. Power Orbs use exact player-owned chat, while Flares require confirmed placement and a local three-minute lifecycle. Distance, effect range, and entity unloading are deliberately ignored.
- Added Century Cake expiry tracking for all 20 cakes, `/cake` and `/centurycakeeffect`, real-world 48-hour timers, cake icons, unified expiry alerts, and a click-only `/visit northwestcloudy` renewal link.
- Fixed The Park's `Jungle Island` being classified as Crystal Hollows `Jungle`, empty settings categories such as Dungeons remaining visible, and the duplicated Fishing → Fishing subgroup.
- Fixed first-use and refresh tracking for Century Cakes. Starborn Century Cake now recognises Hypixel's exact `Hunting Fortune` message and no longer remains grey after a matching activation/refresh.

## Safety and compatibility

- QCA remains standalone and client-only. The five provider mods are optional and are not build or runtime dependencies.
- Capability discovery reads installed client classes and local configuration only after confirmation. It does not contact a server or external API.
- `/visit northwestcloudy` is sent only after the player clicks the underlined Century Cake renewal text. No command is sent automatically.
- Minecraft 26.2 provider integration may omit branches where no compatible provider build is installed; QCA-owned functions remain available.

## Downloads

Playable mods:

- `QCloudy_Addition-Beta-2.9.28+26.1.2.jar`
- `QCloudy_Addition-Beta-2.9.28+26.2.jar`

Developer sources:

- `QCloudy_Addition-Beta-2.9.28+26.1.2-sources.jar`
- `QCloudy_Addition-Beta-2.9.28+26.2-sources.jar`

Install only the playable JAR matching your Minecraft version. Do not install a `-sources.jar` as the mod.

This is a Beta pre-release. Automated tests and archive checks pass, but authenticated Hypixel and full-provider-modpack regressions listed in `docs/VALIDATION.md` still require manual testing.

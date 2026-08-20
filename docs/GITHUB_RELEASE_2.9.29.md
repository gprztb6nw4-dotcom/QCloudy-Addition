# QCloudy_Addition Beta 2.9.29 for Minecraft 26.1.2 and 26.2

Beta 2.9.29 makes QCA's two compatible-mod management systems explicit and independently controllable.

## Changes since Beta 2.9.28

- Added a clearly visible **Manage Other Mod Settings** master switch under **General -> Supported Mods**. It controls recognised settings from installed compatible mods.
- Kept **Manage Other Mod HUDs** as a separate master switch. It controls recognised external HUD positions only.
- The Supported Mods group now starts expanded so neither control is confused with QCA's own **Edit HUD** button.
- Both integrations remain opt-in and default to off. Each retains its own confirmation, scan progress, Refresh flow, and fail-closed handling for unknown provider members.
- Added regression coverage requiring both independent controls to remain registered.

## Compatibility and safety

- QCloudy_Addition remains a standalone, client-only Fabric mod.
- SkyHanni, Skyblocker, Firmament, BabyZombieAddons, and Feesh are optional providers, not dependencies.
- Provider discovery reads installed client classes and local configuration only after confirmation. It does not send a server command or contact an external service.
- Unsupported or changed provider members are omitted and reported as compatibility gaps rather than guessed.

## Downloads

Playable mods:

- `QCloudy_Addition-Beta-2.9.29+26.1.2.jar`
- `QCloudy_Addition-Beta-2.9.29+26.2.jar`

Developer sources:

- `QCloudy_Addition-Beta-2.9.29+26.1.2-sources.jar`
- `QCloudy_Addition-Beta-2.9.29+26.2-sources.jar`

Install only the playable JAR matching your Minecraft version. Do not install a `-sources.jar` as the mod.

Both targets passed 193 automated tests with no failures, plus metadata and archive validation. Authenticated Hypixel and full provider-modpack visual regression remain manual checks; see `docs/VALIDATION.md`.

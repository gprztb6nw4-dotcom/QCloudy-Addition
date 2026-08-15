# QCloudy_Addition Alpha 2.8.18 for Minecraft 26.1.2 and 26.2

Alpha 2.8.18 adds a transparent, read-only way to see which recognised settings or HUD controls from installed SkyBlock mods QCA still cannot manage.

## Added

- Added **Compatibility Gaps** under **General → Supported Mods**.
- The special card has no toggle or enabled strip; left- and right-click both open the report.
- Results are grouped by SkyHanni, Skyblocker, Firmament, and BabyZombieAddons.
- Each recognised gap is labelled `[Settings]`, `[HUD Editor]`, or both. Fully supported functions are hidden.

## Compatibility behavior

- Opening the report performs a fresh read-only capability audit even when the two unified-editor master switches are off.
- Empty or unreadable recognised provider roots are shown as provider-level gaps instead of being called fully supported.
- Recognised complex color, keybind, or position structures that QCA cannot safely write can be reported without being exposed to the normal editor.
- Completely unknown future structures are not assigned invented feature names.

## Safety

The report does not change provider settings, edit provider files, send packets, commands, chat, HTTP requests, or telemetry. QCA remains standalone and client-only.

Choose the playable JAR matching the exact Minecraft version. Do not install the Sources JAR as the mod.

# QCloudy_Addition Beta 2.8.17 for Minecraft 26.1.2 and 26.2

This Beta improves the unified settings and HUD editor so normal provider updates no longer disable an entire SkyHanni, Skyblocker, Firmament, or BabyZombieAddons integration merely because its version string changed.

## Changed

- Replaced the exact-version whitelist with live capability discovery.
- Added independent **Unified Settings Editor** and **Unified HUD Editor** master switches under General; both default to off and leave QCA-owned controls untouched.
- After the relevant opt-in is enabled, re-checks installed provider configuration roots and native save/update hooks whenever QCA settings are opened.
- Keeps recognised, safely writable existing functions and HUD coordinates available after compatible provider updates.
- Omits only unknown, inaccessible, read-only, unsupported, or changed branches instead of hiding the whole provider.
- Recognises common prefixed layouts such as `enabledCommissions` with `commissionsX`, `commissionsY`, and `commissionsScale` without a version-specific field list.
- Removed the local `/aca` and `/ca` settings aliases. Use `/qca`, `/qc`, the default `O` key, or Mod Menu.

## Compatibility boundary

This is defensive best-effort compatibility, not a promise to understand every future setting automatically. If a provider removes its recognised configuration root or native save/update contract, only that provider adapter is hidden until QCA is updated. QCA never edits another mod's configuration file directly and remains independently usable without any of the four optional providers.

Use the JAR matching the exact Minecraft version. The `-sources.jar` files are source attachments, not playable mods.

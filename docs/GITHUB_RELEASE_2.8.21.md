# QCloudy_Addition Alpha 2.8.21 for Minecraft 26.1.2 and 26.2

Alpha 2.8.21 adds Feesh as QCA's fifth optional unified-settings and HUD provider.

## Added

- Capability-based Feesh setting discovery through live public getter/setter pairs.
- Native Feesh saving through `Settings.save()` after a successful user edit.
- Feesh Overlay discovery, visibility filtering, anchor-aware position conversion, scale/alignment editing, and native coordinate persistence.
- Feesh entries in visual scan progress and the provider-grouped Compatibility Gaps report.

## Compatibility and safety

- Feesh is optional and not a QCA build/runtime dependency.
- There is no exact Feesh version whitelist. Compatible branches remain usable; unsupported branches are omitted and reported without hiding valid siblings.
- Both unified-editor master switches remain independent and disabled by default.
- QCA does not invoke Feesh API, chat, command, sharing, or gameplay paths.
- Separate artifacts are provided for Minecraft 26.1.2 and 26.2. Install only the file matching the instance version.

Automated builds validate code and archives, but do not replace an authenticated in-game test with every provider combination.

# QCloudy_Addition Alpha 2.8.20 for Minecraft 26.1.2 and 26.2

Alpha 2.8.20 makes the optional unified SkyBlock-mod editors predictable, visible, and independent.

## Added

- Enabling **Unified Settings Editor** or **Unified HUD Editor** now opens a live capability-scan page.
- The page shows progress, the installed provider being read, the current phase/item, recent activity, and an editor-specific result count.
- Both pages have a manual **Refresh** action.
- A small deterministic offline classifier handles only provider functions that remain uncategorised after native paths and verified rules.

## Changed

- Settings and HUD discovery share one validated session snapshot, while their switches and displayed totals remain independent.
- Only installed providers that successfully expose readable capabilities are shown; uninstalled providers are silent.
- Refresh keeps the previous valid snapshot until the replacement has completed and validated.
- Opening the ordinary settings menu no longer rescans providers. Disabling both unified-editor switches cancels pending work and unloads the snapshot.

## Safety and compatibility

- Provider versions are not an exact whitelist. Compatible recognised branches can continue working after an update; changed branches fail individually.
- Scanning and classification are local and read-only. There is no cloud AI, model download, HTTP request, server query, packet, command, chat, telemetry, automatic input, or direct configuration-file edit.
- QCA remains standalone. SkyHanni, Skyblocker, Firmament, and BabyZombieAddons are optional.

Use the JAR matching the exact Minecraft version. Do not install the Sources JAR as the playable mod.

# QCloudy_Addition Alpha 2.8.22 for Minecraft 26.1.2 and 26.2

Alpha 2.8.22 adds an explicit second confirmation before every optional provider capability scan.

## Changes

- First enabling Unified Settings Editor or Unified HUD Editor without a valid session snapshot now opens a scope-specific confirmation window before the master switch changes or scanning starts.
- Every Refresh action opens the same confirmation flow. Cancelling keeps the current validated snapshot and performs no scan.
- An enabled master restored after restarting Minecraft no longer creates a silent startup scan.
- The dialog explains that provider metadata is read locally and may briefly use additional CPU. No server request or automatic input is performed.
- Refresh is disabled while another scan is already running.

## Supported files

- `QCloudy_Addition-Alpha-2.8.22+26.1.2.jar`
- `QCloudy_Addition-Alpha-2.8.22+26.2.jar`

Use the file matching the exact Minecraft version. Fabric API remains required. Mod Menu and all supported SkyBlock providers remain optional.

## Safety boundary

Provider scans remain deterministic, local, and read-only. This update adds no HTTP request, server query, packet, chat, command, telemetry, automatic input, or direct provider-configuration file edit.

This is an Alpha build. Automated dual-target validation is documented in `docs/VALIDATION.md`; authenticated-server behavior, every provider version, every resource pack, and every full modpack still require in-game testing.

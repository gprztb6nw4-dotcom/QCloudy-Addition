# Alpha 2.8.20 — visual on-demand provider scanning

- Added a live progress page when Unified Settings Editor or Unified HUD Editor is enabled.
- Added Refresh to both independent editor pages.
- Shared one validated session snapshot while keeping settings/HUD totals separate.
- Hidden uninstalled providers and listed only installed providers with readable capabilities.
- Added deterministic local classification only for still-uncategorised metadata.
- Removed unconditional rescanning when the normal settings menu opens.
- Scanning remains local and read-only: no cloud AI, network/server request, packet, command, telemetry, automation, or direct config-file edit.

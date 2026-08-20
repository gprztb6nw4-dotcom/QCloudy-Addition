Beta 2.9.29 separates compatible-mod settings management from compatible-mod HUD management.

- Added a visible **Manage Other Mod Settings** master switch under **General -> Supported Mods**.
- Kept **Manage Other Mod HUDs** as a separate, HUD-only switch.
- Both controls default to off and retain independent confirmation, scan progress, Refresh, and fail-closed compatibility handling.
- The Supported Mods group now starts expanded, and regression tests require both entries to remain present.
- Available for Minecraft 26.1.2 and 26.2.

QCA remains standalone and client-only. Optional provider mods are not dependencies. Install only the playable JAR matching your Minecraft version; `-sources.jar` files are for developers.

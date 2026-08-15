# Beta 2.8.17 — provider-update compatibility

- Replaced exact provider-version whitelisting with live configuration/save capability discovery.
- Added separate default-off master switches for provider settings and provider HUD editing.
- Compatible known settings and HUD coordinates remain editable after normal SkyHanni, Skyblocker, Firmament, or BabyZombieAddons updates.
- Unknown or changed branches are omitted individually instead of hiding the whole provider.
- After the relevant opt-in is enabled, provider capabilities are re-probed whenever QCA settings are opened.
- Removed `/aca` and `/ca`; `/qca` and `/qc` remain local settings commands.
- QCA remains standalone and client-only for Minecraft 26.1.2 and 26.2.

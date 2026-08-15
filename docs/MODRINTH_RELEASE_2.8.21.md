# Alpha 2.8.21 — Feesh unified controls

- Adds Feesh as the fifth optional QCA unified-settings/HUD provider.
- Detects supported live settings by capability, writes through Feesh's public setters, and saves through Feesh itself.
- Adds enabled/non-empty Feesh overlays to Edit HUD with correct alignment-anchor conversion and native persistence.
- Reports unsupported Feesh settings/HUDs in Compatibility Gaps instead of guessing or hiding valid siblings.
- Keeps both integration master switches independent, opt-in, and disabled by default.
- Built separately for Minecraft 26.1.2 and 26.2; Feesh remains optional.

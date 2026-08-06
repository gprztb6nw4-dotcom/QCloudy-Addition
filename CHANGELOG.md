# Changelog

All notable public changes to QCloudy_Addition are documented here.

## [1.5.1] - 2026-08-06

First publication-ready build for Minecraft 26.1.2.

### Added

- Dwarven Mines and three-layer Glacite Tunnels maps.
- Mining commissions, Mithril/Gemstone/Glacite Powder, HOTM slot, and Crimson Isle task tracking.
- Torrhus Chapter/resource, Tree Critter, Miria Contest, Benefactor, and personal Tree Gift tracking.
- Critter Safari Dashboard/Critterdex, Cold/campfire, Doomspiral, Warden, Sparkling, Floor Drop, quest-item, Wumpa, Snoozle-wall, Safari Belt, and Critter highlight helpers.
- Beeheemoth outline, spawn beacon, and spatial sound-volume control.
- Configurable Lasso REEL audio and center-screen alert system.
- Equipped Pet HUD with verified player heads, skins, XP, overflow levels, and held items.
- Ender Dragon outline, Chat Peek, slot/item protection, item timestamps, cursor memory, Storage Overlay, configurable teleport sounds, and menu middle-click conversion.
- Bilingual BLC-inspired settings and per-HUD editor.
- Manual reconnect button, `/th`, and `/helia` client shortcuts.

### Fixed

- Removed every legacy pre-rendered pet-icon fallback that could show a wrong or blurred icon.
- Prevented max-level pets from showing a redundant max-XP line while retaining their held-item row.
- Prevented bold text and long task/pet lines from overflowing or being shortened with ellipses.
- Prevented Safari capture Armor Stands from receiving Critter outlines.
- Fixed Wumpa party Loot Share progress, spawned-state HUD replacement, and Ravager-body route selection.
- Fixed four Safari Belt milestone layouts and account/profile persistence.
- Fixed Helia Chapter, Benefactor, Whispers, Essence, Forest Fortune, and Sweep acquisition/persistence.
- Fixed nearby-player and repeated Tree Gift alerts with a bounded personal-ownership state machine.
- Hardened Storage cache registry rebinding so one stale item cannot crash the render thread.
- Replaced the final deprecated loaded-chunk call without expanding scan scope.

### Changed

- Renamed the project and controls category to QCloudy_Addition / QCloudy Addition.
- Reorganized settings into General, Maps, Mining, Foraging, Hunting, Safari, Crimson Isle, Combat, Pets, Chat, and Inventory with no duplicate feature cards.
- Made Storage Overlay and menu middle-click conversion opt-in.
- Kept AOTE/AOTV teleport sounds vanilla by default and exposed sound, volume, and pitch choices.
- Standardized alert volume defaults at 64%.

### Removed

- Catch-all `ALL` settings category.
- Golden Dragon/Dragon's Lair finder.
- Duplicate feature switches, redundant right-click hints, and separate key-capture screen.
- Runtime Firmament dependency and legacy pet PNG selection.

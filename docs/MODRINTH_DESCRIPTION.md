# QCloudy_Addition

**A client-only Hypixel SkyBlock utility mod for Fabric on Minecraft 26.1.2 and 26.2.**

QCloudy_Addition brings maps, objective tracking, hunting and foraging helpers, pet information, a complete offline Attribute Shard Fusion guide/planner, and deeply configurable HUDs into one clean bilingual interface.

The interface is English by default and can be switched to Simplified Chinese. Names received from Hypixel—locations, items, tasks, pets, skins, accessories, Shards, and player-renamed HOTM slots—remain in their original form so translations never change the meaning of game data.

> **Current channel: Beta 2.8.17.** The same QCA-owned feature set is built for Minecraft 26.1.2 and 26.2. Optional third-party settings/HUD adapters use capability discovery instead of an exact-version whitelist.

## Main features

### Unified settings and HUD editor — Beta

- One function-first catalog for QCA and safely recognised live settings from installed SkyHanni, Skyblocker, Firmament, and BabyZombieAddons builds.
- Separate **Unified Settings Editor** and **Unified HUD Editor** master switches under General; both default to off, work independently, and never hide QCA-owned controls.
- Exact top-level order: General, Maps, Items & Menus, Combat, Dungeons, Slayer, Mining, Farming, Foraging, Fishing, Hunting, Rift, Events.
- One shared card for one exact function. Right-click selects the provider first, then edits that provider's safe native Boolean, enum, numeric, position, and scale settings.
- Enabling a shared card enables the selected implementation and disables only exact equivalents in the other compatible providers.
- With its independent master enabled, Edit HUD includes selected, enabled third-party HUDs and writes native position/scale on mouse release.
- Optional capability-detected integrations: provider version changes alone do not hide a mod; compatible known fields remain editable and unsupported new structures are omitted safely.

### Maps

- The supplied single-layer **Dwarven Mines map**, recalibrated region by region for an X/Z-synchronized live player arrow without Y-layer switching.
- Three coordinate-aligned **Glacite Tunnels maps** that switch automatically by player elevation.
- Optional pink **Fairy Soul waypoint beams** for Torrhus and Critter Safari, with locally saved collected state.

### Items, menus, and pets

- A standalone, JEI-inspired **Attribute Shard Fusion Guide** covering the official 320-Shard catalog.
- Search by English Shard name, ID, effect, rarity, category, family, skill, mob type, or acquisition source.
- Separate **Details**, **Recipes**, and **Uses** views with ordered inputs, quantities, selectable outputs, Special Fusion yields, Chameleon behavior, and Pure Reptile information.
- Shard-specific bundled icons, documented natural/Fusion acquisition methods, semantic game colors, and complete reverse recipes—including Shards such as Queen Bee that have both natural and Fusion sources.
- A separate **Shard Planner** with target quantities, bounded multi-step Fusion trees, other candidate routes, Materials Only totals, direct input/output recipe filters, editable Shards/hour rates, draggable Fusion Lines, and a local per-profile Hunting Box warehouse.
- Ironman planning never uses Bazaar. Normal Fastest can compare hunting and buying time; Normal Cheapest uses an optional compatible Skyblocker price cache. QCA never downloads prices and has no hard dependency. If no stable provider exists, price routes are clearly unavailable while all offline and rate-based features remain usable.
- An equipped **Pet HUD** with verified pet/skin heads, rarity-colored names, XP, progress to maximum level, supported overflow levels, skin names, and pet-item icons/names.
- Item creation timestamps, cursor-position memory, and configurable AOTE/AOTV teleport sounds.

The Shard database and fallback icons are bundled with the mod. The guide performs no runtime Wiki/API request and never clicks a menu or executes a Fusion. Use `/qshard [English query]` to open it locally.

### Combat

- Configurable-color **Ender Dragon outlines** while in The End or Dragon's Nest.
- A complete, non-truncating **Crimson Isle faction quest HUD** built from the received Tab widget; completed tasks are hidden once confirmed complete.

### Mining

- Full-name mining objectives with compact progress bars and percentage or current/target display modes.
- Mithril, Gemstone, and Glacite Powder tracking.
- Selected HOTM loadout-name display.
- Support for Dwarven Mines, Crystal Hollows, Glacite Tunnels, and Glacite Mineshafts.

### Foraging

- A combined **Torrhus HUD** for Helia Chapter progress, Forest/Desert Whispers, Forest Essence, Safari Essence, Sweep, and Forest Fortune.
- A separate **Galatea HUD** with Hina Chapter and Agatha's Contest information.
- Tree Critter countdowns read from the visible Tree Protection Order state instead of a guessed local timer.
- Miria Contest tier/remaining-score information, Benefactor state, and personal rare Tree Gift alerts with per-loot controls.

### Hunting and Critter Safari

- Beeheemoth outline, temporary spawn beacon, and independent spatial Beeheemoth-sound volume.
- Lasso `REEL` cue and center-screen Critter behavior instructions.
- Safari session dashboard, optional Shard statistics, Critterdex progress, and biome-grouped results.
- Cold threshold alerts and nearest loaded campfire beacon.
- Doomspiral readiness, Warden cooldown, Sparkling Critter, Floor Drop, Quest Item, Wumpa, Snoozle-wall, and Safari Belt helpers.
- Real Critters use their Shard-rarity outline color; Armor Stand capture props are excluded.
- Experimental Wumpa route projection is optional and disabled by default.

### General and chat

- A manual **Reconnect** button with no timer, retry loop, or automatic join.
- **Chat Peek** with keyboard, mouse-button, and modifier combinations.
- In-place hotkey editing, including mouse buttons and multi-key chords.
- Thirteen ordered top-level categories—General, Maps, Items & Menus, Combat, Dungeons, Slayer, Mining, Farming, Foraging, Fishing, Hunting, Rift, and Events—with searchable collapsible groups and exactly one owner for every feature.

## HUD customization

Each HUD stores its own position and 50–200% scale. Background color and transparency, border visibility/width/color, title color, bold text, and text shadow are configured independently.

The HUD editor only exposes panels that are currently loaded and have real content. Drag a panel to move it or drag its border/corner to resize it; changes persist across restarts. Empty panels do not leave title-only boxes on screen.

Center-screen alerts have per-feature sound switches and 0–100% volume sliders. Alert volume defaults to 64%, with an additional General master mute.

## Installation

Required:

- Minecraft **26.1.2** or **26.2** (use the matching file)
- Fabric Loader **0.19.3+**
- Fabric API **0.155.2+26.1.2** for MC 26.1.2, or **0.154.2+26.2** for MC 26.2
- Java **25**

Optional:

- Mod Menu **18.0.0** for MC 26.1.2, or **20.0.1** for MC 26.2

Put either `QCloudy_Addition-Beta-2.8.17+26.1.2.jar` or `QCloudy_Addition-Beta-2.8.17+26.2.jar` in the instance's `mods` folder, matching the exact game version. Do not install the `-sources.jar` as the playable mod.

Press `O`, open QCA through Mod Menu, or enter `/qca` or `/qc` to open settings. These settings aliases and `/qshard` are local client commands and are not sent to Hypixel.

## Client-only boundary

QCA reads only information already available to the client, such as received Tab/scoreboard/chat/title text, currently open menus, local inventory, loaded entities, and already-loaded blocks. It contains no macro, automatic movement, automatic combat, automatic capture, telemetry, remote updater, hidden chunk request, or runtime Hypixel API dependency.

The only server-command payloads implemented by QCA are:

- `/th` → `warp torrhus`
- `/helia` → `chapter torrhus`

Both are sent only after the player explicitly enters the corresponding local shortcut. Manual reconnect creates one normal Minecraft connection only after the player clicks the button.

## Compatibility and disclaimer

QCloudy_Addition is standalone. Firmament, SkyHanni, Skyblocker, BabyZombieAddons, JEI, and Mod Menu are not required. The unified editor does not reject a provider only because its version changed: it probes recognised live configuration and save capabilities, keeps compatible existing functions editable, and omits new or changed structures it cannot safely understand. Complex native color/keybind editor objects remain in their provider's own screen until their contracts are supported. A compatible Skyblocker is optional only for Bazaar-price routes; without it the planner's price mode is unavailable, not broken. SkyHanni and Firmament are not price providers because they expose no stable public cross-mod price API.

All Minecraft modifications are used at the player's own risk. Passive HUDs, outlines, waypoint beams, overlays, and predictions are not the same as official Hypixel approval. Review Hypixel's current modification rules and disable any feature you are not comfortable using.

QCloudy_Addition is not affiliated with or endorsed by Hypixel Studios, Mojang Studios, or Microsoft.

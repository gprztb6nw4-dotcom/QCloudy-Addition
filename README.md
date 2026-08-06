# QCloudy_Addition

QCloudy_Addition is a client-only Fabric mod for Minecraft 26.1.2. It adds compact Hypixel SkyBlock maps and read-only HUD tools while deliberately avoiding gameplay automation, server-state mutation, remote APIs, and runtime network services.

Default language: English. Press `O` (rebindable under Controls → Key Binds → QCloudy_Addition) or use `/aca`, `/qca`, `/ca`, or `/qc` to open the client-side settings, then switch to Simplified Chinese at any time. Command aliases are registered only when their client-command names are free. They open a local screen and are never sent to Hypixel.

The language option translates QCA interface labels only. Hypixel location names, task names, pets, skins, accessories, items, and player-renamed HOTM slots remain in their original client-received form.

## Feature categories

### General

- **Manual Reconnect** — adds one vanilla-sized `Reconnect` button to connection-failed and disconnected screens. The target is captured when the normal connection attempt begins, so the button also works after an initial failure. It reconnects only after the player clicks it; there is no timer, loop, retry counter, command, or automatic join.

### Maps

- **Dwarven Mines Map** — an original single-layer overview with 12 individually shaped region blocks, material-themed colors, thick readable boundaries, Minecraft bitmap labels, and a live red player arrow. It omits the old internal route web. The arrow is projected from the local player's X/Y/Z/yaw and the already-visible scoreboard sub-location. Point-of-interest labels always use Hypixel's original English names.
- **Glacite Tunnels Layer Map** — low, middle, and high tunnel images share one coordinate system. The displayed layer changes at Y 126 and Y 143; the live arrow remains spatially consistent between layers. Generated English point-of-interest cards use collision avoidance so nearby locations never overlap.

### Mining

- **Mining Tasks & Powders** — displays the `Commissions:` and `Powders:` widgets already received in the player list. Every task uses its full, untruncated name and a separate progress bar. A bar ends at approximately the widest complete task name instead of stretching across the fixed panel; normal and bold styles are measured exactly, with enough room reserved for the full progress value. Progress defaults to one-decimal percentage mode and can be changed to current/target mode in the feature settings. Exact server-provided counts take priority; otherwise known targets are derived from the documented commission definition, while unknown future tasks safely remain percentages. An optional, default-on `HOTM: <slot name>` line caches the selected Heart of the Mountain loadout name observed in the slot/loadout menu. It supports Dwarven Mines, Crystal Hollows, Glacite Tunnels, and Glacite Mineshafts and separately shows Mithril, Gemstone, and Glacite Powder.

### Crimson Isle

- **Faction Task Tracker** — while on Crimson Isle, reads only the received `Faction Quests:` Tab widget and shows every original quest name, required amount, and the server's `✖`/`✔` status without shortening or translating it. It is a separate, default-on feature and shares the mutually exclusive task HUD position/style with the mining tracker.

### Foraging

- **Torrhus Chapter & Resources** — one wrapped, non-truncating HUD for the current Helia Chapter/task/progress plus Forest Whispers, Desert Whispers, Forest Essence, Safari Essence, Sweep, and Forest Fortune. Tab and scoreboard are parsed as separate bounded sources so a later `SB Level` fraction cannot become the Chapter task. The real `Helia's Chapters` overview and chapter-detail inventory layouts are supported, as are short split chat blocks. Confirmed absolute values are cached separately for each Minecraft account and received SkyBlock profile, survive reconnects, and change only when the client observes a newer value; stale non-Chapter tasks from older configs are repaired on load. Chat gain messages remain bounded additive updates. Safari Essence is intentionally not repeated inside Critter Safari. Optional completed-count, total-progress, and next-unlock rows default off.
- **Tree Critter Timer** — default-on and independently switchable. It reads the nearest visible `Critter in: 26m 47s` Tree Protection Order nameplate and adds that exact countdown to the combined Hunting HUD. It does not start a guessed local timer, so Fun-Sized (60m), Family-Sized (30m), Jumbo (15m), Behemoth (instant), Honeycomb Artifact acceleration, Honey Serendipity instant procs, and future server-side modifiers remain accurate.
- **Miria Contest** — parses received scoreboard/Tab tier lines such as `COMMON with 151` and `Uncommon requires +99`, then shows the next bracket, exact remaining score, and estimated Safari Ticket only in the combined Hunting HUD. It does not inject into the right sidebar or duplicate its contest timer.
- **Benefactor & Tree Gifts** — Benefactor state is merged from bounded Tab/scoreboard blocks, the already-open Forest/Desert Temple menu, and the player's exact received donation message. Multi-day donations, countdowns, temple-specific effects, expiration, and account/profile persistence are supported; a newly received donation is protected from a briefly stale open menu. Rare Tree Gift rewards are read from the player's exact personal reward-summary hover and from exact bonus rows inside that same bounded, ownership-proven gift block. This also consumes raw client-received messages canceled by compatible chat compactors; a nearby player's public drop line by itself never arms an alert.

### Hunting

- **Beeheemoth & Lasso cues** — detects only the reference-mod signature of a scale-9 Bee. Its vanilla outline is default-on with the shared RGB/HSV color picker, while a yellow beacon marks the first visible spawn position until the player comes within 10 blocks, receives their own Beeheemoth capture confirmation, or the entity disappears. Bee sounds spatially associated with that scale-9 entity—including its short spawn/capture window—have their own default-on 64% volume control; ordinary Bee sounds elsewhere are untouched. A separate default-on, 64%-volume cue plays once when the local player's visible Lasso state changes to the exact `REEL` label.
- **Critter Behavior Assistant** — center-screen prompts for documented special Critter mechanics, with bounded suppression after the received capture confirmation.
- **Fairy Soul Waypoints** — one cross-island Hunting feature with independent Torrhus and Safari coordinate switches; it appears only in this category.

### Safari

- **Safari Run Dashboard & Critterdex** — session Shards, timer, Ticket Tier, four-biome progress, and complete current-biome captured/missing lists across the official 37 Critters.
- **Cold, Doomspiral, Critter, Snoozle, and Wumpa helpers** — two configurable Cold warnings (80/90 by default), an immediate red nearest-loaded-campfire beacon above the first threshold that closes once Cold begins falling, a 4-Soothing-Incense warning, a dedicated Doomspiral Warden capture-ready alert, official Shard-rarity real-entity Critter outlines, and an optional red Wumpa motion/collision projection. The Wumpa HUD accepts personal and teammate Loot Share captures for its eight Icy party prerequisites, then replaces the checklist with `Wumpa: Spawned`; projection follows the real Ravager body. A separate default-green RGB option overlays only nearby exposed `Cobbled Deepslate + Tuff` Snoozle wall faces. Armor Stand capture props are excluded from highlighting to prevent support-body outlines. Wumpa route prediction defaults off; the remaining helpers default on.
- **Sparkling, Floor Drop, and Quest Item assistants** — center alerts and read-only HUD state from received chat, visible names/entities, nearby already-loaded String blocks, and local inventory. Sparkling outline color is editable.
- **Safari Belt details** — embeds all four locally observed Cavern/Forest/Haunted/Icy milestone levels and received attribute bonuses in the actual belt tooltip. Split title/lore menu layouts are supported; confirmed levels are saved per account/profile and only increase when a higher observed level is received.

Foraging, Hunting, and Safari are mutually exclusive settings categories: every feature card has exactly one owner category. All related warnings use center titles. Every alert feature owns its own default-on 64% sound and continuous 0–100% volume slider; General also has a master mute. The combined HUD has its own persisted appearance, scale, and position in **Edit HUD**.

### Combat

- **Ender Dragon Highlight** — puts Hypixel Ender Dragons in the vanilla outline pipeline while the scoreboard location is The End or Dragon's Nest. The outline color is selectable from red, yellow, cyan, green, purple, and white.

### Pets

- **Equipped Pet HUD** — uses summon/despawn/Autopet chat notices for immediate state changes, then treats the received `Pet:` Tab widget as the source of truth. It constructs a plain player head from QCA's bundled verified profile and never adds synthetic `petInfo`, so another mod cannot replace the HUD icon with an unrelated item model. Dynamic skin-family frames—including all published Baby Spinosaurus variants—map back to their real skin. The HUD never shortens a pet, skin, XP, or accessory line with an ellipsis; bold text is measured before sizing. Current-level and max-level XP lines are independently switchable and default to on, while the max-level line is automatically hidden for a maxed pet without hiding its held item. A held item confirmed through the Pets menu, Tab, or received chat is retained locally across reconnects. Optional skin-name and cosmetic-overflow-level display are enabled by default. Ancient Golden Dragon overflow levels are derived only from received total/overflow XP. All 87 current pet-item resources are indexed; the held item can be shown as icon + name (default), icon only, or name only. Standard pets use their rarity-adjusted level-100 curve; Golden, Jade, and Rose Dragons use their level-200 curve.

### Chat

- **Chat Peek** — hold a user-defined key or modifier combination to temporarily render the focused-height chat history without opening Chat. While peeking, the mouse wheel defaults to scrolling chat; the secondary setting can leave it controlling the hotbar instead. The peek key is intentionally unbound by default to avoid conflicts.

### HUD appearance

- Left-click a feature card to toggle it; the blue strip on its left is the only enabled-state indicator. Right-click still opens that feature's complete secondary settings page, without a redundant on-card hint.
- Per-HUD background opacity/color, border visibility/width/color, title color, bold text, and text shadow
- A shared RGB/HSV color picker with a wheel, brightness and R/G/B sliders, color presets, and a Transparent choice for every background color
- Per-HUD 50–200% scale; drag a loaded HUD's border or corner like a desktop window to resize it
- The bottom-left **Edit HUD** button opens an editor containing only HUDs currently loaded by the player's location/state; drag to reposition and use each panel's small gear for its settings
- Positions and individual scales are saved on mouse release and persist across restarts
- UI opening animations are enabled by default and can be disabled
- Optional Mod Menu integration opens QCA's settings directly when Mod Menu is installed

The configuration screen uses a compact BLC-inspired information hierarchy—not copied assets or layout code—with one **Features** tab, category navigation, and searchable feature cards. **General** is the first category and contains **UI animations**, the alert master mute, and the manual reconnect toggle; HUD position editing remains available from the bottom-left **Edit HUD** button. Feature cards no longer repeat a top-right switch or bottom-right right-click hint, and secondary pages do not repeat the primary enable switch. There is deliberately no catch-all `ALL` category: every feature appears only under its own category.

Inventory tools include the default-off, resource-pack-aware **Storage Overlay** and its page/player-inventory backgrounds, scrolling anywhere inside the Storage viewport, enlarged player inventory rendering, and small top-left lock stars. Storage cache items are rebound to the active world registry before persistence; a stale dynamic-registry component is isolated to its own slot instead of crashing the render thread or discarding the other pages. Every QCA hotkey is edited inline on its existing secondary-settings page instead of opening a separate capture screen. Keyboard keys, mouse buttons 1–5/side buttons, and Ctrl/Shift/Alt/Cmd-Super combinations are supported; while a row is listening, `Esc` clears it to unbound. Middle-click menu conversion is default-off; when first enabled it converts only physical left-click item buttons unless the player selects another mode.

**AOTE/AOTV sound settings** never silence teleport tools by default. Instant Transmission and Etherwarp each default to their original sound and can independently use Chorus Teleport, Enderman Teleport, Amethyst Chime, Experience Orb, End Portal Fill, or Shulker Teleport. Custom volume and pitch are continuous 10–200% and 50–200% sliders. Other broad numeric settings—including HUD opacity/scale, Storage height/scroll speed/padding/margin, and cursor-memory duration—use Windows-style draggable sliders and save on release; short discrete choices remain buttons.

## Installation

1. Install Minecraft 26.1.2, Fabric Loader 0.19.3 or newer, Fabric API 0.155.2+26.1.2 or newer, and Java 25.
2. Put the `QCloudy_Addition-*.jar` release in the instance's `mods` folder. Mod Menu is optional.
3. Start the game and press `O` or type one of the local settings commands to configure the mod.

## Building from source

Install JDK 25 and run `./gradlew clean build`. The repository includes its own pinned Gradle 9.6.1 Wrapper and Fabric Loom 1.17.17 configuration; the inspected reference mods are not build or runtime dependencies. Pet profile metadata is generated offline from a local NEU repository snapshot and committed into QCA resources. The shipped mod performs no runtime network request and runs without Firmament.

## Safety boundary

The release contains no `sendChat`, Hypixel Mod API subscription, WebSocket, HTTP client, macro, automatic movement, or chunk-request code. Its normal HUD features consume only client-received state. Explicit Storage controls send only `storage`, `enderchest <1-9>`, or `backpack <1-18>` for the page physically selected by the player. The always-available local `/th` and `/helia` shortcuts send exactly `warp torrhus` and `chapter torrhus`, equivalent to entering `/warp torrhus` and `/chapter torrhus`; they run only when the player types the corresponding shortcut. No command, chat, click, or movement action is generated without physical user input.

Hypixel states that all modifications are used at the player's own risk and that an unlisted feature is not guaranteed to be allowed. Review [docs/COMPLIANCE.md](docs/COMPLIANCE.md) and the current [Hypixel Allowed Modifications guide](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications) before use.

Chinese documentation: [README_zh_CN.md](README_zh_CN.md)

Detailed feature specification: [docs/FEATURES.md](docs/FEATURES.md)

Implementation and data flow: [docs/IMPLEMENTATION.md](docs/IMPLEMENTATION.md)

Modrinth-ready description: [docs/MODRINTH_DESCRIPTION.md](docs/MODRINTH_DESCRIPTION.md)

GitHub release notes: [docs/GITHUB_RELEASE_1.5.1.md](docs/GITHUB_RELEASE_1.5.1.md)

Publication checklist: [docs/PUBLISHING_CHECKLIST.md](docs/PUBLISHING_CHECKLIST.md)

Changelog: [CHANGELOG.md](CHANGELOG.md)

Release validation: [docs/VALIDATION.md](docs/VALIDATION.md)

2026-08-04 crash analysis: [docs/CRASH_ANALYSIS_2026-08-04.md](docs/CRASH_ANALYSIS_2026-08-04.md)

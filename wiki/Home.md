# QCloudy_Addition Wiki

![QCloudy_Addition icon](https://raw.githubusercontent.com/gprztb6nw4-dotcom/QCloudy-Addition/main/src/main/resources/assets/qcloudy_addition/icon.png)

QCloudy_Addition is an English-first, bilingual, client-only Fabric mod for Hypixel SkyBlock. It provides readable maps, task and resource HUDs, hunting and fishing cues, pet information, an offline Attribute Shard guide and planner, configurable visual helpers, and a function-first settings and HUD editor.

> **Current build:** Alpha 2.6.15 for Minecraft 26.1.2  
> **Required:** Java 25, Fabric Loader 0.19.3 or newer, and Fabric API 0.155.2+26.1.2 or newer  
> **Optional:** Mod Menu and reviewed builds of supported SkyBlock mods  
> **Important:** Every Minecraft modification is used at the player's own risk. QCloudy_Addition is not affiliated with Hypixel or Mojang.

## Contents

- [Installation](#installation)
- [Opening QCloudy_Addition](#opening-qcloudy_addition)
- [Settings and HUD editing](#settings-and-hud-editing)
- [Feature guide](#feature-guide)
- [Attribute Shard guide and planner](#attribute-shard-guide-and-planner)
- [Optional unified mod controls](#optional-unified-mod-controls)
- [Commands and outbound actions](#commands-and-outbound-actions)
- [Client-only and safety boundary](#client-only-and-safety-boundary)
- [Configuration and saved data](#configuration-and-saved-data)
- [Compatibility](#compatibility)
- [Troubleshooting](#troubleshooting)
- [Reporting a bug](#reporting-a-bug)
- [Project status and validation](#project-status-and-validation)
- [License and credits](#license-and-credits)

## Installation

1. Install **Minecraft 26.1.2**.
2. Install **Fabric Loader 0.19.3 or newer**.
3. Install **Fabric API 0.155.2+26.1.2 or newer**.
4. Run the instance with **Java 25**.
5. Download the playable `QCloudy_Addition-*.jar`. Do not install the file ending in `-sources.jar` as the playable mod.
6. Put the playable JAR in the instance's `mods` folder.
7. Remove older QCloudy_Addition JARs from the same folder to avoid loading duplicate versions.
8. Start Minecraft and open the settings with `O` or a local settings command.

Mod Menu is optional. SkyHanni, Skyblocker, Firmament, and BabyZombieAddons are **not required** for QCloudy_Addition to start or for its own features to work.

## Opening QCloudy_Addition

Use any of the following local entry points:

- Press `O` by default. The binding can be changed under **Controls → Key Binds → QCloudy_Addition**.
- Type `/aca`, `/qca`, `/ca`, or `/qc`.
- Open QCloudy_Addition from Mod Menu when Mod Menu is installed.

The four settings commands are client-side commands. Each alias is registered only when another client command has not already claimed the same name, and none of them is sent to Hypixel.

The interface language defaults to English and can be changed to Simplified Chinese. QCA translates its own interface labels, while Hypixel-provided item, pet, skin, task, location, and player-renamed preset names remain in their original client-received form.

## Settings and HUD editing

The settings screen is organized by function rather than by source mod. Its top-level order is:

1. General
2. Maps
3. Items & Menus
4. Combat
5. Dungeons
6. Slayer
7. Mining
8. Farming
9. Foraging
10. Fishing
11. Hunting
12. Rift
13. Events

Subgroups such as **Safari**, **Garden**, **Crimson Isle**, and **Kuudra** are collapsible and start closed. A function has one owner and appears in one place rather than being duplicated across categories.

### Feature cards

- **Left-click** a feature card to enable or disable it.
- A blue strip on the left side of the card indicates that the feature is enabled.
- **Right-click** a card to open all settings belonging to that function.
- Secondary pages do not repeat the primary enable switch.
- Broad numeric values use draggable sliders; sound volume uses 0–100% and defaults to 64% unless a feature explicitly states otherwise.
- Color settings use the shared RGB/HSV picker, presets, and a Transparent background option.

### Edit HUD

Select **Edit HUD** at the bottom-left of the settings screen.

- Only enabled HUDs that are currently loaded and have content are editable.
- Drag a panel to move it.
- Drag its border or corner to resize it, like a desktop window.
- Each HUD keeps its own 50–200% scale.
- The small gear button opens that HUD's settings.
- Position and scale are saved when the mouse is released and persist after restart.
- A HUD with no visible rows does not leave an empty title or background panel on the screen.
- Background color/opacity, border, border width/color, title color, bold text, and text shadow are configurable per HUD.

## Feature guide

### General

- **Manual Reconnect** adds one normal Reconnect button to connection-failed and disconnected screens. It reconnects only after a click and has no automatic retry loop.
- **Chat Peek** temporarily shows expanded received chat while a configurable key or key combination is held. The mouse wheel can scroll chat or remain assigned to the hotbar.
- **Interface animation and shared presentation controls** configure local menu behavior without changing gameplay.

### Maps

- **Dwarven Mines Map** uses the supplied single-layer 12-region map with a live red player arrow. Projection uses the local player's X/Z position and yaw; Y is deliberately ignored.
- **Glacite Tunnels Map** selects low, middle, or high artwork from the local Y coordinate and keeps the live arrow aligned across layers.
- **Fairy Soul Waypoints** can show the bundled Torrhus and Safari coordinates as optional pink beacon beams. This is one cross-island Maps feature rather than separate duplicate switches.

Map labels and points of interest use canonical English names.

### Items & Menus

- **Equipped Pet HUD** shows the received pet level, rarity-colored name, real pet or skin head, XP progress, remaining XP to maximum, optional skin, and held pet item. Max-level pets automatically hide the redundant remaining-XP line without hiding their held item.
- **Attribute Shard Fusion Guide and Planner** provide offline recipe browsing, reverse uses, detailed Shard information, route planning, materials, warehouse counts, and Fusion Lines. See the dedicated section below.
- **Item timestamps** display locally observed item creation times.
- **Cursor memory** restores configured menu cursor positions for supported interfaces.
- **AOTE/AOTV sounds** allow Instant Transmission and Etherwarp sounds, volume, and pitch to be customized locally. Teleport tools are not muted by default.

Slot locking, Storage Overlay, and menu middle-click conversion were removed from the mod rather than merely hidden from settings.

### Combat

- **Ender Dragon Highlight** places received Hypixel Ender Dragons in Minecraft's vanilla outline pipeline while the player is in The End or Dragon's Nest. The outline color is configurable.
- **Crimson Isle Faction Tasks** show incomplete received `Faction Quests:` rows with their original names and progress. Completed tasks are omitted from the HUD.
- Compatible provider-backed Crimson Isle and Kuudra functions are grouped under Combat when the reviewed provider build is installed.

### Dungeons and Slayer

These categories are reserved for exact, function-matched settings exposed by compatible installed providers. QCA does not invent a replacement implementation when no reviewed provider is present, and incompatible adapters fail closed.

### Mining

- **Mining Tasks & Powders** reads received Tab data in Dwarven Mines, Crystal Hollows, Glacite Tunnels, and Glacite Mineshafts.
- Commission names are kept complete and are shown with progress bars.
- Progress can be displayed as a one-decimal percentage or as current/target values.
- Mithril, Gemstone, and Glacite Powder are tracked separately.
- The optional `HOTM: <slot name>` row remembers a selected Heart of the Mountain preset observed in the relevant menu.

### Farming

Farming and Garden functions supplied by reviewed compatible providers appear in this category. Provider selection affects only exact equivalent functions; nearby profit, tooltip, tracker, or price features are not combined simply because their names are similar.

### Foraging

- **Torrhus Chapter & Resources** combines the current Helia Chapter, full task/progress, Forest Whispers, Desert Whispers, Forest Essence, Safari Essence, Sweep, and Forest Fortune in one wrapped HUD.
- **Galatea tracking** uses separate settings for Hina Chapter and Agatha's Contest while following the same content and empty-panel rules.
- **Tree Critter Timer** displays the exact visible server countdown rather than predicting one locally.
- **Miria/Agatha Contest information** can show the next bracket, remaining score, and estimated Safari Ticket without duplicating the scoreboard timer.
- **Benefactor state** is derived from bounded received Tab, scoreboard, chat, and physically opened menu content.
- **Tree Gift alerts** use the player's ownership-proven reward block. Configured rare loot and exact creature lines such as `A wild Groundhog appeared!` can create a center title and local sound, while an unrelated nearby player's public line does not arm an alert by itself.

### Fishing

- **Fishing Bite Sound** is disabled by default.
- It detects the exact nearby `!!!` marker associated with the local player's water hook or bounded Hypixel lava-hook presentation.
- It plays the bundled Ciallo sound once per hook at the configurable 0–100% volume, default 64%.
- Reeling an active hook does not re-arm or replay the cue.
- It never casts or reels automatically.

### Hunting

Safari belongs to Hunting and appears as a collapsible subgroup.

- **Beeheemoth helper** provides an optional outline, a temporary yellow spawn beacon, and an independent 64%-default Beeheemoth sound control.
- **Lasso REEL cue** plays once when the local player's visible Lasso state changes to the exact `REEL` label.
- **Critter Behavior Assistant** provides center-screen prompts for supported special Critter mechanics.
- **Safari Run Dashboard & Critterdex** show session time, Ticket Tier, captured/missing Critters, and optional Shard totals organized by biome and rarity.
- **Cold safety** uses configurable warning thresholds and can mark the nearest already-loaded campfire until Cold begins falling.
- **Doomspiral and Warden readiness** provide local inventory/entity-state warnings.
- **Critter highlights** use received entities, Shard rarity colors, and vanilla outlines without highlighting Armor Stand capture props.
- **Sparkling, Floor Drop, Quest Item, Wumpa, and Snoozle helpers** render local alerts, HUD state, outlines, lines, or exposed wall faces from already-received/loaded data.
- **Safari Belt details** add all four observed milestone levels and received bonuses to the real belt tooltip and persist higher confirmed values per profile.

Wumpa route prediction and Fairy Soul beams default off. Other defaults are documented on their feature cards and secondary pages.

### Rift and Events

These categories contain supported exact functions provided by compatible installed mods. When a provider is missing, unreviewed, or incompatible, QCA hides its adapter instead of guessing field names or editing files belonging to an unloaded mod.

## Attribute Shard guide and planner

Type `/qshard` to open the local Shard screen, or `/qshard <English query>` to open it with a search already filled in. The command does not send a message or command to Hypixel.

### Guide

The bundled guide contains all 320 current Bazaar-listed Attribute Shards and their Shard-specific icons.

- Search by canonical English name, Shard ID, effect, rarity, category, family, Skill, mob type, or acquisition text.
- **Details** shows the effect and documented natural or Fusion acquisition methods.
- **Recipes** shows ordered input pairs that can create the selected Shard, including Shards that have both natural and Fusion sources.
- **Uses** shows what the selected Shard can create.
- Recipe cards preserve left/right order, input quantities, output choices, normal or special yield, and Pure Reptile information.
- Clickable Shard names darken and underline on hover.
- Rarity and semantic fields use their matching Minecraft/SkyBlock colors.
- The running mod never contacts the Wiki, an API, or an icon service for the guide.

### Planner

The planner keeps the direct guide intact and adds:

- a target Shard and quantity;
- complete multi-step Fusion trees;
- Fastest or Cheapest route selection;
- alternative routes for each result;
- Materials Only totals;
- separate direct input/output recipe filtering;
- Shard effect, family, Skill, acquisition, and editable Shards-per-hour information;
- draggable Fusion Lines for ID and Special Fusion routes;
- Kraken/Kuudra parameters;
- a local, per-profile Shard warehouse.

Ironman mode uses local acquisition-rate planning and does not depend on Bazaar. Normal price-based planning is available only when a compatible installed Skyblocker exposes values already stored in its own client cache. QCA does not download Bazaar prices. Without a compatible price provider, price routes are marked unavailable and all offline/rate-based tools continue to work.

The warehouse updates only from exact Shard counts visible while the player physically has a Hunting Box page open. QCA does not send `/hb`, turn pages, click slots, fuse Shards, or select an output.

## Optional unified mod controls

QCA can expose reviewed settings and HUDs from these optional exact builds:

| Provider | Reviewed build |
|---|---:|
| SkyHanni | 7.41.0 |
| Skyblocker | 6.8.2 |
| Firmament | 44.3.0 |
| BabyZombieAddons | 3.4.1 |

When multiple supported mods implement the same exact function:

1. QCA shows one function card.
2. Right-clicking the card shows the provider selector first.
3. Selecting a provider shows that provider's safely editable native settings.
4. Enabling the card enables the selected implementation and disables only exact equivalents from other detected providers.
5. Related functions with different purposes remain separate.

Changes are applied to the provider's live local configuration and saved through that provider's own save path. QCA never edits the config file of an unloaded mod. The first Alpha exposes reviewed Boolean, enum, bounded numeric, HUD position, and HUD scale values. Unsupported compound color/keybind types remain in the provider's native editor.

These integrations are version-locked and fail closed. A missing or structurally changed provider does not prevent standalone QCA features from loading.

## Commands and outbound actions

| User input | Result | Sent to the server? |
|---|---|---|
| `/aca`, `/qca`, `/ca`, `/qc` | Opens local QCA settings | No |
| `/qshard [English query]` | Opens the bundled offline Shard guide | No |
| `/th` | Sends the exact command payload `warp torrhus` | **Yes, only after the user types it** |
| `/helia` | Sends the exact command payload `chapter torrhus` | **Yes, only after the user types it** |
| Click **Reconnect** | Starts one normal connection to the remembered in-memory server target | **Yes, only after the click** |

QCA has no automatic command calls, no `sendChat` calls, and no automatically generated chat contents.

## Client-only and safety boundary

QCloudy_Addition is declared with `"environment": "client"` and runs as a client-side mod.

Its normal features consume data already available to the client, including:

- local player coordinates and orientation;
- received Tab, scoreboard, chat, item name, and lore text;
- screens and slots the player physically opened;
- entities, names, sounds, block states, and Block Entities already loaded by Minecraft;
- local inventory contents;
- bundled offline maps, Shard data, icons, pet metadata, and sounds;
- compatible client-price values already cached by an optional provider.

The mod contains no Hypixel API subscription, HTTP client, WebSocket, telemetry, remote updater, macro, simulated input, automatic click, automatic movement, automatic item use, or automatic reconnect loop. It does not request chunks, hidden inventories, or server-only data.

Some passive visual helpers—such as outlines, beacons, wall overlays, and motion projections—make already-received world information easier to see and therefore carry higher policy risk. Passive rendering is not the same as official approval. Review the current [Hypixel Allowed Modifications guide](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications) and [Hypixel SkyBlock Rules](https://support.hypixel.net/hc/en-us/articles/4508088842898-Hypixel-SkyBlock-Rules), and disable any feature you are not comfortable using.

For the complete per-feature data-flow table, see [COMPLIANCE.md](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/COMPLIANCE.md).

## Configuration and saved data

QCA saves ordinary local JSON files in the Minecraft instance's `config` folder:

- `config/qcloudy_addition.json` stores language, features, HUD appearance/position/scale, local tracker memory, pet details, planner settings/rates, and Fusion Lines positions.
- `config/qcloudy_addition_shard_warehouse.json` stores per-profile Shard counts observed from Hunting Box pages and their last observation time.

The legacy `config/autumecloudyaddition.json` is read once for migration. Configuration writes use a temporary file followed by atomic replacement when supported.

QCA stores no password, access token, Hypixel API key, chat history, or remote account data. The reconnect address is kept only in memory.

## Compatibility

### Required

- Minecraft 26.1.2
- Java 25 or newer
- Fabric Loader 0.19.3 or newer
- Fabric API 0.155.2+26.1.2 or newer

### Optional

- Mod Menu 18.x or a compatible build for direct settings access
- Reviewed versions of SkyHanni, Skyblocker, Firmament, and BabyZombieAddons for unified provider controls
- Compatible Skyblocker client-price cache for Normal-mode price routes

QCA does not have a build-time or runtime dependency on any of the four reference SkyBlock mods. Unknown provider versions are hidden by the adapter rather than written through an unverified layout.

## Troubleshooting

### The settings command does not open QCA

Try the default `O` key, Mod Menu, or a different alias. QCA skips an alias when another client command already owns that name.

### A HUD is enabled but not visible

Many HUDs appear only on the relevant island and only after the client receives content for them. Empty HUDs intentionally hide their title, border, and background. Enter the relevant area or wait for the corresponding Tab/scoreboard/chat/menu observation.

### A HUD cannot be moved in Edit HUD

Only enabled, loaded HUDs with visible content are included in the editor. Open the relevant island or state first, then return to **Edit HUD**.

### Text or controls overlap at my GUI scale

Alpha 2.6.15 added responsive layouts, independent scrolling, clipping, and safe short-screen fallbacks. If a layout still overlaps, include the exact Minecraft window size, GUI Scale, QCA language, open page/tab, installed mods, and a full screenshot in the report.

### Cheapest or Bazaar planning is unavailable

QCA has no Bazaar network client. Use Ironman or rate-based planning, or install a compatible reviewed Skyblocker build that already provides prices through its client cache.

### An external provider is missing

Check the installed provider version. Unified adapters are intentionally version-locked and disappear when the provider is absent or no longer matches the reviewed structure. QCA's own standalone features should remain available.

### Pet, Shard, or item artwork looks wrong with a resource pack

QCA prefers a matching real ItemStack already received by the client where supported. Include the resource-pack name/order, the affected item or pet, its canonical English name, and screenshots with and without the pack.

### The fishing cue does not play

Confirm that **Fishing Bite Sound** is enabled and its feature volume is above 0%. The cue requires the local hook association and the exact received `!!!` marker. Also check Minecraft's relevant master/category volume.

## Reporting a bug

Open a [GitHub Issue](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/issues) and include:

- QCloudy_Addition version and filename;
- Minecraft, Fabric Loader, Fabric API, and Java versions;
- all installed SkyBlock mods and versions;
- QCA language, GUI Scale, window resolution, and active resource packs;
- island/area and the feature involved;
- exact steps to reproduce the problem;
- expected and actual behavior;
- screenshots or a short video for visual bugs;
- `latest.log` and a crash report for crashes;
- whether the problem still occurs with only QCA, Fabric API, and required dependencies.

Do not publish access tokens, session data, private chat, or other personal information in a report.

## Project status and validation

Alpha 2.6.15 has been built and archive-checked with Java 25. The current validation run reports 160 automated tests with no failures, errors, or skips; binary/source archives validate, language key sets match, and the packaged metadata declares a client-only Minecraft 26.1.2 mod.

These checks do **not** replace authenticated in-game testing. Before treating an Alpha as stable, the project still needs live regression across the supported providers, different GUI scales/languages, resource packs, Hypixel wording changes, latency conditions, and a full target modpack.

- [Current changelog](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/CHANGELOG.md)
- [Detailed feature specification](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/FEATURES.md)
- [Implementation and data flow](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/IMPLEMENTATION.md)
- [Validation report](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/VALIDATION.md)

## License and credits

QCloudy_Addition source code is licensed under **GNU Lesser General Public License v3.0 or later (`LGPL-3.0-or-later`)**. See the repository [LICENSE](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/LICENSE) file.

The project uses reviewed, attributed offline facts or assets from sources listed in [THIRD_PARTY_NOTICES.md](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/THIRD_PARTY_NOTICES.md), including the Hypixel SkyBlock Wiki and MIT-licensed SkyShards icon data. The running mod does not contact those sources.

QCloudy_Addition is an independent community project and is not endorsed by Hypixel, Mojang, Microsoft, SkyHanni, Skyblocker, Firmament, or BabyZombieAddons.

# QCloudy_Addition implementation and data-flow reference

This document explains what each public feature is for, which client-visible information it consumes, how QCA processes that information, what the player should see, and whether the feature can produce an outbound action. It describes version `Beta-2.6.6+26.1.2`.

## 1. Runtime architecture

QCA is a Fabric client entrypoint. `QCloudyAdditionClient` performs five kinds of registration:

1. Loads normalized JSON settings and profile-aware inventory data.
2. Samples the vanilla Tab list and scoreboard once per second.
3. Receives normal and canceled-display game chat through Fabric message events.
4. Registers HUD and world-render callbacks.
5. Applies narrowly targeted Mixins for container input/rendering, chat peek, outlines, sound replacement, hotkeys, cursor memory, and connection screens.

The main tracking flow is:

```text
received Tab / scoreboard / chat / menu / title / entity / inventory / loaded blocks
                              ↓
                bounded parser or local-state filter
                              ↓
              session state or account/profile cache
                              ↓
        HUD, tooltip, outline, beacon, overlay, line, or sound
```

Location detection first confirms a Hypixel host and a received SkyBlock scoreboard. It then classifies the current island from the location-marked scoreboard line and a bounded list of known original location names. Island-specific parsers and renders do not run globally.

## 2. Settings, localization, and HUD framework

### Purpose

Give every feature a clear single category and let players customize visual output without editing a file.

### Information and APIs used

- Local mouse/keyboard input through Minecraft input events and targeted input Mixins.
- `HudElementRegistry` for screen HUD submission.
- `GuiGraphicsExtractor` for Minecraft 26.1.2 text, item, panel, and texture rendering.
- Local `qcloudy_addition.json` for settings and HUD layout.
- Bundled `en_us.json` and `zh_cn.json` for QCA-owned labels.

### Implementation

- `ConfigScreen` owns one searchable Features page and eleven mutually exclusive categories.
- Left-click changes a feature's primary state; right-click opens only settings specific to that feature.
- `HudLayoutScreen` lists only HUD types currently loaded by location/state. Dragging changes position; dragging a border/corner changes only that HUD's 50–200% scale.
- `PanelStyle` separately stores background color/alpha, border width/color, title color, bold state, shadow state, and scale for Map, Mining, Hunting, and Pet panels.
- `ColorPickerScreen` supplies RGB/HSV controls, brightness, presets, and transparent backgrounds.
- Key chords store a keyboard key or mouse button plus Ctrl/Shift/Alt/Super modifiers. `Esc` while listening clears the binding.

### Expected presentation

A compact dark BLC-inspired—not asset- or code-copied—settings window, blue enabled strip, searchable cards, smooth optional opening animation, and a separate direct-manipulation HUD editor. Positions and scales remain unchanged after restarting.

### Defaults and outbound behavior

English, animations on, Minecraft bitmap font, shadow on, one-pixel cyan border, partially transparent dark background. QCA UI translations never rename server-provided items, tasks, locations, pets, or HOTM presets. No outbound server action.

## 3. Manual reconnect

### Purpose

Let a player retry a failed or interrupted connection without returning through multiple menus.

### Information and APIs used

- Normal `ConnectScreen.startConnecting` arguments: server name, address, server type, and resource-pack preference.
- `DisconnectedScreen` and its vanilla `LinearLayout`.

### Implementation

`ConnectScreenMixin` records the last explicit connection target in memory for the current client process. `DisconnectedScreenMixin` appends one vanilla-width button before the original layout is arranged. Clicking it creates a fresh `ServerData` object and invokes the normal Minecraft connection screen once.

### Expected presentation

A `Reconnect`/`重新连接` button aligned with the existing disconnect-screen controls.

### Defaults and outbound behavior

On by default. One physical click starts one ordinary server connection. No saved address, countdown, retry loop, background connection, command, chat, or authentication bypass.

## 4. Maps

### 4.1 Dwarven Mines

- **Purpose:** replace an unreadable route web with a compact regional overview.
- **Inputs:** local player X/Y/Z/yaw and received scoreboard sub-location.
- **Implementation:** `DwarvenMapProjection` maps coordinates and named regions into one original schematic texture containing 12 shaped areas. Y and the received sub-location disambiguate vertically overlapping regions, but the displayed map remains one layer.
- **Expected effect:** material-colored region shapes, thick borders, clear Minecraft-font English labels, and a live red directional arrow.
- **Default/outbound:** on; render only.

### 4.2 Glacite Tunnels

- **Purpose:** keep a multi-height tunnel network readable.
- **Inputs:** local X/Y/Z/yaw.
- **Implementation:** `HudRenderer` selects low, middle, or high bundled map imagery at Y 126 and Y 143. All images use the same X/Z projection; generated label cards are collision-separated.
- **Expected effect:** the map changes layer as elevation changes while the red arrow does not jump horizontally. All point names remain English.
- **Default/outbound:** on; render only.

## 5. Mining and Crimson Isle tasks

### 5.1 Mining commissions, powders, and HOTM preset

- **Purpose:** show objectives without holding Tab open.
- **Inputs:** the received `Commissions:` and `Powders:` Tab widgets; exact `Heart of the Mountain Slot`/loadout menu contents.
- **Implementation:** `TabListTracker` extracts a maximum bounded widget block. Exact `current/target` wins. Percentage-only known commissions can use documented island targets; unknown tasks remain percentages. `HotmSlotTracker` caches only a menu row explicitly marked `SELECTED` or received `Current:` lore.
- **Expected effect:** every complete commission name above a separate progress bar; percentage by default or numeric mode; Mithril, Gemstone, and Glacite Powder rows; optional `HOTM: <original name>`.
- **Default/outbound:** tracker and HOTM row on; percentage mode; no command or menu click.

### 5.2 Crimson Isle Faction Quests

- **Purpose:** preserve the complete faction task list outside Tab.
- **Inputs:** bounded received `Faction Quests:` Tab widget.
- **Implementation:** each `✖`/`✔`, name, and optional amount is parsed by `TabListTracker` and rendered through the Mining HUD slot only on Crimson Isle.
- **Expected effect:** complete original English task names, amounts, and ready state without ellipses.
- **Default/outbound:** on; render only.

## 6. Torrhus and Foraging

### 6.1 Helia Chapter and resources

- **Purpose:** combine long-lived progression in one readable HUD.
- **Inputs:** separately bounded Tab and scoreboard blocks, a four-second/twelve-line Chapter chat block, and already-open Helia Chapter menus.
- **Implementation:** `HuntingTextParser` creates partial snapshots; `HuntingTracker` merges only nonblank fields and clears stale task state when an explicitly different Chapter is observed. Forest/Desert Whispers, Forest/Safari Essence, Sweep, and Forest Fortune accept absolute received snapshots; only exact gain chat is additive. Values are saved per Minecraft account and received SkyBlock profile.
- **Expected effect:** Chapter, complete task name, task progress, and six resources in the combined HUD. Completed count, total progress, and next unlock are optional and off by default. Safari Essence appears here, not in the Safari Dashboard.
- **Default/outbound:** base rows and resources on; no command or menu click.

### 6.2 Tree Critter Timer

- **Purpose:** display the server's actual Honeycomb attraction time without drift.
- **Inputs:** the nearest loaded entity/nameplate matching exact `Critter in: <duration>` text.
- **Implementation:** every ten client ticks, `HuntingTracker` chooses the nearest matching visible label. It does not infer which Pot was used or run an independent countdown.
- **Expected effect:** the exact received countdown inside the combined HUD, including any server-side speed or instant modifier.
- **Default/outbound:** on; read/render only.

### 6.3 Miria Contest

- **Purpose:** show the next useful target rather than duplicating the scoreboard timer.
- **Inputs:** received contest tier/score/requirement lines from Tab and scoreboard.
- **Implementation:** `HuntingTextParser.ContestSnapshot` computes the next bracket, remaining score, and ticket estimate only when an active contest snapshot is complete enough.
- **Expected effect:** next bracket, required remaining score, and expected Safari Ticket rows inside the combined HUD. No sidebar injection and no duplicate timer.
- **Default/outbound:** on; render only.

### 6.4 Benefactor

- **Purpose:** keep temple benefit status and expiry visible.
- **Inputs:** bounded Tab/scoreboard text, already-open Forest/Desert Temple menu, and exact received donation chat.
- **Implementation:** received donation data is briefly authoritative so an old open menu cannot overwrite it. Local arithmetic converts a received duration into an expiry timestamp. State is persisted per account/profile and expires locally.
- **Expected effect:** active/inactive status, remaining time, temple/effect, and donation rows.
- **Default/outbound:** all rows and its independent 64% alert on; no command, click, or donation action.

### 6.5 Rare Tree Gift

- **Purpose:** alert only for configured rare rewards that belong to the local player's Tree Gift.
- **Inputs:** raw received game-chat `Component`, including `SHOW_TEXT`, plus messages canceled from display by a compatible chat compactor.
- **Implementation:** `TreeGiftAlertSession` opens only on the 64-character Gift border and expires after 15 seconds. The personal `+N rewards gained! (hover)` summary can reveal its own attached loot. Separate bonus percentage and `A <loot> fell from the Tree!` rows require the same block to contain the Gift header, local `You helped cut...` contribution, and personal reward summary. Early rows are buffered, each loot is deduplicated per block, and public/incomplete/lasso text is inert.
- **Expected effect:** `RARE TREE GIFT` center title, loot subtitle, and independent sound for an enabled loot.
- **Default/outbound:** feature, all ten configured rare loots, and sound on; volume64%; no chat or command.

## 7. Hunting

### 7.1 Beeheemoth

- **Purpose:** make spawn location and audio easier to notice without interacting automatically.
- **Inputs:** already-loaded Bee entities, entity scale, positions, exact local capture confirmation, and spatial Bee sound instances.
- **Implementation:** the helper accepts only a Bee with scale approximately 9.0. `EntityRendererMixin` supplies the configured vanilla outline. The first observed position becomes a yellow beacon until the player enters ten blocks, the exact personal capture confirmation arrives, or the entity disappears. `BeeheemothSoundCustomizer` changes only non-relative Bee event/resolved sounds within 12 blocks of the loaded entity or a three-second last-known origin.
- **Expected effect:** configurable outline, temporary yellow beacon, and normal spawn/capture Bee sound at the selected volume.
- **Default/outbound:** helper, outline, beacon, and sound on; sound64%; no capture action.

### 7.2 Lasso REEL cue

- **Purpose:** notify the player when a locally held Lasso is ready to reel.
- **Inputs:** local player's visible leash relation and nearby exact `REEL` Armor Stand label.
- **Implementation:** `HuntingTracker` relates locally leashed entities to nearby labels and plays only on a false-to-true state transition.
- **Expected effect:** one short cue at readiness, not a sound every tick.
- **Default/outbound:** on at64%; no simulated input or reel action.

### 7.3 Critter Behavior Assistant

- **Purpose:** surface the documented special interaction state of Blue Jay, Goldolot, Dustybit, and Hideonsun.
- **Inputs:** loaded entity names, local movement, held capture-tool name, progress labels, and exact capture confirmation.
- **Implementation:** bounded nearest-entity selection and per-behavior state calculate stand-still or interaction readiness. After a received capture, only that Critter name is suppressed for three seconds to prevent a stale entity from replaying the alert.
- **Expected effect:** center titles such as stand-still, follow-jumps, return-projectile, or ready prompts.
- **Default/outbound:** all behavior helpers and independent sound on at64%; advisory only.

### 7.4 Fairy Souls

- **Purpose:** show known Torrhus/Safari Soul positions on request.
- **Inputs:** fixed documented coordinates, local position, island, and received success/already-found confirmation.
- **Implementation:** `HuntingWorldRenderer` submits pink vanilla beacon beams. A success message hides only the nearest listed coordinate within ten blocks and persists the island/coordinate key per profile.
- **Expected effect:** uncollected pink beams on the selected island; a confirmed collected Soul disappears immediately.
- **Default/outbound:** master off; Torrhus and Safari subsets preselected for when enabled; render only.

## 8. Critter Safari

### 8.1 Dashboard and Critterdex

- **Purpose:** summarize the current Safari run and biome collection.
- **Inputs:** received capture/chat lines, scoreboard/Tab tier and biome text, and local session time.
- **Implementation:** a session accumulator counts only parsed capture results and Shards. The official 37-Critter table provides biome membership and Shard rarity; Loot Share can update Wumpa prerequisites without entering the personal Critterdex.
- **Expected effect:** run time, Shards, Ticket Tier, biome progress, and complete captured/missing names in the combined HUD. Safari Essence is deliberately absent here.
- **Default/outbound:** all dashboard/Critterdex rows on; read/render only.

### 8.2 Cold and campfire safety

- **Purpose:** warn before dangerous Cold and point to a nearby recovery source.
- **Inputs:** received Cold value and already-loaded campfire Block Entities.
- **Implementation:** ordered one-shot thresholds are strictly above80 and90 by default. On first threshold entry, QCA scans only already-loaded chunks within a bounded radius and chooses the nearest campfire. The beacon remains while Cold is high and not falling, and closes as soon as a lower received value establishes a falling state.
- **Expected effect:** two center warnings with independent sound and a red beacon above the nearest eligible campfire.
- **Default/outbound:** on; thresholds configurable; sound64%; no movement or block interaction.

### 8.3 Doomspiral and Warden readiness

- **Purpose:** show when the inventory meets the encounter requirement and when the visible Warden can be captured.
- **Inputs:** local inventory count of exact Soothing Incense, loaded Warden age/pose in the bounded arena, and local-player latency.
- **Implementation:** the incense alert is one-shot at four or more. `WardenCooldownSupport` applies the known 140-client-tick window with latency compensation and rejects emerging/digging poses.
- **Expected effect:** center readiness titles and independent sounds; each Warden alerts once per ready transition.
- **Default/outbound:** both on at64%; no item use or capture action.

### 8.4 Critter and Sparkling visibility

- **Purpose:** distinguish capturable entities and rare Sparkling events.
- **Inputs:** loaded real entities, visible entity names, received Sparkling chat, and official Shard rarity mapping.
- **Implementation:** `EntityRendererMixin` adds real non-Armor-Stand Critters to vanilla outline rendering. Capture props and their supporting Armor Stands are explicitly excluded. Sparkling can use its own configured outline color and center alert.
- **Expected effect:** rarity-colored real Critters, no full Armor Stand support body, and a configurable Sparkling prompt/outline.
- **Default/outbound:** on; Sparkling sound64%; rendering only.

### 8.5 Floor Drop and quest items

- **Purpose:** make already-visible nearby drops and required inventory items easier to track.
- **Inputs:** nearby already-loaded String block states, loaded names/entities, and local inventory.
- **Implementation:** bounded periodic scans update the nearest distance and exact quest-item counts. Persistent objects are deduplicated before alerting.
- **Expected effect:** center prompt and/or combined-HUD rows with distance and counts.
- **Default/outbound:** on at64%; no pickup, pathing, or interaction.

### 8.6 Wumpa

- **Purpose:** track party prerequisites and optionally preview the visible charge path.
- **Inputs:** exact personal capture and teammate `LOOT SHARE ... catching a <Critter>` chat, Wumpa spawn/phase text, loaded Wumpa name carrier, loaded Ravager body, movement, and local collision clipping.
- **Implementation:** eight Icy prerequisite names are stored separately from personal Critterdex state. At 8/8 or an exact spawn signal, the checklist becomes `Wumpa: Spawned`. Optional route logic follows the nearest matching Ravager body, confirms movement/stillness in short windows, and clips a red forward line against local collision data.
- **Expected effect:** check/cross prerequisite list before spawn, then one spawned/phase row; optional red charge line.
- **Default/outbound:** HUD and alerts on, route prediction off, sound64%; no movement or capture.

### 8.7 Snoozle breakable wall

- **Purpose:** mark plausible breakable wall surfaces without highlighting the entire cave.
- **Inputs:** only nearby already-loaded block states.
- **Implementation:** once per second, a bounded flood-fill accepts a small connected component only when it contains both Cobbled Deepslate and Tuff. Single-material and oversized terrain components are rejected. Only faces adjacent to air are submitted as translucent quads. `ClientLevel.hasChunk(chunkX, chunkZ)` prevents any chunk request.
- **Expected effect:** thin translucent color on exposed wall faces; green by default and RGB configurable.
- **Default/outbound:** on; local render only.

### 8.8 Safari Belt

- **Purpose:** keep all four milestone levels and actual received bonuses in the belt tooltip.
- **Inputs:** currently opened Safari Milestone menu and Safari Belt tooltip/lore.
- **Implementation:** `SafariMilestoneParser` accepts combined rows and split title/lore layouts, rejects locked/progress-fraction false levels, and updates Cavern, Forest, Haunted, and Icy independently only when a higher confirmed level is observed. Levels are cached per account/profile. `SafariBeltTooltip` reuses the item tooltip pipeline and reads bonus text instead of hard-coding potentially changing totals.
- **Expected effect:** four milestone rows plus received attribute bonuses embedded in the normal item tooltip.
- **Default/outbound:** on; no menu opening or click.

## 9. Combat

### Ender Dragon outline

- **Purpose:** make Ender Dragons easier to locate in The End.
- **Inputs:** loaded Ender Dragon entity and received scoreboard location classified as The End/Dragon's Nest.
- **Implementation:** `EntityRendererMixin` uses the vanilla glowing/outline pipeline and returns the configured RGB color.
- **Expected effect:** clean configurable dragon outline, not an altered model or hitbox.
- **Default/outbound:** on; local rendering only.

## 10. Equipped Pet HUD

### Purpose

Show the equipped pet's identity and useful progression without opening the Pets menu.

### Information used

- Received summon, despawn, and Autopet chat.
- Received `Pet:` Tab widget as periodic source of truth.
- Already-open Pets menu and nearby rendered pet profile when they match.
- Bundled offline profile/skin/accessory metadata generated from the inspected NEU repository snapshot.

### Implementation

`PetTracker` maintains active pet identity, rarity, level, and experience. `PetSkinTracker` confirms a matching profile/skin/held item without reusing a complete unrelated ItemStack. `PetHeadResources` creates a normal player head and never attaches synthetic `petInfo`; exact and longest skin-family matches handle animated/dynamic frames. `PetLeveling` applies rarity-offset level-100 curves and the Golden/Jade/Rose Dragon level-200 curves. Confirmed skin, held item, and total XP are retained locally per pet.

### Expected presentation

A sharp 3D player-head icon, rarity-colored `[Lvl N] Pet Name`, current-level XP and percentage, optional XP to max, optional skin name, optional overflow level, and pet item as icon+name, icon-only, or name-only. Values use one decimal with `k/m/b/t`. No line is shortened with an ellipsis. At max level, only the redundant to-max line disappears; the held item remains.

### Defaults and outbound behavior

All information rows on; icon+name accessory mode; read/render only; no runtime texture or API download and no Firmament dependency.

## 11. Chat Peek

- **Purpose:** inspect chat history temporarily without opening Chat.
- **Inputs:** a configured held key/mouse chord and mouse wheel.
- **Implementation:** `ChatComponentMixin` renders focused-height chat when `ChatPeekManager.active()` is true. `MouseHandlerMixin` routes wheel input to chat or leaves it for the hotbar according to the selected mode.
- **Expected effect:** chat expands only while the chord is held; wheel controls chat by default.
- **Default/outbound:** feature on, chord unbound, scroll target Chat; no message is sent.

## 12. Inventory and menu tools

### 12.1 Item timestamps

- **Purpose:** show item creation time and supported completion countdowns.
- **Inputs:** item components/lore already present in the local ItemStack.
- **Implementation:** `ItemTimestampTooltip` formats received timestamps as local24-hour, local12-hour, ISO, or RFC text and appends tooltip rows.
- **Expected effect:** timestamp/countdown beneath the normal tooltip.
- **Default/outbound:** on; local tooltip only.

### 12.2 Cursor memory

- **Purpose:** return the pointer near its last useful position when reopening a compatible screen.
- **Inputs:** local screen identity, cursor coordinates, elapsed local time.
- **Implementation:** `CursorPositionSaver` records and restores within the configured tolerance; it does not synthesize a click.
- **Expected effect:** cursor returns to the saved position when the matching screen reopens soon enough.
- **Default/outbound:** on,500ms tolerance; local pointer positioning only.

### 12.3 AOTE/AOTV sound customization

- **Purpose:** replace, rather than indiscriminately mute, Instant Transmission and Etherwarp sounds.
- **Inputs:** held SkyBlock item ID, local sound event/resolved sound path, source coordinates, and selected sound settings.
- **Implementation:** `SoundEngineMixin` delegates only nearby matching sounds while an Aspect of the End/Void is held. `TeleportSoundCustomizer` preserves vanilla mode or plays one selected vanilla sound with independent volume/pitch, guarded against recursion.
- **Expected effect:** vanilla sound by default, or Chorus, Enderman, Amethyst, XP Orb, End Portal Fill, or Shulker sound at the chosen volume/pitch.
- **Default/outbound:** customization available but both modes remain vanilla; local sound only.

### 12.4 Attribute Shard Fusion Guide

- **Purpose:** provide a complete local answer for both reverse recipe lookup and forward uses lookup without guessing an order-sensitive Attribute Fusion pair.
- **Bundled inputs:** `assets/qcloudy_addition/data/shard_fusions.json`, generated offline from the current [Hypixel SkyBlock Wiki Attributes](https://hypixelskyblock.minecraft.wiki/w/Attributes) effect/acquisition tables and [Attribute Fusion rules](https://hypixelskyblock.minecraft.wiki/w/Attribute_Fusion), with identities cross-checked against [SkyShards](https://github.com/Campionnn/SkyShards), the [NotEnoughUpdates item repository](https://github.com/NotEnoughUpdates/NotEnoughUpdates-REPO), and the [official Bazaar product list](https://api.hypixel.net/v2/skyblock/bazaar). The 320 local Shard PNGs are generated from SkyShards `public/shardIcons` at reviewed MIT commit `9688031dbc4e726168ffceb0f44884ff26e6e728`; the 321-source set is filtered through the catalog allow-list, excluding Rainbug.
- **Data calibration:** the runtime catalog is required to contain exactly 320 official Bazaar-listed Shards. Compared with the stale 317-item snapshot, Anteater, Zombuddy, Troodon, and Ghost Crab are present, Goldolot uses `R92`, and Rainbug is excluded because it is not in the official Bazaar Shard allow-list. The Wiki Attributes list is treated as supporting documentation rather than the cardinality authority because that page marks itself incomplete/outdated.
- **Implementation:** `ShardFusionCatalog` loads and validates the committed JSON once, including normalized rich-text effect spans, acquisition methods, mob types, and semantic colours. Its search index covers name/ID/attribute/effect/rarity/category/family/skill/mob type/acquisition; ordered-pair indexes serve both Recipes and Uses, so a Shard with natural sources (for example Queen Bee) still exposes every Fusion recipe. Special rules are checked symmetrically while remaining ID outputs retain first/second-input order. Chameleon follows numeric ID stepping and rarity rollover. `ShardItemResolver` keeps a session-wide native-ItemStack cache: a matching stack already received in an open menu/inventory overrides the bundled item model, while every unseen catalog entry resolves to its own offline Shard texture instead of amethyst. QCA performs no HTTP or texture request; an already-received player head continues through Minecraft's normal item renderer.
- **Recipe arithmetic:** Chameleon consumes `1`; Reptile, Amphibian, and Elemental consume `2`; all other Shards consume `5`. An ID/Chameleon result yields `1`, a special-rule result yields `2`, and Pure Reptile displays its received level's 2–20% double-output chance. Up to three selectable outputs are shown in their actual order and never equal either input.
- **Presentation:** `ShardFusionScreen` supplies Details/Recipes/Uses tabs, searchable result rows, Back/Forward history, page controls, item icons, input amounts, outputs, yields, and an explicit order note. Details presents exact effect and acquisition lines, explicitly labels Fusion-only Shards, and shows the verified Fusion-recipe count whenever nonzero. Epic is Minecraft `§5`; other rarity/stat/category/mob-type/acquisition text uses reviewed semantic colours. Hovering clickable Shard text darkens and underlines only the visible text. Clicking outside search, `Esc`, or `Tab` releases text focus; clicking search restores it. Input pairs and output candidates are measured as compact centered clusters, and hitboxes are derived from the same visible bounds. Text wraps or scales instead of using ellipses.
- **Entry points/default/outbound:** the feature is enabled by default. Its secondary setting contains **Open Guide** and an optional unbound keyboard/mouse chord. `/qshard [English query]` is a local client command that opens the same screen with the query prefilled. It sends no server command, chat, packet, menu input, Wiki request, Bazaar request, or other network traffic.

## 13. Persistence

- `config/qcloudy_addition.json`: language, feature settings, per-HUD appearance/position/scale, remembered pet details, Hunting resources/Chapter/Benefactor/Safari Belt state, and collected Fairy Soul keys. The old `autumecloudyaddition.json` is read once for migration.
- Configuration writes use a temporary file followed by atomic replacement when supported.

QCA stores no password, access token, Hypixel API key, chat history, remote account data, or reconnect address on disk.

## 14. Complete outbound-action inventory

| Trigger | Exact action | Automatic? |
|---|---|---|
| `/aca`, `/qca`, `/ca`, `/qc` | Opens the local QCA settings screen | No server payload |
| `/qshard [English query]` | Opens the local offline Shard Fusion Guide and pre-fills its search | No server payload |
| Player types `/th` | `sendCommand("warp torrhus")` | No |
| Player types `/helia` | `sendCommand("chapter torrhus")` | No |
| Player clicks Reconnect | One normal Minecraft server connection to the remembered in-memory target | No |

`sendChat` calls: none. Automatically generated chat: none. Automatic commands: none. Automatic movement, combat, capture, item use, block interaction, or reconnect: none.

## 15. Expected validation boundary

Automated tests validate parsers, defaults, routing, persistence normalization, boundary calculations, and archive structure. Local launches validate initialization with Fabric alone and with the four supplied reference mods. They do not prove every future Hypixel wording, live entity layout, user resource pack, GUI scale, latency condition, or policy interpretation. Live regressions should therefore compare the expected presentation in this document with actual Hypixel behavior before promoting a beta build to stable.

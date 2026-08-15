# Changelog

All notable public changes to QCloudy_Addition are documented here.

## [2.8.17] - 2026-08-15

### Improved

- Added separate **Unified Settings Editor** and **Unified HUD Editor** master switches under General. Both are disabled by default, can be enabled independently, and do not affect QCA-owned settings or HUDs.
- Replaced the exact-version whitelist for SkyHanni, Skyblocker, Firmament, and BabyZombieAddons with capability discovery. An installed provider can continue exposing recognised settings and HUD positions after a version update when its live configuration and save contracts remain compatible.
- Added per-field defensive discovery for provider configuration trees. Recognised writable toggles, enums, bounded numeric settings, and known HUD position structures are shown; new or changed structures that QCA cannot safely edit are omitted without hiding the rest of the provider or preventing QCA from opening.
- Added prefixed toggle/HUD-coordinate recognition so layouts such as `enabledCommissions` with `commissionsX`, `commissionsY`, and `commissionsScale` remain editable without a version-specific field list.
- When the relevant master is enabled, re-probes provider capabilities whenever the unified settings screen is opened, preventing an early partial scan from remaining cached after another mod finishes initialising.

### Removed

- Removed the local `/aca` and `/ca` settings aliases. `/qca` and `/qc` remain available when their client-command names are free.

### Compatibility boundary

- This is best-effort structural compatibility, not a claim that unknown future provider code is automatically understood. A recognised live field is exposed only when QCA can read it, safely write its supported value type, and use the provider's own save path. Unsupported new functions remain in their original mod and are simply absent from QCA until an adapter is added.
- No provider configuration files are edited directly, and this change adds no server packet, command, chat, HTTP request, gameplay automation, or provider runtime dependency.

## [2.7.17] - 2026-08-14

Beta consolidation release for Minecraft 26.1.2 and 26.2, covering the completed work since Beta 2.6.6.

### Added

- Added the opt-in Fishing Bite Sound with its own 0–100% volume control at the shared 64% default. Directly owned water hooks and Hypixel's bounded ownerless lava-hook presentation are supported, and the cue plays only once for each confirmed bite.
- Added the local 320-Shard Planner alongside the existing Fusion Guide: multi-step Fusion trees, alternative direct routes, Materials Only totals, editable acquisition rates, Fastest/Cheapest routing, Ironman mode, Kraken/Kuudra parameters, draggable Fusion Lines, and a profile-scoped Hunting Box warehouse recorded only from pages the player opens.
- Added optional Bazaar-price routing through a compatible Skyblocker public cached-price API. QCA performs no price HTTP request and all price routes visibly stay unavailable when no reviewed provider is installed.
- Added the first function-first unified settings/HUD layer for the reviewed SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and BabyZombieAddons 3.4.1 builds. Exact equivalent features can select one provider, safe native values persist through that provider, and unsupported versions fail closed.
- Added a maintained build matrix and one-command build script for separate Minecraft 26.1.2 and 26.2 playable and Sources artifacts.

### Improved

- Promoted Fishing to its own top-level category and made category spacing responsive on short screens.
- Reworked the Shard Planner, Shard details, Settings, Fusion Lines, RGB picker, feature pages, and HUD editor for narrow windows, long bilingual text, independent scrolling, clipping, and hitboxes that follow visible controls.
- Replaced the Dwarven Mines background and changed its live arrow to one continuous approximate X/Z-only projection across the complete single-layer overview. Y and scoreboard sub-locations do not influence the marker.

### Fixed

- Fixed missing Fishing Bite Sound support for the bounded Hypixel lava-hook presentation and prevented the cue from replaying when the player reels in.
- Fixed owned Tree Gift creature alerts such as `A wild Groundhog appeared!` being discarded, while preserving the rejection of an unrelated nearby player's public line.
- Fixed an unmaxed Ancient Golden Dragon being shown as `[Lvl 200]`; overflow levels now require received exact experience at or beyond the real maximum.
- Fixed overlapping Shard detail/rate controls, Planner controls, Fusion Line nodes, settings fields, RGB controls, and HUD-editor rows at supported compact layouts; clipped rows no longer keep invisible hitboxes.
- Fixed Dwarven marker jumps or disappearance on bridges above The Mist and other vertically overlapping paths, added `C&C Minecarts Co.` recognition, and kept out-of-range arrows safely inside the overview.

### Replaced

- Removed the old Dwarven scoreboard sub-location snapping and per-region marker clamping. No user-facing feature from Beta 2.6.6 was removed; the old positioning method was replaced by the continuous real-time X/Z projection.

### Compatibility and safety

- QCA remains a standalone, client-only Fabric mod. It adds no automatic click, Fusion, fishing action, movement, combat, capture, packet, chat, command, HTTP request, telemetry, or hidden server-data request.
- The QCA-owned feature set is built for both Minecraft 26.1.2 and 26.2. Exact-version third-party settings/HUD adapters remain reviewed for 26.1.2 only and deliberately fail closed on 26.2 until matching provider builds are audited.
- `/th` and `/helia` remain the only documented user-triggered server-command shortcuts; neither runs without direct player input.

## [2.6.17] - 2026-08-13

Alpha Dwarven Mines overview synchronization correction for Minecraft 26.1.2 and 26.2.

### Fixed

- Removed scoreboard sub-location selection and per-region clamping from the Dwarven Mines marker. The arrow now uses one continuous approximate X/Z transform across the complete single-layer background, so bridges above The Mist and other vertically overlapping paths no longer jump into another named area.
- Added the official `C&C Minecarts Co.` Dwarven sub-location to island recognition so entering that area cannot unload the map.
- Dwarven map coordinates now display X/Z only. Y is absent from the projection API and cannot influence the marker directly or indirectly.
- Kept the live marker safely inside the background at out-of-range coordinates and added regression coverage for continuous one-axis movement, The Mist bridge overlap, representative regions, and clamping.

## [2.6.16] - 2026-08-13

Alpha dual-version platform update for Minecraft 26.1.2 and 26.2.

### Added

- Added a version matrix that builds the same QCA feature set for both Minecraft 26.1.2 and 26.2 with the correct Fabric API and optional Mod Menu version for each target.
- Added `tools/build_all_versions.sh`, which tests and prepares both playable JARs and both Sources JARs in one run.

### Compatibility

- Ported screen access, screen switching, overlay checks, HUD visibility, title alerts, chat scrolling, player-list reading, and block-center distance checks to Minecraft 26.2 while preserving the 26.1.2 behavior through small target-specific adapters.
- Kept the mod client-only; this port adds no packet, command, click, automation, HTTP, or server-data behavior.
- Existing exact-version SkyHanni, Skyblocker, Firmament, and BabyZombieAddons adapters remain reviewed for 26.1.2 only and fail closed on 26.2 until matching provider builds are reviewed.

## [2.6.15] - 2026-08-12

Alpha responsive-UI correction for Minecraft 26.1.2.

### Fixed

- Rebuilt the Shards detail layout so the title, metadata, acquisition text, rate input, Save button, and Reset button share one consistent detail column instead of overlapping.
- Added independent scrolling for the Shard result list and detail text. Long effects and acquisition descriptions wrap inside a clipped viewport while rate controls remain anchored.
- Made Planner controls reflow before collision, made narrow Settings fields stack into one column, and made Fusion Lines use a scrollable canvas instead of stacking overflowing nodes. If the Settings page is too short to contain its controls safely, it now asks for a taller GUI instead of drawing fields outside the panel.
- Corrected responsive sizing and text fitting in the main settings screen, feature secondary pages, RGB picker, and HUD editor toolbar. Clipped setting rows no longer retain invisible hitboxes.

### Validation

- Added deterministic layout tests for wide and narrow Shards pages, Planner controls, Settings columns, and Fusion Lines canvas growth.

## [2.6.14] - 2026-08-12

Alpha pet-level display correction for Minecraft 26.1.2.

### Fixed

- Fixed an unmaxed Golden Dragon using the Ancient Golden Dragon Skin being displayed as `[Lvl 200]`. Cosmetic overflow levels now activate only after the received exact total experience has reached the pet's real level-200 maximum.
- When exact experience is unavailable, an ordinary non-max pet now keeps the level received from Hypixel instead of being promoted to its maximum level by the overflow fallback.

### Preserved

- Maxed Ancient Golden Dragons still display level 200, and verified experience beyond level 200 still displays the supported cosmetic overflow level when that option is enabled.

## [2.6.13] - 2026-08-12

First Alpha unified SkyBlock-mod controls for Minecraft 26.1.2.

### Added

- Added one function-first settings registry spanning QCloudy_Addition and the inspected SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and BabyZombieAddons 3.4.1 builds. Integrations are optional; QCA still starts and works alone.
- Added the ordered top-level categories General, Maps, Items & Menus, Combat, Dungeons, Slayer, Mining, Farming, Foraging, Fishing, Hunting, Rift, and Events. Safari is grouped under Hunting, Garden under Farming, and Crimson Isle/Kuudra under Combat. A feature is shown only once.
- Added provider selection as the first row of each shared feature's secondary page. Selecting one provider and enabling the shared card enables that implementation and disables only exact equivalents from the other detected providers; related but different features remain independent.
- Added live native Boolean, enum, bounded numeric, position, and scale controls for compatible provider versions. Values are read from and written to each provider's own runtime config and saved through its native save path; QCA does not edit another mod's JSON while it is unloaded.
- Added external HUD panels to QCA's existing Edit HUD screen. Only enabled HUDs owned by the currently selected provider are shown; drag/resize previews remain local until mouse release, then update the provider's own position/scale.

### Safety and compatibility

- Integrations are reflection-based and version-locked to the exact reviewed builds. A missing, incompatible, or structurally changed provider is hidden instead of guessed or force-written.
- Complex provider-specific editors whose safe value contract is not yet audited—such as custom color objects and compound keybind objects—remain in the provider's native screen for this first Alpha. The unified menu exposes only settings it can validate and persist safely.
- No integration downloads data, sends a packet or command, clicks a menu, or creates a hard dependency on another SkyBlock mod.

## [2.6.12] - 2026-08-11

Beta fishing cue and settings-navigation fix for Minecraft 26.1.2.

### Fixed

- Fixed the bundled Ciallo bite cue playing a second time when the player reeled in. Physical rod use is now classified as either a new cast or a reel action; only a confirmed new cast re-arms the once-per-hook sound gate.
- Preserved the exact `!!!` bite-marker requirement, direct water-hook priority, bounded ownerless lava-hook association, per-hook deduplication, default-off state, and independent 64%-default volume setting.

### Changed

- Promoted Fishing from the General subgroup to its own top-level settings category, ordered between Foraging and Hunting.
- Made the eight-category sidebar compress its row spacing on short GUI layouts so Fishing and the existing bottom controls do not overlap.

### Safety

- The change only classifies the player's physical rod-use callback and plays a local sound. It does not cast, reel, cancel input, click, move, send chat, send a command, or send an additional packet.

## [2.6.11] - 2026-08-11

Beta Shard planning update for Minecraft 26.1.2.

### Added

- Preserved the existing offline 320-Shard Fusion Guide and added a separate in-game **Shard Planner** with target quantity, a complete multi-step Fusion tree, alternative direct recipes, and a Materials Only summary.
- Added **Fastest** routing from editable Shards-per-hour rates and **Cheapest** routing from an optional client-side Bazaar price cache. Normal mode can compare hunting time with buying time; Ironman mode never uses Bazaar prices.
- Added a read-only Hunting Box warehouse. QCA records Shard IDs and `Owned: N Shards` only while the player physically has a received `/hb` Hunting Box page open, stores each profile locally, and offsets planner material requirements with the saved quantities.
- Added separate Planner pages for direct input/output recipe filtering, full Shard effects/family/Skill/acquisition details and custom rates, draggable Fusion Lines, warehouse inspection, and local settings.
- Added Kraken planning controls for Kuudra tier, completion time, coins/hour opportunity cost, Hunter Fortune, Crocodile level, and per-Fusion handling time.

### Compatibility

- Bazaar pricing is optional and dependency-free. QCA can read Skyblocker's already-cached prices through its public `ItemUtils.getItemPrice` API when a compatible Skyblocker version is installed.
- SkyHanni and Firmament are not treated as price providers because they currently expose no stable public cross-mod Bazaar-price API. If no compatible provider is present, price-based Cheapest planning is shown as unavailable; the offline guide, Ironman routes, rate-based routes, warehouse, recipes, details, and Fusion Lines continue to work.

### Safety and persistence

- The planner, rates, graph positions, mode, target, quantities, and Kuudra parameters are stored in QCA's local config; the warehouse uses a separate per-profile local JSON file.
- QCA performs no price HTTP request, Wiki request, `/hb` command, container click, Fusion, output selection, packet send, chat send, movement, or automation. It consumes only bundled data, optional data already cached by another client mod, and menus the player has actually opened.

## [2.6.10] - 2026-08-11

Beta Tree Gift ownership and creature-alert fix for Minecraft 26.1.2.

### Fixed

- Fixed configured Tree Gift creatures such as `-A wild Groundhog appeared!` being parsed but silently rejected by the ownership state machine.
- Personal ownership now uses the player-only `+N rewards gained!` summary instead of also requiring one legacy `You helped cut...` sentence. Public creature lines from nearby players still cannot arm an alert by themselves.
- Preserved proven ownership for five seconds after the closing Tree Gift border, covering Hypixel's post-block creature-spawn line without opening an unbounded public-chat window.
- Added support for a complete Tree Gift arriving as one multi-line chat component, including compacted borderless components whose personal summary and creature line share that same received value.
- Pending creature/reward rows now flush correctly regardless of whether the personal summary arrives before or after them; duplicate loot remains limited to one alert per gift session.

### Safety

- The fix only reads already-received chat text and `SHOW_TEXT` hover data. It sends no packet, chat message, command, click, movement, or server request.

## [2.6.9] - 2026-08-11

Beta fishing compatibility fix for Minecraft 26.1.2.

### Fixed

- Fixed the Fishing Bite Sound not playing during some Hypixel lava-fishing casts. Water fishing continues to use the directly owned vanilla hook; after a physical local rod use, QCA can now briefly associate a newly loaded ownerless Fishing Hook used by the lava-fishing presentation.
- The fallback rejects hooks that were already present before the cast and hooks explicitly owned by another player, then keeps the same associated hook until it disappears or the player reels it in.

### Performance and safety

- The broader hook lookup runs only during the bounded 40-tick association window or while the associated fallback hook remains loaded; idle gameplay does not scan for hooks every tick.
- Detection remains passive and local. It does not cast, reel, click, move, cancel the rod use, send a packet, chat message, or command.

## [2.6.8] - 2026-08-11

Beta client-audio update for Minecraft 26.1.2.

### Added

- Added an opt-in Fishing Bite Sound under General > Fishing. It detects the exact visible `!!!` ArmorStand next to the local player's own Fishing Hook and plays the bundled Ciallo cue once per cast.
- Added a continuous 0–100% per-feature volume slider, defaulting to 64%.

### Safety

- The feature defaults off, scans only a four-block box around the local player's already-loaded hook, and never reels, clicks, moves, sends a packet, chat message, or command.
- The supplied MP3 is converted to a bundled OGG resource. Playback is fully local and requires no separate resource pack or runtime network request.

## [2.6.7] - 2026-08-10

Beta map update for Minecraft 26.1.2.

### Changed

- Replaced the bundled Dwarven Mines artwork with the newly supplied single-layer 12-region map.
- Recalibrated every named Dwarven region to the replacement image so the live player arrow follows the correct region and local X/Z position.
- Removed Y from Dwarven map selection and projection. The map now uses only received sub-location text, local X/Z, and yaw; a generic `Dwarven Mines` label falls back to the nearest X/Z region center.
- Bumped the Beta patch version to `2.6.7`; playable and source artifacts now use `QCloudy_Addition-Beta-2.6.7+26.1.2`.

### Safety

- The map remains client-only and render-only. It reads no hidden terrain, sends no packet, chat, command, click, movement, or other server interaction.

## [2.6.6] - 2026-08-10

Beta promotion for Minecraft 26.1.2.

### Changed

- Promoted the reviewed `2.5.6` feature set from Alpha to Beta without adding new gameplay automation or server interaction.
- Changed the version line to `2.6.6`, following the project's rule that Beta updates increment the second version component.
- Standardized the playable artifact as `QCloudy_Addition-Beta-2.6.6+26.1.2.jar` and the source artifact as `QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`.
- Updated GitHub, Modrinth, implementation, validation, and publication documentation for the Beta channel.
- Rewrote the Modrinth project description around the actual seven-category settings structure, current Beta scope, dependencies, HUD customization, and explicit client/server-command boundaries.

### Included from the 2.5.x Alpha line

- The standalone offline Attribute Shard Fusion Guide for the official 320-Shard set, with Recipes, Uses, Details, ordered inputs, quantities, selectable outputs, acquisition information, semantic colours, and Shard-specific icons.
- Search-focus, compact recipe-layout, Epic-colour, clickable-link, Wiki-formatting, reverse-recipe, and natural-plus-Fusion source fixes from Alpha 2.5.4 through 2.5.6.
- Complete removal of slot locking, Storage Overlay, and menu middle-click conversion.

### Safety

- The Beta remains client-only and passive. The Shard guide performs no runtime Wiki/API request, packet send, inventory click, Fusion, chat send, server command, or automation.
- `/th` and `/helia` remain explicit user-triggered shortcuts documented in the compliance notes; no command is sent without direct player input.

## [2.5.6] - 2026-08-10

Alpha update for Minecraft 26.1.2.

### Added

- Added a dedicated **Details** view for every one of the 320 Shards. It shows the exact Wiki-listed effect, rarity/category/skill/family/mob-type classification, and every documented acquisition method. Capture entries retain the mob, tool, and biome; kill/drop/fusion/tree-gift/shop/chest entries retain the available source detail rather than inventing missing probabilities.
- Added an explicit verified Fusion-recipe count to Shards that can be produced through Fusion, including Shards such as Queen Bee that also have natural acquisition methods. Fusion-only Shards are labelled separately.

### Fixed

- Corrected Epic Shard names from light-purple/pink (`§d`) to Minecraft's Epic dark-purple (`§5`). Stat, category, mob-type, acquisition, and rarity text now use their corresponding SkyBlock/Minecraft semantic colours.
- Clickable Shard text now darkens and gains an underline only while the pointer is over the visible text, making recipe navigation clear without changing the click target.
- Preserved spaces between differently coloured effect fragments and removed residual Wiki formatting markers from the offline catalog.

### Changed

- Bumped the alpha version to `2.5.6` and the artifact name to `QCloudy_Addition-alpha-2.5.6-26.1.2.jar`.
- Updated the offline 320-Shard detail catalog against the current Wiki rarity-table revisions and the official Bazaar allow-list. Runtime behaviour remains fully local and read-only.

## [2.5.5] - 2026-08-10

Alpha update for Minecraft 26.1.2.

### Fixed

- Replaced the generic amethyst fallback with 320 bundled, Shard-specific icons. A native Shard `ItemStack` already received by the client still takes priority and is retained in the session cache, so server/resource-pack presentation remains authoritative when available.
- Search focus now exits when the player clicks outside the search field, presses `Esc`, or presses `Tab`; clicking the field focuses it again. This restores recipe navigation and normal screen shortcuts without forcing the player to close the guide.
- Centered each recipe's two-input expression and output set as compact content-width groups. Their click targets now follow the visible items instead of spanning distant halves of the card.

### Changed

- Bumped the alpha version to `2.5.5` and the release artifact name to `QCloudy_Addition-alpha-2.5.5-26.1.2.jar`.
- Generated the 320 offline icons from the MIT-licensed SkyShards `public/shardIcons` set at reviewed commit `9688031dbc4e726168ffceb0f44884ff26e6e728`, filtered through QCA's exact 320-Shard catalog and excluding the extra Rainbug asset.

### Safety

- The Shard catalog, fallback icons, item models, and UI remain bundled and read-only. QCA performs no runtime Wiki/API/icon request and sends no chat, server command, packet, menu click, fusion, or automation.

## [2.5.4] - 2026-08-09

Alpha update for Minecraft 26.1.2.

This begins the `2.5.x` Alpha development line after `1.5.3`, which remains the latest published release baseline. The release channel is still Alpha; only the post-1.5.3 version line was renumbered.

### Added

- Added a JEI-inspired, completely offline Attribute Shard Fusion Guide under Items & Menus.
- Added search across original Shard name, ID, attribute, rarity, category, family, and skill; Recipes/Uses tabs; order-preserving input pairs; history; pagination; native item icons observed by the client; input/output quantities; special yields; and the Pure Reptile double-output chance.
- Added the local `/qshard [English query]` screen command, an **Open Guide** settings action, and an optional unbound keyboard/mouse chord. None sends a server payload.

### Changed

- Bumped the alpha version to `2.5.4` and the release artifact name to `QCloudy_Addition-alpha-2.5.4-26.1.2.jar`.
- Rebuilt the Shard catalog as an exact 320-product official Bazaar allow-list. Anteater, Zombuddy, Troodon, and Ghost Crab are present; Goldolot is `R92`; Rainbug is excluded because it is absent from the official Bazaar Shard universe.
- Preserved Attribute Fusion input order, up to three selectable outputs, Chameleon numeric stepping/rarity rollover, and the documented consumption/yield rules.
- Kept separate output slots when ID Fusion and Special Fusion produce the same Shard, because the selectable yields remain different (`x1` versus `x2`).

### Removed

- Removed slot locking, Storage Overlay, and menu middle-click conversion from the implementation, configuration, tests, and current documentation.

### Safety

- The guide uses committed offline JSON and only client-observed ItemStacks for optional resource-pack-aware icons. It performs no runtime Wiki/API/network request, menu click, fusion, command, chat, or automation.

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
- Ender Dragon outline, Chat Peek, item timestamps, cursor memory, and configurable teleport sounds.
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
- Replaced the final deprecated loaded-chunk call without expanding scan scope.

### Changed

- Renamed the project and controls category to QCloudy_Addition / QCloudy Addition.
- Reorganized settings into General, Maps, Mining, Foraging, Hunting, Safari, Crimson Isle, Combat, Pets, Chat, and Inventory with no duplicate feature cards.
- Kept AOTE/AOTV teleport sounds vanilla by default and exposed sound, volume, and pitch choices.
- Standardized alert volume defaults at 64%.

### Removed

- Catch-all `ALL` settings category.
- Golden Dragon/Dragon's Lair finder.
- Duplicate feature switches, redundant right-click hints, and separate key-capture screen.
- Runtime Firmament dependency and legacy pet PNG selection.

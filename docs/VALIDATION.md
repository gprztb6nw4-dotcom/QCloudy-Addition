# QCloudy_Addition Beta 2.6.11 Shard planner validation

Validation date: 2026-08-11

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.11+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.11+26.1.2-sources.jar`

## Result

Beta 2.6.11 preserves the original 320-Shard recipe guide and adds a fully local multi-step planner. It provides fastest and cheapest route modes, Fusion Trees, Materials Only totals, alternative direct recipes, per-Shard acquisition-rate editing, a draggable Fusion Lines view, Kraken/Kuudra inputs, and a profile-scoped Shard warehouse assembled only from Hunting Box pages the player actually opens.

Normal-mode Bazaar calculations are optional. This build contains no Bazaar HTTP client and currently reads prices only through Skyblocker's public `ItemUtils.getItemPrice` API when a compatible Skyblocker version is already loaded. SkyHanni and Firmament are not dependencies and are not accessed through private fields; if no compatible public provider is present, price-based routes are visibly unavailable while Ironman and rate-based planning remain functional.

## Automated, data, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 27 suites and 146 tests, with 0 failures, 0 errors, and 0 skips.
- The packaged catalog contains 320 unique Shard IDs and 320 unique Bazaar IDs. Its 320 acquisition-rate entries match the catalog ID set exactly; all values are finite and non-negative.
- The binary contains 320 per-ID item models and 320 Shard texture resources. No stale suffixed duplicate resource survives the clean build.
- English and Simplified Chinese resources each contain 449 keys with identical key sets and valid JSON.
- Expanded Fabric metadata declares `Beta-2.6.11+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+; class files use major version 69.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- Static data-flow review confirms that the planner performs no HTTP request, automatic `/hb`, inventory click, Fusion, chat send, command, movement, packet, or hidden-server-data request. The warehouse parser accepts only exact visible `Owned: N Shard(s)` lore inside a screen titled `Hunting Box`.

## Validation boundary

This audit verifies compilation, planner calculations covered by tests, catalog/rate/resource completeness, language parity, client-only data flow, archive integrity, metadata, filenames, checksums, and build/release identity. It does not claim an authenticated Hypixel Hunting Box regression, visual approval at every GUI scale, compatibility with every future Skyblocker price API, or a full installed-modpack performance run. Before wider publication, open every `/hb` page on a real profile, compare several recorded counts, compare representative multi-step routes with the live Fusion preview, and test Normal mode once with and once without a compatible Skyblocker build.

## SHA-256

- Binary JAR: `12044c22054f9af08038e6569d95e043e013fc47f39621ec4b98b4a531f3a0a2`
- Sources JAR: `21b33ca81ae0d3359591a07c5c82b5805736c1ad2bd5d1e56cb2259aaca32fb2`

---

# QCloudy_Addition Beta 2.6.10 Tree Gift creature-alert validation

Validation date: 2026-08-11

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.10+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.10+26.1.2-sources.jar`

## Result

Beta 2.6.10 fixes Tree Gift creature lines that were correctly recognized but discarded by the old ownership gate. The player-only `+N rewards gained!` summary now proves ownership without requiring one legacy contribution sentence. Exact creature rows are supported before or after that summary, in a single multi-line component, and for five seconds after a proven block's closing border. A nearby player's public creature line remains inert without the local player's summary.

## Automated and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 25 suites and 137 tests, with 0 failures, 0 errors, and 0 skips.
- Eight focused session tests cover the normal personal block, nearby public rejection, buffered rewards, post-border creature delivery, post-border expiry, missing legacy contribution text, a complete multi-line block, and a compacted borderless multi-line value.
- Expanded Fabric metadata declares `Beta-2.6.10+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- Static data-flow review confirms the changed session only consumes received chat components and hover text, deduplicates each loot within the bounded session, and contains no packet, chat send, command, click, movement, HTTP, or server-query path.

## Validation boundary

This audit verifies compilation, state-machine behavior, false-positive rejection in the covered message orders, metadata, archive integrity, filenames, checksums, and build/release identity. It does not claim an authenticated Hypixel Tree Gift regression. The remaining acceptance check is to earn a real Tree Gift creature and confirm one center-screen alert and one configured local sound, then stand beside another player's Tree Gift and confirm their public creature line stays silent.

## SHA-256

- Binary JAR: `25382321625a5be940e97ab0e42cd36d6a41ed6366f69f354170f979bb67ad99`
- Sources JAR: `3d6b8c8cf171e21e75be01e79965cd1124117ea0b90d73253d2693e12bc4a2cd`

---

# QCloudy_Addition Beta 2.6.9 lava-fishing sound validation

Validation date: 2026-08-11

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.9+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.9+26.1.2-sources.jar`

## Result

Beta 2.6.9 fixes the missing bite cue on Hypixel lava-fishing casts whose Fishing Hook does not populate the local player's direct owner link. Directly owned water/lava hooks still take priority. A physical local fishing-rod use now opens a bounded 40-tick association window for one newly loaded local-owned or ownerless hook, while excluding every hook already present and every hook explicitly owned by another player. The existing exact nearby `!!!` marker and once-per-hook sound gate remain unchanged.

## Automated, resource, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 25 suites and 132 tests, with 0 failures, 0 errors, and 0 skips.
- Focused resolver tests cover direct-water-hook priority, a newly loaded ownerless lava hook, preference for a locally owned candidate, rejection of pre-cast and other-player hooks, reel/reset behavior, the 40-tick expiry, and the no-idle-scan state.
- English and Simplified Chinese resources each contain 378 keys with identical key sets and valid JSON.
- Expanded Fabric metadata declares `Beta-2.6.9+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- The binary JAR contains `FishingBiteAlert`, `FishingBiteSession`, `FishingHookResolver`, `assets/qcloudy_addition/sounds.json`, and `assets/qcloudy_addition/sounds/fishing/ciallo.ogg`.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- Static data-flow review confirms that the item-use callback returns `PASS`, the broader hook query is inactive while idle, and the feature contains no automatic cast/reel, click, movement, command, chat, packet, HTTP, or audio-download path.

## Validation boundary

This audit verifies compilation, resolver behavior, resource presence, bilingual configuration, archive integrity, metadata, filenames, checksums, and build/release identity. It does not claim an authenticated Hypixel lava-fishing regression. Before wider publication, the owner should test one real water bite and one real lava bite, confirm the Ciallo cue occurs once in each case, and confirm redistribution rights for the supplied recording.

## SHA-256

- Binary JAR: `b3ebf47ef848f629782784b22ef14e6d7e03fb9bbe86bb0222a8ab725518e3e9`
- Sources JAR: `3d79108989509ffa1c02f4e663d33c067d1082f74b52741c2c52b4fb24e42e3f`

---

# QCloudy_Addition Beta 2.6.8 Fishing Bite Sound validation

Validation date: 2026-08-11

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.8+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.8+26.1.2-sources.jar`

## Result

Beta 2.6.8 adds an opt-in Fishing Bite Sound under General > Fishing. It watches only the local player's own loaded Fishing Hook for Hypixel's exact nearby visible `!!!` ArmorStand and plays the bundled Ciallo OGG at most once per hook. The feature defaults off and has an independent continuous 0–100% volume slider at the project-wide 64% default.

## Automated, resource, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 24 suites and 127 tests, with 0 failures, 0 errors, and 0 skips; class files use major version 69.
- Focused tests verify once-per-hook playback gating, re-arming after the hook is gone or its entity ID changes, the `sounds.json` registration, and the bundled resource's OggS signature.
- English and Simplified Chinese resources each contain 378 keys with identical key sets and valid JSON.
- Expanded Fabric metadata declares `Beta-2.6.8+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- The binary JAR contains `assets/qcloudy_addition/sounds.json`, `assets/qcloudy_addition/sounds/fishing/ciallo.ogg`, and both fishing detector/session classes. The packaged audio is Ogg Vorbis stereo at 44.1 kHz and is loaded from QCA's own client resource pack; no separate pack or runtime download is required.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- Static data-flow review confirms the detector scans only the four-block neighborhood of `Player.fishing`, plays a local sound, and contains no cast, reel, click, movement, command, chat, packet, HTTP, or texture/audio download path.

## Validation boundary

This audit verifies compilation, automated behavior, resource presence and format, bilingual configuration, archive integrity, metadata, filenames, checksums, and build/release identity. It does not claim an authenticated Hypixel timing/audio regression. Before wider publication, the owner should test one real bite at the intended GUI/audio settings and confirm redistribution rights for the supplied `Ciallo.mp3` recording.

## SHA-256

- Binary JAR: `e8806bfd92c6b4629e968dc636d3fc5e4af546d3b6361cb3a1237be83fdeb4e7`
- Sources JAR: `aa9491473810f148f7cb15522e2119317416b922ec59477213bdfd8f634abb01`

---

# QCloudy_Addition Beta 2.6.7 Dwarven map validation

Validation date: 2026-08-10

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.7+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.7+26.1.2-sources.jar`

## Result

Beta 2.6.7 replaces the Dwarven Mines texture with the supplied one-layer 12-region map and recalibrates the marker projection to its image geometry. Dwarven projection now reads X/Z, yaw, and the already-visible sub-location only; Y is absent from both the projection API and fallback calculation.

## Automated, coordinate, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 123 tests, 0 failures, 0 errors, and 0 skips; class files use major version 69.
- The supplied `2000×2000` PNG (`cb714dc325ae4971088ade84846d9ad97af0e3966553d7d1f63931c3be1ef15a`) was resampled to the HUD's native `200×200` RGBA texture. The packaged texture hash is `639492c458d4acd232cf57fd250cf1d2548f4c07f95ca48bcc83a96417fb85c0` in source, binary JAR, and Sources JAR.
- Projection tests cover all 12 named regions on the replacement image, explicit sub-location selection, X/Z-only generic-location fallback between Royal Mines and Royal Palace, clamping at region bounds, and a coordinate grid that confirms every calibrated marker centre remains on opaque map content. Each region uses an inset bilinear X/Z calibration rather than a Y layer or a single global rectangle.
- Resource tests verify the 200×200 texture dimensions, transparent outside corner, and the exact fill colours for Village, Upper Mines, Rampart Quarry, Forge, Lava Springs, Cliffside, Far Reserve, Goblin Burrows, The Mist, Ice Wall, Royal Mines, and Royal Palace.
- Expanded Fabric metadata declares `Beta-2.6.7+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- The Dwarven map path remains `assets/qcloudy_addition/textures/gui/dwarven_mines.png`; the map generator deliberately leaves this maintained supplied asset untouched.

## Validation boundary

This audit verifies source/configuration consistency, all 12 projection calibrations, automated behavior, archive integrity, metadata, filenames, checksums, and build/release identity. It does not claim an authenticated Hypixel visual regression. The replacement should still be checked in-game at the user's GUI scale and in each named region; any real-server offset report should include the displayed sub-location and player X/Z.

## SHA-256

- Binary JAR: `97d7a9df937075eb071a77bb80c700cf865a91eac909a8b7982aac4e57c895ef`
- Sources JAR: `55bc0b309c7faafab5f19bcbb434e22f0f69da70607b404083f826b9deea8905`

---

# QCloudy_Addition Beta 2.6.6 promotion validation

Validation date: 2026-08-10

Validated artifacts:

- `release/QCloudy_Addition-Beta-2.6.6+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`

## Result

Beta 2.6.6 promotes the reviewed Alpha 2.5.6 implementation without changing Java feature behavior. The pre-promotion Alpha 2.5.6 baseline and the renamed Beta 2.6.6 build both completed the full Java 25 test/build pipeline. The Beta change is limited to the release channel, version, artifact naming, and publication documentation.

## Automated, data, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully for the original Alpha 2.5.6 baseline and again after the Beta promotion. The final XML reports 120 tests, 0 failures, 0 errors, and 0 skips; class files use major version 69.
- The final playable artifact is exactly `QCloudy_Addition-Beta-2.6.6+26.1.2.jar`; the source artifact is exactly `QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`.
- Expanded Fabric metadata declares `Beta-2.6.6+26.1.2`, client-only environment, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- The binary includes `LICENSE_QCloudy_Addition`, `THIRD_PARTY_NOTICES.md`, and `SHARD_DATA_NOTICE.txt`.
- The binary contains exactly 320 Shard PNGs, 320 item definitions, and 320 item-model definitions. The catalog/resource invariants remain covered by the passing test suite.
- English and Simplified Chinese resources each contain 373 keys with identical key sets and valid JSON.
- No Java source or runtime resource behavior changed between Alpha 2.5.6 and this Beta; required and optional dependencies remain unchanged.

## Validation boundary

This audit verifies source/configuration consistency, automated behavior, generated data invariants, archive integrity, metadata, filenames, checksums, and build/release identity. It does not claim a fresh authenticated Hypixel regression or pixel-level visual acceptance with every GUI scale, resource pack, operating system, and modpack. Beta status is an owner-approved testing channel, not a claim of official Hypixel approval or stable-release completeness.

## SHA-256

- Binary JAR: `0871774cfa47641d220d18d53f9235ee1b02ff2abfc9ac586dd2a55a0adbc2fd`
- Sources JAR: `2baa8c557826d2bdf69816576ba7891261d7cde48bdeb12fcf6ebcc480f75137`

---

# QCloudy_Addition Alpha 2.5.6 Shard details and semantic-colour validation

Validation date: 2026-08-10

Validated artifacts:

- `release/QCloudy_Addition-alpha-2.5.6-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.6-26.1.2-sources.jar`

## Result

Alpha 2.5.6 adds a dedicated Details view to all 320 Shards. It shows the Wiki-listed effect, semantic classification, and documented acquisition methods without replacing missing facts with guesses. Epic uses Minecraft's `§5` dark purple; stats, categories, mob types, acquisition methods, and rarities use their corresponding semantic colours. Clickable Shard names darken and underline only while the visible text is hovered. Recipes remain indexed independently from natural acquisition: Queen Bee, for example, keeps its Honeyhive/Honeycomb Collection acquisition details and also exposes every verified ordered Fusion recipe that can produce it.

## Automated, data, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 120 tests, 0 failures, 0 errors, and 0 skips; class files use major version 69.
- The catalog contains 320 unique Shard IDs, names, Bazaar IDs, internal IDs, detail records, and Shard-specific icon resource sets. Rainbug/L49 is absent.
- Every Shard has a non-empty effect and acquisition display. The current Wiki tables provide a documented acquisition for 319 catalog Shards; Wild Hog is the only current table gap and is explicitly labelled as not documented instead of receiving a fabricated source.
- Gemzie is regression-tested as Epic, with `+0.25–2.5 Gemstone Spread`, a yellow Gemstone Spread label, and the Critter Capsule/Cavern Biome capture source. Defense and Animal/Aquatic semantic colours are covered by catalog tests.
- Pandarai is regression-tested as Fusion-only. Queen Bee is regression-tested as having both natural acquisition data and non-empty reverse Fusion recipes. The same reverse index powers the Recipes view for every possible output Shard.
- Search is regression-tested across canonical name, ID, family/category metadata, effect text, acquisition text, and mob type. Generated detail text contains no residual Wiki templates, links, HTML tags, or bold markers.
- English and Simplified Chinese resources each contain 373 keys with identical key sets. The bundled `SHARD_DATA_NOTICE.txt` and third-party notices document the Wiki-data and icon-source licences.
- Both binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`; their `release` copies are byte-identical to `build/libs`.
- Metadata is client-only and declares `alpha-2.5.6-26.1.2`, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- Static inspection finds no Shard-guide runtime HTTP/API client, packet send, chat/command send, inventory click, Fusion action, or automation. Wiki/API data generation happens offline before packaging.

## Validation boundary

This pass validates source, generated data, unit tests, build outputs, archive integrity, and static client-only boundaries. It does not claim an authenticated Hypixel regression or pixel-level acceptance at every GUI scale/resource pack. Those live checks remain required before promoting this alpha to beta or release. The 2.5.5 report below remains historical evidence only.

## SHA-256

- Binary JAR: `d4ed9ba609a64787b4de247f6561c1e5d1961f8359bdf9f25df3ba053a9b82ce`
- Sources JAR: `13251eaafe50c00ab4f10554dd8ca1b78dca6d65ca011191d5d5f7ffbf41fca0`

---

# QCloudy_Addition Alpha 2.5.5 Shard icon and interaction validation

Validation date: 2026-08-10

Validated artifacts:

- `release/QCloudy_Addition-alpha-2.5.5-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.5-26.1.2-sources.jar`

## Scope

Alpha 2.5.5 replaces the amethyst fallback with a bundled, Shard-specific icon for every one of the 320 catalog IDs, while keeping an already-received native ItemStack as the session-cached priority. It also releases search focus on outside click, `Esc`, or `Tab`, restores focus on a direct search-field click, and centers each input pair/output set as a compact group whose hitboxes follow the rendered bounds. Six pairs intentionally share the same PNG because the reviewed upstream Shard icon set gives those Shards the same in-game appearance.

## Automated, data, and artifact checks

- Java 25 `clean test build prepareRelease` completed successfully. Fresh XML reports 116 tests, 0 failures, 0 errors, and 0 skips across 22 suites; class files use major version 69.
- The 320 catalog IDs, 320 bundled Shard PNGs, 320 item models, and 320 item definitions have exactly equal ID sets. Every PNG decoded successfully, every dimension is between 16 and 64 pixels with alpha, and Rainbug/L49 is absent.
- The generic amethyst fallback was removed. Static inspection confirms that an already-received native Shard ItemStack is cached by Shard ID for the session and takes priority over the bundled local model; the fallback itself is a Shard-specific `PLAYER_HEAD` with an overrideable `qcloudy_addition:shards/<id>` model.
- Regression tests cover search-focus exit keys, compact input/output geometry at wide and constrained widths, catalog/icon completeness, recipe invariants, and responsive-layout bounds. The outside-click/refocus branches and rendered-hitbox wiring were also inspected directly.
- Both renumbered binary and Sources JARs pass JDK 25 `jar --validate` and `unzip -t`. A full extracted-payload comparison against the corresponding pre-renumbering archives confirms that only `fabric.mod.json` version metadata changed.
- Metadata is client-only and declares `alpha-2.5.5-26.1.2`, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- English and Simplified Chinese resources both contain 362 keys with identical key sets. `git diff --check`, JSON parsing, and a static scan of the Shard package for network clients, packets, commands, chat, inventory clicks, fusion automation, and the removed amethyst fallback found no match.
- A fresh combined development-client smoke launch initialized QCloudy_Addition alongside BabyzombieAddons, Firmament, Skyblocker, SkyHanni, and Mod Menu, completed the combined resource reload, created the item atlas, and started the sound engine. The log contains no missing or failed `qcloudy_addition:shards/*` model/texture load and no QCloudy exception. The observed errors came from the unauthenticated development account and SkyHanni rejecting current NEU constants (`HUNTING_FORTUNE` and `FISHING_NET`), not from QCloudy_Addition.

## Validation boundary

This pass includes a fresh combined initialization/resource smoke launch, but it does not claim an authenticated Hypixel regression or an in-game pixel-level acceptance check at every GUI scale/resource pack. Those live visual and server checks remain required before promoting this alpha to beta or release. The 2.5.4 report below remains historical evidence only.

## SHA-256

- Binary JAR: `b7ca1fa7477e31f86bd4f97c045e17238d3a7920138ebe6364d1a63689042f56`
- Sources JAR: `ba050233e7dabe0ee8c65d5784f38ca40fec8ca00e6aac446a0c33d539f09095`

---

# QCloudy_Addition Alpha 2.5.4 Shard Fusion validation addendum

Validation date: 2026-08-10

Validated artifacts:

- `release/QCloudy_Addition-alpha-2.5.4-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.4-26.1.2-sources.jar`

## Result

The standalone, client-only Shard Fusion guide is included in Alpha 2.5.4. It provides JEI-inspired search, Recipes and Uses views, ordered-input swapping, one-to-three output slots, responsive narrow-screen layouts, bilingual UI labels, and resource-pack-aware observed item icons without depending on JEI or another SkyBlock mod. The runtime reads only its bundled, versioned catalog and client-visible item data; it performs no Wiki/API request, packet send, inventory click, chat send, command send, or fusion automation.

## Automated, data, and artifact checks

- Java 25 clean testing passed 108 tests with zero failures, errors, or skips.
- Java 25 `./gradlew clean build prepareRelease` completed successfully; the new class files use major version 69.
- The catalog contains 320 unique Shard IDs, names, and Bazaar IDs and exactly matches the 320 `SHARD_*` products in the reviewed official Bazaar snapshot. Anteater, Zombuddy, Troodon, Goldolot (`R92`), and Ghost Crab are present; Rainbug is absent.
- Fusion invariants, ordered input behavior, first-input quantities, Chameleon stepping/exclusions, reverse Recipes/Uses indexes, shared recipe-instance indexing, and separate same-Shard ID/Special output slots are covered by tests.
- Both renumbered JARs pass JDK 25 `jar --validate` and `unzip -t`. A full extracted-payload comparison against the corresponding pre-renumbering archives confirms that only `fabric.mod.json` version metadata changed.
- Metadata is client-only and declares `alpha-2.5.4-26.1.2`, Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25+.
- English and Simplified Chinese resources both contain 362 keys with identical key sets.
- `git diff --check`, JSON parsing, and static scans of the new Shard code completed without an error.

## Validation boundary

This addendum does not claim an authenticated Hypixel regression, pixel-level acceptance at every GUI scale/resource pack, or a fresh combined launch with all four supplied reference mods. Those live checks remain required before promoting the alpha to beta or release. The older 1.5.1 report below is retained as historical evidence and must not be read as a 2.5.4 live-test result.

## SHA-256

- Binary JAR: `4a26801c3d63cfb2cf4ae10f0249efd761fe6e1264caedb239133e9a698fb773`
- Sources JAR: `a2a3232c5d6342da89037225e3ec78302d8a0910a72c3cbe56a313f409720025`

---

# QCloudy_Addition 1.5.1 release validation

Validation date: 2026-08-06

Validated artifacts:

- `release/QCloudy_Addition-1.5.1+26.1.2.jar`
- `release/QCloudy_Addition-1.5.1+26.1.2-sources.jar`

## Result

The 1.5.1 release adds a player-clicked reconnect button, spatial Beeheemoth sound controls, and a bounded personal Tree Gift alert state machine that remains observable when a chat-compaction mod cancels normal display. It passes local source, unit-test, archive, reproducibility, standalone initialization, and a fresh supplied-reference-mod compatibility launch for Minecraft 26.1.2. This is a release-readiness result, not a claim that every live Hypixel entity, message, sound, or screen was exercised on an authenticated account.

## Exact environment

- Minecraft 26.1.2
- Fabric Loader 0.19.3
- Fabric API 0.155.2+26.1.2
- Eclipse Temurin Java 25.0.4; class-file major version 69
- Gradle Wrapper 9.6.1
- Fabric Loom 1.17.17

## Automated and artifact checks

- 98 JUnit tests passed across 23 suites, including the real Helia overview/detail layouts, bounded Tab/scoreboard Chapter parsing, rejection and repair of cached `SB Level` false tasks, official multi-day Benefactor donation chat, Tab countdown and inactive Temple menu blocks, strict local-player Tree Gift ownership and bonus-block buffering, personal-versus-teammate Loot Share Wumpa capture policy, Snoozle mixed-material/size boundaries, all four Safari Milestone layouts, account/profile normalization, Chapter-switch isolation, Cold campfire eligibility, the complete 37-Critter rarity table, all eight Wumpa prerequisites, Warden cooldown boundaries, Tree Protection Order countdown, Lasso REEL parsing, Beeheemoth sound-path isolation, manual reconnect address reconstruction, the scale-9 Beeheemoth signature, unique category ownership, Hunting defaults/settings routing, and all 16 official Fairy Soul coordinates.
- Java compilation enables deprecation lint and completes without a warning; the Snoozle scanner uses `ClientLevel.hasChunk(chunkX, chunkZ)` so it retains the already-loaded-chunk boundary without the retired `hasChunkAt` API.
- Two final Java 25 `clean test build` runs produced byte-identical binary and Sources JARs.
- JDK 25 `jar --validate` and `unzip -t` passed for both final artifacts.
- Binary and Sources metadata contain version `1.5.1+26.1.2`.
- The release-directory copies are byte-identical to the final `build/libs` artifacts.
- Both artifacts contain `LICENSE_QCloudy_Addition` and `THIRD_PARTY_NOTICES.md`.
- No reference-mod class, test class, legacy pet-icon PNG directory, `PetIconRegistry`, or removed lair-finder implementation is present in either release JAR.
- Static inspection found no `sendChat`, HTTP, WebSocket, packet-sender, automatic movement, or chunk-request code.
- The only outbound command payloads are the documented, physically user-triggered Storage navigation commands (`storage`, `enderchest <1-9>`, and `backpack <1-18>`), `/th` payload `warp torrhus`, and `/helia` payload `chapter torrhus`.

## Launch and compatibility matrix

| Instance | Result |
|---|---|
| QCA 1.4.6 + Fabric/API only | Standalone Loom launch loaded 51 modules, initialized QCA, reloaded resources, and started the sound engine without Firmament or another SkyBlock mod |
| QCA 1.4.6 + four supplied reference mods | A fresh 94-mod launch with BabyzombieAddons 3.4.1, SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and Mod Menu 18.0 initialized QCA, all reference mods, combined resources, and the sound engine without a QCA exception |
| QCA 1.4.9 transparent-icon delta | The selected original emblem was retained, converted to 128×128 RGBA with transparent corners, and included in two byte-identical clean builds; metadata, class version 69, archive integrity, tests, and release-copy hashes passed |
| QCA 1.5.0 + Fabric/API | Standalone Loom loaded 51 modules; QCA initialized, resources and sound engine completed, and no QCA exception appeared before the client was intentionally stopped after the main-menu load |
| QCA 1.5.1 + Fabric/API | Standalone Loom loaded 51 modules; QCA initialized, resources and the sound engine completed, and no QCA exception appeared before the client was intentionally stopped after the main-menu load |
| QCA 1.5.1 + four supplied reference mods | A fresh 94-mod launch with BabyzombieAddons 3.4.1, SkyHanni 7.41.0, Skyblocker 6.8.2, Firmament 44.3.0, and Mod Menu 18.0 initialized QCA, combined resources, and the sound engine; it remained alive for about 33 minutes and stopped cleanly. A second launch after the final loaded-chunk API cleanup reached the same initialization boundary. Neither run reported a QCA or mixin-injection exception |

The combined instance warnings/errors concerned reference-mod refmaps/resources, an optional ModernUI class, missing BabyzombieAddons custom-disc files, SkyHanni/Skyblocker remote-repository requests, unauthenticated profile/Realms activity, and SkyHanni 7.41.0 rejecting the current NEU-repository constants `HUNTING_FORTUNE` and `FISHING_NET`. They were not thrown from QCA and did not stop client/resource/sound initialization. QCA has no Firmament runtime dependency; its optional duplicate-feature handoff checks only whether the mod id is loaded and leaves QCA fully available when Firmament is absent.

## Final integrity fixes

- Replaced the previous mod icon with the selected original cloud-ring/orange-core/cyan-locator emblem. Only the background was converted to alpha; the subject was not redrawn. The shipped PNG is 128×128 RGBA, all four corner alpha values are zero, and a 32×32/checkerboard preview remains legible without a black square.
- Added the categorized Torrhus Canyon and Critter Safari module: combined Chapter/resource HUD, Miria Contest calculations inside that HUD, Critter behavior guidance, Benefactor status, configurable Tree Gift alerts, Safari run/biome Critterdex dashboard, Sparkling alerts/highlight, Floor Drop and Quest Item assistance, Wumpa encounter state, and Safari Belt milestone tooltip rows. QCA neither modifies the right scoreboard nor duplicates its contest timer.
- Added a default-on, separately switchable Tree Critter Timer to the combined Torrhus HUD. Every 10 client ticks it strictly parses the nearest loaded `Critter in: <duration>` entity display name, following SkyHanni's passive tree-progress acquisition pattern; it never guesses which Pot was used or starts a synthetic timer. This covers all four currently indexed Pot of Honeycomb sizes and server-applied speed/instant-attraction modifiers without hard-coded drift.
- Removed Safari Critter/Sparkling outline assignment from Armor Stand backed capture props. The prior marker-state workaround still allowed the support body to enter the outline pass on this renderer version; the safe fallback now excludes those stands entirely while preserving rarity/configured outlines for real non-Armor-Stand Critter entities. The entity itself remains untouched.
- Fixed intermittent Critter Behavior replay after a Lasso capture. Removed entities are excluded, and an exact received `CAPTURE! You caught ...` confirmation now suppresses only that captured behavior-Critter name for three seconds; other Critter types remain promptable and normal same-type behavior resumes after the bounded window.
- Added a default-on Beeheemoth helper using the supplied BabyzombieAddons scale-9 Bee signature. Its vanilla outline color uses QCA's RGB/HSV picker; a fixed yellow first-observed-position beacon dismisses on a 10-block approach, the player's exact capture confirmation, or entity disappearance and cannot respawn for the same UUID after dismissal.
- Added a separate default-on Beeheemoth sound control with a 64% default volume. It scales only non-relative Bee event/resolved sounds within 12 blocks of the loaded scale-9 Beeheemoth or its just-observed position; unrelated Bees and all other sounds are unchanged. Disabling this sub-option makes only matching Beeheemoth sounds silent.
- Added a separate default-on Lasso REEL sound at 64% volume. It uses SkyHanni's local-player leash plus nearby exact-ArmorStand relation and plays only on the false-to-true REEL transition; the secondary settings page exposes a continuous 0–100% volume slider without a redundant enable switch.
- The Hunting parser uses anchored or bounded formats and only locally received scoreboard, Tab, chat, title, entity-name, inventory, and already-loaded block-state data. The module contains no new command, chat, network, inventory-click, movement, combat, or interaction sender.
- All Hunting alerts use the shared center-title rendering path, while each alert feature owns its own sound switch and continuous 0–100 volume slider defaulting to 64%. The General sound switch is master mute only. Long task and Critter names wrap; no ellipsis fallback exists in the Hunting renderer.
- The official list of 37 Safari Critters, quest items, contest thresholds, ticket tiers, and documented behavior were covered by local parser/config tests. Safari Belt bonus values are deliberately read from received lore instead of hard-coded totals.
- Fixed Safari Belt Milestones by using one contextual parser for the already-open Milestone menu and the belt tooltip. Combined rows and split title/lore rows now populate Cavern, Forest, Haunted, and Icy independently; locked entries and capture fractions are rejected. The four confirmed levels are stored per Minecraft account/SkyBlock Profile and update only on a higher observed level.
- Added account/profile-scoped persistence for received Forest/Desert Whispers, Forest/Safari Essence, Forest Fortune, Sweep, Helia Chapter/task/progress, and Safari Belt Milestones. Repeated Tab, scoreboard, and menu snapshots are treated as absolute values and do not accumulate; only exact received chat gain messages are additive. Switching to a newly observed Chapter clears stale previous-task fields.
- Repaired Helia Chapter acquisition by parsing Tab and scoreboard independently, recognizing the actual Chapter overview/detail inventory shapes, joining only a four-second/12-line received-chat block, and removing previously cached non-Chapter tasks such as `SB Level`. Repaired Benefactor acquisition from bounded Tab/scoreboard blocks, Forest/Desert Temple menus, and the official donation chat form; day units, same-temple extension, cross-temple replacement, stale-menu protection, expiration, and account/profile persistence are covered.
- Safari Essence was removed from the Safari Dashboard and is now shown only in the Torrhus resource section, with an independent Torrhus toggle.
- Added ordered, configurable Cold warnings (strictly above 80/90 by default), a dedicated default-on 64% Cold-alert sound setting, and a nearest-loaded-campfire red beacon. The first above-threshold observation now triggers an immediate scan and the active state refreshes every 40 ticks; the beacon stops immediately when the next received Cold value falls.
- Added a one-shot Doomspiral readiness warning at the Wiki-documented requirement of at least four `Soothing Incense`, default-off red Wumpa motion/collision projection, default-off pink beams at the 12 Torrhus and four Safari Fairy Soul coordinates, and default-on outlines for all 37 capturable Critters using their official Shard rarity colors.
- Added a default-on Warden capture-ready alert in the bounded Doomspiral arena. It follows the supplied BabyzombieAddons 140-client-tick rule, compensates with received local-player latency, rejects emerging/digging poses, and uses a dedicated center alert plus default-on 64% sound without sending a capture action.
- Split Wumpa party prerequisites from the personal Safari Critterdex. Anchored personal captures and received `LOOT SHARE ... catching a <Critter>` teammate confirmations update the eight-item Wumpa set, while Loot Share remains excluded from personal Critterdex rows. Once spawned, the checklist is replaced by `Wumpa: Spawned` plus live phase. Movement/projection resolves the actual Ravager body near the Wumpa label and uses short movement/stillness confirmation windows; 8/8 and massive-footsteps/awoken still share one per-run alert flag.
- Added a separate default-on Snoozle Wall Overlay feature in the Safari category. A once-per-second bounded scan checks only already-loaded nearby blocks, accepts small connected components containing both Wiki-documented `Cobbled Deepslate` and `Tuff`, and submits translucent quads only on air-exposed faces. Oversized formations and single-material patches are rejected; the default green color uses the standard RGB/HSV picker.

- The two supplied 2026-08-04 crash ZIPs were byte-identical (`8abff84c45b6b2ecb8ffada8de514a446755c70fc2d1ff6f853d47a24811a5d7`) and identify one QCA 1.2.5 Storage-cache failure: a cached Efficiency enchantment Holder belonged to an older dynamic registry set and was serialized on the render thread without an exception boundary. QCA now detects registry replacement, rebinds normal/stored enchantments by resource key, isolates load/search/hash/encode/write failures per item, preserves a failed item's slot as empty, and prevents any Storage snapshot encoding failure from escaping to the render thread. See `CRASH_ANALYSIS_2026-08-04.md`.
- Removed the catch-all `ALL` settings category. Foraging, Hunting, and Safari now have separate sidebar categories with a single enum owner for every card: Torrhus/tree progression lives in Foraging, cross-island capture utilities live in Hunting, and Critter Safari systems live in Safari. No feature is registered in more than one category. The combined HUD gear routes to Foraging in Torrhus, Safari in Critter Safari, and Hunting elsewhere.
- Collapsed the old two-tab header into one `Features` tab. `General` is now the first sidebar category and contains `UI animations` plus the alert-sound master mute; the old appearance/layout sidebar and duplicate layout card were removed because the bottom-left `Edit HUD` button already opens the loaded-HUD editor.
- Removed secondary-page feature switches and empty secondary pages. Left-click on a feature card is the only feature toggle; right-click opens only meaningful feature-specific settings.
- Every HUD background color picker now has an explicit Transparent preset in addition to RGB selection.
- Registered `/th` as a client command with no setting or disable path; a physical `/th` input sends exactly `warp torrhus` unless another client command already owns the root name.
- Registered `/helia` as a client command with no setting; a physical `/helia` input sends exactly `chapter torrhus` unless another client command already owns the root name.
- Reworked Tree Gift alerts into a 15-second, border-bounded received-chat block. The local player's exact `+N rewards gained! (hover)` summary remains sufficient for its own `SHOW_TEXT`; separate percentage and `A <loot> fell from the Tree!` bonus rows become eligible only when the same block also contains the Tree Gift header, the local `You helped cut...` contribution line, and the personal reward summary. Early bonus rows are buffered until that ownership proof arrives, duplicate loot is emitted once, public/nearby-player blocks and lasso messages are rejected, and `GAME_CANCELED` observation preserves the parser when another client mod compacts the visible chat.
- Added a default-on manual reconnect card in General and a vanilla-width button on the disconnect screen. It remembers only the current session's last explicit multiplayer target and resource-pack preference; one physical button click starts one normal Minecraft connection attempt. It has no timer, automatic loop, server bypass, persisted address, chat payload, or command payload.
- Fairy Soul success and already-found confirmations now hide the nearest listed Soul within 10 blocks immediately and persist that island-coordinate key per received SkyBlock profile. A failed/unconfirmed click does not hide a waypoint.
- Removed every feature card's duplicate top-right switch and bottom-right right-click label. Left-click still toggles the feature, the left blue strip remains the enabled-state indicator, and right-click still opens the complete secondary settings page.
- The custom search frame and vanilla borderless `EditBox` now use separate but shared geometry: the editable text baseline is vertically centered from the real font line height, horizontal padding is symmetric, the complete visible frame remains clickable, and navigation tabs shrink before they can overlap the field on narrow GUI widths.
- QCA hotkeys now edit inline on their existing feature-settings page. Keyboard and mouse buttons, including buttons 1–5/side buttons, support modifier chords. `Esc` clears the active row to unbound, and the removed `KeyChordScreen` is absent from the source and release. Runtime paths were kept for mouse-bound Open Settings and Chat Peek.
- Completely removed the Golden Dragon/Dragon's Lair finder from config, feature cards, HUD types, renderer, scanner, translations, and release artifacts. The text `Dragon's Lair` may still occur only as an ordinary Crystal Hollows location name used by island classification.
- Pet held-item details confirmed from the received Pets menu, Tab widget, or chat are retained per pet in QCA's own config. A max-level pet hides only the redundant progress-to-max line; it does not suppress the held-item row.
- Replaced the generated PNG fallback path with normal verified player-head profiles. QCA writes no synthetic `petInfo`, so external item-model predicates cannot replace the HUD icon with an unrelated orb or pet model.
- Generated metadata contains 88 base profiles, 352 skin profiles, 5,422 pet-owned current/animated texture mappings, and 87 accessory definitions. Baby Spinosaurus has 60 recognized current/animation textures assigned to its exact skin family.
- Legacy pet PNG folders and `PetIconRegistry` are excluded from both binary and Sources JARs and cannot be selected at runtime.
- Pet text, held-item text, commission names, and progress values use complete measured width, including bold style; no ellipsis fallback is used.
- All adjacent UI, map, Storage, key-chord, middle-click, Chat Peek, teleport sound, and slider behavior remains covered by the existing test/build and launch checks.

## Remaining live-test boundary

No authenticated Hypixel account was available in the local instance, and SkyBlock was under maintenance during this validation. The desktop UI controller also could not attach to Loom's unbundled Java process, so the reconnect button was verified against the exact 26.1.2 `DisconnectedScreen` layout and mixin target, configuration/unit tests, archive contents, and client initialization, but not by an automated pixel-level click-through. Real Torrhus/Safari messages and entity states, Tree Gift ownership/order variants, Beeheemoth spawn/capture sound events, teammate Loot Share capture sequencing, Wumpa Ravager/name-carrier association and projected line accuracy, the exact Snoozle wall component geometry/overlay appearance, Armor Stand capture-prop suppression, Tree countdown labels, Lasso timing and sound feel, Cold text variants and campfire selection, Doomspiral Warden timing, Fairy Soul beams, live widgets, the user's resource pack, pet transitions, Ender Dragon outlines, GUI-scale combinations, reconnect-screen appearance, and physical input feel still need an in-game user regression. The launch check establishes initialization, not screenshot-level correctness or a zero-bug/zero-anti-cheat-risk guarantee. See `COMPLIANCE.md`.

## SHA-256

- Binary JAR: `e3d3131d4f1d40e7859b655aed56aa72ef9a5dae2bd045710d4bde9daf705536`
- Sources JAR: `ab825c382b6f672cfc6ce2381db0a904ea60b23e593fa5254bd7e87722442ada`

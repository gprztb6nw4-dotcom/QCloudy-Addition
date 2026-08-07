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

# Client and rules boundary

## Runtime data-flow inventory

| Feature | Client input | Local output | Outgoing traffic |
|---|---|---|---|
| Dwarven map | Player X/Z/yaw, parsed visible sub-location; Y deliberately unused | Static PNG + arrow HUD | None |
| Glacite map | Player X/Y/Z/yaw, parsed location | Static layer PNG + arrow HUD | None |
| Mining tasks/powders/HOTM slot | Received Tab display names and already-open menu item names/lore | Text/progress-bar HUD and cached selected slot name | None |
| Fishing Bite Sound | Directly owned local Fishing Hook, or a newly loaded local/ownerless hook associated after physical rod use; exact nearby received `!!!` ArmorStand | One bundled local sound per hook | None |
| Crimson Isle tasks | Received `Faction Quests:` Tab display names | Text HUD | None |
| Torrhus Chapter/resources/Contest/Benefactor | Received scoreboard, Tab, chat, and already-open HOTF/menu text | Wrapped HUD, local bracket calculation, center titles | None |
| Tree Critter Timer | Nearest loaded entity display name matching `Critter in: <duration>` | One line in the combined Hunting HUD | None |
| Beeheemoth helper | Loaded Bee type/scale/UUID, local player position, received capture confirmation, nearby received Bee sound | Configurable vanilla outline/beacon and local sound-volume scaling | None |
| Lasso REEL sound | Local player's held Lasso, received leash-holder relation, nearby exact `REEL` ArmorStand | One local sound on state transition | None |
| Tree Gift alerts | Personal reward-summary `SHOW_TEXT` and exact rows in the same ownership-proven received Gift block, including canceled-display chat | Center title and local sound | None |
| Safari Dashboard/Critterdex | Received chat, Tab/scoreboard, local session clock | Combined HUD | None |
| Sparkling/Wumpa/behavior assistants | Received capture/spawn chat, visible custom names/entity motion, local player motion/death | Center title, local sound, checklist/phase HUD, optional vanilla outline | None |
| Cold/campfire safety | Received Cold text and campfire Block Entities in already-loaded chunks | Center title, local sound, nearest-campfire beacon | None |
| Doomspiral readiness | Local inventory contents | Center title and local sound at 4+ Soothing Incense | None |
| Warden capture readiness | Loaded Warden type/position/pose/client age and received local-player latency | One center title and local sound on the 140-tick ready transition | None |
| Fairy Souls | Fixed official Wiki coordinates and parsed current island | Optional pink beacon beams | None |
| Safari Critter rarity highlight | Visible received entity custom name and bundled official rarity table | Vanilla entity outline color | None |
| Wumpa route projection | Visible Wumpa position/motion and local block collision ray | Optional red line | None |
| Snoozle wall overlay | Nearby already-loaded Cobbled Deepslate/Tuff block states | Translucent exposed-face overlay | None |
| Floor Drop/Quest Item assistant | Already-loaded nearby block states and local inventory | Distance/item HUD and center title | None |
| Safari Belt details | Received item ID/lore and already-open milestone-menu items | Tooltip and account/profile-scoped local config cache | None |
| Dragon highlight | Received Ender Dragon entity and parsed location | Vanilla outline render state | None |
| Pet HUD | Received chat and Tab display names | Text HUD | None |
| Chat Peek | Physical held key and already received chat history | Temporarily changes local chat rendering/scroll target | None |
| AOTE/AOTV sound customization | Held-item ID and received nearby sound event | Keeps the original sound or replaces it with a local vanilla sound at configured volume/pitch | None |
| Attribute Shard Fusion Guide | Bundled offline 320-Shard JSON with effect/acquisition/Fusion data and 320 local icons; optional native ItemStacks already received in local menus/inventory; physical search/click/key input | Local Details/Recipes/Uses screen, Shard-specific offline icons, semantic text colours, and resource-pack-aware observed overrides | None; `/qshard` is a client-only screen command |
| Configuration | Rebindable local key, local `/aca`/`/qca`/`/ca`/`/qc` client commands, and mouse input | JSON config file | None |
| Manual reconnect | Last normal `ConnectScreen` target and an explicit click on the disconnect-screen button | Opens a fresh vanilla connection screen | One normal server connection to that recorded target, only after the click |
| Torrhus shortcut | Explicit local `/th` input | None | Sends exact payload `warp torrhus` |
| Helia shortcut | Explicit local `/helia` input | None | Sends exact payload `chapter torrhus` |

## Commands and chat

- Registered local settings commands: `/aca`, `/qca`, `/ca`, and `/qc`; each alias is skipped if another client command already owns that root literal. They open QCA settings and send nothing.
- Registered local Shard command: `/qshard [English query]`; it opens the bundled offline Attribute Shard Fusion Guide and optionally pre-fills the local search. It sends no chat, server command, packet, menu input, or network request.
- Registered local Torrhus shortcut: `/th`; it has no setting and cannot be disabled. When the user explicitly types it, QCA sends the exact payload `warp torrhus`, equivalent to the user entering `/warp torrhus`. It is skipped only if another client command already owns `/th`.
- Registered local Helia shortcut: `/helia`; it has no setting. When the user explicitly types it, QCA sends the exact payload `chapter torrhus`, equivalent to the user entering `/chapter torrhus`. It is skipped only if another client command already owns `/helia`.
- Automatic `sendCommand` calls: **none**.
- `sendChat` calls: **none**.
- Automatically generated chat contents: **none**.

## Network and automation audit

QCA contains no Hypixel Mod API, Hypixel public API, WebSocket, HTTP client, telemetry, coordinate sharing, remote updater, macro, simulated input, automatic click/movement helper, or block interaction. The only outbound actions are the documented user-triggered `/th` warp command, `/helia` chapter command, and one ordinary server connection when the player clicks `Reconnect`; none run without physical user input. Reconnect has no timer, retry loop, background attempt, or automatic join.

The Fishing Bite Sound prefers the local player's directly owned loaded hook. To support Hypixel lava hooks whose owner link may be absent, it observes a physical local rod use, excludes every hook already present and every explicitly other-player-owned hook, and accepts a newly loaded local/ownerless hook only during a bounded 40-tick window. It then scans only that selected hook's four-block neighborhood for the exact received `!!!` ArmorStand and plays the bundled local cue at most once per hook. The callback passes the original use through unchanged; it never casts, reels, clicks, moves the player, or sends a command or packet. The broader scan is inactive while idle.

The Shard Fusion Guide is a read-only local reference. Its 320-item catalog, normalized Wiki effect/acquisition summaries, fusion rules, 320 Shard-specific PNGs, item models, and mappings are generated before release and packaged in the JAR; the generator uses the [Attributes tables](https://hypixelskyblock.minecraft.wiki/w/Attributes), [Attribute Fusion rules](https://hypixelskyblock.minecraft.wiki/w/Attribute_Fusion), the [official Bazaar product list](https://api.hypixel.net/v2/skyblock/bazaar), and the reviewed MIT-licensed SkyShards icon set. The running QCA code has no path that contacts those sources or an icon service. Searching, changing focus, opening Details/Recipes/Uses, following history, resolving a bundled icon, or rendering an already-received native ItemStack do not click a container, perform a fusion, select an output, send `/qshard`, or otherwise affect server state. Native player-head stacks, when already received, continue through Minecraft's normal renderer; QCA does not initiate an additional texture request. The guide is informational only and does not automate crafting decisions or gameplay actions.

The Hunting HUD and trackers add no outgoing path. They never send a command/chat message, request a chunk, change a scoreboard objective, target an entity, throw a tool/capsule, move the player, or interact with a Floor Drop, campfire, Critter, wall, or Fairy Soul. Profile memory is ordinary local JSON keyed by the local account UUID and received Profile label; it contains only previously received Chapter/resource/Safari-Milestone/Benefactor values and is updated on a changed observation. Chapter parsing keeps Tab, scoreboard, already-open menu, and a short received-chat block bounded rather than scanning arbitrary cached text. Benefactor parsing likewise consumes only bounded received Tab/scoreboard/chat/menu text; its expiry is local arithmetic over a received duration and causes no server action. The Tree Critter Timer reads only an already-loaded entity display name and does not detect a click, consume a Pot, or synthesize a countdown. Beeheemoth detection uses the same scale-9 loaded-Bee signature as the supplied reference mod; the fixed marker is removed by local distance, received capture confirmation, or entity disappearance, and only spatially associated Bee-family sound volume is changed locally. The Lasso cue reads only a received leash relation and exact nearby display label, then plays a local sound. The Wumpa party checklist is an in-memory set updated by anchored personal capture confirmations and received teammate Loot Share capture text; the separate personal Critterdex still excludes Loot Share. The spawn message and 8/8 completion share one per-run alert flag. Route prediction follows only the loaded Ravager body and local collision data. The Snoozle overlay checks nearby already-loaded blocks once per second, rejects large or single-material components, and renders only local exposed faces. Warden readiness reads the client-visible Warden age/pose within the bounded arena and local connection latency; it does not alter the entity or send a capture action. Tree Gift parsing accepts the local player's exact summary hover and separate exact bonus rows only after the same bounded received block proves personal contribution and summary ownership; canceled-display messages are still client-received, and a public nearby-player row alone remains inert. A Fairy Soul beam is hidden only after a received success/already-found confirmation and a bounded nearest-coordinate match. Campfire discovery inspects Block Entities only in chunks already loaded by vanilla. Miria results render only in QCA's combined HUD; no sidebar mixin or contest timer duplication remains. `/th` and `/helia` are separate explicit shortcuts documented above.

## Hypixel policy note

The implementation was intentionally restricted to passive client data and rendering. That reduces anti-cheat and interaction risk, but it is not an approval from Hypixel. Hypixel's current guide says modifications are used at the player's own risk, warns against features that provide significant advantage, and does not guarantee permission for unlisted features. Users should review the current official guide and disable any feature they are not comfortable using:

Entity outlines, beacon waypoints, wall overlays, and motion projections are the highest-policy-risk parts because they can make world information easier to see. They remain passive renders, but passive does not automatically mean allowed. Wumpa projection and Fairy Soul beams therefore default off; the user-requested Critter rarity outline, Cold campfire beacon, and Snoozle wall overlay default on but have independent master switches.

- [Hypixel Allowed Modifications](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications)
- [Hypixel SkyBlock Rules](https://support.hypixel.net/hc/en-us/articles/4508088842898-Hypixel-SkyBlock-Rules)

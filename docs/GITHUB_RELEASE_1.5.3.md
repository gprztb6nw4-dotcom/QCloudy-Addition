# QCloudy_Addition Alpha 1.5.3

For Fabric on Minecraft 26.1.2. This release note covers the cumulative changes from Alpha 1.5.1 through Alpha 1.5.3.

> This is still an alpha build.

## Added

- Added a rebuilt BLC-inspired settings interface with seven ordered top-level categories: General, Maps, Items & Menus, Combat, Mining, Foraging, and Hunting.
- Added collapsible feature groups for HUD, connection, maps, waypoints, Torrhus, Galatea, Safari, Crimson Isle, pets, chat, and item/menu tools.
- Added a separate Galatea HUD and settings for Hina Chapter/resources and Agatha's Contest without mixing them with the Torrhus/Helia settings.
- Added an optional **Captured Shard Stats** section to the Safari Hunting HUD. It is disabled by default, groups captured Shards by biome, and colors Shard names by rarity.

## Improved

- Moved Fairy Soul waypoints into Maps > Waypoints as one unified feature instead of separate island entries.
- Merged Safari into Hunting, pets into Items & Menus, and Crimson Isle objectives into Combat so each feature appears in only one category.
- Empty HUD panels now disappear completely when no enabled option has renderable content. Their title, background, border, and HUD-editor handle are no longer left on screen.
- Split Safari run information into clearer sections: the Run Dashboard keeps run time and Ticket Tier, Critterdex keeps capture progress, and captured Shard statistics use their own optional section.
- Added biome colors and rarity colors to Safari Critterdex and captured-Shard rows.
- The Pet HUD is no longer displayed in Critter Safari, where pets cannot be equipped.
- Galatea/Hina chapter text and Agatha contest data can now be parsed from the same client-received sources used by the Torrhus tracker while retaining separate settings.
- Tree Gift rare-creature detection now understands messages such as `-A wild Groundhog appeared!` and keeps the personal Tree Gift ownership/deduplication checks.
- Beeheemoth bee-sound volume handling now works from relevant client-received bee sounds in Torrhus and Safari.
- Safari Critter highlighting now ignores Armor Stands used by capture props and colors named capturable Critters by their Shard rarity.
- AOTE/AOTV replacement sounds now use a 0–100% volume control with a 64% default; pitch adjustment was removed to keep playback predictable.
- Sweep and Safari biome labels now use clearer in-game-style colors.

## Fixed

- Fixed Sweep parsing for messages such as `Sweep: 952.84, 13.78 logs (-50%) (-50%) -> 5.46 logs`; only the value immediately following `Sweep:` is tracked.
- Fixed completed Crimson Isle tasks remaining in the HUD; completed entries received from the Tab widget are now filtered out.
- Fixed Hunting, Mining, Pet, map, and Crimson HUD shells remaining visible when every corresponding row was disabled or unavailable.
- Fixed Tree Gift wild-creature rewards not triggering a personal alert after a valid personal Tree Gift block.
- Fixed nearby players' Tree Gifts and unrelated Lasso capture messages causing duplicate or false personal alerts.
- Fixed Galatea chapter context not recognizing Hina/Galatea chapter text.
- Fixed the Safari capture Armor Stand being outlined together with the actual Critter.

## Removed

- Completely removed Slot/Item Locking, including its configuration, input hooks, rendering, and tests.
- Completely removed Storage Overlay, including its cache, controller, screen, configuration, and tests.
- Completely removed menu middle-click conversion, including the click mixin, configuration, and tests.
- Removed AOTE/AOTV pitch controls.

## Compatibility and safety

- Client-only; it does not require a server-side mod.
- Requires Minecraft 26.1.2, Java 25, Fabric Loader 0.19.3 or newer, and Fabric API 0.155.2+26.1.2 or newer.
- Mod Menu is optional and only provides a convenient settings entry.
- HUDs and trackers use information already received by the client, such as chat, Tab, scoreboard, visible entities, opened menus, and local item data.
- No automated movement, automated combat, automated fusion, inventory automation, or hidden server-data requests are included.
- The manual `/th` and `/helia` shortcuts still send `warp torrhus` and `chapter torrhus` only when the player explicitly enters those client commands.

## Artifact

Upload the binary JAR named:

`QCloudy_Addition-alpha-1.5.3-26.1.2.jar`

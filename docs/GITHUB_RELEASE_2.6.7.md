# QCloudy_Addition Beta 2.6.7 for Minecraft 26.1.2

Beta 2.6.7 replaces and recalibrates the Dwarven Mines map while retaining the complete Beta 2.6.6 feature set.

## Changed

- Replaced the Dwarven Mines texture with the newly supplied single-layer 12-region map.
- Recalibrated Village, Upper Mines, Rampart Quarry, Forge, Lava Springs, Cliffside, Far Reserve, Goblin Burrows, Royal Mines, The Mist, Ice Wall, and Royal Palace against the replacement image.
- Dwarven marker projection now uses only local X/Z, yaw, and the already-visible scoreboard sub-location. Y is deliberately ignored because this map is a single layer.
- When only the generic `Dwarven Mines` location is visible, the nearest normalized X/Z region center is used instead of a height check.

## Safety and compatibility

- Client-only Fabric mod for Minecraft 26.1.2; Java 25 is required.
- Required: Fabric API `0.155.2+26.1.2` or newer. Mod Menu is optional.
- The map is a bundled local PNG and render-only projection. It sends no packet, chat, command, click, movement, or other server interaction.
- Automated tests and archive checks do not replace authenticated Hypixel or every-GUI-scale visual testing.

## Files

- Playable: `QCloudy_Addition-Beta-2.6.7+26.1.2.jar`
- Sources: `QCloudy_Addition-Beta-2.6.7+26.1.2-sources.jar`

Install the playable JAR. The Sources JAR is only for source browsing and development.

# QCloudy_Addition Alpha 2.5.6 for Minecraft 26.1.2

This alpha completes the information layer of the offline Attribute Shard Fusion Guide. QCloudy_Addition remains a standalone, client-only Fabric mod and does not depend on JEI, Firmament, SkyHanni, Skyblocker, or BabyzombieAddons.

## Shard details and acquisition

- Added a **Details** tab for all 320 current catalog Shards.
- Shows each Shard's normalized Wiki effect, rarity, category, skill, family, mob type, and all documented acquisition methods.
- Capture sources keep the mob, required tool, and biome. Kill/drop/Fusion/tree-gift/shop/chest sources retain the details actually documented; missing probabilities are not invented.
- Fusion-only Shards are labelled. Any Shard with verified Fusion outputs also shows its recipe count, including Queen Bee and other Shards that have both natural and Fusion sources.

## Colour and navigation fixes

- Epic now uses Minecraft dark purple (`§5`) instead of light-purple/pink (`§d`).
- Stat, category, mob-type, skill, acquisition, and rarity text use reviewed SkyBlock/Minecraft semantic colours.
- Clickable Shard text darkens and underlines while its visible text is hovered.
- Preserved spacing across coloured effect fragments and removed residual Wiki formatting markers.

## Safety

All Shard data and icons are bundled offline and read-only. The guide does not contact the Wiki/API at runtime, click a menu, send a packet/chat/server command, select an output, or perform a Fusion.

This is an Alpha build. Live authenticated Hypixel and resource-pack/GUI-scale visual verification is still required before Beta or Release promotion.

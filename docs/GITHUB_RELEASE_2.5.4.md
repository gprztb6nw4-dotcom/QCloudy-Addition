# QCloudy_Addition Alpha 2.5.4 for Minecraft 26.1.2

This alpha adds a complete, client-only Attribute Shard Fusion reference inspired by JEI's information layout. It remains a standalone Fabric mod and does not depend on JEI, Firmament, SkyHanni, Skyblocker, or BabyzombieAddons.

`2.5.4` starts the post-`1.5.3` Alpha development line. The channel remains Alpha; this is a version-line renumbering, not a promotion to Beta or Release.

## New: Attribute Shard Fusion Guide

- Browse exactly 320 current Bazaar-listed Attribute Shards from a bundled offline catalog.
- Search original English Shard names, IDs, attributes, rarities, categories, families, and skills.
- **Recipes** shows every ordered input pair that can produce the selected Shard.
- **Uses** shows every ordered fusion that consumes the selected Shard.
- Recipe cards preserve left/right order, required input count, up to three selectable output slots, ID/Chameleon ×1 yield, Special ×2 yield, and the Pure Reptile double-output chance.
- Separate ID and Special output slots are preserved even when they contain the same Shard but have different yields.
- Use left click for Recipes, right click for Uses, Swap Inputs, Back/Forward history, pagination, or the local `/qshard [English query]` command.
- Native Shard ItemStacks already observed by the client follow the active resource pack; unseen Shards use a safe local fallback icon.

## Data correction

The community Wiki tables currently expose 317 rows, while the reviewed official Bazaar snapshot contains 320 `SHARD_*` products. This release adds Anteater (`R70`), Zombuddy (`R84`), Troodon (`R86`), and Ghost Crab (`L38`), corrects Goldolot to `R92`, and excludes Rainbug because no `SHARD_RAINBUG` Bazaar product exists.

## Safety and requirements

- Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25.
- The guide is read-only and offline at runtime. It sends no chat, server command, packet, click, movement, API request, or automatic fusion action.
- `/qshard` is a local client command and sends no server payload.
- This remains an Alpha build and is not an official Hypixel approval.

See `CHANGELOG.md`, `docs/COMPLIANCE.md`, and `docs/VALIDATION.md` for the full boundary and verification record.

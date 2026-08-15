# QCloudy_Addition Alpha 2.8.19 for Minecraft 26.1.2 and 26.2

Alpha 2.8.19 is a maintenance, integrity, compatibility, and performance update. It preserves the complete Alpha 2.8.18 feature set while removing confirmed redundant state and reducing repeated work in frequently rendered or ticked paths.

## Improved

- Compatibility Gaps now groups providers once per opened report and caches wrapped row geometry until the content width changes instead of rebuilding the same layout every frame.
- Lasso `REEL` detection now traverses the loaded entity view once per active check and avoids ArmorStand-name parsing when the local player has no lasso target.
- Shard Fusion indexing no longer keeps a duplicate, unread all-recipes reference list. Exact pair, output, and uses lookups still share the same immutable recipe objects and results.
- The Gradle resource pipeline captures Fabric Loader metadata during configuration, removing the project's Gradle 10 execution-time API deprecation warning.
- Playable and Sources archives explicitly exclude Finder `.DS_Store` metadata.

## Compatibility and safety

- No feature default, Shard recipe result, provider save path, HUD meaning, command payload, or network behavior changed.
- QCA remains a standalone, client-only Fabric mod. This update adds no packets, automatic clicks, commands, chat, HTTP requests, telemetry, movement, or gameplay automation.
- Separate playable and Sources artifacts are provided for Minecraft 26.1.2 and 26.2. Use the file matching the exact Minecraft version.

Do not install a `-sources.jar` as the playable mod. The exact test results and SHA-256 values are recorded in `docs/VALIDATION.md`.

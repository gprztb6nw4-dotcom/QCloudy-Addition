# Alpha 2.8.19 — maintenance and performance

- Cached Compatibility Gaps provider grouping and wrapped rows instead of rebuilding them every frame.
- Reduced active Lasso `REEL` detection to one loaded-entity traversal.
- Removed an unused duplicate Shard all-recipes reference list without changing recipe results.
- Removed the project's Gradle 10 execution-time API deprecation warning.
- Explicitly excludes Finder `.DS_Store` metadata from playable and Sources archives.
- No feature defaults, provider writes, commands, network behavior, or client-only safety boundaries changed.
- Built separately for Minecraft 26.1.2 and 26.2.

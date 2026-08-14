# Beta 2.7.17 — Planner, unified controls, fishing, dual-version support, and fixes

This Beta consolidates all completed changes since Beta 2.6.6.

## Added

- Full local Shard Planner with multi-step Fusion trees, alternative routes, Materials Only totals, editable acquisition rates, Fastest/Cheapest planning, Ironman mode, Kraken/Kuudra inputs, Fusion Lines, and a Hunting Box warehouse recorded only from pages you open.
- Optional cached Bazaar prices through a compatible Skyblocker public API; no provider means price routes are unavailable while all offline/rate routes keep working.
- Function-first unified settings/HUD controls for exact reviewed builds of SkyHanni, Skyblocker, Firmament, and BabyZombieAddons. Integrations are optional and fail closed.
- Default-off Ciallo Fishing Bite Sound with an independent 0–100% volume slider (64% default), water/lava-hook support, and once-per-bite gating.
- Separate builds for Minecraft 26.1.2 and 26.2.

## Improved and fixed

- Responsive Shard/Planner/Settings/Fusion Lines/RGB/HUD-editor layouts with scrolling, clipping, text wrapping, and visible-only hitboxes.
- Fixed Tree Gift creature alerts, unmaxed Ancient Golden Dragon levels, lava-fishing detection, and reel-time sound replay.
- Replaced the Dwarven Mines background and fixed bridge/The Mist desynchronization with one continuous approximate X/Z-only projection. Y and scoreboard sub-locations cannot move the arrow.

## Compatibility

- Install the playable JAR matching the exact Minecraft version; do not install a Sources JAR as the mod.
- Requires Java 25, Fabric Loader 0.19.3+, and the matching Fabric API.
- Third-party settings/HUD adapters are reviewed for 26.1.2 only and safely stay unavailable in 26.2.
- Client-only and standalone. No automatic click, Fusion, fishing action, movement, packet, chat, command, HTTP request, or gameplay automation was added.

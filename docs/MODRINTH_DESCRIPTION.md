# QCloudy_Addition

QCloudy_Addition is a client-only Fabric mod for Hypixel SkyBlock on Minecraft 26.1.2. It combines readable mining maps, objective HUDs, Torrhus and Critter Safari helpers, pet information, inventory protection, and configurable visual alerts in one bilingual interface.

English is the default interface language. Simplified Chinese can be selected from the QCA settings. Names received from Hypixel—including locations, items, pets, tasks, skins, accessories, and player-renamed HOTM slots—stay in their original form to avoid misleading translations.

## Highlights

- Simple single-layer Dwarven Mines map with an accurate live player arrow
- Three elevation-aware Glacite Tunnels maps
- Mining commission, three-powder, and selected HOTM slot tracker
- Crimson Isle faction quest tracker
- Combined Torrhus Chapter, resource, Miria Contest, Benefactor, and Tree Critter HUD
- Personal rare Tree Gift alerts with per-loot controls and nearby-player rejection
- Beeheemoth outline, spawn beacon, and independent spatial sound volume
- Critter Safari dashboard, Critterdex, Cold/campfire safety, Wumpa, Warden, Sparkling, Floor Drop, quest-item, Snoozle-wall, and Safari Belt helpers
- Equipped Pet HUD with real pet/skin heads, rarity-colored names, XP, level-200 overflow, skin name, and held-item icon/name
- Ender Dragon outline with a configurable color
- Slot/item locking, item timestamps, cursor memory, optional Storage Overlay, configurable AOTE/AOTV sounds, and optional menu middle-click conversion
- Chat Peek with keyboard or mouse-button chords
- BLC-inspired searchable settings and a dedicated drag/resize HUD editor
- Manual reconnect button with no automatic retry loop

## HUD and accessibility

Every HUD has its own saved position and 50–200% scale. Background color and transparency, border color/width, title color, bold text, and text shadow can be edited independently. The RGB/HSV picker includes common presets and a transparent background option. Only HUDs currently loaded by the player's location or state appear in the HUD editor.

Alerts appear as center-screen titles. Each alert owns its own sound switch and 0–100% volume slider; alert volumes default to 64%. A General master mute is also available.

## Installation

Requirements:

- Minecraft 26.1.2
- Fabric Loader 0.19.3 or newer
- Fabric API 0.155.2+26.1.2 or newer
- Java 25
- Mod Menu is optional

Place the QCloudy_Addition JAR in the instance's `mods` folder. Press `O` by default, use Mod Menu, or enter `/aca`, `/qca`, `/ca`, or `/qc` to open settings. These four aliases are local client commands and are registered only when no other client mod owns the name.

## Client-only and safety boundary

QCA has no runtime web service, telemetry, remote updater, Hypixel API dependency, macro, automatic movement, automatic combat, automatic capture, or hidden server-data request. Tracking features use only information already received by the client: Tab, scoreboard, chat, titles, open-menu contents, local inventory, loaded entities, and already-loaded blocks.

The only server command payloads in the mod are physically user-triggered Storage navigation (`storage`, `enderchest <1-9>`, `backpack <1-18>`), `/th` (`warp torrhus`), and `/helia` (`chapter torrhus`). QCA contains no `sendChat` call. Manual reconnect starts one normal Minecraft connection only after the player clicks the button; it has no timer or background retry.

All Minecraft modifications are used at the player's own risk. Entity outlines, waypoint beams, wall overlays, and route projections are passive client renders, but passive rendering is not the same as official Hypixel approval. Review Hypixel's current modification rules and disable any feature you are not comfortable using.

## Compatibility

QCA is standalone and does not require Firmament, SkyHanni, Skyblocker, or BabyzombieAddons. It has been initialized in a 94-mod local instance containing all four reference mods and Mod Menu. Optional inventory-feature handoff can avoid duplicating Firmament behavior when Firmament is installed.

QCloudy_Addition is not affiliated with or endorsed by Hypixel Studios or Mojang Studios.

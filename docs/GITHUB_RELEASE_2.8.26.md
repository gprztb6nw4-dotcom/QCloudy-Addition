# QCloudy_Addition Alpha 2.8.26 for Minecraft 26.1.2

Alpha 2.8.26 replaces the incomplete Flare-chat assumption with a confirmed, client-only lifecycle and makes every Deployable alert control explicit.

## Fixed

- **Power Orb & SOS Despawn Alert** now treats Power Orbs and Flares through separate verified inputs.
- Radiant, Mana Flux, Overflux, and Plasmaflux Power Orbs require the exact player-owned `Your <Power Orb> despawned.` chat line.
- Warning, Alert, and SOS Flares start only after the local player uses the exact Flare item and the client receives the matching successful placement sound.
- Failed or cooldown-blocked uses do not start a timer. A newly confirmed Flare silently replaces the old record.
- World/server changes and disconnects clear pending state silently. Entity unload, render distance, player distance, and buff range do not trigger or suppress an expiry.
- One confirmed three-minute Flare lifecycle end produces at most one `<Flare Name> Despawned!!!` alert.

## Settings

- Separate Power Orb and Flare alert toggles.
- Separate center-screen text and local alert-sound toggles.
- Continuous 0–100% volume slider; sound defaults to enabled at 64%.

## Safety boundary

The feature reads only already-received chat, the local player's exact item use, the received successful placement sound, and local monotonic time. It sends no chat, command, packet, interaction, or network request and does not use distance or entity-unload inference.

## Installation

- Install `QCloudy_Addition-Alpha-2.8.26+26.1.2.jar` as the playable mod.
- Requires Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2, and Java 25.
- Do not install the `-sources.jar` as the playable mod.
- This is an Alpha build and should be marked as a pre-release.

Automated checks cannot replace a natural authenticated Hypixel expiry test. Before wider publication, verify one Power Orb and each Flare tier through its full lifetime.

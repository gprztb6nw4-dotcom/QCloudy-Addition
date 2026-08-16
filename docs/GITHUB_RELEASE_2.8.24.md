# QCloudy_Addition Alpha 2.8.24 for Minecraft 26.1.2

Alpha 2.8.24 adds a client-only expiry reminder for the local player's Power Orbs and Flares.

## Added

- Added **Deployable Expiry Alert** under **Combat → Deployables**.
- Exact received despawn messages are supported for Radiant, Mana Flux, Overflux, and Plasmaflux Power Orbs, plus Warning, Alert, and SOS Flares.
- A matching message displays `<Deployable Name> Despawned!!!` as a large red center-screen title.
- The alert has an independent local sound toggle and a continuous 0–100% volume slider. Sound defaults to enabled at 64%.

## Safety boundary

- Detection requires the exact player-owned chat form `Your <approved deployable> despawned.`.
- The feature does not send chat, commands, packets, interactions, or network requests.
- QCloudy_Addition remains a client-only Fabric mod.

## Files

- Install `QCloudy_Addition-Alpha-2.8.24+26.1.2.jar` as the playable mod.
- The `-sources.jar` is for source inspection and development; it is not a playable mod.

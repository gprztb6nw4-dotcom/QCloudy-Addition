# QCloudy_Addition Alpha 2.8.24 for Minecraft 26.1.2

Alpha 2.8.24 introduced the first client-only Deployable expiry reminder. Its original Flare-chat assumption was incomplete and is fully superseded by the confirmed local Flare lifecycle in Alpha 2.8.26.

## Added

- Added the first **Deployable Expiry Alert** under **Combat → Deployables**.
- Added exact received despawn-chat support for Radiant, Mana Flux, Overflux, and Plasmaflux Power Orbs.
- The original Flare-chat branch in this historical build was not a reliable Flare implementation; current builds no longer contain it.
- A confirmed expiry displays `<Deployable Name> Despawned!!!` as a large red center-screen title.
- The alert has an independent local sound toggle and a continuous 0–100% volume slider. Sound defaults to enabled at 64%.

## Safety boundary

- Power Orb detection requires the exact player-owned chat form `Your <Power Orb> despawned.`.
- The feature does not send chat, commands, packets, interactions, or network requests.
- QCloudy_Addition remains a client-only Fabric mod.

## Files

- Install `QCloudy_Addition-Alpha-2.8.24+26.1.2.jar` as the playable mod.
- The `-sources.jar` is for source inspection and development; it is not a playable mod.

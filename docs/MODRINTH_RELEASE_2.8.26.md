## Alpha 2.8.26

- Replaced the incomplete Flare-chat assumption with a confirmed local three-minute lifecycle for Warning, Alert, and SOS Flares.
- A Flare timer starts only after exact item use plus the matching successful placement sound; failed/cooldown-blocked uses cannot create false alerts.
- Kept exact player-owned despawn-chat matching for the four Power Orbs.
- Added separate Power Orb, Flare, center-text, sound, and 0–100% volume controls. Sound defaults to 64%.
- Removed distance, buff-range, and entity-unload inference. World/server changes clear state silently, and one lifecycle end alerts at most once.

Client-only Alpha for Minecraft 26.1.2. No outgoing chat, command, packet, interaction, or network request.

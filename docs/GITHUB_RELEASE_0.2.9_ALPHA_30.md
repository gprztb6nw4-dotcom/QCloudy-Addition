# QCloudy_Addition 0.2.9 Alpha 30 for Minecraft 26.1.2

Version 0.2.9 Alpha 30 fixes early SOS/Flare despawn alerts after a replacement is placed and adopts the new channel-separated artifact naming convention.

## Fixed

- Replacing an active Warning, Alert, or SOS Flare now restarts the complete three-minute lifecycle from the newly observed replacement use, even when the replacement does not repeat the initial confirmation signal.
- The previous Flare expiry is invalidated, so it cannot warn while the replacement is still active.
- Placement tracking now observes use-on-block interactions as well as normal item use.
- If the second use callback is missed, the exact successful-placement sound can recover the placement only when the local player still holds a recognised Flare.
- Added regression tests for same-SOS replacement, missed-use recovery, and unrelated held items.

## Compatibility and safety

- QCloudy_Addition remains a standalone, client-only Fabric mod.
- The alert does not use distance, effect range, or entity unloading, and it does not send a command, packet, or automated interaction.
- Power Orb chat matching and all existing alert settings remain unchanged.

## Downloads

Playable mods:

- `QCloudy_Addition-0.2.9+26.1.2-Alpha-30.jar`

Developer sources:

- `QCloudy_Addition-0.2.9+26.1.2-Alpha-30-sources.jar`

Install only the playable JAR. Do not install the `-sources.jar` as the mod.

Minecraft 26.1.2 passed the complete automated test suite with no failures, plus metadata and archive validation. A live Hypixel replacement test remains necessary: replace SOS before the old three-minute deadline, confirm the old deadline stays silent, and confirm the new deadline alerts once.

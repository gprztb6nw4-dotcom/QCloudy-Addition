# QCloudy_Addition Alpha 2.8.27 for Minecraft 26.1.2

Alpha 2.8.27 fixes newly eaten Century Cakes remaining inactive in QCA's effects screen.

## Fixed

- Recognises Hypixel's real first-activation message: `Yum! You gain <bonus> for 48 hours!`.
- Preserves the real refresh message: `Big Yum! You refresh <bonus> for 48 hours!`.
- Correctly normalizes the private-use stat glyph embedded in Starborn Century Cake's `+1 Hunter Fortune` message.
- Rejects invented or ambiguous message combinations instead of starting a false timer.

Existing Century Cake expiry behavior, Power Orb/Flare alerts, settings, and outbound-action boundaries are unchanged.

## Validation

- Minecraft 26.1.2 only; Java 25; client-only Fabric mod.
- 191 tests across 37 suites passed with no failures, errors, or skips.
- Both binary and Sources JARs passed archive validation and match their release copies byte-for-byte.

Install `QCloudy_Addition-Alpha-2.8.27+26.1.2.jar`. Do not install the `-sources.jar` as the playable mod.

An activation missed by an older QCA build cannot be reconstructed from past chat. Eat or refresh that cake again while 2.8.27 is running.

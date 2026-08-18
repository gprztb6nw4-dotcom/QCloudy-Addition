# QCloudy_Addition Alpha 2.8.28 for Minecraft 26.1.2

Alpha 2.8.28 fixes Starborn Century Cake refreshes still appearing inactive in QCA.

## Fixed

- Corrected the Starborn Century Cake metadata from `Hunter Fortune` to Hypixel's actual `Hunting Fortune` name.
- Recognises the exact client line `Big Yum! You refresh +1<stat icon> Hunting Fortune for 48 hours!`.
- Keeps support for the exact first-activation form `Yum! You gain ... for 48 hours!`.
- Normalizes the private-use stat icon before matching while rejecting the old incorrect `Hunter Fortune` spelling.
- Displays `+1 Hunting Fortune` in the Century Cake effects screen and tooltip.

Existing Century Cake expiry alerts, Power Orb/SOS alerts, settings, and outbound-action boundaries are unchanged.

## Installation

Install `QCloudy_Addition-Alpha-2.8.28+26.1.2.jar`. Do not install the `-sources.jar` as the playable mod.

An activation missed by an older build cannot be reconstructed from past chat. Refresh or eat Starborn Century Cake again while 2.8.28 is running.

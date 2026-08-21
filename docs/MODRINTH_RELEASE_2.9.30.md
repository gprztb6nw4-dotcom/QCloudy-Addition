Alpha 2.9.30 fixes SOS/Flare replacement timing.

- Replacing an active Warning, Alert, or SOS Flare now restarts a complete three-minute lifecycle immediately, including replacements that do not repeat the first-placement confirmation signal.
- The old expiry is invalidated and cannot alert while the replacement remains active.
- Added use-on-block tracking and safe recovery when a use callback is missed but the exact placement sound and held Flare still match.
- Added regression tests for repeated SOS placement and false-reset prevention.
- Available for Minecraft 26.1.2.

QCA remains standalone and client-only. Install only the playable JAR; the `-sources.jar` is for developers.

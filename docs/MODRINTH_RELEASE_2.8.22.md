# Alpha 2.8.22 — confirm every provider scan

- Added a second confirmation before the first Unified Settings/HUD scan and before every Refresh.
- Cancelling initial confirmation leaves the master switch off; cancelling Refresh preserves the last validated snapshot.
- Restored enabled switches no longer scan silently after restart.
- Refresh is unavailable while another scan is running.
- The scan remains local, deterministic, read-only, and free of server/network requests or automatic input.
- Separate Fabric builds are provided for Minecraft 26.1.2 and 26.2.

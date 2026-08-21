# Publication checklist

## Shared project metadata

| Field | Value |
|---|---|
| Name | QCloudy_Addition |
| Suggested slug | `qcloudy-addition` |
| Version | `Alpha-2.9.30+26.1.2` |
| Release channel | Alpha; SOS/Flare replacement-timer hotfix |
| Environment | Client only |
| Loader | Fabric |
| Minecraft | 26.1.2 |
| Java | 25 |
| License | LGPL-3.0-or-later |
| Required dependency | Fabric API 0.155.2+26.1.2 |
| Optional dependency | Mod Menu 18.0.0 |
| Standalone from | Firmament, SkyHanni, Skyblocker, BabyZombieAddons, Feesh; optional capability-detected integrations only |

Suggested Modrinth summary:

> Client-only Hypixel SkyBlock maps, trackers, pet HUD, offline Shard Fusion recipes, and configurable visual alerts for Fabric 26.1.2.

Suggested Modrinth categories: Utility, Optimization, Game Mechanics.

Suggested GitHub topics: `minecraft`, `fabric`, `hypixel-skyblock`, `skyblock`, `client-side`, `hud`, `minecraft-mod`, `java`.

## Modrinth

- Use `docs/MODRINTH_DESCRIPTION.md` as the English project description.
- Keep English as the primary description; place `docs/MODRINTH_DESCRIPTION_zh_CN.md` in a linked Chinese page or below the English copy if desired.
- Upload `release/QCloudy_Addition-Alpha-2.9.30+26.1.2.jar` for Minecraft 26.1.2.
- Use `docs/MODRINTH_RELEASE_2.9.30.md` as the concise version changelog; the Chinese companion is `docs/MODRINTH_RELEASE_2.9.30_zh_CN.md`.
- Mark Fabric API as required and Mod Menu as optional.
- Mark client environment as required and server environment as unsupported.
- Do not mark Firmament, SkyHanni, Skyblocker, BabyzombieAddons, or Feesh as required.
- Add at least: one settings overview, one HUD editor, Dwarven map, Glacite map, Mining HUD, combined Torrhus HUD, Safari HUD, and Pet HUD screenshot.
- Avoid screenshots that expose player UUIDs, private chat, server IPs, session data, or other players' private information.

## GitHub repository

- Keep `README.md` as the default English landing page and `README_zh_CN.md` as the Chinese version.
- Keep `LICENSE`, `THIRD_PARTY_NOTICES.md`, `CHANGELOG.md`, and the complete `docs/` directory in the repository.
- Add the real repository URL, issue tracker URL, and Modrinth project URL after they exist; do not publish placeholder links.
- Replace the generic `QCloudy_Addition contributors` metadata author with the final public author/team name, and add public contact/support links only after the user chooses them.
- Enable Issues and provide a bug template requesting Minecraft/Fabric/QCA versions, mod list, logs, reproduction steps, and screenshots.
- Do not commit `run/`, `run-standalone/`, `.gradle/`, `.gradle-user-home/`, local configs, logs, crash ZIPs, or the supplied reference JARs.
- Confirm `.gitignore` covers local build/runtime files before the first commit.

## GitHub Alpha 2.9.30

- Title: `QCloudy_Addition Alpha 2.9.30 for Minecraft 26.1.2`
- Use `docs/GITHUB_RELEASE_2.9.30.md` as the English release body and optionally append/link `docs/GITHUB_RELEASE_2.9.30_zh_CN.md`.
- Attach the playable JAR and, optionally, its Sources JAR. Keep the Minecraft version in every filename.
- Verify uploaded hashes against `docs/VALIDATION.md` after downloading the release once.
- Mark it as **Pre-release** on GitHub because this is an Alpha, not a stable Release.
- On Modrinth, choose **Alpha** as the version type and create the Minecraft 26.1.2 file entry.

## Final safety and quality gate

- Re-run `clean test build prepareRelease` with Java 25 after any code, resource, metadata, or version change.
- Re-run `jar --validate`, `unzip -t`, metadata inspection, class-major inspection, and release/build hash comparison.
- Derive the final passing-test count from the fresh XML results; do not reuse the 2.5.4 count.
- Confirm exactly 320 bundled Shard textures, item-model definitions, and item definitions are present; confirm the catalog-to-icon ID sets match and Rainbug is absent.
- Confirm search focus exits through outside click, `Esc`, and `Tab`, can be restored by clicking search, and does not block recipe navigation shortcuts.
- Confirm compact input/output bounds and their click targets remain aligned at the supported GUI scales.
- Confirm Epic uses `§5`, Details wraps every effect/acquisition line, hover styling applies only to visible clickable text, and natural-plus-Fusion Shards expose both source types.
- Test at least one standalone launch and one launch with all five optional providers, including Feesh.
- In the five-provider launch, verify that both integration masters default off, cancelling their first confirmation keeps them off, confirmation starts exactly one scoped scan, every Refresh confirms again, restart never silently scans, and both editors independently gate provider settings/HUD discovery while leaving QCA-owned controls available. Then verify provider selection, exact-equivalent exclusivity, native persistence, version-mismatch capability discovery, per-branch omission, every recognised external HUD position/scale/alignment at multiple GUI scales, and the read-only Compatibility Gaps report.
- Recheck every command/chat payload in `docs/COMPLIANCE.md`.
- Confirm the uploaded icon has transparent corners and remains recognizable at 32×32.
- Confirm the README never claims official Hypixel approval, guaranteed safety, or complete authenticated-server validation.
- Confirm the public author name, source URL, issue URL, and Modrinth URL are final; these are the only publication fields intentionally not invented by this repository.

# Publication checklist

## Shared project metadata

| Field | Value |
|---|---|
| Name | QCloudy_Addition |
| Suggested slug | `qcloudy-addition` |
| Version | `1.5.1+26.1.2` |
| Release channel | Beta is recommended until authenticated Hypixel regressions are complete |
| Environment | Client only |
| Loader | Fabric |
| Minecraft | 26.1.2 |
| Java | 25 |
| License | LGPL-3.0-or-later |
| Required dependency | Fabric API 0.155.2+26.1.2 or newer |
| Optional dependency | Mod Menu 18.0.0 |
| Standalone from | Firmament, SkyHanni, Skyblocker, BabyzombieAddons |

Suggested Modrinth summary:

> Client-only Hypixel SkyBlock maps, mining/Torrhus/Safari trackers, pet HUD, inventory protection, and configurable visual alerts for Fabric 26.1.2.

Suggested Modrinth categories: Adventure, Utility, Transportation.

Suggested GitHub topics: `minecraft`, `fabric`, `hypixel-skyblock`, `skyblock`, `client-side`, `hud`, `minecraft-mod`, `java`.

## Modrinth

- Use `docs/MODRINTH_DESCRIPTION.md` as the English project description.
- Keep English as the primary description; place `docs/MODRINTH_DESCRIPTION_zh_CN.md` in a linked Chinese page or below the English copy if desired.
- Upload only `release/QCloudy_Addition-1.5.1+26.1.2.jar` as the playable file.
- Mark Fabric API as required and Mod Menu as optional.
- Mark client environment as required and server environment as unsupported.
- Do not mark Firmament, SkyHanni, Skyblocker, or BabyzombieAddons as required.
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

## GitHub Release 1.5.1

- Title: `QCloudy_Addition 1.5.1 for Minecraft 26.1.2`
- Tag: `v1.5.1+26.1.2`
- Use `docs/GITHUB_RELEASE_1.5.1.md` as the release body.
- Attach the binary JAR and optionally the Sources JAR.
- Verify uploaded hashes against `docs/VALIDATION.md` after downloading the release once.
- Mark as pre-release/beta until live Hypixel testing covers the remaining validation boundary.

## Final safety and quality gate

- Re-run `clean test build prepareRelease` with Java 25 after any code, resource, metadata, or version change.
- Re-run `jar --validate`, `unzip -t`, metadata inspection, class-major inspection, and release/build hash comparison.
- Confirm all 98 current tests still pass, or update the published test count only from actual XML results.
- Test at least one standalone launch and one launch with the four reference mods.
- Recheck every command/chat payload in `docs/COMPLIANCE.md`.
- Confirm the uploaded icon has transparent corners and remains recognizable at 32×32.
- Confirm the README never claims official Hypixel approval, guaranteed safety, or complete authenticated-server validation.
- Confirm the public author name, source URL, issue URL, and Modrinth URL are final; these are the only publication fields intentionally not invented by this repository.

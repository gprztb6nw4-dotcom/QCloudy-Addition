# QCloudy_Addition Alpha 2.5.5 for Minecraft 26.1.2

This alpha is a focused Shard Fusion Guide visual and interaction update. It remains a standalone, client-only Fabric mod and does not depend on JEI, Firmament, SkyHanni, Skyblocker, or BabyzombieAddons.

## Fixed

- Every one of the 320 catalogued Shards now has its own bundled icon. Unseen entries no longer appear as the same amethyst item.
- A native Shard `ItemStack` already received in an open menu or inventory still takes priority and is retained in a session-wide cache, preserving its active resource-pack/server presentation across guide pages.
- Clicking outside search, pressing `Esc`, or pressing `Tab` now releases text focus. Clicking search directly focuses it again, so recipe navigation and screen shortcuts are no longer trapped by the input field.
- Each recipe's two inputs are measured and centered as one compact expression. Candidate outputs use the same content-width approach, and click targets match their visible icon/text bounds.

## Offline icon provenance

The icon generator uses `public/shardIcons/<Shard ID>.png` from [SkyShards](https://github.com/Campionnn/SkyShards) at reviewed MIT-licensed commit `9688031dbc4e726168ffceb0f44884ff26e6e728`. Its 321-source set is filtered through QCA's exact 320-Shard catalog, so the extra Rainbug asset is not included. The transformed local icons, Minecraft item models, and mappings are packaged in the JAR.

## Safety and requirements

- Minecraft 26.1.2, Fabric Loader 0.19.3+, Fabric API 0.155.2+26.1.2+, and Java 25.
- The guide is read-only. QCA performs no runtime Wiki, Bazaar API, SkyShards, or icon-service request.
- `/qshard` is a local client command and sends no chat, server command, packet, click, movement, or fusion action.
- This remains an Alpha build and is not an official Hypixel approval.

See `CHANGELOG.md`, `THIRD_PARTY_NOTICES.md`, `docs/COMPLIANCE.md`, and `docs/VALIDATION.md` for the full provenance, safety boundary, and current verification status.

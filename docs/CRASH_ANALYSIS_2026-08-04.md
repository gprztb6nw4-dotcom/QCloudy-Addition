# Storage cache crash analysis — 2026-08-04

## Evidence

The two supplied ZIP files have the same SHA-256 value, `8abff84c45b6b2ecb8ffada8de514a446755c70fc2d1ff6f853d47a24811a5d7`, so they are duplicate exports of one incident. The originals were inspected read-only. The export includes logs and the crash report, but not `qcloudy_addition-storage.nbt`, so the exact offending stack cannot be reconstructed offline.

The crash occurred on Windows 11 with Minecraft 26.1.2, Java 25.0.1, and QCloudy_Addition 1.2.5. The decisive exception is:

```text
java.lang.IllegalStateException: Element Reference{ResourceKey[minecraft:enchantment / minecraft:efficiency]=Enchantment Efficiency} is not valid in current registry set
```

The stack trace enters `StorageCacheManager.encode`, then `saveAsync`, `tick`, and the client tick callback on the render thread. The client had been running for about 30 minutes. This is not an out-of-memory, GPU, resource-pack, or mixin-application failure.

## Root cause

A locally cached `ItemStack` retained an Efficiency enchantment Holder belonging to an older dynamic registry set. By the time ACA saved the cache, the client had a different active registry serialization context. ACA 1.2.5 tried to serialize the old Holder through that current context; `CompoundTag.store` rejected the cross-registry reference, and the unguarded exception escaped from the render thread. The export proves the registry mismatch, but does not identify which earlier world/server lifecycle event first made that one cached Holder stale.

## Repair in 1.2.8

- Detect a changed world registry set and rebind cached normal and stored enchantments by resource key.
- Rebind each item again immediately before serialization.
- Isolate item decoding, search text generation, hashing, encoding, and persistence failures.
- Preserve the page and slot order. An item that still cannot be encoded becomes an empty cached slot; other items and pages continue saving.
- Add an outer encoding barrier so no Storage-cache serialization exception can terminate the render thread.
- Keep the last in-memory cache available if a whole snapshot cannot be encoded.
- Add regression tests for valid and invalid per-slot encoding.

The user does not need to delete the old cache before upgrading. If an unsupported stale item is encountered, that one cached slot may appear empty until the corresponding Storage page is visited again and refreshed from normal client-received inventory data.

## Validation boundary

The repair passes local Java 25 unit tests and build validation. A local macOS Loom instance cannot exactly reproduce the supplied authenticated Windows/Hypixel registry transition, so the same page-switch/reconnect path should still receive an in-game regression check. The fix adds no packet, command, chat, HTTP, automation, or server-data behavior.

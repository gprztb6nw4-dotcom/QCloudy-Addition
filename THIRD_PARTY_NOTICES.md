# Third-party notices and reference provenance

QCloudy_Addition's Java implementation and visual design are original. The following open-source projects were inspected to understand Minecraft 26.1.2 APIs and resilient client-side patterns. They are not bundled as dependencies.

| Project | Inspected version / commit | License | Use in QCA |
|---|---|---|---|
| Firmament | `44.3.0+mc26.1` / `d7fb24768ab2e211cc9237ce0197ff8cfeae5a37` | GPL-3.0-or-later (file-specific exceptions possible) | Architecture and compatibility reference only; no source or resource copied |
| Skyblocker | `v6.8.2+26.1.2` / `0f53a9cb7cafc4fd94f20de5e84e84168dde90a9` | LGPL-3.0-or-later | Reference for current HUD extraction, player-map transform, and vanilla outline integration; QCA code is independently written |
| SkyHanni | `7.41.0` / `3d047fc5a4683f51d73215ee31e8392f8a2f4c5c` | LGPL-2.1 | Reference for current Tab/chat formats |
| BabyzombieAddons | `v3.4.1` / `821fc044e336c551c8e2d27b627c8782793a68a1` | LGPL-3.0 | Reference for current client event/accessor patterns and bounded loaded-entity scanning; QCA code is independently written |
| NotEnoughUpdates item repository | local 2026-08-04 snapshot | MIT, Copyright (c) 2020 Moulberry | Pet identifiers, rarity XP offsets, special level-200 pet metadata, and fallback skin texture references |
| Mod Menu API | 18.0.0 | MIT | Optional compile-only configuration entrypoint; Mod Menu is not bundled |

## Hypixel SkyBlock pet resources

Compact offline metadata for 88 base-pet profiles, 352 applied-pet-skin profiles, 5,422 pet-owned current/animated texture mappings, and 87 pet accessories is generated from the local NotEnoughUpdates item-repository snapshot. QCA constructs a normal Minecraft player head from the verified profile at runtime. It does not ship or select pre-rendered pet PNG fallbacks, does not add synthetic SkyBlock `petInfo`, and does not reuse an unrelated nearby item stack. These resources are used only to interpret and present client-received pet data. No Wiki, item-repository, Hypixel API, or texture request occurs while the mod is running.

The [Hypixel SkyBlock Wiki pet leveling table](https://hypixelskyblock.minecraft.wiki/w/Pets) was used to verify the rarity-adjusted level 1–100 XP curve. Local item-repository constants were used to verify Golden Dragon, Jade Dragon, and Rose Dragon level-200 extensions. Minecraft and Hypixel visual assets remain the property of their respective owners and are used subject to the applicable site terms, Minecraft EULA, and usage guidelines.

## SkyHanni-REPO map graph

The bundled Dwarven Mines and Glacite Tunnels PNGs are generated schematic derivatives of `constants/island_graphs/DWARVEN_MINES.json` and `GLACITE_TUNNELS.json` from the `SkyHanni-REPO` snapshot embedded in SkyHanni 7.41.0. The source repository is licensed under the MIT License:

> MIT License
>
> Copyright (c) 2022 hannibal2
>
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

`tools/generate_maps.py` documents and reproduces the transformation when the inspected SkyHanni JAR is present locally. QCA does not bundle any of the four reference JARs or their Java classes.

## Minecraft bitmap fonts

The generated Dwarven Mines PNGs use glyph bitmaps read from the locally installed Minecraft 26.1.2 `ascii.png` and `unifont.zip` assets so map labels match Minecraft's default visual language. The original font files are not separately bundled by QCA; only the small rendered labels inside the finished map textures are included. Minecraft assets remain property of Mojang Studios and are used subject to the Minecraft EULA and Usage Guidelines.

## Torrhus Canyon and Critter Safari reference data

The official Hypixel SkyBlock Wiki pages for [Torrhus Canyon](https://hypixelskyblock.minecraft.wiki/w/Torrhus_Canyon), [Critter Safari](https://hypixelskyblock.minecraft.wiki/w/Critter_Safari), [Safari Critters](https://hypixelskyblock.minecraft.wiki/w/Critters/Critter_Safari), [Safari Belt](https://hypixelskyblock.minecraft.wiki/w/Safari_Belt), [Tree Gifts](https://hypixelskyblock.minecraft.wiki/w/Tree_Gifts), [Wumpa](https://hypixelskyblock.minecraft.wiki/w/Wumpa), [Doomspiral](https://hypixelskyblock.minecraft.wiki/w/Doomspiral), [Torrhus Fairy Souls](https://hypixelskyblock.minecraft.wiki/w/Fairy_Souls/List/Torrhus_Canyon), [Critter Safari Fairy Souls](https://hypixelskyblock.minecraft.wiki/w/Fairy_Souls/List/Critter_Safari), and [Starlyn Contest](https://hypixelskyblock.minecraft.wiki/w/Starlyn_Contest) were used to verify names, Critter lists and Shard rarities, behavior, ticket tiers, contest thresholds, quest-item names, Fairy Soul coordinates, milestones, and received-message formats. QCA stores only compact factual identifiers, coordinates, and thresholds needed for local parsing/rendering. It does not bundle Wiki images or page text and makes no Wiki or Hypixel API request at runtime.

Safari Belt bonus values are read from the item lore already received by the client instead of hard-coding a potentially stale total. Torrhus and Safari trackers likewise consume only received scoreboard, Tab, chat, title, entity-name, inventory, and already-loaded block-state data; they do not request server state or perform gameplay actions.

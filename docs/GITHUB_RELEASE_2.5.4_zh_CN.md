# QCloudy_Addition Alpha 2.5.4（Minecraft 26.1.2）

`2.5.4` 是 `1.5.3` 最新发布基线之后的 Alpha 开发线起点。发布通道仍为 Alpha；这只是版本线重编号，并非升级为 Beta 或 Release。

本 Alpha 版本新增一套受 JEI 信息结构启发、完全在客户端运行的 Attribute Shard Fusion 查询功能。模组仍可独立运行，不依赖 JEI、Firmament、SkyHanni、Skyblocker 或 BabyzombieAddons。

## 新功能：Attribute Shard Fusion Guide

- 使用随 JAR 打包的离线目录，浏览官方 Bazaar 当前列出的全部 320 种 Attribute Shard。
- 可按原始英文 Shard 名称、ID、属性、品质、分类、Family 与 Skill 搜索。
- **Recipes** 显示能够产出目标 Shard 的全部有序输入组合。
- **Uses** 显示会消耗当前 Shard 的全部有序合成。
- 配方卡保留左右输入顺序、所需数量、最多三个可选输出槽、ID/Chameleon ×1 产量、Special ×2 产量，以及 Pure Reptile 的双倍产出概率。
- 当 ID Fusion 与 Special Fusion 产出同名 Shard 但数量不同时，会保留两个独立输出槽。
- 支持左键 Recipes、右键 Uses、交换输入、前进/后退历史、分页与本地 `/qshard [英文查询]`。
- 客户端已经观察到的原生 Shard ItemStack 会遵循当前材质包；尚未观察到时使用安全的本地图标回退。

## 数据修正

社区 Wiki 表格当前有 317 行，而已审核的 Hypixel 官方 Bazaar 快照包含 320 个 `SHARD_*` 产品。本版本加入 Anteater（`R70`）、Zombuddy（`R84`）、Troodon（`R86`）和 Ghost Crab（`L38`），把 Goldolot 校正为 `R92`，并因官方 Bazaar 不存在 `SHARD_RAINBUG` 而排除 Rainbug。

## 安全边界与需求

- 需要 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25。
- Guide 运行时完全离线且只读，不发送聊天、服务器命令、数据包、点击、移动、API 请求，也不会自动执行合成。
- `/qshard` 是本地客户端命令，不产生服务器载荷。
- 当前仍为 Alpha，不代表获得 Hypixel 官方认可。

完整内容与验证边界见 `CHANGELOG_zh_CN.md`、`docs/COMPLIANCE_zh_CN.md` 和 `docs/VALIDATION_zh_CN.md`。

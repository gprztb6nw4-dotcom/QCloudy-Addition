# QCloudy_Addition Alpha 2.5.5（Minecraft 26.1.2）

本 Alpha 专门改进 Shard Fusion Guide 的图标与交互。模组仍为独立运行的纯客户端 Fabric 模组，不依赖 JEI、Firmament、SkyHanni、Skyblocker 或 BabyzombieAddons。

## 修复

- 目录中的 320 种 Shard 现在各自拥有对应的内置图标；未观察过的条目不再全部显示成同一个紫水晶物品。
- 客户端已经在打开菜单或物品栏收到的原生 Shard `ItemStack` 仍然优先，并保留在整次会话缓存中，因此跨 Guide 页面继续遵循当前材质包/服务器原生显示。
- 点击搜索框外、按 `Esc` 或按 `Tab` 会释放文字焦点；直接点击搜索框即可重新输入。配方导航与界面快捷键不再被输入框持续拦截。
- 每张配方的两个输入会作为一个紧凑组合完成测量并居中；候选输出使用相同的内容宽度布局，点击范围与可见图标/文字边界一致。

## 离线图标来源

图标生成器使用 [SkyShards](https://github.com/Campionnn/SkyShards) 已审核 MIT commit `9688031dbc4e726168ffceb0f44884ff26e6e728` 中的 `public/shardIcons/<Shard ID>.png`。源集合共 321 张；生成时严格按 QCA 的 320-Shard 目录筛选，因此不会打包额外 Rainbug 资源。转换后的本地图标、Minecraft 物品模型与映射全部放入 JAR。

## 安全边界与需求

- 需要 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25。
- Guide 只读；QCA 运行时不会请求 Wiki、Bazaar API、SkyShards 或图标服务。
- `/qshard` 是本地客户端命令，不发送聊天、服务器命令、数据包、点击、移动或 Fusion 操作。
- 当前仍为 Alpha，不代表获得 Hypixel 官方认可。

完整来源、规则边界与当前验证状态见 `CHANGELOG_zh_CN.md`、`THIRD_PARTY_NOTICES.md`、`docs/COMPLIANCE_zh_CN.md` 和 `docs/VALIDATION_zh_CN.md`。

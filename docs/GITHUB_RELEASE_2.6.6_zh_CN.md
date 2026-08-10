# QCloudy_Addition Beta 2.6.6（Minecraft 26.1.2）

Beta 2.6.6 将已经审核的 Alpha 2.5.6 功能集晋级到 Beta 通道。QCloudy_Addition 仍是可独立运行的纯客户端 Fabric 模组，不依赖 JEI、Firmament、SkyHanni、Skyblocker 或 BabyzombieAddons。

## 相比 1.5.3 Release 基线的主要变化

- 新增面向官方 320 种 Shard 的 JEI 风格、完全离线的 Attribute Shard Fusion Guide。
- 可按原始英文名称、ID、效果、品质、分类、家族、Skill、生物类型或获取文字搜索。
- 可浏览**详细信息**、**合成来源**和**可合成内容**，包含有序输入、数量、1–3 个可选产物、Special Fusion 产量、Chameleon 规则与 Pure Reptile 信息。
- 为每个目录 ID 内置对应的 Shard 图标；客户端已经收到的原生 `ItemStack` 仍拥有本次会话最高显示优先级。
- 显示已记录效果、语义分类、自然获取方式、Fusion-only 标记和反向 Fusion 配方数量。Queen Bee 等 Shard 会同时显示自然来源和已验证 Fusion 配方。

## 汇总 Alpha 2.5.4–2.5.6 的修复

- 用 Shard 对应图标替换通用紫水晶回退。
- 点击搜索框外、按 `Esc` 或 `Tab` 会退出输入焦点；点击搜索框可恢复输入。
- 两个输入与输出组按内容宽度紧凑居中，不再把相关 Shard 误导性地分散在两端。
- Epic Shard 改用 Minecraft 深紫色（`§5`）；属性、分类、生物类型、获取方式与品质使用已审核的语义颜色。
- 可点击 Shard 文字仅在鼠标悬停时变深并添加下划线。
- 保留彩色效果片段之间的空格，并清理残留 Wiki 格式标记。
- 当 ID Fusion 与 Special Fusion 产出同名但数量不同的 Shard 时，继续保留独立产物槽。

## 已删除

- 槽位锁定。
- Storage 覆盖。
- 菜单中键转换。

## 安全与兼容性

- 适用于 Minecraft 26.1.2 的纯客户端 Fabric 模组，需要 Java 25。
- 必需依赖：Fabric API `0.155.2+26.1.2` 或更新版本；Mod Menu 为可选依赖。
- Shard Guide 只使用内置离线数据，运行时不请求 Wiki/API，不发送数据包，不点击菜单，不执行 Fusion，不发送聊天、服务器命令或自动操作。
- `/th` 与 `/helia` 是明确披露、只能由玩家主动输入触发的快捷命令，不会自行运行。
- 静态与构建验证不能代替登录 Hypixel 的实服测试，也不能代替所有材质包与 GUI Scale 的视觉验收。任何模组均需玩家根据 Hypixel 当前规则自行承担使用风险。

## 文件

- 可运行文件：`QCloudy_Addition-Beta-2.6.6+26.1.2.jar`
- 源码文件：`QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`

普通玩家只需安装可运行 JAR；Sources JAR 仅用于查看源码和开发。

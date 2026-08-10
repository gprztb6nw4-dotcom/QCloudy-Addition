# QCloudy_Addition

**适用于 Minecraft 26.1.2 Fabric 的纯客户端 Hypixel SkyBlock 辅助模组。**

QCloudy_Addition 将地图、任务追踪、砍树与狩猎辅助、宠物信息、离线 Attribute Shard Fusion 查询及可深度自定义的 HUD 整合进一套清晰的中英双语界面。

界面默认英文，可在设置中切换简体中文。Hypixel 返回的地点、物品、任务、宠物、皮肤、配件、Shard 和玩家重命名 HOTM 配置始终保留原名，避免翻译改变游戏数据的实际含义。

> **当前通道：Beta 2.6.7。** Beta 表示该构建已经通过自动测试和归档验证，但仍需要更广泛的实服、材质包、GUI Scale、操作系统和模组组合测试。

## 核心功能

### 地图

- 使用本次提供的单层 **Dwarven Mines 地图**，逐区域重新校准实时玩家箭头，只根据 X/Z 同步且不进行 Y 分层。
- 三张坐标一致、根据玩家高度自动切换的 **Glacite Tunnels 地图**。
- Torrhus 与 Critter Safari 的可选粉色 **Fairy Soul 光柱**，已收集状态保存在本地。

### 物品、菜单与宠物

- 独立运行、受 JEI 信息结构启发的 **Attribute Shard Fusion Guide**，覆盖官方 320-Shard 目录。
- 可按英文 Shard 名称、ID、效果、品质、分类、家族、Skill、生物类型或获取来源搜索。
- 独立的**详细信息**、**合成来源**和**可合成内容**页面，显示有序输入、数量、可选产物、Special Fusion 产量、Chameleon 规则和 Pure Reptile 信息。
- 内置 Shard 对应图标、已记录自然/Fusion 获取方式、游戏语义颜色和完整反向配方；Queen Bee 等自然与 Fusion 双来源 Shard 会同时显示两类来源。
- 当前 **Pet HUD** 支持已验证宠物/皮肤头颅、品质色名称、经验、满级进度、受支持的溢出等级、皮肤名和宠物用品图标/名称。
- 物品创建时间戳、光标位置记忆和 AOTE/AOTV 传送音效自定义。

Shard 数据库与回退图标随模组离线打包。Guide 运行时不访问 Wiki/API，不点击菜单，也不会执行 Fusion。可输入 `/qshard [英文查询]` 在本地打开。

### 战斗

- 位于 The End 或 Dragon's Nest 时显示可自定义颜色的 **Ender Dragon 轮廓**。
- 从客户端收到的 Tab Widget 构建完整、不省略的 **Crimson Isle Faction Quest HUD**；确认完成的任务会自动隐藏。

### 挖矿

- 显示完整任务名称、紧凑进度条，并支持百分比或当前/目标数值模式。
- Mithril、Gemstone 与 Glacite Powder 追踪。
- 当前 HOTM Loadout 名称显示。
- 支持 Dwarven Mines、Crystal Hollows、Glacite Tunnels 与 Glacite Mineshafts。

### 砍树

- 综合 **Torrhus HUD**：Helia Chapter、Forest/Desert Whispers、Forest Essence、Safari Essence、Sweep 与 Forest Fortune。
- 独立 **Galatea HUD**：Hina Chapter 与 Agatha's Contest 信息。
- Tree Critter 倒计时读取可见 Tree Protection Order 的实际状态，不运行猜测计时器。
- Miria Contest 档位/剩余分数、Benefactor 状态和严格确认本人归属的稀有 Tree Gift 提醒；每种 Loot 可独立控制。

### 狩猎与 Critter Safari

- Beeheemoth 轮廓、临时生成光柱与独立空间 Beeheemoth 音量。
- Lasso `REEL` 提示音和屏幕中央 Critter 行为指引。
- Safari 本轮 Dashboard、可选 Shard 统计、Critterdex 进度和按区域分类的结果。
- Cold 两档预警与最近已加载篝火光柱。
- Doomspiral、Warden 冷却、Sparkling Critter、Floor Drop、Quest Item、Wumpa、Snoozle 墙体与 Safari Belt 辅助。
- 真实 Critter 按对应 Shard 品质显示轮廓；Armor Stand 抓捕道具会被排除。
- 实验性 Wumpa 路线预测可选，默认关闭。

### 通用与聊天

- 不含定时器、重试循环或自动加入的手动**重新连接**按钮。
- 支持键盘、鼠标按键与修饰键组合的**聊天偷窥**。
- 直接在设置行编辑热键，支持鼠标按键和多键组合。
- 可搜索、可折叠的功能组；每项功能只存在于一个分类中。

## HUD 自定义

每个 HUD 单独保存位置与 50–200% 缩放。背景颜色/透明度、边框开关/宽度/颜色、标题颜色、粗体和文字阴影均可分别设置。

HUD 编辑器只显示当前已经加载且确实存在内容的面板。拖动面板可改位置，拖动边框或四角可调整大小，所有改动在重启后保留。没有内容的 HUD 不会留下只显示标题的空框。

屏幕中央预警拥有各自的音效开关和 0–100% 音量滑条，默认音量为 64%；“通用”中另有总静音。

## 安装

必需：

- Minecraft **26.1.2**
- Fabric Loader **0.19.3+**
- Fabric API **0.155.2+26.1.2 或更新版本**
- Java **25**

可选：

- Mod Menu **18.0.0**

将可运行文件 `QCloudy_Addition-Beta-2.6.7+26.1.2.jar` 放入实例的 `mods` 文件夹。不要把 `-sources.jar` 当作可运行模组安装。

默认按 `O`、通过 Mod Menu，或输入 `/aca`、`/qca`、`/ca`、`/qc` 打开设置。这些设置别名和 `/qshard` 都是本地客户端命令，不会发送给 Hypixel。

## 纯客户端边界

QCA 只读取客户端已经能够获得的信息，包括收到的 Tab/计分板/聊天/标题、当前打开菜单、本地物品栏、已加载实体和已加载方块。模组不包含宏、自动移动、自动战斗、自动捕捉、遥测、远程更新、隐藏区块请求或运行时 Hypixel API 依赖。

QCA 唯一实现的服务器命令载荷为：

- `/th` → `warp torrhus`
- `/helia` → `chapter torrhus`

两者都只会在玩家明确输入对应本地快捷命令后发送。手动重连也只会在玩家点击按钮后建立一次普通 Minecraft 连接。

## 兼容性与免责声明

QCloudy_Addition 可以独立运行，不要求安装 Firmament、SkyHanni、Skyblocker、BabyzombieAddons、JEI 或 Mod Menu。安装 Firmament 后，可通过可选让出设置避免部分重复物品工具同时运行。

所有 Minecraft 模组均由玩家自行承担使用风险。被动 HUD、轮廓、路径光柱、覆盖和预测并不等于获得 Hypixel 官方认可。请阅读 Hypixel 当前规则，并关闭任何你不愿承担风险的功能。

QCloudy_Addition 与 Hypixel Studios、Mojang Studios 或 Microsoft 没有从属关系，也未获得其官方认可。

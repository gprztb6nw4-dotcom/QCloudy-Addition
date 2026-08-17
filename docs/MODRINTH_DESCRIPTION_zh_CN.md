# QCloudy_Addition

**适用于 Minecraft 26.1.2 Fabric 的纯客户端 Hypixel SkyBlock 辅助模组。**

QCloudy_Addition 将地图、任务追踪、砍树与狩猎辅助、宠物信息、完整离线 Attribute Shard Fusion Guide/Planner 及可深度自定义的 HUD 整合进一套清晰的中英双语界面。

界面默认英文，可在设置中切换简体中文。Hypixel 返回的地点、物品、任务、宠物、皮肤、配件、Shard 和玩家重命名 HOTM 配置始终保留原名，避免翻译改变游戏数据的实际含义。

> **当前通道：Minecraft 26.1.2 的 Alpha 2.8.27。** Alpha 只构建一个 Minecraft 目标。可选第三方设置/HUD 适配使用按需能力探测，不再使用精确版本白名单。

## 核心功能

### 统一设置与 HUD 编辑——Alpha

- 以功能为中心，统一管理 QCA 及已安装 SkyHanni、Skyblocker、Firmament、BabyZombieAddons、Feesh 中能够安全识别的实时设置。
- “通用”中有相互独立的“统一设置编辑”和“统一 HUD 编辑”总开关；两者均默认关闭，且不会隐藏 QCA 自身内容。
- 每一次提供方扫描都必须经过第二次确认。首次开启但没有有效快照时，以及每一次 Refresh，都会先显示对应范围的本地确认窗口；取消不会创建任务，重启后恢复的开关不会静默扫描，确认后才会打开提供方、阶段、当前内容与进度页面。
- 只显示已安装且成功读取能力的提供方。先使用原生与已验证分类，固定离线分类器只处理剩余未分类元数据，而且不能合并或改变功能。
- 可用一级分类保持以下顺序：通用、地图、物品与菜单、战斗、地牢、Slayer、挖矿、种地、砍树、钓鱼、狩猎、Rift、活动；没有任何可用功能的分类会直接隐藏。
- 一个完全相同的功能只显示一张卡。右键先选提供方，再直接编辑所选模组中安全的布尔、枚举、数值、位置与缩放设置。
- Feesh 使用其实时委托属性 setter 与原生保存路径；已启用且存在内容的 Feesh Overlay 按锚点编辑位置、缩放和对齐，并通过 Feesh 自己的存储保存。不支持的 Feesh 分支只进入“兼容性缺失报告”。
- 开启共享卡片时，只启用所选实现，并关闭其他兼容提供方中完全等价的实现。
- 启用独立 HUD 总开关后，编辑 HUD 才会显示所选且已启用的第三方 HUD，松开鼠标时写回其原生位置/缩放。
- 集成按能力探测；仅版本号变化不会让整个模组消失，仍兼容的已知字段继续可编辑，不认识的新结构会安全省略；QCA 可独立运行。
- “通用 → 兼容模组”中的“兼容性缺失报告”不是功能开关；它只读检查已识别能力，按模组列出不能统一管理的功能，并标记“设置/HUD 编辑”，正常功能不会显示。

### 地图

- 使用本次提供的单层 **Dwarven Mines 地图**，逐区域重新校准实时玩家箭头，只根据 X/Z 同步且不进行 Y 分层。
- 三张坐标一致、根据玩家高度自动切换的 **Glacite Tunnels 地图**。
- Torrhus 与 Critter Safari 的可选粉色 **Fairy Soul 光柱**，已收集状态保存在本地。

### 物品、菜单与宠物

- **Century Cake 效果过期提醒**使用一个默认开启的总开关管理全部 20 种收到的蛋糕加成。倒计时按真实世界 48 小时计算并包含离线时间；`/cake` 与 `/centurycakeeffect` 打开本地效果计时界面。同批过期会合并为一条中央/聊天提醒，带下划线的续效果文字只有在玩家直接点击后才执行 `/visit northwestcloudy`。

- 独立运行、受 JEI 信息结构启发的 **Attribute Shard Fusion Guide**，覆盖官方 320-Shard 目录。
- 可按英文 Shard 名称、ID、效果、品质、分类、家族、Skill、生物类型或获取来源搜索。
- 独立的**详细信息**、**合成来源**和**可合成内容**页面，显示有序输入、数量、可选产物、Special Fusion 产量、Chameleon 规则和 Pure Reptile 信息。
- 内置 Shard 对应图标、已记录自然/Fusion 获取方式、游戏语义颜色和完整反向配方；Queen Bee 等自然与 Fusion 双来源 Shard 会同时显示两类来源。
- 独立的 **Shard Planner**：目标数量、有限深度多步 Fusion Tree、其他候选路线、Materials Only 总数、输入/输出直接配方筛选、可编辑 Shards/hour、可拖动 Fusion Lines 和按 Profile 保存的本地 Hunting Box 仓库。
- Ironman 永不使用 Bazaar；Normal 最快路线可比较狩猎与购买耗时，Normal 最便宜路线使用可选兼容 Skyblocker 缓存。QCA 不下载价格、没有硬依赖；缺少稳定提供者时只关闭价格路线，全部离线与速率功能仍能使用。
- 当前 **Pet HUD** 支持已验证宠物/皮肤头颅、品质色名称、经验、满级进度、受支持的溢出等级、皮肤名和宠物用品图标/名称。
- 物品创建时间戳、光标位置记忆和 AOTE/AOTV 传送音效自定义。

Shard 数据库与回退图标随模组离线打包。Guide 运行时不访问 Wiki/API，不点击菜单，也不会执行 Fusion。可输入 `/qshard [英文查询]` 在本地打开。

### 战斗

- 位于 The End 或 Dragon's Nest 时显示可自定义颜色的 **Ender Dragon 轮廓**。
- **Power Orb 与 SOS 消失提醒**：四种 Power Orb 使用本人精确消失聊天，Warning/Alert/SOS Flare 使用确认后的本地三分钟生命周期。放置失败、实体卸载、距离和增益范围不会触发到期。中央大字和独立本地音效可设置，音效默认 64%。
- 从客户端收到的 Tab Widget 构建完整、不省略的 **Crimson Isle Faction Quest HUD**；确认完成的任务会自动隐藏。

### 挖矿

- 显示完整任务名称、紧凑进度条，并支持百分比或当前/目标数值模式。
- Mithril、Gemstone 与 Glacite Powder 追踪。
- 当前 HOTM Loadout 名称显示。
- 支持 Dwarven Mines、Crystal Hollows、Glacite Tunnels 与 Glacite Mineshafts。
- Crystal Hollows 的 `Jungle` 使用完整地点名匹配，因此 The Park 的 `Jungle Island` 不会再误开挖矿 HUD。

### 钓鱼

- 一级“钓鱼”分类中的下级组更名为“咬钩提示”，不再重复显示“钓鱼 → 钓鱼”。
- 可选 Ciallo 咬钩提示默认关闭，并且只响应本地玩家检测到的水钓或岩浆钓鱼咬钩状态。

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
- 可用一级分类遵循固定顺序，没有任何可用功能的分类会隐藏；支持搜索与可折叠功能组，每项功能只有一个归属。

## HUD 自定义

每个 HUD 单独保存位置与 50–200% 缩放。背景颜色/透明度、边框开关/宽度/颜色、标题颜色、粗体和文字阴影均可分别设置。

HUD 编辑器只显示当前已经加载且确实存在内容的面板。拖动面板可改位置，拖动边框或四角可调整大小，所有改动在重启后保留。没有内容的 HUD 不会留下只显示标题的空框。

屏幕中央预警拥有各自的音效开关和 0–100% 音量滑条，默认音量为 64%；“通用”中另有总静音。

## 安装

必需：

- Minecraft **26.1.2**
- Fabric Loader **0.19.3+**
- Fabric API **0.155.2+26.1.2**
- Java **25**

可选：

- Mod Menu **18.0.0**

将 `QCloudy_Addition-Alpha-2.8.27+26.1.2.jar` 放入实例 `mods` 文件夹。不要把 `-sources.jar` 当作可运行模组安装。

默认按 `O`、通过 Mod Menu，或输入 `/qca`、`/qc` 打开设置。这些设置别名和 `/qshard` 都是本地客户端命令，不会发送给 Hypixel。

## 纯客户端边界

QCA 只读取客户端已经能够获得的信息，包括收到的 Tab/计分板/聊天/标题、当前打开菜单、本地物品栏、已加载实体和已加载方块。模组不包含宏、自动移动、自动战斗、自动捕捉、遥测、远程更新、隐藏区块请求或运行时 Hypixel API 依赖。

QCA 唯一实现的服务器命令载荷为：

- `/th` → `warp torrhus`
- `/helia` → `chapter torrhus`
- 点击 Century Cake 续效果文字 → `visit northwestcloudy`

前两项只会在玩家明确输入对应本地快捷命令后发送；蛋糕续效果命令只会在玩家实际点击带下划线的聊天文字后发送。`/cake` 与 `/centurycakeeffect` 只是本地界面命令，不发送任何内容。手动重连也只会在玩家点击按钮后建立一次普通 Minecraft 连接。

## 兼容性与免责声明

QCloudy_Addition 可以独立运行，不要求安装 Firmament、SkyHanni、Skyblocker、BabyZombieAddons、Feesh、JEI 或 Mod Menu。统一编辑器不会只因提供方版本变化就拒绝整个模组：它会探测已识别的实时配置和保存能力，让仍兼容的原有功能继续编辑，并跳过无法安全理解的新结构。复杂原生颜色/快捷键编辑对象在契约得到支持前仍需在提供方自己的界面中调整。兼容 Skyblocker 只作为 Bazaar 价格路线的可选来源；没有它时价格模式不可用，但模组本身不会出错。SkyHanni/Firmament 没有稳定公开的跨模组价格 API，因此不作为价格提供者。

所有 Minecraft 模组均由玩家自行承担使用风险。被动 HUD、轮廓、路径光柱、覆盖和预测并不等于获得 Hypixel 官方认可。请阅读 Hypixel 当前规则，并关闭任何你不愿承担风险的功能。

QCloudy_Addition 与 Hypixel Studios、Mojang Studios 或 Microsoft 没有从属关系，也未获得其官方认可。

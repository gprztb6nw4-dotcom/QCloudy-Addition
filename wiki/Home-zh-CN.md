# QCloudy_Addition Wiki（简体中文）

[English](Home)

![QCloudy_Addition 图标](https://raw.githubusercontent.com/gprztb6nw4-dotcom/QCloudy-Addition/main/src/main/resources/assets/qcloudy_addition/icon.png)

QCloudy_Addition（QCA）是一个以英文为默认语言、提供中英文界面、仅在客户端运行的 Hypixel SkyBlock Fabric 模组。它用一个按“功能”分类的界面统一管理地图、HUD、被动视觉辅助、宠物信息、Attribute Shard 工具和部分客户端体验优化。

> **当前版本：** Beta 2.7.17<br>
> **Minecraft：** 26.1.2 与 26.2<br>
> **必需：** Java 25、Fabric Loader 0.19.3 或更高版本，以及与 Minecraft 对应的 Fabric API<br>
> **可选：** Mod Menu，以及经过适配审核的其他 SkyBlock 模组版本<br>
> **说明：** QCA 是独立的社区项目，与 Hypixel、Mojang、Microsoft、SkyHanni、Skyblocker、Firmament 和 BabyZombieAddons 均无隶属或官方认可关系。

## 目录

- [安装](#安装)
- [打开模组](#打开模组)
- [设置、语言与 HUD 编辑](#设置语言与-hud-编辑)
- [功能指南](#功能指南)
- [Attribute Shard 指南与规划器](#attribute-shard-指南与规划器)
- [兼容 SkyBlock 模组的统一控制](#兼容-skyblock-模组的统一控制)
- [纯客户端与安全边界](#纯客户端与安全边界)
- [命令与对外操作](#命令与对外操作)
- [本地保存的数据](#本地保存的数据)
- [兼容性与故障排查](#兼容性与故障排查)
- [报告 Bug](#报告-bug)
- [验证、许可证与致谢](#验证许可证与致谢)

## 安装

必须选择与游戏实例 Minecraft 版本完全一致的 JAR。

| Minecraft | 所需 Fabric API | 可运行文件 |
|---|---|---|
| 26.1.2 | 0.155.2+26.1.2 或更高的兼容版本 | `QCloudy_Addition-Beta-2.7.17+26.1.2.jar` |
| 26.2 | 0.154.2+26.2 或更高的兼容版本 | `QCloudy_Addition-Beta-2.7.17+26.2.jar` |

两个版本都要求 Fabric Loader 0.19.3 或更高版本以及 Java 25。

1. 安装对应版本的 Minecraft、Fabric Loader、Fabric API 和 Java。
2. 从 [GitHub Releases 页面](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/releases)下载可运行的 QCA JAR。
3. 将可运行 JAR 放入该实例的 `mods` 文件夹。
4. 删除同一文件夹中的旧版 QCA，确保只加载一个 QCA 版本。
5. 启动 Minecraft，按 `O` 或输入本地设置命令打开 QCA。

不要把以 `-sources.jar` 结尾的文件当作模组安装。它是供开发者和 IDE 使用的源代码包。

QCA 独立运行不需要 SkyHanni、Skyblocker、Firmament、BabyZombieAddons 或 Mod Menu。Mod Menu 只会额外提供一个进入 QCA 设置的入口。

## 打开模组

可以通过以下方式打开设置：

- 默认按 `O`。可以在 **控制 → 按键绑定 → QCloudy_Addition** 中重新绑定。
- 输入 `/aca`、`/qca`、`/ca` 或 `/qc`。
- 安装 Mod Menu 后，从 Mod Menu 中打开 QCA。

以上四个斜杠命令都是本地客户端命令。只有在对应名称没有被其他客户端命令占用时才会注册；它们只打开本地界面，不会发送给 Hypixel。

输入 `/qshard [英文搜索内容]` 可以打开离线 Attribute Shard 指南，并预先填入搜索内容。

## 设置、语言与 HUD 编辑

### 按功能分类

左侧栏固定使用以下顺序：

1. 通用
2. 地图
3. 物品与菜单
4. 战斗
5. 地牢
6. Slayer
7. 挖矿
8. 耕种
9. 砍树
10. 钓鱼
11. 狩猎
12. Rift
13. 活动

Safari 是狩猎下的可折叠分组，Garden 属于 Farming，Crimson Isle 与 Kuudra 属于战斗。同一个功能只归属于一个位置，不会在多个分类中重复出现。可折叠分组默认收起。

### 功能卡片

- 左键点击卡片：开启或关闭功能。
- 开启后，卡片左侧显示蓝色条。
- 右键点击卡片：打开属于该功能的全部二级设置。
- 二级菜单不重复显示一级菜单已经拥有的总开关。
- 大范围数值使用可拖动滑块。功能音量统一为 0–100%，除非另有说明，默认 64%。
- 可编辑颜色使用统一 RGB/HSV 选择器、预设颜色；所有背景颜色都可以选择完全透明。
- 热键可使用键盘、鼠标按键以及 Ctrl/Shift/Alt/Cmd-Super 组合。监听按键时按 `Esc`、Backspace 或 Delete 可以清空绑定。

### 语言

模组默认显示英文，可在 QCA 设置中切换为简体中文。

语言选项只翻译 QCA 自己的界面。Hypixel 提供的物品、任务、地点、宠物、皮肤、宠物配件，以及玩家重命名的 HOTM 预设都会保留客户端收到的原始名称。这样可以避免错误翻译，也能保证搜索与游戏内名称一致。

### 编辑 HUD

点击设置界面左下角的 **编辑 HUD**。

- 只有“已开启、当前已加载、并且确实有可见内容”的 HUD 才能编辑。
- 拖动面板可以移动位置。
- 像桌面窗口一样拖动边框或角落可以缩放。
- 每个 HUD 单独保存 50–200% 的缩放。
- HUD 上的小齿轮会打开该 HUD 自己的设置。
- 松开鼠标时保存位置与缩放，重启后仍然保留。
- 每个 HUD 可以单独设置背景颜色/透明度、边框是否显示、边框宽度/颜色、标题颜色、粗体和文字阴影。
- 当 HUD 没有任何可见内容时，整个面板都不会渲染，不会留下空标题、空边框或空背景。

## 功能指南

### 通用

- **界面动画**控制 QCA 本地菜单的过渡动画。
- **预警总静音**可以统一关闭 QCA 的警告声音，而不强制关闭所有视觉提示。
- **手动重连**在连接失败与断开连接界面添加一个正常大小的“重连”按钮。只有玩家点击后才会连接，没有倒计时、循环或自动重试。
- **聊天偷窥**在按住自定义按键或组合键时临时展开已经收到的聊天记录。鼠标滚轮可以选择滚动聊天，或继续控制快捷栏。

### 地图

- **Dwarven Mines 地图**使用给定的单层 12 区域概览图和实时红色箭头。一个连续变换只把玩家实时的 **X/Z 两个坐标**映射到背景；Y 坐标与计分板上的子区域名称被明确忽略。指针表达的是大致可视位置，而不是精确到方块的测绘坐标。因此位于 The Mist 上方的桥梁不会切换到另一个楼层或区域算法。
- **Glacite Tunnels 地图**根据玩家本地 Y 坐标选择低、中、高三张地图；所有层共用一个坐标系，地点标签会自动避让。
- **Fairy Soul 路标**是地图分类中的一个统一功能，包含内置的 Torrhus 与 Safari 坐标。该功能默认关闭；开启后两个坐标组会同时启用。粉色信标在收到与最近坐标匹配的“已找到/已经找到”确认后消失。

地图点位名称统一使用官方英文名称。

### 物品与菜单

- **已装备宠物 HUD**显示收到的宠物等级、按品质着色的名字、真实宠物/皮肤头、经验进度、距离满级经验、可选皮肤名和宠物配件。支持 200 级 Dragon 曲线以及 Ancient Golden Dragon 溢出等级。宠物满级时只隐藏多余的“距离满级”一行，不会隐藏配件。
- **Attribute Shard 指南与规划器**提供离线 320 Shard 浏览、直接配方、用途、多步规划、仓库和 Fusion Lines，详见下方独立章节。
- **物品时间戳**显示客户端观察到的物品创建时间。
- **保存光标位置**为支持的菜单恢复设定的鼠标位置。
- **AOTE/AOTV 声音**可以让 Instant Transmission 与 Etherwarp 保留原声或改用本地声音预设，音量为 0–100%，默认 64%。QCA 不改变传送距离、冷却、移动、数据包或物品使用逻辑。

槽位锁定、Storage 覆盖和菜单中键点击转换已经从 QCA 底层彻底删除，并不是仅从设置界面隐藏。

### 战斗

- **Ender Dragon 高亮**在玩家位于 The End 或 Dragon's Nest 时，把客户端收到的 Hypixel Ender Dragon 加入 Minecraft 原版轮廓渲染；颜色可自定义。
- **Crimson Isle 阵营任务**读取收到的 `Faction Quests:` Tab 区块，显示未完成任务的原始名称和进度；已完成任务不会显示。
- 只有安装完全匹配且经过审核的外部模组版本时，兼容的 Crimson Isle 与 Kuudra 功能才会作为战斗的下级分组出现。

### 地牢

该分类用于显示兼容外部模组提供、并且功能定义完全匹配的地牢设置。没有经过审核的提供者时，QCA 不会虚构一个替代实现。

### Slayer

该分类用于显示兼容外部模组提供、并且功能定义完全匹配的 Slayer 设置。不支持或版本不匹配的适配器会被隐藏。

### 挖矿

- **挖矿任务与粉尘**读取 Dwarven Mines、Crystal Hollows、Glacite Tunnels 和 Glacite Mineshafts 中客户端收到的 Tab 数据。
- Commission 名称完整显示，并使用单独测量宽度的进度条。
- 进度可以显示为保留一位小数的百分比，或 current/target 数值。
- Mithril、Gemstone 与 Glacite Powder 分开追踪。
- 可选的 `HOTM: <预设名>` 一行会记住在相关菜单中观察到的 Heart of the Mountain 预设。

### 耕种

完全匹配的外部模组提供的 Garden 与 Farming 功能会显示在这里。QCA 只合并真正等价的功能；即使价格提示、利润统计和任务追踪都与 Farming 有关，它们也不会被错误合并成同一个开关。

### 砍树

- **Torrhus Chapter 与资源**在一个可换行 HUD 中组合显示当前 Helia Chapter、完整任务/进度、Forest Whispers、Desert Whispers、Forest Essence、Safari Essence、Sweep 和 Forest Fortune。
- **Galatea 追踪**使用独立设置显示 Hina Chapter 与 Agatha's Contest，并遵循相同的内容与空面板规则。
- **Tree Critter 计时**读取已加载 Tree Protection Order 名牌上服务器显示的准确倒计时，不自行猜测或启动一个本地计时器。
- **Miria 与 Agatha Contest 信息**可以显示下一档、距离下一档还差多少，以及预计 Safari Ticket；不会重复计分板已有的比赛计时。
- **Benefactor 状态**只从范围明确的 Tab、计分板、聊天和玩家实际打开的菜单内容组合获得。
- **Tree Gift 提醒**必须先确认属于本地玩家的奖励区块。配置的稀有物品和准确生物提示可以显示屏幕中央文字并播放本地声音；旁边其他玩家单独出现的公共提示不会触发。

### 钓鱼

- **钓鱼上钩声音**默认关闭。
- 它把附近收到的准确 `!!!` 标记与本地玩家的水钓浮标，或范围受限的 Hypixel 岩浆钓鱼浮标表现进行关联。
- 每根鱼竿只播放一次内置 Ciallo 声音，音量 0–100%，默认 64%。
- 收杆不会再次播放，也不会重新触发本次提示。
- QCA 永远不会自动抛竿或收杆。

### 狩猎

- **Beeheemoth 辅助**提供可自定义轮廓、临时黄色生成信标和独立 Beeheemoth 声音控制。玩家靠近、收到自己的抓捕确认或实体消失时，信标会被移除。
- **Lasso REEL 提示音**在本地玩家收到的 Lasso 状态首次变为准确 `REEL` 时播放一次。
- **Critter Behavior Assistant**根据已记录的 Critter 机制显示范围受限的屏幕中央提示。

#### Safari 下级分组

- **Safari Run Dashboard**追踪本轮时间和 Ticket Tier。“本轮捕获 Shard 统计”是独立开关，默认关闭。
- **Safari Run Critterdex**按 Cavern、Forest、Haunted 和 Icy 分类显示本轮已捕获/缺失进度，名称不会被省略。
- **Critter 高亮**按照官方 Shard 品质给真实可见实体着色，不穿墙显示；抓捕用 Armor Stand 道具会被排除。
- **Cold Safety**提供可调整的第一/第二次预警（默认 80/90），以及可选的最近已加载篝火信标；Cold 开始下降后信标关闭。
- **Doomspiral 就绪**在玩家拥有至少 4 个 Soothing Incense 时提醒；**Warden 就绪**在本地可见抓捕冷却进入就绪状态时提醒。
- **Sparkling、Floor Drop 与 Quest Item 辅助**只使用收到的聊天、可见名称/实体、附近已经加载的方块和本地物品栏。
- **Wumpa HUD**接受玩家本人和队友 Loot Share 的 8 个 Icy 前置捕获；全部完成后改为显示 `Wumpa: Spawned`。红色移动/碰撞预测为可选功能，默认关闭。
- **Snoozle 墙覆盖**只给附近符合条件且暴露在外的墙面着色，颜色可自定义。
- **Safari Belt Tooltip**显示客户端确认的四个 milestone 等级和属性加成，并按本地账号/Profile 保存已确认进度。

### Rift

该分类预留给完全匹配的兼容外部功能。没有经过审核的实现时，QCA 会隐藏对应分组。

### 活动

该分类预留给完全匹配的兼容外部活动功能。即使多个提供者拥有同一个功能，QCA 仍然只显示一个功能入口。

## Attribute Shard 指南与规划器

Shard 系统完全是只读信息工具。QCA 不会点击 Fusion 菜单、选择产物、移动物品、发送 `/hb` 或执行任何 Fusion。

### 指南

内置目录包含准确 320 种当前 Bazaar 中存在的 Attribute Shard，以及每个 ID 对应的离线图标。

- 可以按官方英文名、Shard ID、效果、品质、分类、家族、Skill、生物类型或获取方式搜索。
- **Details** 显示效果、语义分类、所有内置的自然获取方式，并明确标记只能通过 Fusion 获得的 Shard。
- **Recipes** 显示所有可以产出目标 Shard 的有序输入组合；Queen Bee 等同时拥有自然来源与 Fusion 配方的 Shard 也包含在内。
- **Uses** 显示当前 Shard 可以作为输入参与的所有配方。
- 配方卡保留左右输入顺序、所需数量、最多三个可选产物、ID/Chameleon/Special 产量和 Pure Reptile 翻倍说明。
- 可以点击的 Shard 名字在悬停时加深并加下划线；品质与语义信息使用对应的 SkyBlock/Minecraft 颜色。
- 如果客户端已经收到匹配的真实 ItemStack，则真实物品渲染优先；否则使用内置的对应 ID 图标。

运行中的模组不会访问 Wiki、Hypixel API、Bazaar API、SkyShards 或图标服务。版本化数据在发布前生成并审核，然后直接打包进 JAR。

### 规划器页面

- **Plan：**输入目标 Shard 与数量，生成有边界限制的多步 Fusion Tree。
- **Recipes：**分别设置输入和输出过滤条件，查看直接有序配方关系。
- **Shards：**查看效果、家族、Skill、生物类型、获取方式、默认每小时获取速度，以及自定义本地速度。
- **Fusion Lines：**查看 ID、Special 与 Chameleon 路线；节点可点击、可拖动，位置保存在本地。
- **Warehouse：**使用玩家实际打开的 Hunting Box 页面中观察到的 Shard 数量。
- **Settings：**设置路线模式、价格侧、操作时间假设、Kuudra/Kraken 参数、是否使用仓库等。

规划支持 Fastest/最快或 Cheapest/最便宜、Normal 或 Ironman、其他候选配方，以及只显示汇总材料的 **Materials Only**。Ironman 永远不使用 Bazaar。Kraken 计算可以纳入 Kuudra Tier、通关时间、coins/hour 机会成本、Key 成本和停顿时间。

QCA 不包含 Bazaar 下载器。只有当已安装的兼容 Skyblocker 通过审核的公开方法提供其本地缓存价格时，价格路线才可用。没有价格提供者时，Cheapest 会明确显示不可用，但离线指南、Ironman、获取速度、Fusion Lines 和仓库功能仍然正常。

仓库只读取当前打开的 Hunting Box 页面中可见的 Shard ID 和准确 `Owned: N Shards` lore。它不会输入 `/hb`、切换页面、点击槽位、读取隐藏物品栏或自动执行规划。

## 兼容 SkyBlock 模组的统一控制

在 Minecraft 26.1.2 中，本 Beta 审核并适配了：

- SkyHanni 7.41.0
- Skyblocker 6.8.2
- Firmament 44.3.0
- BabyZombieAddons 3.4.1

这些模组都是可选项，不是 QCA 的编译或运行依赖。

当多个受支持模组实现完全相同的功能时，QCA 只显示一张功能卡片。二级菜单最上方是提供者选择。比如选择 SkyHanni 后，该卡片将控制 SkyHanni 对应实现；开启时只关闭其他模组中定义完全一致的功能。然后同一个二级菜单会显示该 SkyHanni 功能中可以安全编辑的原生设置。

QCA 直接修改已加载提供者的本地实时配置对象，并调用该模组自己的保存方式。它不会修改未加载模组的原始配置文件。经过验证的布尔值、枚举、范围数值、HUD 位置和缩放可以在 QCA 中显示；不安全或复杂的颜色/热键对象仍保留在对应模组自己的编辑器中。

提供者拥有的 HUD 也可以出现在 **编辑 HUD** 中，并标明来源模组；松开鼠标时才写入修改。

当前审核的提供者版本只对应 Minecraft 26.1.2。QCA 的 26.2 版本保留全部 QCA 自己的功能，但会隐藏这些版本锁定的适配器，直到对应的 26.2 提供者版本完成审核。当模组未安装、版本不符或配置结构不符时，所有适配器都会安全关闭而不是猜测写入。

## 纯客户端与安全边界

QCA 在 Fabric metadata 中声明为仅客户端模组。普通功能只读取客户端已经收到或已经能够看到的信息：本地位置、已加载实体/方块、计分板、Tab、聊天、当前可见菜单、物品 lore 和本地输入。

QCA 不包含 Hypixel Mod API 订阅、Hypixel 公共 API 客户端、HTTP 客户端、WebSocket、遥测、远程更新器、宏、自动移动、自动点击、自动抓捕、自动使用物品、隐藏物品栏读取或区块请求。

读取另一个本地模组已经缓存的数值，不会给 QCA 增加硬依赖，也不会让 QCA 自己发起额外网络请求。可选 Skyblocker 价格桥在审核方法不存在时会安全关闭。

“被动渲染”不等于“官方批准”。实体轮廓、信标、墙体覆盖和移动预测会让客户端已经收到的信息更容易观察，因此政策风险相对更高。所有 Minecraft 模组均由玩家自行承担使用风险。请阅读最新的 [Hypixel Allowed Modifications](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications) 与 [Hypixel SkyBlock Rules](https://support.hypixel.net/hc/en-us/articles/4508088842898-Hypixel-SkyBlock-Rules)。

完整表格见[客户端数据流与合规清单](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/COMPLIANCE.md)。

## 命令与对外操作

| 玩家操作 | 结果 | 发往服务器的内容 |
|---|---|---|
| `/aca`、`/qca`、`/ca`、`/qc` | 打开本地 QCA 设置 | 无 |
| `/qshard [英文搜索内容]` | 打开本地离线 Shard 指南 | 无 |
| `/th` | 玩家主动触发 Torrhus 快捷命令 | `warp torrhus` |
| `/helia` | 玩家主动触发 Helia 快捷命令 | `chapter torrhus` |
| 点击 **重连** | 对内存中记住的服务器目标发起一次正常连接 | Minecraft 正常连接 |

`/th` 等价于玩家手动输入 `/warp torrhus`；`/helia` 等价于玩家手动输入 `/chapter torrhus`。它们都不会自动触发。QCA 没有 `sendChat`、自动命令、自动重连循环或自动生成聊天内容。

## 本地保存的数据

QCA 只保存普通本地 JSON：

- `config/qcloudy_addition.json`：语言、功能设置、HUD 外观/位置/缩放、记住的宠物信息、按 Profile 保存的已接收狩猎数据、Fairy Soul 状态，以及 Shard Planner 设置/速度/图位置。
- `config/qcloudy_addition_shard_warehouse.json`：玩家实际打开 Hunting Box 页面后，按本地 Profile 保存的 Shard 数量和观察时间。

旧版 `autumecloudyaddition.json` 只读取一次用于迁移。保存时会先写临时文件，并在系统支持时进行原子替换。

QCA 不会保存密码、Access Token、Hypixel API Key、聊天记录、远程账户数据或服务器重连地址。

## 兼容性与故障排查

### 游戏无法启动

- 确认可运行 JAR 与 Minecraft 26.1.2 或 26.2 完全匹配。
- 确认 Java 25、Fabric Loader 0.19.3+ 和对应 Fabric API。
- 删除重复或旧版 QCA JAR。
- 不要把 `-sources.jar` 当作可运行模组放入 `mods`。

### HUD 或 HUD 编辑框没有出现

这通常是正常逻辑。只有当功能开启、当前位置/状态已经加载并且确实存在可见行时，HUD 才会渲染并出现在编辑器中。标题或占位符不能让一个空面板继续存在。

### Dwarven 指针没有精确落在某个方块

Dwarven 地图是单层、近似的 X/Z 概览图，明确忽略 Y 和楼层/子区域名称。箭头应该实时、连续移动，但背景本身不是逐方块精确测绘地图。

### Shard 的 Cheapest 规划不可用

QCA 不会下载 Bazaar 价格。可以使用离线/Ironman/获取速度模式，或者安装能公开现有客户端价格缓存的兼容 Skyblocker。提供者价格和统一外部设置目前只审核了上文列出的 Minecraft 26.1.2 版本。

### 外部模组提供者没有出现

适配器会锁定版本并安全关闭。请检查完全一致的模组版本。在 Minecraft 26.2 中，当前外部适配器会主动隐藏，直到对应版本完成审核。

### 真实物品图标与内置 Shard 图标不同

如果 QCA 已经收到匹配的真实 ItemStack，会优先让 Minecraft 渲染真实物品，使服务器或材质包表现生效；从未观察过的目录条目使用内置的对应 ID 离线图标。

## 报告 Bug

请创建 [GitHub Issue](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/issues)，并提供：

- QCA 版本和完整文件名
- Minecraft、Fabric Loader、Fabric API 与 Java 版本
- 完整模组列表及版本
- 使用语言和 GUI Scale
- 出现问题的具体岛屿/地点以及复现步骤
- 预期结果与实际结果
- UI/渲染问题的截图或短视频
- `latest.log`；如果崩溃，还需要 Crash Report
- 只安装 Fabric API 与 QCA 时是否仍然发生

不要上传 Access Token、会话标识、私人聊天或其他敏感信息。

## 验证、许可证与致谢

Beta 2.7.17 已使用 Java 25 为两个 Minecraft 版本构建。当前验证报告记录了每个目标 159 个测试且无失败、320 个目录 Shard/模型/纹理、完全一致的中英文语言键，以及通过检查的可运行包与 Sources 包。

自动测试与压缩包检查不能代替真实 Hypixel 回归、所有 GUI Scale、所有材质包和未来每一种模组组合。准确测试边界请查看[当前验证报告](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/VALIDATION.md)。

项目文档：

- [更新日志](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/CHANGELOG.md)
- [完整功能定义](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/FEATURES.md)
- [实现与数据流](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/IMPLEMENTATION.md)
- [合规清单](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/docs/COMPLIANCE.md)
- [第三方声明](https://github.com/gprztb6nw4-dotcom/QCloudy-Addition/blob/main/THIRD_PARTY_NOTICES.md)

QCloudy_Addition 源代码采用 **GNU Lesser General Public License v3.0 or later（`LGPL-3.0-or-later`）**。经过审核的离线事实/资源及其许可证均记录在 `THIRD_PARTY_NOTICES.md` 中，其中包括 Hypixel SkyBlock Wiki 和 MIT 许可证的 SkyShards 图标数据。运行中的模组不会联系这些来源。

# QCloudy_Addition

QCloudy_Addition 是适用于 Minecraft 26.1.2 的纯客户端 Fabric 模组。它为 Hypixel SkyBlock 提供地图与只读 HUD，同时明确排除游戏操作自动化、服务器状态修改、远程账户 API 和运行时网络服务。

默认语言为英文。按 `O`（可在“控制 → 按键绑定 → QCloudy_Addition”中改键）或输入 `/aca`、`/qca`、`/ca`、`/qc` 可打开客户端设置，并随时切换为简体中文。只有名称未被其他客户端命令占用时才会注册对应别名；这些命令只打开本地界面，不会发送给 Hypixel。

语言选项只翻译 QCA 自己的界面标签。Hypixel 地点、任务、宠物、皮肤、配件、物品以及玩家重命名的 HOTM 配置均保留客户端收到的原始名称；例如 `Terminator` 不会被改写成中文名称。

## 功能分类

### 通用

- **手动重连**：在连接失败和断线界面加入一个原版尺寸的“重新连接”按钮。正常连接尝试开始时就记录目标，所以首次加入失败后也能使用。只有玩家点击按钮才会重新连接；没有倒计时、循环、重试计数、命令或自动加入。

### 地图

- **矮人矿洞地图**：原创的单层总览图，以 12 个独立区域块呈现主要地点；区域按属性配色，使用粗边界与 Minecraft 位图字体，移除旧版密集路线网。红色箭头仅根据本地玩家 X/Y/Z、朝向以及计分板已经显示的子地点投影；地图点位固定使用 Hypixel 英文原名。
- **冰川隧道分层地图**：低层、中层和高层图片使用完全相同的坐标边界；在 Y=126 与 Y=143 切层，切换后玩家箭头位置保持一致。地图点位固定使用英文原名，并在生成阶段自动检测、避让相邻标签，防止文字重叠。

### 挖矿

- **任务与粉尘追踪**：读取客户端已经收到的 `Commissions:` 与 `Powders:` Tab Widget。每个任务显示完整名称和独立进度条，不再使用省略号；进度条会大致与当前最宽的完整任务名右端齐平，不再横跨整个固定面板。普通字体和粗体都用实际渲染样式测宽，并额外保证完整进度数值不会穿出边框。进度默认以一位小数百分比显示，可在功能二级设置中切换为“当前数值/目标数值”。服务器直接提供的数值优先，否则仅根据已记录的任务目标换算；未来未知任务会安全回退为百分比而不会伪造数值。HUD 还会默认显示 `HOTM: <配置名>`，从山心配置/Loadouts 菜单中读取并缓存玩家当前选择的原始名称，也可在二级设置中关闭。适用于矮人矿洞、水晶矿洞、冰川隧道和 Mineshaft，并分别显示秘银、宝石和冰川粉尘。

### Crimson Isle

- **阵营任务追踪**：位于 Crimson Isle 时，只读取客户端已经收到的 `Faction Quests:` Tab Widget，完整显示任务原名、需求数量及服务器给出的 `✖`/`✔` 状态，不省略也不翻译。该功能独立分类、默认开启，并与不会同时出现的挖矿追踪共用 HUD 位置和外观。

### 砍树

- **Torrhus Chapter 与资源**：在同一个会自动换行且绝不省略的 HUD 中显示当前 Helia Chapter、完整任务名、进度、Forest Whispers、Desert Whispers、Forest Essence、Safari Essence、Sweep 与 Forest Fortune。Tab 与计分板按两个独立的有限来源解析，后面的 `SB Level` 分数不会再串成 Chapter 任务；同时支持真实的 `Helia's Chapters` 总览、章节详情物品栏和短时间内分行收到的聊天状态。已确认的绝对数值按 Minecraft 账号和客户端收到的 SkyBlock Profile 分开保存，重连后仍存在；旧配置中误存的非 Chapter 任务会在载入时修复，只有观察到更新数值时才改写。聊天中的明确获取提示才进行有限增量累加。Safari Essence 在 Critter Safari 内不重复显示。已完成数量、Chapter 总进度和下一项解锁默认关闭。
- **Tree Critter 计时**：默认开启且可单独关闭。读取离玩家最近的 Tree Protection Order 可见名称牌 `Critter in: 26m 47s`，把服务器实际倒计时加入综合 Hunting HUD；不自行按物品猜测倒数，因此可准确兼容 Fun-Sized（60m）、Family-Sized（30m）、Jumbo（15m）、Behemoth（立即出现）、Honeycomb Artifact 加速、Honey Serendipity 立即触发及未来服务器修正。
- **Miria Contest**：解析客户端收到的计分板/Tab 档位行（例如 `COMMON with 151` 与 `Uncommon requires +99`），只在综合 Hunting HUD 中显示下一档、准确差值与预计 Safari Ticket；不再向右侧计分板注入内容，也不重复显示计分板已有的竞赛倒计时。
- **Benefactor 与 Tree Gift**：将有限范围的 Tab/计分板、已经打开的 Forest/Desert Temple 菜单和玩家本人收到的准确捐赠消息合并为 Benefactor 状态；支持多日捐赠、剩余时间、寺庙对应效果、到期处理和账号/Profile 持久保存，新捐赠也不会被仍未刷新的旧菜单立刻覆盖。十种稀有 Tree Gift 奖励可分别开关：读取玩家本人精确奖励汇总的 hover，也读取同一个经过个人贡献与汇总证明的有限 Gift 区块内精确 BONUS 行；兼容被聊天压缩模组取消显示但客户端已经收到的原始消息。附近玩家单独出现的公开掉落行不会触发。

### 狩猎

- **Beeheemoth 与 Lasso 提示**：只按参考模组使用的 scale-9 Bee 特征识别 Beeheemoth；原版轮廓默认开启，并接入统一 RGB/HSV 颜色选择器。黄色信标标在首次看见的生成位置，在玩家进入 10 格、收到自己捕捉 Beeheemoth 的确认，或实体消失时关闭。与该 scale-9 实体空间关联的 Bee 声音（包括短暂生成/捕捉窗口）拥有独立开关和音量，默认开启、64%；其他位置的普通 Bee 不受影响。独立的 Lasso `REEL` 提示音仅在本地玩家可见状态首次变为精确 `REEL` 时响一次，默认开启、64%。
- **Critter 行为辅助**：针对特殊捕捉机制显示中央提示，并在收到捕捉确认后进行短时间、有界去重。
- **Fairy Soul 点位**：这是唯一一个跨 Torrhus/Safari 的狩猎功能，两组坐标可分别开关，但功能卡只出现在“狩猎”。

### Safari

- **Safari Run Dashboard 与 Critterdex**：统计本轮 Shards、时间、Ticket Tier，并按官方 37 种 Critter 显示四个 Biome 的进度和当前 Biome 完整的已捕捉/缺失名称。
- **Cold、Doomspiral、Critter、Snoozle 与 Wumpa 辅助**：默认在 Cold 高于 80/90 时两次预警；超过第一档时立即扫描最近的已加载篝火并显示红色信标，Cold 开始下降时关闭；持有 4 个 Soothing Incense 时提示；按 Shard 品质色高亮真实 Critter 实体。Wumpa 的八项组队前置同时接受本人和队友 Loot Share 捕捉，生成后折叠为 `Wumpa：已生成`，路线改为跟踪真正的 Ravager 身体。独立 Snoozle 功能用默认绿色、可自定义 RGB 的半透明表面覆盖附近 `Cobbled Deepslate + Tuff` 可撞墙。Armor Stand 捕捉道具会被排除，避免再次描边支架身体。Wumpa 路线默认关闭，其余功能默认开启。
- **Sparkling、Floor Drop 与 Quest Item**：只依据收到的聊天、可见名称/实体、已加载的附近 String 方块和本地背包显示中央预警与 HUD；Sparkling 轮廓颜色可自定义。
- **Safari Belt 详情**：把本地观察到的 Cavern/Forest/Haunted/Icy 四项 Milestone 等级与物品实际说明中的属性增益嵌入 Safari Belt 提示；支持标题和 lore 分行的菜单格式，按账号/Profile 保存，只在收到更高的确认等级时更新。

“砍树”“狩猎”“Safari”是互斥设置分类，每张功能卡只有唯一归属，不会跨分类重复。相关预警都使用屏幕中央标题；每种预警分别拥有默认开启、64% 音量的独立音效与 0–100% 滑条，“通用”另有总静音。综合 HUD 拥有独立保存的外观、缩放与位置。

### 战斗

- **末影龙高亮**：当计分板地点为 The End 或 Dragon's Nest 时，将末影龙加入原版轮廓渲染管线；轮廓色可选红、黄、青、绿、紫或白。

### 宠物

- **当前宠物 HUD**：用召唤、收回和 Autopet 提示立即更新，再以客户端收到的 `Pet:` Tab Widget 校正。HUD 只用 QCA 内置且已验证的 Profile 构造普通 player head，不再写入合成 `petInfo`，因此其他模组无法把 HUD 头像替换成无关物品模型。动态皮肤家族的每一帧都会归回正确皮肤，包括 Baby Spinosaurus 已发布的全部变体。头像由 Minecraft 原生物品渲染器按整数 2× 清晰绘制；宠物、皮肤、经验和配件文本完整测量，粗体也不会溢出或省略。“当前等级经验”和“到满级进度”默认开启；满级只隐藏后者，不会隐藏宠物配件。通过 Pets 菜单、Tab 或已收到聊天确认的配件会按宠物保存在本地，重登后继续显示。皮肤名称和 Ancient Golden Dragon 装饰溢出等级默认开启。内置当前 87 种宠物配件资源，可选“图标＋名称”（默认）、“仅图标”或“仅名称”。

### 聊天

- **聊天偷窥**：按住用户设置的按键或组合键，在不打开聊天界面的情况下临时显示完整高度的聊天历史。偷窥时鼠标滚轮默认翻聊天记录；二级设置可改为继续切换快捷栏。为避免按键冲突，偷窥键默认未绑定。

### HUD 外观

- 左键点击功能卡片即可启用或关闭，左侧蓝条是唯一的启用状态提示；右键仍会进入该功能的完整二级设置页，但卡片不再重复显示右键提示
- 每个 HUD 分别保存背景透明度/颜色、边框开关/宽度/颜色、标题颜色、粗体与文字阴影
- 所有可编辑颜色统一使用带色轮、亮度、R/G/B 滑条和预设色的颜色选择器；每一个背景颜色都额外提供“透明”选项
- 每个 HUD 分别以 50–200% 缩放；在编辑器中拖住边框或四角即可像桌面窗口一样改变大小
- 左下角“编辑 HUD”会打开只包含当前位置/状态下实际已加载 HUD 的编辑器；拖动改位，点右下角小齿轮进入专项设置
- 松开鼠标时立即保存该 HUD 的位置和缩放，重启游戏后继续沿用
- 界面打开动画默认开启，也可关闭
- 安装 Mod Menu 后，可从 Mod Menu 直接进入 QCA 设置

设置页采用受 BLC 信息层级启发、但没有复制其素材或界面代码的紧凑结构：顶部只保留“功能”，左侧第一个分类为“通用”，其中放“界面动画”、预警音效总开关和手动重连开关；HUD 位置继续从左下角“编辑 HUD”进入。功能卡片不再重复绘制右上角开关和右下角右键提示，二级设置也不再重复一级功能开关。侧栏没有“全部”分类，每项功能只出现在自己的分类下。

背包工具还包括：默认关闭的 **Storage 覆盖**；Storage 页面与玩家背包使用当前材质包的 `generic_54.png`，鼠标停在物品上也可滚动，玩家背包加大显示；锁定物品只在左上角画小星标，不再覆盖颜色。Storage 缓存落盘前会把物品重新绑定到当前世界注册表；即使某个旧缓存物品仍含失效的动态注册表组件，也只会把该槽位隔离为空，不会再让渲染线程崩溃或丢弃其他页面。所有 QCA 热键都直接在原有二级设置行内进入等待输入，不再跳转到独立捕获菜单；支持键盘、鼠标 1–5/侧键以及 Ctrl、Shift、Alt、Cmd/Super 组合，等待输入时按 `Esc` 会像原版一样清空绑定。菜单中键替代整体默认关闭；首次开启时只把玩家真实左键物品按钮转换为中键，右键保持原样，也可在二级设置中改模式。

**AOTE/AOTV 传送声音**不会默认静音。普通传送与 Etherwarp 分别默认保留原声，也可独立改成紫颂果传送、末影人传送、紫水晶清响、经验球、末地传送门填充或潜影贝传送；自定义音量使用 10–200% 滑条，音调使用 50–200% 滑条。HUD 透明度/缩放、Storage 高度/滚动速度/间距/边距和光标记忆时间等跨度大的数值也统一使用 Windows 风格拖动条，松开即保存；少量离散档位仍保留按钮切换。

## 安装

1. 安装 Minecraft 26.1.2、Fabric Loader 0.19.3 或更新版本、Fabric API 0.155.2+26.1.2 或更新版本，以及 Java 25。
2. 将 `QCloudy_Addition-*.jar` 发布文件放入实例的 `mods` 文件夹；Mod Menu 为可选依赖。
3. 启动游戏后按 `O` 或输入任一本地设置命令进行配置。

## 从源码构建

安装 JDK 25 后运行 `./gradlew clean build`。项目已包含独立且固定为 Gradle 9.6.1 的 Wrapper，并固定 Fabric Loom 1.17.17；参考模组不是构建或运行依赖。宠物 Profile 元数据由本地 NEU item-repo 快照离线生成并直接打包进 QCA；发布版运行时不会联网，也不需要 Firmament。

## 安全边界

发布版不包含 `sendChat`、Hypixel Mod API 订阅、WebSocket、HTTP 请求、宏、自动移动或区块请求代码。普通 HUD 只读取客户端已收到的数据。存储控件只会在玩家真实选择页面后发送 `storage`、`enderchest <1-9>` 或 `backpack <1-18>`。永久可用的本地 `/th` 与 `/helia` 只会在玩家输入时分别发送准确内容 `warp torrhus` 与 `chapter torrhus`，等同手动输入 `/warp torrhus` 与 `/chapter torrhus`。没有真实用户输入时，模组不会生成命令、聊天、点击或移动。

Hypixel 明确说明所有模组均由玩家自行承担使用风险，未明确列出的功能也不代表获得许可。使用前请阅读 [docs/COMPLIANCE_zh_CN.md](docs/COMPLIANCE_zh_CN.md) 和最新的 [Hypixel Allowed Modifications 说明](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications)。

完整功能说明：[docs/FEATURES_zh_CN.md](docs/FEATURES_zh_CN.md)

功能实现与数据流：[docs/IMPLEMENTATION_zh_CN.md](docs/IMPLEMENTATION_zh_CN.md)

Modrinth 中文发布描述：[docs/MODRINTH_DESCRIPTION_zh_CN.md](docs/MODRINTH_DESCRIPTION_zh_CN.md)

GitHub 1.5.1 发布说明：[docs/GITHUB_RELEASE_1.5.1_zh_CN.md](docs/GITHUB_RELEASE_1.5.1_zh_CN.md)

发布检查清单：[docs/PUBLISHING_CHECKLIST_zh_CN.md](docs/PUBLISHING_CHECKLIST_zh_CN.md)

更新日志：[CHANGELOG_zh_CN.md](CHANGELOG_zh_CN.md)

发布验收报告：[docs/VALIDATION_zh_CN.md](docs/VALIDATION_zh_CN.md)

2026-08-04 崩溃分析：[docs/CRASH_ANALYSIS_2026-08-04_zh_CN.md](docs/CRASH_ANALYSIS_2026-08-04_zh_CN.md)

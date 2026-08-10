# QCloudy_Addition

QCloudy_Addition 是一个面向 Minecraft 26.1.2 Hypixel SkyBlock 的纯客户端 Fabric 模组。它把清晰的挖矿地图、任务 HUD、Torrhus 与 Critter Safari 辅助、宠物信息、菜单便捷功能和可自定义视觉提示整合进一套中英双语界面。

界面默认英文，可在 QCA 设置中切换简体中文。Hypixel 返回的地点、物品、宠物、任务、皮肤、配件和玩家自定义 HOTM 槽位名称始终保留原文，避免错误翻译。

## 主要功能

- 单层简洁 Dwarven Mines 地图与实时玩家箭头
- 按高度切换的三层 Glacite Tunnels 地图
- 挖矿任务、三种 Powder 与当前 HOTM 槽位追踪
- Crimson Isle Faction Quest 追踪
- Torrhus Chapter、资源、Miria Contest、Benefactor 和 Tree Critter 综合 HUD
- 严格确认本人归属、可逐物品控制的稀有 Tree Gift 提醒
- Beeheemoth 高亮、生成光柱和独立空间声音音量
- Critter Safari Dashboard、Critterdex、Cold/篝火、Wumpa、Warden、Sparkling、Floor Drop、Quest Item、Snoozle 墙体与 Safari Belt 辅助
- 使用真实宠物/皮肤头颅的 Pet HUD，支持品质色名称、经验、200级溢出等级、皮肤名与宠物用品图标/名称
- 可自定义颜色的 Ender Dragon 高亮
- 完全离线的 320-Shard Attribute Fusion Guide，支持专属图标、语义配色详情、完整已记录获取方式、Recipes/Uses（包括自然＋Fusion 双来源 Shard）、有序输入、紧凑卡片与本地 `/qshard` 搜索
- 物品时间戳、光标位置记忆、AOTE/AOTV 声音自定义和聊天偷窥
- 支持键盘、鼠标按键和组合键的 Chat Peek
- BLC 风格的信息层级、搜索和独立 HUD 拖动/缩放界面
- 不含自动重试循环的手动重新连接按钮

## HUD 与提示

每个 HUD 都会单独保存位置和 50–200% 缩放。背景颜色/透明度、边框颜色/宽度、标题颜色、粗体和文字阴影均可独立调整；RGB/HSV 颜色选择器包含常用预设及透明背景。HUD 编辑器只显示当前地点或状态下实际加载的 HUD。

预警统一显示为屏幕中央大字。每种预警都有自己的音效开关和 0–100% 音量滑条，默认音量64%；“通用”中另有总静音。

## 安装

需要：

- Minecraft 26.1.2
- Fabric Loader 0.19.3或更高版本
- Fabric API 0.155.2+26.1.2或更高版本
- Java 25
- Mod Menu可选

把 QCloudy_Addition JAR 放入实例的 `mods` 文件夹。默认按 `O`、通过 Mod Menu，或输入 `/aca`、`/qca`、`/ca`、`/qc` 打开设置。这四个都是本地客户端命令，只有在没有其他客户端模组占用名称时才注册。

## 客户端与安全边界

QCA 不包含运行时网络服务、遥测、远程更新、Hypixel API依赖、宏、自动移动、自动战斗、自动捕捉或隐藏服务器数据请求。追踪功能只读取客户端已经收到的 Tab、计分板、聊天、标题、已打开菜单、本地物品栏、已加载实体和已加载方块。

模组中唯一的服务器命令载荷是 `/th` 对应的 `warp torrhus`，以及 `/helia` 对应的 `chapter torrhus`，并且都只在玩家明确输入本地快捷命令时触发。QCA 没有 `sendChat` 调用。手动重连只会在玩家点击按钮后发起一次普通 Minecraft 连接，没有定时或后台重试。

所有 Minecraft 模组都由玩家自行承担使用风险。实体高亮、信标路径、墙体覆盖和路线预测都是被动客户端渲染，但“被动”不等于获得 Hypixel 官方批准。请阅读最新规则，并关闭任何你不愿承担风险的功能。

## 兼容性

QCA 可以独立运行，不依赖 Firmament、SkyHanni、Skyblocker 或 BabyzombieAddons。它已在同时包含四个参考模组与 Mod Menu 的本地94-mod实例中完成初始化。安装 Firmament 时可以选择让出重复的物品栏功能。

QCloudy_Addition 与 Hypixel Studios、Mojang Studios 没有从属或官方认可关系。

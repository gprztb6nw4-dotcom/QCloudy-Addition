# QCloudy_Addition Beta 2.7.17（Minecraft 26.1.2 / 26.2）

Beta 2.7.17 整合了 Beta 2.6.6 之后已完成的更新。QCloudy_Addition 仍是可独立运行的纯客户端 Fabric 模组；请安装与 Minecraft 版本完全匹配的 JAR。

## 距离 2.6.6 的主要新功能

### Shard Planner 与本地仓库

- 输入目标 Shard 和数量，生成完整多步 Fusion Tree。
- 查看其他直接候选配方，或切换 Materials Only 只显示材料总数。
- 根据可编辑 Shards/hour 计算最快路线。
- 可选从兼容 Skyblocker 公开缓存价格 API 计算最便宜路线；QCA 自身不下载 Bazaar 价格。
- Ironman 模式完全不使用 Bazaar 数据。
- 可设置 Kraken/Kuudra Tier、通关时间、coins/hour、Hunter Fortune、Crocodile 等级和 Fusion 操作时间。
- 仅在玩家亲自打开 Hunting Box 页面时记录按 Profile 保存的本地 Shard 仓库。
- 查看 Shard 效果、家族、Skill、获取方式、速度、Recipes/Uses 和可拖动 Fusion Lines。

### 统一 SkyBlock 模组控制

- 新增以功能为中心的设置目录，覆盖 QCA 及已审核的 SkyHanni 7.41.0、Skyblocker 6.8.2、Firmament 44.3.0、BabyZombieAddons 3.4.1。
- 完全等价功能可选择唯一提供方；开启共享卡片时只关闭其他已检测模组中的等价实现。
- 已审核的布尔、枚举、有边界数值、HUD 位置和缩放通过提供方自己的运行时配置/保存路径读写。
- 已启用的提供方 HUD 可进入 QCA HUD 编辑器，鼠标松开时才写回位置/缩放。
- 所有集成均为可选、锁定已审核版本、基于反射且失败时安全关闭；不安装任何提供方模组时 QCA 仍可独立运行。

### 钓鱼上钩提示音

- 新增默认关闭的本地 Ciallo 上钩提示音，独立 0–100% 音量，默认 64%。
- 支持直接归属的水钓鱼钩与 Hypixel 有限关联的 owner 缺失岩浆鱼钩。
- 必须检测鱼钩附近精确 `!!!` 标记，每次确认上钩最多播放一次。
- “钓鱼”已成为独立一级设置分类。

### Minecraft 双版本

- QCA 自身功能现在分别构建为 Minecraft 26.1.2 与 26.2。
- 每个目标使用完全匹配的 Fabric API 与可选 Mod Menu 版本。
- 第三方设置/HUD 适配目前仅审核 26.1.2；26.2 中会主动安全关闭，直到对应提供方版本通过审核。

## 改进

- 重做 Shard 详情、Planner、Fusion Lines、Settings、RGB 选择器、功能二级页和 HUD 编辑器的窄屏布局，支持长中英文本、独立滚动、安全裁剪与可见点击区。
- 替换矮人矿洞地图图像，玩家箭头改为整张单层总览上连续、大致的仅 X/Z 投影。
- 将 `C&C Minecarts Co.` 加入矮人矿洞识别。

## Bug 修复

- 修复岩浆钓鱼不播放提示音，以及收杆时重复播放。
- 修复属于本人的 Tree Gift 生物行（如 `A wild Groundhog appeared!`）被丢弃，且不允许附近其他玩家的公共行误触发。
- 修复未满级、佩戴 Ancient 皮肤的 Golden Dragon 被显示为 `[Lvl 200]`。
- 修复紧凑尺寸下 Shard、Planner、Settings、Fusion Lines、RGB、功能设置与 HUD 编辑器的重叠和隐形点击区。
- 修复 The Mist 上方桥梁等垂直重叠区域中矮人矿洞箭头跳区/消失；Y 与计分板子地点不再影响箭头。

## 替换的逻辑

- 删除旧的矮人矿洞计分板子地点吸附和分区内裁剪，以连续实时 X/Z 投影取代。本 Beta 没有删除 Beta 2.6.6 中的用户功能。

## 安全与兼容性

- 纯客户端 Fabric 模组，需要 Java 25 与 Fabric Loader 0.19.3+。
- MC 26.1.2 需要 Fabric API `0.155.2+26.1.2`；MC 26.2 需要 `0.154.2+26.2`。
- Mod Menu 可选；SkyHanni、Skyblocker、Firmament、BabyZombieAddons 与 JEI 均不是必需依赖。
- 没有新增自动点击、Fusion、抛竿/收竿、移动、战斗、抓捕、数据包、聊天、命令、HTTP 请求、遥测或隐藏服务器数据请求。
- `/th` 和 `/helia` 仍是合规文档中明确写出的玩家手动快捷命令。
- 自动化构建/静态检查不等于完成 Hypixel 登录实服、所有材质包、所有 GUI Scale 和完整模组包回归。使用任何模组都需要玩家根据 Hypixel 当前规则自行承担风险。

## 文件

- MC 26.1.2 可运行：`QCloudy_Addition-Beta-2.7.17+26.1.2.jar`
- MC 26.1.2 源码：`QCloudy_Addition-Beta-2.7.17+26.1.2-sources.jar`
- MC 26.2 可运行：`QCloudy_Addition-Beta-2.7.17+26.2.jar`
- MC 26.2 源码：`QCloudy_Addition-Beta-2.7.17+26.2-sources.jar`

普通玩家只需安装与 Minecraft 版本完全匹配的可运行 JAR。Sources JAR 仅用于查看源码和开发。

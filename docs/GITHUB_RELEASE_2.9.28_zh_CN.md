# QCloudy_Addition Beta 2.9.28（Minecraft 26.1.2 与 26.2）

Beta 2.9.28 汇总了上一个 Beta 2.8.17 之后 Alpha 2.8.18–2.8.28 完成的内容，是面向 Minecraft 26.1.2 与 26.2 的纯客户端 Fabric 构建。

## 主要变化

- 加入针对已安装 SkyHanni、Skyblocker、Firmament、BabyZombieAddons 与 Feesh 的可选统一设置/HUD 探测。设置与 HUD 扫描仍由两个独立且默认关闭的总开关控制，扫描前必须确认，会显示进度，并对未知提供方分支失败关闭。
- 加入 Power Orb 与 Warning/Alert/SOS Flare 消失提醒。Power Orb 使用本人精确聊天，Flare 必须确认成功放置后才开始本地三分钟生命周期；刻意忽略距离、增益范围与实体卸载。
- 加入全部 20 种 Century Cake 的过期追踪、`/cake`、`/centurycakeeffect`、真实世界 48 小时倒计时、蛋糕图标、统一过期提醒，以及仅点击后执行的 `/visit northwestcloudy` 续效果链接。
- 修复 The Park 的 `Jungle Island` 被当作 Crystal Hollows `Jungle`、Dungeons 等空分类仍显示，以及“钓鱼 → 钓鱼”重复分组的问题。
- 修复 Century Cake 首次生效与刷新追踪。Starborn Century Cake 现在识别 Hypixel 精确的 `Hunting Fortune` 消息，匹配到生效/刷新后不再保持灰色。

## 安全与兼容

- QCA 仍可独立运行且为纯客户端模组；五个提供方模组均为可选，不是构建或运行依赖。
- 能力探测只在确认后读取已安装客户端类与本地配置，不会联系服务器或外部 API。
- 只有玩家点击 Century Cake 消息中带下划线的续效果文字后，才会发送 `/visit northwestcloudy`；不会自动发送命令。
- Minecraft 26.2 中，如果没有兼容的提供方构建，对应第三方分支会被省略；QCA 自身功能不受影响。

## 下载文件

可运行模组：

- `QCloudy_Addition-Beta-2.9.28+26.1.2.jar`
- `QCloudy_Addition-Beta-2.9.28+26.2.jar`

开发者源码：

- `QCloudy_Addition-Beta-2.9.28+26.1.2-sources.jar`
- `QCloudy_Addition-Beta-2.9.28+26.2-sources.jar`

只安装与 Minecraft 版本匹配的可运行 JAR；不要把 `-sources.jar` 放入 `mods` 作为模组运行。

这是 Beta 预发布版本。自动测试与归档检查会完成，但 `docs/VALIDATION_zh_CN.md` 中列出的 Hypixel 实服及完整提供方模组组合仍需要人工回归。

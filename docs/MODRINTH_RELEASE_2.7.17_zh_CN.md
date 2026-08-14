# Beta 2.7.17——Planner、统一控制、钓鱼、双版本与修复

本 Beta 整合 Beta 2.6.6 之后已完成的全部变化。

## 新增

- 完整本地 Shard Planner：多步 Fusion Tree、候选路线、Materials Only、可编辑获取速度、最快/最便宜路线、Ironman、Kraken/Kuudra 参数、Fusion Lines 与仅从玩家打开页面记录的 Hunting Box 仓库。
- 可选通过兼容 Skyblocker 公开 API 读取已缓存 Bazaar 价格；没有提供方时仅价格路线不可用，离线/速度路线仍正常。
- 面向精确审核 SkyHanni、Skyblocker、Firmament 与 BabyZombieAddons 版本的功能中心统一设置/HUD 控制；集成可选且失败时安全关闭。
- 默认关闭的 Ciallo 钓鱼上钩提示音，独立 0–100% 音量（默认 64%），支持水钓/岩浆钓鱼，每次上钩最多一次。
- Minecraft 26.1.2 与 26.2 分别构建。

## 改进与修复

- Shard/Planner/Settings/Fusion Lines/RGB/HUD 编辑器响应式布局，完整支持滚动、裁剪、换行与可见点击区。
- 修复 Tree Gift 生物提示、未满级 Ancient Golden Dragon 等级、岩浆钓鱼检测和收杆重复播放。
- 替换矮人矿洞背景，使用连续、大致的仅 X/Z 投影修复 The Mist 上方桥梁同步问题。Y 和计分板子地点无法改变箭头。

## 兼容性

- 安装与 Minecraft 版本完全匹配的可运行 JAR；不要把 Sources JAR 当作模组安装。
- 需要 Java 25、Fabric Loader 0.19.3+ 和对应 Fabric API。
- 第三方设置/HUD 适配只审核了 26.1.2，26.2 中会安全保持不可用。
- 可独立运行的纯客户端模组；没有新增自动点击、Fusion、钓鱼动作、移动、数据包、聊天、命令、HTTP 请求或游戏自动化。

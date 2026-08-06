# QCloudy_Addition 1.5.1 — Minecraft 26.1.2

这是 QCloudy_Addition 第一份达到公开发布准备状态的版本。它是纯客户端 Fabric 模组，重点为清晰的 SkyBlock HUD、被动视觉辅助和可深度自定义的物品栏/界面功能。

## 运行要求

- Minecraft 26.1.2
- Fabric Loader 0.19.3+
- Fabric API 0.155.2+26.1.2+
- Java 25
- Mod Menu 18.0.0可选

## 版本重点

- 新增原创Dwarven Mines地图与按高度切换的Glacite Tunnels地图。
- 新增Mining、Crimson Isle、Torrhus、Hunting和Critter Safari追踪器。
- 新增完整可配置Pet HUD，并修复动态宠物皮肤与头颅显示。
- 新增受BLC信息层级启发的设置、每HUD拖动/缩放、RGB/HSV颜色、透明背景、滑条、动画及键盘/鼠标组合键行内编辑。
- 独立实现槽位锁定、时间戳、Storage覆盖和光标记忆，不依赖Firmament。
- 新增Ender Dragon、Critter、Sparkling和Beeheemoth高亮。
- 新增Wumpa前置、Ravager身体路线预测、Snoozle墙面、Cold/篝火、Warden就绪、Lasso REEL音效及Fairy Soul。
- 新增严格本人归属的Tree Gift稀有奖励解析，支持逐物品开关、聊天压缩兼容和附近玩家排除。
- 新增默认开启、64%的Beeheemoth声音控制，不改变普通Bee声音。
- 新增由玩家点击触发、无自动重试循环的重新连接按钮。
- 新增 `/th` → `warp torrhus` 与 `/helia` → `chapter torrhus` 客户端快捷命令。

## 重要默认值

- 默认英文，可切换简体中文。
- 每项预警声音默认开启并设为64%。
- Wumpa路线预测与Fairy Soul总开关默认关闭。
- Storage覆盖与菜单中键转换默认关闭。
- AOTE/AOTV声音保持原版，除非玩家主动选择替代音效。
- Chat Peek功能开启，但按住热键默认未绑定。

## 验证

- 23个JUnit套件共98项测试全部通过。
- 两次Java 25 clean build生成逐字节相同的二进制与Sources JAR。
- 两个归档均通过 `jar --validate` 与 `unzip -t`。
- Fabric元数据确认Minecraft 26.1.2、Java 25和client-only。
- 独立51模块启动完成。
- 包含BabyzombieAddons 3.4.1、SkyHanni 7.41.0、Skyblocker 6.8.2、Firmament 44.3.0与Mod Menu 18.0的94-mod实例完成资源及声音引擎初始化，没有QCA或QCA Mixin异常。

本地实例没有登录Hypixel，因此实服Torrhus/Safari消息变体、精确实体时机、所有GUI Scale、玩家材质包与操作手感仍需玩家回归。精确边界见 `docs/VALIDATION_zh_CN.md`。

## 文件

- `QCloudy_Addition-1.5.1+26.1.2.jar`
  - SHA-256：`e3d3131d4f1d40e7859b655aed56aa72ef9a5dae2bd045710d4bde9daf705536`
- `QCloudy_Addition-1.5.1+26.1.2-sources.jar`
  - SHA-256：`ab825c382b6f672cfc6ce2381db0a904ea60b23e593fa5254bd7e87722442ada`

## 安全提醒

QCA是被动纯客户端模组，但这不等于Hypixel官方批准。使用前请阅读 `docs/COMPLIANCE_zh_CN.md` 与Hypixel最新规则；所有玩家主动触发的命令载荷都已在合规文档中列明。

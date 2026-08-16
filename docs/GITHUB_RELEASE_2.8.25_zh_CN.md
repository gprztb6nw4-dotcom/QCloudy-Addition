# QCloudy_Addition Alpha 2.8.25（Minecraft 26.1.2）

Alpha 2.8.25 加入完整的纯客户端 Century Cake 到期追踪，并保留上一 Alpha 中加入的 Power Orb/Flare 到期提醒。

## 新增

- 一个默认开启的 **Century Cake 效果过期提醒**总开关统一管理全部 20 种蛋糕加成；不存在分效果开关。
- 使用绝对时间保存真实世界 48 小时倒计时，玩家离线期间也继续计算。
- `/cake` 与 `/centurycakeeffect` 均为本地命令，打开仿 `/effects` 的计时界面，显示蛋糕头像、加成、品质、状态和剩余时间。
- 中央大字与本地聊天提醒；同一次检查发现多个效果到期时会合并为一条数量提醒。
- 带下划线的 `Click Here For Cake Eating` 聊天操作；只有玩家点击后才执行精确 `/visit northwestcloudy`。
- 独立本地提示音默认开启，音量 64%。

## 保留与改进

- Power Orb 与 Flare 到期提醒继续使用精确收到的 despawn 聊天行进行纯客户端判断。
- 提醒设置继续归属于各自功能，不建立共享的分效果列表。
- 全部倒计时与保存状态均为本地、按 Profile 隔离。

## 安全边界

QCA 绝不会自动发送蛋糕续效果命令。`/cake` 与 `/centurycakeeffect` 没有服务器载荷；只有直接点击带下划线的续效果聊天 Component 才会执行 `/visit northwestcloudy`。此功能不会自动吃蛋糕、点击菜单、移动玩家或查询服务器。

## 安装

- 可运行模组为 `QCloudy_Addition-Alpha-2.8.25+26.1.2.jar`。
- 需要 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2 与 Java 25。
- 不要把 `-sources.jar` 当作可运行模组安装。
- 这是 Alpha，应标记为 Pre-release。

自动构建与归档结果记录在 [VALIDATION_zh_CN.md](VALIDATION_zh_CN.md)。Hypixel 实际文字和最终游戏内界面/聊天效果仍需要正常的实服回归测试。

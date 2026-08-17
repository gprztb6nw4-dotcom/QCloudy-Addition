# QCloudy_Addition Alpha 2.8.24（Minecraft 26.1.2）

Alpha 2.8.24 首次加入纯客户端 Deployable 到期提醒。其中最初假定 Flare 会发送消失聊天的实现并不完整，已在 Alpha 2.8.26 中由确认后的本地 Flare 生命周期彻底替代。

## 新增

- 在 **战斗 → Deployables** 中加入第一版 **Deployable 到期提醒**。
- 精确支持 Radiant、Mana Flux、Overflux、Plasmaflux Power Orb 的消失聊天。
- 此历史版本原有的 Flare 聊天分支不是可靠的 Flare 实现；当前版本已彻底删除。
- 确认到期后会在屏幕中央以红色大字显示 `<Deployable Name> Despawned!!!`。
- 提醒拥有独立本地音效开关与连续 0–100% 音量滑条；默认开启，音量默认 64%。

## 安全边界

- Power Orb 只有精确匹配玩家本人 `Your <Power Orb> despawned.` 的聊天行才会触发。
- 不发送聊天、命令、数据包、交互或网络请求。
- QCloudy_Addition 仍然是纯客户端 Fabric 模组。

## 文件

- 可运行模组为 `QCloudy_Addition-Alpha-2.8.24+26.1.2.jar`。
- `-sources.jar` 只用于查看源码与开发，不能作为可运行模组安装。

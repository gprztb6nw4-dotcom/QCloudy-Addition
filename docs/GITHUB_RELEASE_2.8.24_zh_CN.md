# QCloudy_Addition Alpha 2.8.24（Minecraft 26.1.2）

Alpha 2.8.24 为玩家本人的 Power Orb 与 Flare 加入纯客户端到期提醒。

## 新增

- 在 **战斗 → Deployables** 中加入 **Deployable 到期提醒**。
- 精确支持 Radiant、Mana Flux、Overflux、Plasmaflux Power Orb，以及 Warning、Alert、SOS Flare 的消失消息。
- 收到对应消息后，在屏幕中央以红色大字显示 `<Deployable Name> Despawned!!!`。
- 提醒拥有独立本地音效开关与连续 0–100% 音量滑条；默认开启，音量默认 64%。

## 安全边界

- 只有精确匹配玩家本人 `Your <白名单 Deployable> despawned.` 的聊天行才会触发。
- 不发送聊天、命令、数据包、交互或网络请求。
- QCloudy_Addition 仍然是纯客户端 Fabric 模组。

## 文件

- 可运行模组为 `QCloudy_Addition-Alpha-2.8.24+26.1.2.jar`。
- `-sources.jar` 只用于查看源码与开发，不能作为可运行模组安装。

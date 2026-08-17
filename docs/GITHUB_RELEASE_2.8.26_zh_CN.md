# QCloudy_Addition Alpha 2.8.26（Minecraft 26.1.2）

Alpha 2.8.26 删除不完整的 Flare 聊天假设，改为确认后的纯客户端生命周期，并把每一项 Deployable 提醒设置写清楚。

## 修复

- **Power Orb 与 SOS 消失提醒**现在使用两套相互独立、来源明确的判定。
- Radiant、Mana Flux、Overflux、Plasmaflux Power Orb 必须收到玩家本人精确的 `Your <Power Orb> despawned.` 聊天行。
- Warning、Alert、SOS Flare 只有在本地玩家使用精确 Flare 道具，并且客户端紧接着收到匹配的成功放置音效后，才开始计时。
- 放置失败或冷却中误点不会启动计时；成功确认的新 Flare 会静默替换旧记录。
- 换世界、换服务器和断线会静默清理状态；实体卸载、渲染距离、玩家距离和增益范围都不会触发或屏蔽到期提醒。
- 每个确认后的三分钟 Flare 生命周期结束时，最多显示一次 `<Flare Name> Despawned!!!`。

## 设置

- Power Orb 与 Flare 提醒分别开关。
- 屏幕中央大字与本地提示音分别开关。
- 0–100% 连续音量滑条；音效默认开启，音量默认 64%。

## 安全边界

本功能只读取客户端已经收到的聊天、本地玩家的精确道具使用、收到的成功放置音效和本地单调时间。不会发送聊天、命令、数据包、交互或网络请求，也不会根据距离或实体卸载猜测消失。

## 安装

- 可运行模组使用 `QCloudy_Addition-Alpha-2.8.26+26.1.2.jar`。
- 需要 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2 与 Java 25。
- `-sources.jar` 只用于源码查看，不能作为可运行模组安装。
- 这是 Alpha 构建，发布时应标记为预发布。

自动检查不能代替登录 Hypixel 后的自然到期测试。扩大分发前，应分别验证一种 Power Orb 和每一级 Flare 的完整生命周期。

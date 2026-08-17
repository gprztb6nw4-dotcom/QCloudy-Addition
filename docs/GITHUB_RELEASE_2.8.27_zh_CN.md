# QCloudy_Addition Alpha 2.8.27（Minecraft 26.1.2）

Alpha 2.8.27 修复新吃下 Century Cake 后，QCA 效果菜单仍显示“未生效”的问题。

## 修复

- 识别 Hypixel 真实首次生效消息：`Yum! You gain <加成> for 48 hours!`。
- 保留真实刷新消息：`Big Yum! You refresh <加成> for 48 hours!`。
- 正确规范化 Starborn Century Cake 的 `+1 Hunter Fortune` 消息中附带的私用区属性图标。
- 对虚构或含糊的消息组合保持安全拒绝，避免错误启动计时。

原有 Century Cake 到期行为、Power Orb/Flare 提醒、设置与对外操作边界均未改变。

## 验证

- 仅 Minecraft 26.1.2；Java 25；纯客户端 Fabric 模组。
- 37 个 suite、191 项测试全部通过，0 failure、0 error、0 skip。
- 可运行与 Sources JAR 均通过归档验证，并分别与 release 副本逐字节一致。

可运行模组使用 `QCloudy_Addition-Alpha-2.8.27+26.1.2.jar`，不要把 `-sources.jar` 当作模组安装。

旧版 QCA 已经漏掉的首次生效消息无法从历史聊天恢复；需要在 2.8.27 运行时重新吃一次或刷新该蛋糕。

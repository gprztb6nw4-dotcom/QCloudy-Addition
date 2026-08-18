# QCloudy_Addition Alpha 2.8.28（Minecraft 26.1.2）

Alpha 2.8.28 修复 Starborn Century Cake 刷新后，在 QCA 中仍显示为未生效的问题。

## 修复

- 将 Starborn Century Cake 的元数据从错误的 `Hunter Fortune` 改为 Hypixel 实际使用的 `Hunting Fortune`。
- 识别精确客户端消息 `Big Yum! You refresh +1<属性图标> Hunting Fortune for 48 hours!`。
- 保留精确首次生效格式 `Yum! You gain ... for 48 hours!`。
- 匹配前会清理私用区属性图标，同时拒绝旧的错误拼写 `Hunter Fortune`。
- Century Cake 效果界面和悬浮提示现在显示 `+1 Hunting Fortune`。

原有 Century Cake 到期提醒、Power Orb/SOS 提醒、设置和对外操作边界均未改变。

## 安装

可运行模组使用 `QCloudy_Addition-Alpha-2.8.28+26.1.2.jar`，不要把 `-sources.jar` 当作模组安装。

旧版本漏掉的聊天事件无法从历史聊天恢复。请在 2.8.28 运行时再次刷新或食用 Starborn Century Cake。

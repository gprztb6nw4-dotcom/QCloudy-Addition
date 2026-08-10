# QCloudy_Addition Beta 2.6.7（Minecraft 26.1.2）

Beta 2.6.7 替换并重新校准 Dwarven Mines 地图，同时保留 Beta 2.6.6 的完整功能集。

## 调整

- 将 Dwarven Mines 纹理替换为本次提供的单层 12 区域地图。
- 按替换图重新校准 Village、Upper Mines、Rampart Quarry、Forge、Lava Springs、Cliffside、Far Reserve、Goblin Burrows、Royal Mines、The Mist、Ice Wall 和 Royal Palace。
- Dwarven 玩家箭头现在只使用本地 X/Z、朝向和计分板已显示的子地点。该地图为单层，因此明确不使用 Y。
- 只显示通用 `Dwarven Mines` 地点时，回退到最近的归一化 X/Z 区域中心，不再使用高度判定。

## 安全与兼容

- 适用于 Minecraft 26.1.2 的纯客户端 Fabric 模组；需要 Java 25。
- 必需 Fabric API `0.155.2+26.1.2` 或更新版本；Mod Menu 可选。
- 地图为随模组打包的本地 PNG，只进行渲染投影；不发送数据包、聊天、命令、点击、移动或其他服务器交互。
- 自动测试与归档检查不等于已完成 Hypixel 登录实服及全部 GUI Scale 的视觉验收。

## 文件

- 可运行：`QCloudy_Addition-Beta-2.6.7+26.1.2.jar`
- 源码：`QCloudy_Addition-Beta-2.6.7+26.1.2-sources.jar`

普通玩家安装可运行 JAR；Sources JAR 只用于查看源码和开发。

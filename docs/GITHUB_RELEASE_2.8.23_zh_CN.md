# QCloudy_Addition Alpha 2.8.23（Minecraft 26.1.2）

Alpha 2.8.23 修复错误岛屿触发，并清理空分类与重复分类名称。

## 修复

- 挖矿任务与粉尘现在只把完整地点名 `Jungle` 识别为 Crystal Hollows。The Park 的 `Jungle Island` 不再误开挖矿 HUD。
- 设置侧栏只显示至少拥有一个可用功能的分类。地牢等没有可用功能的外部分类会直接隐藏，不再打开空白页面。
- 钓鱼下级组更名为**咬钩提示**，不再出现容易误解的“钓鱼 → 钓鱼”。

## 支持文件

- `QCloudy_Addition-Alpha-2.8.23+26.1.2.jar`

Fabric API 仍为必需依赖；Mod Menu 与支持的 SkyBlock 模组仍为可选。

## 验证边界

本版本仍为纯客户端 Alpha。自动测试与归档验证记录在 `docs/VALIDATION_zh_CN.md`；它们不能代替登录 Hypixel 后的游戏内实测，也不能覆盖所有整合包与 GUI Scale 组合。

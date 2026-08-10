# QCloudy_Addition Alpha 2.5.6（Minecraft 26.1.2）

本 Alpha 补全离线 Attribute Shard Fusion Guide 的信息层。QCloudy_Addition 仍是可独立运行的纯客户端 Fabric 模组，不依赖 JEI、Firmament、SkyHanni、Skyblocker 或 BabyzombieAddons。

## Shard 详情与获取方式

- 为当前目录全部 320 个 Shard 加入“详细信息”标签。
- 显示规范化 Wiki 效果、品质、分类、Skill、家族、生物类型和每一种已记录获取方式。
- 捕捉来源保留生物、所需工具和区域；击杀、掉落、Fusion、Tree Gift、商店与宝箱来源只呈现已记录信息，不虚构缺失概率。
- 只能通过 Fusion 获得的 Shard 会单独标明。所有存在已验证 Fusion 配方的 Shard 都显示配方数量，其中包括 Queen Bee 这类同时拥有自然来源和 Fusion 来源的 Shard。

## 颜色与导航修复

- Epic 改用 Minecraft 深紫色（`§5`），不再使用亮紫/粉色（`§d`）。
- 属性、分类、生物类型、Skill、获取方式与品质使用已审核的 SkyBlock/Minecraft 语义颜色。
- 鼠标悬停于可点击 Shard 的可见文字时，文字会变深并添加下划线。
- 修复彩色效果片段之间丢失空格的问题，并清理残留 Wiki 格式标记。

## 安全边界

全部 Shard 数据与图标都离线打包且只读。Guide 运行时不访问 Wiki/API，不点击菜单，不发送数据包、聊天或服务器命令，不选择输出，也不会执行 Fusion。

这是 Alpha 构建；提升为 Beta 或 Release 前仍需完成登录 Hypixel 的实服验证，以及不同材质包/GUI Scale 的视觉验收。

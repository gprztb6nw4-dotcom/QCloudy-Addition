# QCloudy_Addition Alpha 1.5.3

适用于 Minecraft 26.1.2 Fabric。本发布说明总结 Alpha 1.5.1 至 Alpha 1.5.3 的累计变化。

> 此版本仍为 Alpha。

## 新增

- 加入重新设计的 BLC 风格设置界面，左侧按固定顺序保留七个一级分类：通用、地图、物品与菜单、战斗、挖矿、砍树、狩猎。
- 加入可收起/展开的二级功能组，包括 HUD、连接、地图、点位、Torrhus、Galatea、Safari、Crimson Isle、宠物、聊天及物品与菜单工具。
- 加入独立的 Galatea HUD 与设置，用于 Hina Chapter、资源信息和 Agatha's Contest，不再与 Torrhus/Helia 的设置混用。
- 加入可选的 **捕获 Shard 统计**。该功能默认关闭，可在 Safari 的狩猎 HUD 中按地区分类显示本轮捕获的 Shards，并按照品质为 Shard 名称上色。

## 改进

- 将 Fairy Soul 点位统一移入“地图 > 点位”；开启一次即可管理相关点位，不再为不同岛屿重复设置入口。
- Safari 合并进狩猎，宠物合并进物品与菜单，Crimson Isle 任务合并进战斗；每个功能只会出现在一个分类中。
- 当 HUD 没有任何实际可显示内容时，整个面板会自动隐藏，不再残留标题、背景、边框或 HUD 编辑框。
- 重新拆分 Safari HUD：Run Dashboard 只保留运行时间和 Ticket Tier，Critterdex 负责捕获进度，捕获 Shard 统计成为独立且可关闭的功能。
- Safari Critterdex 的地区名称和捕获 Shard 名称分别按照地区及品质颜色显示。
- 在无法佩戴宠物的 Critter Safari 中不再显示宠物 HUD。
- Galatea/Hina Chapter 与 Agatha Contest 使用与 Torrhus Tracker 相同的客户端已接收信息来源，但拥有独立设置。
- Tree Gift 稀有生物检测支持 `-A wild Groundhog appeared!` 一类聊天提示，并保留个人 Tree Gift 归属判断和去重逻辑。
- Beeheemoth 声音调整现在可以处理 Torrhus 与 Safari 中客户端收到的相关蜜蜂声音。
- Safari 生物高亮会排除作为捕获道具载体的 Armor Stand，并按照对应 Shard 品质为真正可捕捉的命名生物上色。
- AOTE/AOTV 替换音效改为 0–100% 音量调节，默认 64%；删除音调调节，使播放结果更稳定。
- Sweep 与 Safari 地区名称使用更清晰、接近游戏原本含义的颜色。

## 修复

- 修复 `Sweep: 952.84, 13.78 logs (-50%) (-50%) -> 5.46 logs` 一类消息会把后面的 logs 数量误识别为 Sweep 的问题；现在只读取紧跟在 `Sweep:` 后面的数值。
- 修复已完成的 Crimson Isle 任务仍出现在 HUD 中的问题；从 Tab 接收到的已完成条目会被过滤。
- 修复狩猎、挖矿、宠物、地图和 Crimson HUD 在所有对应项目关闭或无数据时仍残留空面板的问题。
- 修复个人 Tree Gift 中生成稀有生物后没有提示的问题。
- 修复附近其他玩家的 Tree Gift 或无关 Lasso 捕获消息触发重复/错误个人提示的问题。
- 修复 Galatea Chapter 无法识别 Hina/Galatea 文本上下文的问题。
- 修复 Safari 捕获时 Armor Stand 与真正生物一起被高亮的问题。

## 删除

- 彻底删除槽位/物品锁定，包括配置、输入拦截、渲染逻辑和测试。
- 彻底删除 Storage 覆盖，包括缓存、控制器、界面、配置和测试。
- 彻底删除菜单中键点击转换，包括底层点击 Mixin、配置和测试。
- 删除 AOTE/AOTV 音调设置。

## 兼容性与规则边界

- 纯客户端 Mod，不需要服务器安装。
- 需要 Minecraft 26.1.2、Java 25、Fabric Loader 0.19.3 或更高版本，以及 Fabric API 0.155.2+26.1.2 或更高版本。
- Mod Menu 为可选依赖，只用于提供更方便的设置入口。
- HUD 与追踪器只使用客户端已经收到的聊天、Tab、计分板、可见实体、已打开菜单和本地物品数据。
- 不包含自动移动、自动战斗、自动 Fusion、自动物品栏操作或隐藏服务器数据请求。
- 玩家手动输入 `/th` 与 `/helia` 时，客户端仍会分别发送 `warp torrhus` 与 `chapter torrhus`；不会自动发送。

## 发布文件

上传以下二进制 JAR：

`QCloudy_Addition-alpha-1.5.3-26.1.2.jar`

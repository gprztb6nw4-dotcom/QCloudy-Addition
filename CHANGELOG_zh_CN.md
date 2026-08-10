# 更新日志

## [2.6.7] - 2026-08-10

适用于 Minecraft 26.1.2 的 Beta 地图更新。

### 调整

- 将内置 Dwarven Mines 地图替换为本次提供的单层 12 区域地图。
- 按新图的实际区域位置重新校准全部 Dwarven 地点，使实时玩家箭头与对应区域及局部 X/Z 位置同步。
- Dwarven 地图完全移除 Y 轴判定。现在只使用已收到的子地点、本地 X/Z 和朝向；只显示通用 `Dwarven Mines` 时，使用最近的 X/Z 区域中心回退。
- Beta 补丁版本提升至 `2.6.7`；可运行和源码产物统一使用 `QCloudy_Addition-Beta-2.6.7+26.1.2`。

### 安全边界

- 地图仍为纯客户端、纯渲染；不读取隐藏地形，不发送数据包、聊天、命令、点击、移动或其他服务器交互。

## [2.6.6] - 2026-08-10

适用于 Minecraft 26.1.2 的 Beta 晋级版本。

### 调整

- 将已经审核的 `2.5.6` 功能集从 Alpha 晋级为 Beta，没有新增玩法自动化或服务器交互。
- 按照“Beta 更新第二位版本数字”的项目规则，将版本号更迭为 `2.6.6`。
- 可运行产物统一命名为 `QCloudy_Addition-Beta-2.6.6+26.1.2.jar`，源码产物命名为 `QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`。
- 同步更新 GitHub、Modrinth、实现说明、验证报告和发布清单。
- 按实际七个一级设置分类、当前 Beta 范围、依赖、HUD 自定义与明确的客户端/服务器命令边界，完整重写 Modrinth 项目描述。

### 包含 2.5.x Alpha 开发线内容

- 面向官方 320 种 Shard 的独立离线 Attribute Shard Fusion Guide，包含合成来源、可合成内容、详细信息、有序输入、数量、可选产物、获取方式、语义颜色和专属图标。
- 汇总 Alpha 2.5.4 至 2.5.6 的搜索焦点、紧凑配方布局、Epic 品质颜色、可点击链接、Wiki 格式清理、反向配方及自然来源与 Fusion 来源并存等修复。
- 彻底删除槽位锁定、Storage 覆盖与菜单中键转换。

### 安全边界

- Beta 仍是纯客户端、被动辅助。Shard Guide 运行时不请求 Wiki/API，不发送数据包，不点击物品栏，不执行 Fusion，不发送聊天、服务器命令或自动操作。
- `/th` 与 `/helia` 仍是合规文档中明确披露的玩家主动快捷命令；没有玩家直接输入时不会发送命令。

## [2.5.6] - 2026-08-10

适用于 Minecraft 26.1.2 的 Alpha 更新。

### 新增

- 为 320 个 Shard 全部加入独立的“详细信息”页面，显示 Wiki 当前列出的完整效果、品质/分类/Skill/家族/生物类型，以及每一种已记录获取方式。捕捉来源保留生物、工具与区域；击杀、掉落、Fusion、Tree Gift、商店和宝箱来源保留 Wiki 已提供的细节，不虚构未记录的概率。
- 对所有可由 Fusion 产出的 Shard 显示已验证有序配方数量；Queen Bee 这类同时拥有自然来源和 Fusion 配方的 Shard 也包含在内。只能通过 Fusion 获得的 Shard 会单独标明。

### 修复

- Epic Shard 名称从粉色/亮紫色（`§d`）更正为 Minecraft 的 Epic 深紫色（`§5`）。属性、分类、生物类型、获取方式与品质文字按 SkyBlock/Minecraft 对应语义颜色显示。
- 可点击的 Shard 文字仅在鼠标真正悬停于可见文字时变深并添加下划线，让跳转入口清晰，同时不缩小原有点击范围。
- 保留不同颜色效果片段之间的空格，并清除离线目录中残留的 Wiki 格式标记。

### 调整

- Alpha 版本提升至 `2.5.6`，产物名为 `QCloudy_Addition-alpha-2.5.6-26.1.2.jar`。
- 320-Shard 离线详情目录已按当前 Wiki 品质表修订版及官方 Bazaar 允许列表更新；运行时仍然完全本地、只读。

## [2.5.5] - 2026-08-10

适用于 Minecraft 26.1.2 的 Alpha 更新。

### 修复

- 用 320 个与具体 Shard 对应的内置图标替换通用紫水晶回退。客户端已经收到的原生 Shard `ItemStack` 仍然优先，并保留在本次会话缓存中；存在服务器/材质包原生显示时继续以其为准。
- 点击搜索框外、按 `Esc` 或按 `Tab` 现在都会退出搜索焦点；再次点击搜索框即可重新输入。无需关闭 Guide 即可恢复配方导航与普通界面快捷键。
- 每张配方卡的两个输入与输出集合改为按内容宽度紧凑居中；点击范围跟随实际可见物品，不再分散到卡片两端。

### 调整

- Alpha 版本提升至 `2.5.5`，发布文件名为 `QCloudy_Addition-alpha-2.5.5-26.1.2.jar`。
- 320 个离线图标来自 MIT 许可的 SkyShards `public/shardIcons`，审核 commit 为 `9688031dbc4e726168ffceb0f44884ff26e6e728`；生成时严格按 QCA 的 320-Shard 目录筛选，并排除额外的 Rainbug 资源。

### 安全边界

- Shard 目录、回退图标、物品模型与界面均随模组打包且只读。QCA 运行时不会请求 Wiki、API 或图标，也不会发送聊天、服务器命令、数据包、菜单点击、Fusion 或自动操作。

## [2.5.4] - 2026-08-09

适用于 Minecraft 26.1.2 的 Alpha 更新。

这是 `1.5.3` 最新发布基线之后的 `2.5.x` Alpha 开发线起点。发布通道仍为 Alpha；本次只重编号 1.5.3 之后的版本线。

### 新增

- 在“物品与菜单”加入受 JEI 信息结构启发、完全离线的 Attribute Shard Fusion Guide。
- 支持按原始 Shard 名称、ID、属性、品质、分类、家族和 Skill 搜索；提供 Recipes/Uses 标签、有序输入组合、浏览历史、分页、客户端已观察原生物品图标、输入/输出数量、特殊产量及 Pure Reptile 双倍产出概率。
- 新增本地 `/qshard [英文查询]` 界面命令、设置中的“打开指南”及默认未绑定的键盘/鼠标组合键；三种入口均不发送服务器载荷。

### 调整

- Alpha 版本提升至 `2.5.4`，发布文件名为 `QCloudy_Addition-alpha-2.5.4-26.1.2.jar`。
- Shard 目录改为严格匹配 Hypixel 官方 Bazaar 的 320 项允许列表：包含 Anteater、Zombuddy、Troodon 与 Ghost Crab；Goldolot 为 `R92`；Rainbug 因不在官方 Bazaar Shard 产品集合中而排除。
- 完整保留 Attribute Fusion 输入顺序、最多三个可选输出、Chameleon 数字 ID/品质进位，以及 Wiki 记录的消耗与产量规则。
- 当 ID Fusion 与 Special Fusion 产出同名 Shard 时保留两个独立输出槽，因为玩家可选择的产量仍分别为 `x1` 与 `x2`。

### 删除

- 从实现、配置、测试和当前文档中彻底删除槽位锁定、Storage 覆盖与菜单中键转换。

### 安全边界

- 指南只读取随模组提交的离线 JSON；可选的材质包适配图标只来自客户端已经观察到的 ItemStack。运行时不会请求 Wiki/API/网络，不会点击菜单、执行 Fusion、发送命令/聊天或自动操作。

## [1.5.1] - 2026-08-06

Minecraft 26.1.2首个公开发布候选版本。

### 新增

- Dwarven Mines单层地图与Glacite Tunnels三层地图。
- Mining Commission、三种Powder、HOTM槽位及Crimson Isle任务追踪。
- Torrhus Chapter/资源、Tree Critter、Miria Contest、Benefactor及本人Tree Gift追踪。
- Critter Safari Dashboard/Critterdex、Cold/篝火、Doomspiral、Warden、Sparkling、Floor Drop、Quest Item、Wumpa、Snoozle、Safari Belt与Critter高亮。
- Beeheemoth轮廓、生成光柱与空间声音控制。
- Lasso REEL音效和中央预警系统。
- 支持真实头颅、皮肤、经验、溢出等级及宠物用品的Pet HUD。
- Ender Dragon高亮、Chat Peek、时间戳、光标记忆和自定义传送音效。
- 中英双语设置和每HUD独立编辑器。
- 手动重连按钮、`/th`与`/helia`客户端快捷命令。

### 修复

- 删除会显示错误/模糊宠物图标的旧PNG回退。
- 满级宠物只隐藏多余至满级经验，不再丢失宠物用品。
- 粗体、长任务与宠物文字不再溢出或显示省略号。
- Safari捕捉Armor Stand不再获得Critter轮廓。
- 修复Wumpa队友Loot Share、生成后HUD及Ravager身体路线。
- 修复四项Safari Belt Milestone及按账号/Profile保存。
- 修复Helia Chapter、Benefactor、Whispers、Essence、Forest Fortune和Sweep获取/保存。
- 使用本人归属状态机修复附近玩家及重复Tree Gift提醒。
- 清除最后一个已弃用的已加载区块调用，扫描范围不变。

### 调整

- 项目与原版按键分类统一为QCloudy_Addition / QCloudy Addition。
- 设置分为通用、地图、挖矿、砍树、狩猎、Safari、Crimson Isle、Combat、Pets、Chat与Inventory，每项功能只有一个分类。
- AOTE/AOTV默认保留原声，并提供声音/音量/音调选择。
- 所有预警音量默认统一为64%。

### 删除

- “全部”设置分类。
- Golden Dragon/Dragon's Lair寻找功能。
- 重复开关、右键提示和独立热键捕获菜单。
- Firmament运行依赖与旧宠物PNG选择。

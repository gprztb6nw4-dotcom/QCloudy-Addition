# QCloudy_Addition Alpha 2.6.17 双版本验证

日期：2026-08-13<br>
Minecraft：26.1.2 与 26.2<br>
Java：25

已验证产物：

- `release/QCloudy_Addition-Alpha-2.6.17+26.1.2.jar`
- `release/QCloudy_Addition-Alpha-2.6.17+26.1.2-sources.jar`
- `release/QCloudy_Addition-Alpha-2.6.17+26.2.jar`
- `release/QCloudy_Addition-Alpha-2.6.17+26.2-sources.jar`

26.1.2 可运行 SHA-256：`ca630aaac534f9c03670093289b427bdf0eb378d179d73d981948d8564890037`<br>
26.1.2 Sources SHA-256：`4afee3bab99b63b5f35b61b68efd6a5037b43f0b5c312db3a083f0805a72bffe`<br>
26.2 可运行 SHA-256：`84a20822e23948608a8a99c46a1dba749c2007f07a4c41a9c5a6616a4964f862`<br>
26.2 Sources SHA-256：`89dcf94158beda358916594e0f2cb0c7d6c06e794cbbfabb72c4c80012df9985`

Alpha 2.6.17 保留 26.1.2/26.2 双版本矩阵，并把矮人矿洞箭头从“按地点名称选分区”改为整张背景共用的一套连续大致 X/Z 映射。投影 API 完全没有 Y 与计分板子地点参数，因此位于 Royal Mines/The Mist 上方的 Palace Bridge 等垂直重叠路径不会再导致箭头跨区域跳动。

本工作区已验证：

- Java 25 `clean test build prepareRelease` 在两个目标均成功：每个版本 159 项测试，0 failure、0 error、0 skip。
- 矮人矿洞投影测试覆盖连续单轴移动、The Mist 与其上方桥梁相同 X/Z 得到相同点、代表区域和安全边界裁剪；官方子地点 `C&C Minecarts Co.` 也已归类为矮人矿洞。
- 确定性几何测试覆盖宽/窄 Shard 详情栏、独立速度控件、紧凑 Plan 控件、窄屏 Settings 单列及矮屏安全提示、Recipe 输入框宽度、Fusion Lines 画布扩展、二级设置滑块、RGB 条和预设色块。
- 主代码与测试已从干净输出目录重新编译。
- 每个产物的展开元数据均精确声明目标：26.1.2 使用 Fabric API 0.155.2+26.1.2；26.2 使用 Fabric API 0.154.2+26.2。两者都保持纯客户端并要求 Fabric Loader 0.19.3+ 与 Java 25+。
- `build/libs/` 的可运行与源码产物分别和 `release/` 中对应文件逐字节一致。
- 四个 JAR 均通过 JDK 25 `jar --validate` 与 `unzip -t`；两个目标共用同一套中英文资源。
- 26.2 独立开发客户端已在 Render thread 初始化 QCA，完成资源重载、创建物品/GUI 图集并启动声音引擎，没有 `InjectionError`、`InvalidMixin` 或 QCA 异常。
- 可选集成不产生提供方编译依赖，也未新增 HTTP、数据包、聊天命令、菜单点击、玩法输入或服务器数据请求。
- 对编辑过的配置与物品界面进行静态矩阵/裁剪审查，push/pop 与 enable/disable 均成对。

尚未覆盖的 Alpha 实服边界：

- 几何测试和归档检查不能代替 Minecraft 实际渲染检查。扩大发布前，应在目标整合包使用的每个 GUI Scale 下打开 Shard Planner 六个页签、主设置、二级设置、RGB 选择器和 HUD 编辑器，检查中英文、resize/re-init、长提供方名称、点击区和滚动。
- 四个精确 26.1.2 提供方版本的统一设置集成仍需已登录实服回归；26.2 产物会刻意隐藏这些锁定版本的适配器，直到对应提供方构建完成审核。

---

# QCloudy_Addition Beta 2.6.12 钓鱼提示音与设置分类验证

验证日期：2026-08-11

验证产物：

- `release/QCloudy_Addition-Beta-2.6.12+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.12+26.1.2-sources.jar`

## 结论

Beta 2.6.12 修复玩家收杆时 Ciallo 上钩提示音重复播放的问题。真实钓竿使用路径现在会区分“确认的新抛竿”与“收起仍活动的直接鱼钩或已关联回退鱼钩”；只有新抛竿才会重新打开 `FishingBiteSession` 的播放门。附近精确 `!!!` 条件与每根鱼钩一次的去重保持不变。“钓鱼”现为“砍树”与“狩猎”之间的独立一级设置分类，八分类侧栏在较矮布局下会自适应行距。

## 自动测试与产物检查

- Java 25 `clean test build prepareRelease` 成功；新生成 XML 共 27 个测试套件、149 项测试，0 失败、0 错误、0 跳过。
- 聚焦状态测试确认：已播放鱼钩在收杆使用后仍被拦截；确认的新抛竿会重新待命；仍活动的直接鱼钩会被判定为收杆；活动中的 owner 缺失回退鱼钩不会被误判为另一次抛竿。
- 设置测试确认八个一级分类的精确顺序，并验证最低支持高度下压缩后的全部分类均位于底部控制按钮上方。
- 英文与简体中文资源各有 450 个键，键集合完全一致且 JSON 有效。
- 展开的 Fabric 元数据为 `Beta-2.6.12+26.1.2`、纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+；class major version 为 69。
- 二进制仍恰好包含 320 张 Shard 纹理、320 个 Shard model 定义和 320 个 Shard item 定义。
- 二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 静态检查确认修改后的回调仍原样放行玩家输入，不包含自动抛竿/收杆、取消输入、点击、移动、聊天发送、命令、HTTP 或额外数据包路径。

## 验证边界

本次审核验证编译、已覆盖的抛竿/收杆状态转换、设置归属与顺序、小窗口几何、双语一致性、纯客户端元数据、归档完整性、文件名、校验和及 build/release 一致性；不等同于已登录 Hypixel 完成真实时机回归。扩大公开发布前，应分别实测一次水钓与岩浆钓鱼：两者均应在出现 `!!!` 时只响一次、收杆时保持静默，并在下一次抛竿后正常重新待命。

## SHA-256

- 二进制 JAR：`843787db14501266f0be693be62be6b894998a6b1b2ea6edb3f9daca78fef06b`
- Sources JAR：`8e73bcfb22040584738328df309c63c7fb127061411c236fc119ddbeb442e2d4`

---

# QCloudy_Addition Beta 2.6.11 Shard 规划器验证

验证日期：2026-08-11

验证产物：

- `release/QCloudy_Addition-Beta-2.6.11+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.11+26.1.2-sources.jar`

## 结论

Beta 2.6.11 保留原有 320 种 Shard 的配方指南，并加入完全本地运行的多步规划器：最快/最便宜路线、Fusion Tree、Materials Only 材料总计、其他候选配方、每种 Shard 的每小时获取速度编辑、可拖动 Fusion Lines、Kraken/Kuudra 参数，以及只根据玩家实际打开的 Hunting Box 页面建立的 Profile 独立 Shard 仓库。

普通模式的 Bazaar 计算是可选功能。本版本没有 Bazaar HTTP 客户端；只有已经安装兼容 Skyblocker 时，才会通过其公开的 `ItemUtils.getItemPrice` 接口读取该模组已缓存的价格。SkyHanni 与 Firmament 均不是依赖，也不会通过私有字段读取；没有兼容的公开价格提供方时，基于价格的路线会明确不可用，Ironman 与获取速度路线仍然可以正常使用。

## 自动测试、数据与产物检查

- Java 25 `clean test build prepareRelease` 成功；新生成 XML 共 27 个测试套件、146 项测试，0 失败、0 错误、0 跳过。
- 最终目录包含 320 个唯一 Shard ID 与 320 个唯一 Bazaar ID；320 项获取速度与目录 ID 集合完全一致，所有数值均为有限且非负。
- 二进制包含 320 个按 ID 区分的 item model 与 320 个 Shard 纹理资源；清理构建后没有带编号后缀的陈旧重复资源。
- 英文与简体中文资源各有 449 个键，键集合完全一致且 JSON 有效。
- 展开的 Fabric 元数据为 `Beta-2.6.11+26.1.2`、纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+；class major version 为 69。
- 二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 静态数据流检查确认规划器没有 HTTP 请求、自动 `/hb`、背包点击、Fusion、聊天发送、命令、移动、数据包或隐藏服务器数据请求。仓库解析器只在标题精确为 `Hunting Box` 的界面内接受可见 lore 中精确的 `Owned: N Shard(s)` 格式。

## 验证边界

本次审核验证编译、测试覆盖的规划计算、目录/速度/资源完整性、双语一致性、纯客户端数据流、归档完整性、元数据、文件名、校验和及 build/release 一致性；不等同于已登录 Hypixel 完成真实 Hunting Box 回归，也不代表所有 GUI Scale 都完成视觉验收、未来所有 Skyblocker 价格 API 都必然兼容，或已经完成整个模组包的性能回归。扩大公开发布前，应在真实 Profile 中打开每一个 `/hb` 页面并核对几项仓库数量、将数个多步路线与实际 Fusion 预览交叉核对，并分别在安装和未安装兼容 Skyblocker 时测试普通模式。

## SHA-256

- 二进制 JAR：`12044c22054f9af08038e6569d95e043e013fc47f39621ec4b98b4a531f3a0a2`
- Sources JAR：`21b33ca81ae0d3359591a07c5c82b5805736c1ad2bd5d1e56cb2259aaca32fb2`

---

# QCloudy_Addition Beta 2.6.10 Tree Gift 生物提示验证

验证日期：2026-08-11

验证产物：

- `release/QCloudy_Addition-Beta-2.6.10+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.10+26.1.2-sources.jar`

## 结论

Beta 2.6.10 修复 Tree Gift 生物行已经被正确识别、却被旧归属门槛丢弃的问题。现在只发给玩家本人的 `+N rewards gained!` 汇总即可证明归属，不再强制依赖某一种旧贡献句。精确生物行可以出现在汇总之前、之后、单个多行 Component 中，以及已经证明为本人礼物的结束边框后 5 秒内；附近玩家只有公共生物行、没有本地玩家本人汇总时仍然无效。

## 自动测试与产物检查

- Java 25 `clean test build prepareRelease` 成功；新生成 XML 共 25 个测试套件、137 项测试，0 失败、0 错误、0 跳过。
- 8 项聚焦会话测试覆盖普通本人区块、附近公共消息拒绝、奖励缓冲、结束边框后生物行、窗口超时、缺少旧贡献句、完整多行区块，以及聊天压缩后的无边框多行值。
- 展开的 Fabric 元数据为 `Beta-2.6.10+26.1.2`、纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 二进制和 Sources JAR 均通过 JDK 25 `jar --validate` 与 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 静态数据流检查确认修改后的会话只读取已收到的聊天 Component 与 hover 文字，在有限会话内对每种奖励去重，不包含数据包、聊天发送、命令、点击、移动、HTTP 或服务器查询路径。

## 验证边界

本次审核验证编译、状态机行为、已覆盖消息顺序下的误报拒绝、元数据、归档完整性、文件名、校验和及 build/release 一致性，但不等同于已经登录 Hypixel 完成真实 Tree Gift 回归。剩余验收是本人获得一个真实 Tree Gift 生物，确认屏幕中央与本地音效各提示一次；随后站在其他玩家 Tree Gift 附近，确认对方公共生物行保持静默。

## SHA-256

- 二进制 JAR：`25382321625a5be940e97ab0e42cd36d6a41ed6366f69f354170f979bb67ad99`
- Sources JAR：`3d6b8c8cf171e21e75be01e79965cd1124117ea0b90d73253d2693e12bc4a2cd`

---

# QCloudy_Addition Beta 2.6.9 岩浆钓鱼提示音验证

验证日期：2026-08-11

验证产物：

- `release/QCloudy_Addition-Beta-2.6.9+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.9+26.1.2-sources.jar`

## 结论

Beta 2.6.9 修复 Hypixel 岩浆钓鱼 Fishing Hook 没有写入本地玩家直接 owner 关联时缺少上钩提示音的问题。直接归属本地玩家的水钓/岩浆鱼钩仍始终优先。玩家真实使用钓竿后，会开启有限 40 tick 关联窗口，只接受一根新加载、归属本地玩家或 owner 为空的鱼钩；抛竿前已经存在的鱼钩以及明确属于其他玩家的鱼钩全部排除。原有附近精确 `!!!` 标记和每根鱼钩只响一次的去重逻辑保持不变。

## 自动测试、资源与产物检查

- Java 25 `clean test build prepareRelease` 成功；新生成 XML 报告共 25 个测试套件、132 项测试，0 失败、0 错误、0 跳过。
- 聚焦测试覆盖水钓直接鱼钩优先、新加载 owner 为空的岩浆鱼钩、本地 owner 候选优先、拒绝旧鱼钩与其他玩家鱼钩、收杆/重置、40 tick 超时，以及空闲状态不需要扫描。
- 英文和简体中文资源各 378 个键，键集合一致且 JSON 有效。
- 展开的 Fabric 元数据为 `Beta-2.6.9+26.1.2`、纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 二进制 JAR 包含 `FishingBiteAlert`、`FishingBiteSession`、`FishingHookResolver`、`assets/qcloudy_addition/sounds.json` 与 `assets/qcloudy_addition/sounds/fishing/ciallo.ogg`。
- 二进制和 Sources JAR 均通过 JDK 25 `jar --validate` 与 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 静态数据流检查确认物品使用回调返回 `PASS`，空闲时较大范围鱼钩查找不运行，并且不存在自动抛竿/收杆、点击、移动、命令、聊天、数据包、HTTP 或音频下载路径。

## 验证边界

本次审核验证编译、鱼钩关联行为、资源存在性、双语配置、归档完整性、元数据、文件名、校验和及 build/release 一致性，但不等同于已经登录 Hypixel 完成岩浆钓鱼实服回归。正式广泛发布前，应各测试一次真实水钓与岩浆钓鱼上钩，确认两者都只播放一次 Ciallo，并确认所提供音频的再分发权利。

## SHA-256

- 二进制 JAR：`b3ebf47ef848f629782784b22ef14e6d7e03fb9bbe86bb0222a8ab725518e3e9`
- Sources JAR：`3d79108989509ffa1c02f4e663d33c067d1082f74b52741c2c52b4fb24e42e3f`

---

# QCloudy_Addition Beta 2.6.8 钓鱼上钩提示音验证

验证日期：2026-08-11

已验证产物：

- `release/QCloudy_Addition-Beta-2.6.8+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.8+26.1.2-sources.jar`

## 结果

Beta 2.6.8 在“通用 > 钓鱼”中加入默认关闭的“钓鱼上钩提示音”。它只检查本地玩家自己已经加载的 Fishing Hook，精确匹配其附近、名称可见且为 `!!!` 的 Hypixel ArmorStand，并且每根鱼钩最多播放一次内置 Ciallo OGG。该功能拥有独立的 0–100% 连续音量滑块，默认遵循项目统一的 64%。

## 自动测试、资源与产物检查

- Java 25 `clean test build prepareRelease` 成功完成。最新 XML 共 24 个 suite、127 项测试，0 failures、0 errors、0 skips；class major version 为 69。
- 聚焦测试覆盖每根鱼钩只播放一次、鱼钩消失或实体 ID 变化后重新待命、`sounds.json` 注册，以及内置资源的 OggS 文件头。
- 英文与简体中文资源各有 378 个键，键集合完全一致且 JSON 有效。
- 展开的 Fabric 元数据为 `Beta-2.6.8+26.1.2`，环境为纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 二进制 JAR 内含 `assets/qcloudy_addition/sounds.json`、`assets/qcloudy_addition/sounds/fishing/ciallo.ogg` 以及钓鱼检测/会话类。最终音频为 44.1 kHz 立体声 Ogg Vorbis，由 QCA 自带客户端资源包加载，不需要另装材质包，也不会在运行时下载。
- 二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 静态数据流检查确认检测器只扫描 `Player.fishing` 周围四格并播放本地声音，不包含抛竿、收杆、点击、移动、命令、聊天、数据包、HTTP 或纹理/音频下载路径。

## 验证边界

本次检查验证编译、自动行为、资源存在与格式、双语配置、归档完整性、元数据、文件名、哈希和 build/release 一致性；不代表已经完成登录 Hypixel 的实服上钩时机与声音回归。扩大公开发布前，仍应使用预期 GUI/音频设置实际测试一次上钩，并由项目所有者确认所提供 `Ciallo.mp3` 录音的再分发权利。

## SHA-256

- 二进制 JAR：`e8806bfd92c6b4629e968dc636d3fc5e4af546d3b6361cb3a1237be83fdeb4e7`
- Sources JAR：`aa9491473810f148f7cb15522e2119317416b922ec59477213bdfd8f634abb01`

---

# QCloudy_Addition Beta 2.6.7 Dwarven 地图验证

验证日期：2026-08-10

已验证产物：

- `release/QCloudy_Addition-Beta-2.6.7+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.7+26.1.2-sources.jar`

## 结果

Beta 2.6.7 将 Dwarven Mines 纹理替换为本次提供的单层 12 区域图，并按实际图像几何重新校准玩家箭头投影。Dwarven 投影现在只读取 X/Z、朝向与已可见子地点；投影 API 和回退计算中均不存在 Y。

## 自动、坐标与产物检查

- Java 25 `clean test build prepareRelease` 成功完成。最新 XML 报告为 123 个测试、0 failures、0 errors、0 skips；class major 为 69。
- 提供的 `2000×2000` PNG（`cb714dc325ae4971088ade84846d9ad97af0e3966553d7d1f63931c3be1ef15a`）已重采样为 HUD 原生的 `200×200` RGBA 纹理。源码、二进制 JAR 和 Sources JAR 内的最终纹理哈希均为 `639492c458d4acd232cf57fd250cf1d2548f4c07f95ca48bcc83a96417fb85c0`。
- 投影测试覆盖替换图上全部 12 个命名区域、明确子地点选择、Royal Mines 与 Royal Palace 之间只使用 X/Z 的通用地点回退、区域边界夹取，以及确认每个校准箭头中心均落在非透明地图内容上的坐标网格。每个区域都使用内缩的 X/Z 双线性校准，不使用 Y 分层，也不使用单一全图矩形。
- 资源测试验证 200×200 纹理尺寸、外部角透明，以及 Village、Upper Mines、Rampart Quarry、Forge、Lava Springs、Cliffside、Far Reserve、Goblin Burrows、The Mist、Ice Wall、Royal Mines 和 Royal Palace 的精确填充色。
- 展开的 Fabric 元数据为 `Beta-2.6.7+26.1.2`，环境为纯客户端，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 二进制 JAR 与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- Dwarven 地图路径保持为 `assets/qcloudy_addition/textures/gui/dwarven_mines.png`；地图生成器会明确保留这个维护中的提供资源，不再覆盖它。

## 验证边界

本次检查验证源码/配置一致性、全部 12 个投影校准、自动行为、归档完整性、元数据、文件名、校验和与 build/release 一致性。它不声称已完成 Hypixel 登录实服视觉回归。还需要在用户的 GUI Scale 与每个命名地点中实际检查；如发现实服偏移，应同时提供当时的子地点文本和玩家 X/Z。

## SHA-256

- 二进制 JAR：`97d7a9df937075eb071a77bb80c700cf865a91eac909a8b7982aac4e57c895ef`
- Sources JAR：`55bc0b309c7faafab5f19bcbb434e22f0f69da70607b404083f826b9deea8905`

---

# QCloudy_Addition Beta 2.6.6 晋级验证

验证日期：2026-08-10

验证产物：

- `release/QCloudy_Addition-Beta-2.6.6+26.1.2.jar`
- `release/QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`

## 结论

Beta 2.6.6 将已经审核的 Alpha 2.5.6 实现晋级到 Beta，没有改变 Java 功能行为。晋级前的 Alpha 2.5.6 基线和更名后的 Beta 2.6.6 均通过完整 Java 25 测试/构建流程。本次 Beta 变化只涉及发布通道、版本号、产物命名和发布文档。

## 自动测试、数据与产物检查

- 原始 Alpha 2.5.6 基线与 Beta 晋级后的版本均在 Java 25 下成功完成 `clean test build prepareRelease`。最终 XML 共 120 项测试，0 失败、0 错误、0 跳过；class 文件 major version 为 69。
- 最终可运行产物精确命名为 `QCloudy_Addition-Beta-2.6.6+26.1.2.jar`；源码产物精确命名为 `QCloudy_Addition-Beta-2.6.6+26.1.2-sources.jar`。
- 展开的 Fabric 元数据为 `Beta-2.6.6+26.1.2`，环境为纯客户端，并声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 二进制内含 `LICENSE_QCloudy_Addition`、`THIRD_PARTY_NOTICES.md` 与 `SHARD_DATA_NOTICE.txt`。
- 二进制恰好包含 320 张 Shard PNG、320 个物品定义与 320 个物品模型定义；目录/资源不变量继续由全部通过的测试覆盖。
- 英文与简体中文资源各有 373 个键，键集合完全一致且 JSON 有效。
- Alpha 2.5.6 与本 Beta 之间没有 Java 源码或运行时资源行为变化；必需与可选依赖保持不变。

## 验证边界

本次审计验证源码/配置一致性、自动行为、生成数据不变量、归档完整性、元数据、文件名、哈希和 build/release 一致性；不代表已经重新完成登录 Hypixel 的实服回归，也不代表覆盖每一种 GUI Scale、材质包、操作系统与模组组合的像素级视觉验收。Beta 是项目所有者批准的测试通道，不代表获得 Hypixel 官方认可，也不等同稳定 Release 已完成全部验收。

## SHA-256

- 二进制 JAR：`0871774cfa47641d220d18d53f9235ee1b02ff2abfc9ac586dd2a55a0adbc2fd`
- Sources JAR：`2baa8c557826d2bdf69816576ba7891261d7cde48bdeb12fcf6ebcc480f75137`

---

# QCloudy_Addition Alpha 2.5.6 Shard 详情与语义颜色验证

验证日期：2026-08-10

验证产物：

- `release/QCloudy_Addition-alpha-2.5.6-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.6-26.1.2-sources.jar`

## 结论

Alpha 2.5.6 为 320 个 Shard 全部加入独立详情页，显示 Wiki 当前列出的效果、语义分类与已记录获取方式，不用猜测填补缺失事实。Epic 使用 Minecraft 的 `§5` 深紫色；属性、分类、生物类型、获取方式与品质采用对应语义颜色。可点击 Shard 名称仅在鼠标悬停于可见文字时变深并添加下划线。配方索引与自然获取相互独立：例如 Queen Bee 会保留 Honeyhive/Honeycomb Collection 的自然来源，同时显示每一条可以产出它的已验证有序 Fusion 配方。

## 自动测试、数据与产物检查

- Java 25 下执行 `clean test build prepareRelease` 成功。本次 XML 共统计 120 项测试，0 失败、0 错误、0 跳过；class 文件 major version 为 69。
- 目录包含 320 个唯一 Shard ID、名称、Bazaar ID、internal ID、详情记录及与 ID 对应的图标资源组；不存在 Rainbug/L49。
- 每个 Shard 都有非空效果与获取方式显示。当前 Wiki 表为其中 319 个目录 Shard 提供了获取记录；Wild Hog 是当前唯一缺口，界面会明确标为 Wiki 尚未记录，而不会虚构来源。
- Gemzie 回归测试确认其为 Epic，效果为 `+0.25–2.5 Gemstone Spread`，Gemstone Spread 使用黄色，并显示 Critter Capsule/Cavern Biome 捕捉来源。Defense 以及 Animal/Aquatic 的语义颜色也有目录测试覆盖。
- Pandarai 回归测试确认为仅能通过 Fusion 获取。Queen Bee 回归测试确认同时拥有自然获取信息和非空的反向 Fusion 配方；所有可作为产物的 Shard 都由同一反向索引驱动“合成来源”页面。
- 搜索回归测试覆盖原始名称、ID、家族/分类元数据、效果文字、获取方式文字和生物类型。生成后的详情文字不含残留 Wiki 模板、链接、HTML 标签或粗体标记。
- 英文与简体中文资源各有 373 个键且键集合相同。JAR 内置 `SHARD_DATA_NOTICE.txt` 与第三方声明会说明 Wiki 数据和图标来源许可。
- 二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`；`release` 副本与 `build/libs` 逐字节一致。
- 元数据为纯客户端，版本为 `alpha-2.5.6-26.1.2`，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 静态检查没有发现 Shard 指南运行时 HTTP/API 客户端、数据包发送、聊天/命令发送、物品栏点击、Fusion 动作或自动操作；Wiki/API 数据只在打包前离线生成。

## 验证边界

本次检查覆盖源码、生成数据、单元测试、构建产物、归档完整性与纯客户端静态边界，但不代表已经完成登录 Hypixel 的实服回归，也不代表覆盖所有 GUI Scale/材质包的游戏内像素级验收。Alpha 升级为 beta 或 release 前仍需完成这些实测。下方 2.5.5 报告仅作为历史证据保留。

## SHA-256

- 二进制 JAR：`d4ed9ba609a64787b4de247f6561c1e5d1961f8359bdf9f25df3ba053a9b82ce`
- Sources JAR：`13251eaafe50c00ab4f10554dd8ca1b78dca6d65ca011191d5d5f7ffbf41fca0`

---

# QCloudy_Addition Alpha 2.5.5 Shard 图标与交互验证

验证日期：2026-08-10

验证产物：

- `release/QCloudy_Addition-alpha-2.5.5-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.5-26.1.2-sources.jar`

## 范围

Alpha 2.5.5 为 320 个目录 ID 分别提供与对应 Shard 匹配的内置图标，用它们替换紫水晶回退，同时保留“客户端已收到原生 ItemStack 优先，并在本次会话缓存”的逻辑。搜索框可通过点击外部、`Esc` 或 `Tab` 释放焦点，直接点击搜索框可恢复输入；每组配方输入/输出按内容宽度紧凑居中，点击范围跟随实际渲染边界。已审核的上游图标集本身有 6 对 Shard 使用相同的游戏外观，因此这些对应 PNG 会保持一致。

## 自动测试、数据与产物检查

- Java 25 下执行 `clean test build prepareRelease` 成功。本次 XML 共统计 22 个 suite、116 项测试，0 失败、0 错误、0 跳过；class 文件 major version 为 69。
- 320 个目录 ID、320 张内置 Shard PNG、320 个物品模型和 320 个物品定义的 ID 集合完全相同。每张 PNG 都能正常解码，尺寸均在 16–64 像素之间且带透明通道；不存在 Rainbug/L49。
- 通用紫水晶回退已删除。静态检查确认：客户端已经收到的原生 Shard ItemStack 会按 Shard ID 缓存在本次会话中，并优先于内置模型；离线回退本身是带有可被材质包覆盖的 `qcloudy_addition:shards/<id>` 模型的专属 `PLAYER_HEAD`。
- 回归测试覆盖搜索焦点退出键、宽屏/受限宽度下的紧凑输入输出几何、目录与图标完整性、合成不变量和响应式布局边界；同时直接检查了点击外部、重新聚焦和实际渲染点击范围的连接逻辑。
- 重编号后的二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`。与对应的重编号前归档进行完整解包内容对比后，确认只有 `fabric.mod.json` 版本元数据发生变化。
- 元数据为纯客户端，版本为 `alpha-2.5.5-26.1.2`，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 英文和简体中文资源各有 362 个键，键集合完全一致。`git diff --check`、JSON 解析均通过；针对 Shard 包的网络客户端、数据包、命令、聊天、物品栏点击、自动合成与已删除紫水晶回退的静态扫描没有匹配项。
- 新进行的联合开发客户端冒烟启动同时加载了 QCloudy_Addition、BabyzombieAddons、Firmament、Skyblocker、SkyHanni 与 Mod Menu，完成组合资源重载、物品图集创建和声音引擎启动。日志中没有 `qcloudy_addition:shards/*` 模型/纹理缺失或加载失败，也没有 QCloudy 异常。出现的错误来自未认证开发账号，以及 SkyHanni 无法接受当前 NEU 常量（`HUNTING_FORTUNE` 与 `FISHING_NET`），并非 QCloudy_Addition。

## 验证边界

本次验证已包含新的联合初始化/资源冒烟启动，但不代表已经完成登录 Hypixel 的实服回归，也不代表覆盖所有 GUI Scale 与材质包的游戏内像素级验收。Alpha 升级为 beta 或 release 前仍需完成这些实服与视觉测试。下方 2.5.4 报告仅作为历史证据保留。

## SHA-256

- 二进制 JAR：`b7ca1fa7477e31f86bd4f97c045e17238d3a7920138ebe6364d1a63689042f56`
- Sources JAR：`ba050233e7dabe0ee8c65d5784f38ca40fec8ca00e6aac446a0c33d539f09095`

---

# QCloudy_Addition Alpha 2.5.4 Shard 合成辅助验证补充

验证日期：2026-08-10

验证产物：

- `release/QCloudy_Addition-alpha-2.5.4-26.1.2.jar`
- `release/QCloudy_Addition-alpha-2.5.4-26.1.2-sources.jar`

## 结论

Alpha 2.5.4 已包含可独立运行、纯客户端的 Shard 合成辅助：提供类似 JEI 的搜索、配方/用途视图、有序输入交换、1–3 个输出槽、窄屏响应式布局、双语界面标签和遵循材质包的已观察物品图标，不依赖 JEI 或其他 SkyBlock 模组。运行时只读取随模组打包的版本化目录与客户端可见物品数据，不访问 Wiki/API，不发送数据包、容器点击、聊天或命令，也不会自动执行合成。

## 自动测试、数据与产物检查

- Java 25 干净测试共 108 项全部通过，0 失败、0 错误、0 跳过。
- Java 25 下执行 `./gradlew clean build prepareRelease` 成功；新 class 文件 major version 为 69。
- 目录包含 320 个唯一 Shard ID、名称和 Bazaar ID，并与已审核官方 Bazaar 快照中的 320 个 `SHARD_*` 产品完全一致。包含 Anteater、Zombuddy、Troodon、Goldolot（`R92`）和 Ghost Crab，不包含 Rainbug。
- 测试覆盖合成不变量、有序输入、由第一个输入决定的数量、Chameleon 递进/排除、配方/用途反向索引、共享配方对象索引，以及同一 Shard 的 ID/Special 输出槽分别保留。
- 重编号后的二进制与 Sources JAR 均通过 JDK 25 `jar --validate` 和 `unzip -t`。与对应的重编号前归档进行完整解包内容对比后，确认只有 `fabric.mod.json` 版本元数据发生变化。
- 元数据为纯客户端，版本为 `alpha-2.5.4-26.1.2`，声明 Minecraft 26.1.2、Fabric Loader 0.19.3+、Fabric API 0.155.2+26.1.2+ 与 Java 25+。
- 英文和简体中文资源各有 362 个键，键集合完全一致。
- `git diff --check`、JSON 解析和新 Shard 代码静态扫描均未发现错误。

## 验证边界

本补充不代表已经完成登录 Hypixel 的实服回归、所有 GUI Scale/材质包的像素级验收，或与四个指定参考模组的本次全新联合启动。Alpha 升级为 beta 或 release 前仍需完成这些实测。下方 1.5.1 报告仅作为历史证据保留，不能当作 2.5.4 的实服测试结果。

## SHA-256

- 二进制 JAR：`4a26801c3d63cfb2cf4ae10f0249efd761fe6e1264caedb239133e9a698fb773`
- Sources JAR：`a2a3232c5d6342da89037225e3ec78302d8a0910a72c3cbe56a313f409720025`

---

# QCloudy_Addition 1.5.1 发布验收报告

验证日期：2026-08-06

验证产物：

- `release/QCloudy_Addition-1.5.1+26.1.2.jar`
- `release/QCloudy_Addition-1.5.1+26.1.2-sources.jar`

## 结论

1.5.1 新增由玩家点击触发的重新连接按钮、Beeheemoth 空间声音控制，并把 Tree Gift 提醒改成有限且严格证明本人归属的聊天状态机；即使聊天压缩模组取消正常显示也仍能接收解析。Minecraft 26.1.2 下的源码、单元测试、归档、可重复构建、独立初始化及四个指定参考模组联合启动均通过。该结论表示本地发布就绪，不表示已用登录 Hypixel 的账号覆盖每一种实服实体、消息、声音或界面。

## 精确环境

- Minecraft 26.1.2
- Fabric Loader 0.19.3
- Fabric API 0.155.2+26.1.2
- Eclipse Temurin Java 25.0.4；class major version 69
- Gradle Wrapper 9.6.1
- Fabric Loom 1.17.17

## 自动测试与产物检查

- 23 个测试套件共 98 项 JUnit 测试全部通过，覆盖真实 Helia 总览/详情菜单、有限 Tab/计分板 Chapter 解析、拒绝并修复缓存的 `SB Level` 假任务、官方多日 Benefactor 捐赠聊天、Tab 倒计时和未激活 Temple 菜单、严格的本人 Tree Gift 归属与奖励块缓冲、本人/队友 Loot Share 的 Wumpa 分流策略、Snoozle 双材质与大小边界、四种 Safari Milestone 布局、账号/Profile 归一化、Chapter 切换隔离、Cold 篝火边界、完整 37 种 Critter 品质表、全部 8 个 Wumpa 前置、Warden 冷却边界、Tree Protection Order 倒计时、Lasso REEL、Beeheemoth 声音路径隔离、手动重连地址重建、scale-9 Beeheemoth、唯一分类归属、Hunting 默认值/设置路由和全部 16 个官方 Fairy Soul 坐标。
- Java 编译启用弃用 API 检查且没有警告；Snoozle 扫描改用 `ClientLevel.hasChunk(chunkX, chunkZ)`，继续保证只读取已加载区块，同时不再调用已弃用的 `hasChunkAt`。
- 最终连续两次使用 Java 25 执行 `clean test build`，二进制与 Sources JAR 均逐字节一致。
- 两个最终产物均通过 JDK 25 `jar --validate` 与 `unzip -t`。
- 二进制与 Sources 元数据版本均为 `1.5.1+26.1.2`。
- `release` 目录副本与最终 `build/libs` 产物逐字节一致。
- 两个产物均包含 `LICENSE_QCloudy_Addition` 与 `THIRD_PARTY_NOTICES.md`。
- 两个发布 JAR 均不包含参考模组 class、测试 class、旧宠物 PNG 目录、`PetIconRegistry` 或已经删除的龙巢寻找实现。
- 静态检查未发现 `sendChat`、HTTP、WebSocket、数据包发送器、自动移动或区块请求代码。
- 唯一的外发命令载荷是文档明确记录、由玩家实际操作触发的 Storage 导航（`storage`、`enderchest <1-9>`、`backpack <1-18>`）、`/th` 发送的 `warp torrhus`，以及 `/helia` 发送的 `chapter torrhus`。

## 启动与兼容矩阵

| 实例 | 结果 |
|---|---|
| QCA 1.4.6 + Fabric/API | 独立 Loom 启动共加载 51 个模块；QCA 正常初始化、资源重载、声音引擎启动 |
| QCA 1.4.6 + 四个指定参考模组 | 使用 BabyzombieAddons 3.4.1、SkyHanni 7.41.0、Skyblocker 6.8.2、Firmament 44.3.0 与 Mod Menu 18.0 重新进行 94-mod 联合启动；QCA、全部参考模组、组合资源与声音引擎均完成初始化，未出现 QCA 异常 |
| QCA 1.4.9 透明图标变更 | 保留选定原始主体，转换为四角透明的 128×128 RGBA；两次 clean build 逐字节一致，元数据、class version 69、归档、测试与 release 副本哈希全部通过 |
| QCA 1.5.0 + Fabric/API | 独立 Loom 共加载 51 个模块；QCA 完成初始化、资源重载和声音引擎启动，进入主菜单后主动结束前没有出现 QCA 异常 |
| QCA 1.5.1 + Fabric/API | 独立 Loom 共加载 51 个模块；QCA 完成初始化、资源重载和声音引擎启动，进入主菜单后主动结束前没有出现 QCA 异常 |
| QCA 1.5.1 + 四个指定参考模组 | 使用 BabyzombieAddons 3.4.1、SkyHanni 7.41.0、Skyblocker 6.8.2、Firmament 44.3.0 与 Mod Menu 18.0 的新鲜 94-mod 联合启动完成 QCA、组合资源与声音引擎初始化；客户端持续约 33 分钟后正常停止。最终已加载区块 API 清理后又重跑一次并到达相同初始化边界；两次均没有 QCA 或 mixin 注入异常 |

联合实例中的警告/错误来自参考模组的 refmap/资源、可选 ModernUI 类、BabyzombieAddons 缺失的 custom-disc 文件、SkyHanni/Skyblocker 远程仓库请求、未认证的 profile/Realms 活动，以及 SkyHanni 7.41.0 无法接受当前 NEU 仓库中的 `HUNTING_FORTUNE` 与 `FISHING_NET` 常量；它们均不是由 QCA 抛出，也没有阻止客户端、资源和声音初始化。QCA 不依赖 Firmament 运行；可选的重复功能交接只检查该 mod id 是否已加载，Firmament 不存在时 QCA 的功能仍完整可用。

## 最终完整性修复

- 将旧模组图标替换为选定的原始云环、橙色核心和右下角青色定位点图案，只把背景转换为 Alpha，没有重绘主体。发布 PNG 为 128×128 RGBA，四角 Alpha 均为 0；32×32 与棋盘背景预览仍清晰且不存在黑色方框。
- 新增分类明确的 Torrhus Canyon 与 Critter Safari 模块：Chapter/资源组合 HUD、组合 HUD 内的 Miria Contest 计算、Critter 行为辅助、Benefactor 状态、可配置 Tree Gift 稀有掉落提示、Safari Run/分区 Critterdex 仪表板、Sparkling 提示与高亮、Floor Drop/Quest Item 辅助、Wumpa 遭遇状态以及 Safari Belt 里程碑 tooltip。QCA 不修改右侧计分板，也不重复显示其竞赛倒计时。
- 在 Torrhus 综合 HUD 新增默认开启、可单独关闭的 Tree Critter 计时。每 10 个客户端 tick 严格解析最近已加载实体显示名称中的 `Critter in: <时间>`，采用与 SkyHanni 被动获取砍树进度相同的思路；不会猜测使用了哪个 Pot，也不合成本地时钟。四种当前已索引 Pot of Honeycomb 与服务器应用的加速/立即吸引修正都无需硬编码倒数。
- 删除 Safari Critter/Sparkling 对 Armor Stand 捕捉道具的轮廓赋值。旧 marker 渲染状态在当前版本仍可能让支架身体进入轮廓 pass，因此安全兜底会完整排除这些支架；真实的非 Armor Stand Critter 仍保留品质色或自定义 Sparkling 颜色。实体本身不被修改。
- 修复 Lasso 捕捉后 Critter Behavior 偶发重复提醒：跳过已经移除的实体，并在收到精确 `CAPTURE! You caught ...` 服务器确认后，只对刚捕捉的行为 Critter 名称暂停三秒；其他种类仍可提示，有限窗口结束后同种真实目标也会正常恢复。
- 新增默认开启的 Beeheemoth 辅助，采用指定 BabyzombieAddons 的 scale-9 Bee 特征；原版轮廓颜色接入 QCA RGB/HSV 选择器。固定黄色首次观察位置光柱在进入 10 格、收到本人精确捕捉确认或实体消失时关闭，并且同一 UUID 关闭后不会重新出现。
- 新增独立、默认开启且默认 64% 的 Beeheemoth 声音控制。它只缩放已加载 scale-9 Beeheemoth 或其刚记录位置 12 格内的非相对 Bee 事件/解析声音；其他 Bee 与所有非 Bee 声音不变。关闭该子选项只会静音匹配的 Beeheemoth 声音。
- 新增独立且默认开启、64% 音量的 Lasso REEL 提示音。按 SkyHanni 的本地玩家拴绳关系与附近精确 ArmorStand 关系识别，只在 REEL 从 false 变为 true 时播放；二级设置提供 0–100% 连续音量滑条，不重复主开关。
- Hunting 解析只接受锚定或有限上下文格式，只读取客户端已收到的计分板、Tab、聊天、标题、实体名称、物品栏和已加载方块状态。新模块没有新增命令、聊天、联网、容器点击、移动、战斗或交互发送行为。
- Hunting 预警统一使用屏幕中央标题渲染，但每一种预警功能各自拥有音效开关和连续的 0–100 音量滑条，默认均为 64%；“通用”中的音效开关只作为总静音。长任务名与 Critter 名称自动换行，Hunting 渲染器不存在省略号回退。
- 官方 37 种 Safari Critter、Quest Item、Contest 档位、Ticket 等级和已记录行为均有本地解析/配置测试。Safari Belt 属性数值刻意读取客户端收到的 lore，不硬编码可能变化的总值。
- Safari Belt Milestone 改为由同一个上下文解析器处理已打开的 Milestone 菜单与 Belt tooltip。合并行和标题/lore 分行都可分别填充 Cavern、Forest、Haunted、Icy；未解锁条目与捕捉进度分数会被拒绝。四项确认等级按 Minecraft 账号/SkyBlock Profile 保存，只有观察到更高等级时更新。
- Forest/Desert Whispers、Forest/Safari Essence、Forest Fortune、Sweep、Helia Chapter/任务/进度和 Safari Belt Milestone 现在都按账号/Profile 保存。重复 Tab、计分板与菜单快照作为绝对值，不会累加；只有收到格式准确的聊天获取提示才做增量。切换到新 Chapter 时会清除上个任务的旧字段。
- 修复 Helia Chapter 数据获取：Tab 与计分板分别解析，支持真实的 Chapter 总览/详情物品布局，只合并四秒内最多 12 行收到的聊天块，并清理旧配置里误存的 `SB Level` 等非任务内容。修复 Benefactor 数据获取：读取有限 Tab/计分板、Forest/Desert Temple 菜单和官方捐赠聊天；支持天数、同寺庙续期、跨寺庙替换、旧菜单保护、到期处理及账号/Profile 保存。
- Safari Essence 已从 Safari Dashboard 删除，现在只在 Torrhus 资源区显示，并有独立 Torrhus 开关。
- 新增严格高于默认 80/90 的两档可调 Cold 预警、独立且默认开启并设为 64% 的 Cold 提示音，以及只指向最近已加载篝火的红色信标。第一次超过阈值时现在立即扫描，启用期间每 40 tick 刷新；下一次 Cold 数值下降时立即关闭。
- 新增达到 Wiki 明确条件（至少四个 `Soothing Incense`）时的一次性 Doomspiral 提示、默认关闭的 Wumpa 红色运动/碰撞投影、默认关闭的 Torrhus 12 个与 Safari 四个 Fairy Soul 粉色信标，以及按官方 Shard 品质色高亮全部 37 种可捕捉 Critter 的默认开启功能。
- 新增默认开启的 Warden 可抓捕预警，只扫描有限 Doomspiral 场地内已加载实体；采用指定 BabyzombieAddons 的 140 客户端 tick 规则、收到的本地玩家延迟补偿和 emerging/digging 排除，每个实体就绪时只显示一次中央大字与独立默认 64% 音效，不发送捕捉动作。
- 将 Wumpa 组队前置与本人 Safari Critterdex 分离：本人锚定捕捉确认和收到的 `LOOT SHARE ... catching a <Critter>` 队友确认都会更新八项 Wumpa 集合，但 Loot Share 仍不计入本人 Critterdex。Wumpa 生成后清单替换为 `Wumpa：已生成` 与实时阶段；移动和路线改为寻找名称附近真正的 Ravager 身体，并通过短暂移动/静止确认防抖。8/8 与巨大脚步/awoken 仍共用每轮一个提醒标记。
- 在 Safari 分类新增独立、默认开启的 Snoozle 可撞墙覆盖。每秒有限扫描一次附近已加载方块，只接受同时含 Wiki 明确记录的 `Cobbled Deepslate` 与 `Tuff` 的小型连通组件，并只在空气相邻表面提交半透明四边形；过大地层与单一材质区域会被拒绝，默认绿色接入统一 RGB/HSV 选择器。

- 两份 2026-08-04 崩溃 ZIP 逐字节一致（`8abff84c45b6b2ecb8ffada8de514a446755c70fc2d1ff6f853d47a24811a5d7`），实际是同一条 QCA 1.2.5 Storage 缓存故障：缓存中的“效率”附魔 Holder 属于旧动态注册表集合，却在没有异常边界的情况下由渲染线程序列化。QCA 现在会检测注册表替换、按资源键重新绑定普通/附魔书附魔、逐物品隔离加载/搜索/指纹/编码/写盘异常，并将失败物品保留为空槽位；Storage 快照编码异常不能再逃出到渲染线程。详见 `CRASH_ANALYSIS_2026-08-04_zh_CN.md`。
- 删除设置侧栏里的“全部 / ALL”分类，并把“砍树”“狩猎”“Safari”拆成独立分类。每张功能卡在枚举中只有一个归属：Torrhus/树木进度位于“砍树”，跨岛捕捉工具位于“狩猎”，Critter Safari 系统位于“Safari”，不会跨分类重复。综合 HUD 设置键会在 Torrhus 跳到“砍树”、在 Critter Safari 跳到“Safari”，其他位置跳到“狩猎”。
- 将原来的两个顶部分页合并为唯一的“功能”页。“通用”现在是左侧第一个分类，包含“界面动画”和预警音效总静音；旧 HUD 外观/位置编辑分类及重复的位置编辑卡已删除，左下角“编辑 HUD”继续打开已加载 HUD 编辑器。
- 删除二级页面中的重复功能开关与空白二级页面。左键点击功能卡片是唯一的功能开关；右键只打开确实存在的功能专属设置。
- 所有 HUD 背景颜色选择器均增加明确的“透明”预设，同时保留 RGB 自定义。
- `/th` 作为无设置、不可关闭的客户端命令注册；玩家实际输入 `/th` 时会准确发送 `warp torrhus`，除非该客户端命令根已经被其他模组占用。
- `/helia` 作为无设置的客户端命令注册；玩家实际输入 `/helia` 时会准确发送 `chapter torrhus`，除非该客户端命令根已经被其他模组占用。
- 将 Tree Gift 提醒改成 15 秒、由边框界定的收到聊天块。本人精确的 `+N rewards gained! (hover)` 汇总仍足以触发其自身 `SHOW_TEXT`；独立百分比行和 `A <loot> fell from the Tree!` 行只有在同一块还包含 Tree Gift 标题、本人 `You helped cut...` 贡献行与本人奖励汇总时才有效。提前到达的奖励行会等到归属证据出现，重复物品只提醒一次，公共/附近玩家块与 lasso 消息会被拒绝；监听 `GAME_CANCELED` 可在其他模组压缩可见聊天时保留解析。
- 在“通用”加入默认开启的手动重连功能卡，并在连接中断界面增加原版宽度按钮。它只记住当前客户端会话最后一次明确连接的多人服务器目标与材质包偏好；玩家实际点击一次只发起一次正常 Minecraft 连接。没有定时器、自动循环、服务器绕过、持久化地址、聊天载荷或命令载荷。
- Fairy Soul 成功或“已经找到”确认会立即隐藏十格内最近的已列出 Soul，并按收到的 SkyBlock profile 持久保存岛屿坐标键；点击失败或没有服务器确认不会误删点位。
- 删除所有功能卡片右上角的重复开关和右下角的右键说明。左键仍切换功能，左侧蓝条仍表示开启，右键仍进入完整二级设置。
- 搜索框的自绘外框和原版无边框 `EditBox` 现在共用一套布局数据：文字基线按真实字体行高垂直居中，左右内边距完全对称，整个可见外框均可点击；窄 GUI 宽度下会先缩小顶部页签，避免与搜索框重叠。
- QCA 热键全部改为在原有功能二级页中行内编辑；键盘、鼠标 1–5/侧键及修饰键组合均可使用。等待输入时按 `Esc` 会清空为未绑定，旧 `KeyChordScreen` 已从源码和发布包删除。运行时保留鼠标绑定的打开设置与 Chat Peek 路径。
- 从配置、功能卡、HUD 类型、渲染器、扫描器、翻译和发布产物中彻底删除寻找 Golden Dragon/Dragon's Lair 的功能。`Dragon's Lair` 文本仅可能作为水晶矿洞普通地点名留在岛屿判断数据中，不是寻找功能。
- 从 Pets 菜单、Tab Widget 或聊天中确认的宠物配件按宠物写入 QCA 自己的配置。满级只隐藏多余的“到满级”进度，不会再隐藏配件行。
- 删除生成 PNG 的宠物回退调用，改为用已验证 Profile 构造普通 player head。QCA 不写入合成 `petInfo`，外部物品模型判定不能再把 HUD 头像替换为无关球体或错误宠物模型。
- 生成元数据包含 88 个基础 Profile、352 个皮肤 Profile、5,422 条仅属于宠物的当前/动态纹理映射和 87 个配件定义；Baby Spinosaurus 有 60 个当前/动画纹理，并归入其精确皮肤家族。
- 旧宠物 PNG 文件夹和 `PetIconRegistry` 已从二进制与 Sources JAR 排除，运行时无法再选择。
- 宠物、配件、任务名称和进度数值均按完整文字测宽，包含粗体；不会使用省略号回退。
- 相邻的界面、地图、Storage、组合键、中键替代、Chat Peek、传送声音和滑条行为继续通过现有测试、构建与启动检查。

## 尚需实服确认的边界

本地实例没有可用的已认证 Hypixel 账号，而且本次验证期间 SkyBlock 正在维护；桌面界面控制器也无法附着 Loom 的未打包 Java 进程。因此重新连接按钮已针对 26.1.2 的精确 `DisconnectedScreen` 布局与 mixin 目标、配置/单元测试、归档内容和客户端初始化完成验证，但没有完成自动像素级点击。Tree Gift 实服归属/顺序变体、Beeheemoth 生成/捕捉声音、队友 Loot Share 的真实捕捉顺序、Wumpa Ravager 与名称载体关联及路线精度、实际 Snoozle 墙体组件和覆盖外观、Armor Stand 捕捉道具排除、Tree 倒计时、Lasso 时机、Cold/篝火、Warden、Fairy Soul、实服 Widget、用户材质包、宠物、末影龙、GUI Scale、重连界面外观与实体输入手感仍需实服回归。启动验证只证明初始化，不等于截图级正确，也不能承诺绝对零 bug 或零反作弊风险；详见 `COMPLIANCE_zh_CN.md`。

## SHA-256

- 二进制 JAR：`e3d3131d4f1d40e7859b655aed56aa72ef9a5dae2bd045710d4bde9daf705536`
- Sources JAR：`ab825c382b6f672cfc6ce2381db0a904ea60b23e593fa5254bd7e87722442ada`

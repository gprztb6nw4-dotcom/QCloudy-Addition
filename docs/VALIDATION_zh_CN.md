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

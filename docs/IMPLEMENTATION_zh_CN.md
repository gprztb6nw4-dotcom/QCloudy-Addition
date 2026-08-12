# QCloudy_Addition 功能实现与数据流细致说明

本文对应 `Alpha-2.6.14+26.1.2`，逐项说明每个公开功能的用途、读取的客户端信息、实现方式、应呈现的效果、默认状态，以及是否会产生对外操作。

## 1. 总体架构

QCA 是 Fabric 纯客户端入口。`QCloudyAdditionClient` 启动时完成以下工作：

1. 加载并修复本地 JSON 配置及按账号/Profile保存的物品栏数据。
2. 每秒读取一次原版 Tab 与计分板已经收到的文字。
3. 通过 Fabric 消息事件接收正常显示和被聊天压缩模组取消显示的游戏聊天。
4. 注册 HUD 与世界渲染回调。
5. 使用目标明确的 Mixin 处理容器输入/渲染、Chat Peek、实体轮廓、声音替换、快捷键、光标记忆和连接界面。

总体数据流：

```text
Tab / 计分板 / 聊天 / 已打开菜单 / 标题 / 已加载实体 / 本地物品栏 / 已加载方块
                                  ↓
                         有限解析器或本地过滤器
                                  ↓
                    本轮状态或账号+SkyBlock Profile缓存
                                  ↓
              HUD / Tooltip / 高亮 / 光柱 / 覆盖 / 路线 / 本地声音
```

### 1.1 可选统一提供方适配器

`UnifiedModIntegration` 只识别已审查的提供方 ID 与精确版本：SkyHanni 7.41.0、Skyblocker 6.8.2、Firmament 44.3.0、BabyZombieAddons 3.4.1。QCA 不对它们产生编译依赖。只有 Fabric Loader 确认模组及版本后才进行反射；每个提供方单独捕获失败，所以缺失或结构变化的模组不会阻止 QCA 启动。

注册表遍历提供方实时配置，只开放经过验证的布尔/枚举、带注解或其他明确边界的数值，以及已审查 HUD 的 x/y/scale。写入后调用对应模组自己的更新、保存或 dirty 机制，不直接修改未加载模组的 JSON。只有明确的跨模组等价别名会合并为一个逻辑功能；开启所选提供方时，只关闭挂在同一别名下的其他实现。提供方选择保存在 QCA 配置中，各模组自身设置仍由各自保存。

`HudLayoutScreen` 把 QCA 当前实际加载的面板与所选外部提供方中已经启用的 HUD 合并显示。拖动/缩放过程只保留临时预览，松开鼠标才写入原生坐标或缩放。首个 Alpha 刻意排除尚未审查 setter/校验契约的复杂颜色、快捷键和自定义编辑器对象。

地点识别先确认当前服务器域名属于 Hypixel，并确认计分板中存在 SkyBlock 证据；随后使用带地点标记的计分板行和有限原始地点名进行分类。只在对应岛屿运行对应解析与渲染，不在所有服务器全局扫描。

## 2. 设置、语言与 HUD

### 用途

让每项功能只有一个明确分类，并让玩家不编辑文件也能完整调整界面。

### 调用与数据

- Minecraft 本地键盘/鼠标输入事件与有限输入 Mixin。
- `HudElementRegistry` 提交屏幕 HUD。
- Minecraft 26.1.2 的 `GuiGraphicsExtractor` 绘制文字、物品、面板和贴图。
- 本地 `qcloudy_addition.json` 保存配置。
- 内置 `en_us.json`、`zh_cn.json` 翻译 QCA 自有文字。

### 实现

- `ConfigScreen` 提供一个可搜索的“功能”页，以及按要求排序的13个互斥分类：通用、地图、物品与菜单、战斗、地牢、Slayer、挖矿、种地、砍树、钓鱼、狩猎、Rift、活动。
- 左键卡片切换功能；右键只打开本功能真正拥有的二级设置。
- `HudLayoutScreen` 列出当前地点/状态实际加载的 QCA HUD，以及所选兼容提供方中已启用的 HUD。拖动改变位置；存在原生缩放项时，拖动边框或角落会修改该 HUD 的原生缩放。
- `PanelStyle` 为 Map、Mining、Hunting、Pet 分别保存背景颜色/透明度、边框宽度/颜色、标题色、粗体、阴影与缩放。
- `ColorPickerScreen` 提供 RGB/HSV、亮度、常用预设和透明背景。
- 热键保存键盘键或鼠标键加 Ctrl/Shift/Alt/Super；监听时按 `Esc` 清空绑定。

### 应呈现效果

紧凑、深色、借鉴BLC信息层级但未复制资源或代码的设置界面；启用项左侧显示蓝条；支持搜索、可关闭的打开动画和独立HUD编辑界面。重启后位置和缩放不变。

### 默认与对外行为

默认英文、动画开启、Minecraft点阵字体、文字阴影开启、一像素青色边框和半透明深色背景。QCA语言切换不会翻译Hypixel返回的物品、任务、地点、宠物或HOTM预设名。无服务器操作。

## 3. 手动重新连接

- **用途：**连接失败或断线后直接重试。
- **读取内容：**原版 `ConnectScreen.startConnecting` 已有的服务器名称、地址、类型和材质包偏好。
- **实现：**`ConnectScreenMixin` 只在本次客户端进程内记住最后一次明确连接目标；`DisconnectedScreenMixin` 在原版 `LinearLayout` 排版前追加一个原版宽度按钮。点击时新建 `ServerData`，调用一次正常连接界面。
- **效果：**连接失败/断开界面出现对齐的“重新连接”按钮。
- **默认与对外行为：**默认开启；一次实际点击产生一次普通服务器连接。不会保存地址、倒计时、循环、后台重试、发送命令/聊天或绕过认证。

### 3.1 钓鱼上钩提示音

- **用途：**用短促本地声音补充容易错过的上钩画面，但不自动操作钓鱼。
- **读取内容：**优先使用直接归属于本地玩家的 `Player.fishing` 鱼钩；同时观察玩家真实使用钓竿的动作、有限关联范围内已经加载的 Fishing Hook、选中鱼钩碰撞箱向外四格内已经加载的实体，以及 ArmorStand 已收到的名称与可见状态。
- **实现：**直接归属本地玩家的水钓或岩浆鱼钩始终优先。`FishingHookResolver` 在本地玩家真实使用钓竿时记录已经存在的鱼钩 ID；随后的 40 tick 内，只接受新加载、归属本地玩家或 owner 为空的候选，明确属于其他玩家的鱼钩会被拒绝。如果玩家在直接鱼钩或已关联回退鱼钩仍存在时再次使用钓竿，解析器会把它判定为收杆，而不是新抛竿。`FishingBiteSession` 只在确认的新抛竿时重新待命，因此收杆后短暂残留的 `!!!` 不会让声音播放第二次。`FishingBiteAlert` 仍只接受“不可见实体、名称可见、名称精确等于 `!!!`”的 ArmorStand，并按选中鱼钩实体 ID 去重。提供的 MP3 在打包前转换为 `assets/qcloudy_addition/sounds/fishing/ciallo.ogg`，再通过 `sounds.json` 注册。
- **效果：**本地鱼钩可收杆时播放一次 Ciallo；持续存在的标记不会每 tick 重播。
- **默认/对外：**功能默认关闭；独立音量为 0–100%，默认 64%；只播放本地声音。钓竿使用回调始终放行，不抛竿、收杆、点击、转向、移动、发包、聊天或命令；空闲时不会运行较大范围鱼钩查找。

## 4. 地图

### 4.1 Dwarven Mines

- **用途：**用清晰区域图替代复杂路线网。
- **读取内容：**本地玩家 X/Z、朝向和计分板地点；该地图不读取 Y。
- **实现：**`DwarvenMapProjection` 把 X/Z 和已解析地点映射到本次提供的 12 区域单层图。每个区域都按替换图的实际位置重新校准；只有通用地点时才回退到最近的归一化 X/Z 区域中心。
- **效果：**显示提供的英文区域地图，实时红色玩家箭头与地点和局部 X/Z 位置同步。
- **默认/对外：**开启；纯渲染。

### 4.2 Glacite Tunnels

- **用途：**正确表达不同高度的隧道结构。
- **读取内容：**本地 X/Y/Z 和朝向。
- **实现：**`HudRenderer` 在 Y=126、143 切换低/中/高三张图；三张图共用相同 X/Z 投影；生成时对英文地点卡做碰撞避让。
- **效果：**升降高度时地图层变化，红色箭头水平位置连续，地点文字不重叠。
- **默认/对外：**开启；纯渲染。

## 5. 挖矿与 Crimson Isle

### 5.1 Commission、三种 Powder 与 HOTM

- **用途：**无需持续按Tab也能看到目标。
- **读取内容：**收到的 `Commissions:`、`Powders:` Tab小组件，以及玩家已经打开的HOTM槽位/Loadout菜单。
- **实现：**`TabListTracker` 只取标题后的有限行；服务器给出的 `current/target` 优先。只收到百分比时，已知任务可用文档目标换算，未知任务仍显示百分比，不猜目标。`HotmSlotTracker` 只接受菜单中明确 `SELECTED` 或 lore中 `Current:` 的原始名称。
- **效果：**完整任务名、每项独立进度条、默认一位小数百分比或可选具体数值、Mithril/Gemstone/Glacite Powder，以及可选 `HOTM: <原名>`。
- **默认/对外：**追踪与HOTM行开启、百分比模式；不发命令、不点击菜单。

### 5.2 Crimson Isle Faction Quest

- **用途：**在Crimson Isle持续显示阵营任务。
- **读取内容：**收到的有限 `Faction Quests:` Tab块。
- **实现：**`TabListTracker` 解析 `✖`/`✔`、完整名称和数量；只在Crimson Isle使用Mining HUD位置。
- **效果：**原始英文完整任务名、数量和领取状态，无省略号。
- **默认/对外：**开启；纯显示。

## 6. Torrhus与砍树

### 6.1 Helia Chapter与资源

- **用途：**把长期进度集中到一个HUD。
- **读取内容：**分别限制范围的Tab/计分板、四秒且最多12行的Chapter聊天块、玩家已打开的Helia Chapter菜单。
- **实现：**`HuntingTextParser` 生成局部快照；`HuntingTracker` 只合并非空字段，明确观察到新Chapter时清理旧任务。Forest/Desert Whispers、Forest/Safari Essence、Sweep、Forest Fortune把Tab/计分板/菜单当绝对值；只有精确获取聊天做加法。数据按Minecraft账号与收到的SkyBlock Profile保存。
- **效果：**Chapter、完整当前任务、任务进度及六类资源。完成任务数、Chapter总进度、下一解锁默认关闭。Safari Essence只在Torrhus区显示。
- **默认/对外：**基础行和资源开启；不发命令、不点菜单。

### 6.2 Tree Critter计时

- **用途：**显示服务器真实Honeycomb吸引时间，避免本地倒计时漂移。
- **读取内容：**最近已加载、名称精确匹配 `Critter in: <时间>` 的实体/名称牌。
- **实现：**每10个客户端tick选择最近的有效可见标签；不猜使用的Pot，也不自行启动倒数。
- **效果：**综合HUD显示服务器实际时间，自动兼容Pot大小、加速和立即吸引。
- **默认/对外：**开启；只读。

### 6.3 Miria Contest

- **用途：**只补充计分板没有直接给出的下一目标。
- **读取内容：**Tab/计分板中的当前档、分数和下一档需求。
- **实现：**`ContestSnapshot` 在信息足够时计算下一档、还差分数和预计Ticket；不向右侧计分板注入内容。
- **效果：**综合HUD显示下一档、差值和预计Safari Ticket；不重复竞赛计时。
- **默认/对外：**开启；纯显示。

### 6.4 Benefactor

- **用途：**显示Temple增益状态与到期时间。
- **读取内容：**有限Tab/计分板、玩家已打开的Forest/Desert Temple菜单和精确捐赠聊天。
- **实现：**新捐赠在短时间内具有更高可信度，避免未刷新的旧菜单覆盖；收到的持续时长转换为本地到期时间，按账号/Profile保存并本地过期。
- **效果：**状态、剩余时间、Temple/效果和捐赠信息。
- **默认/对外：**所有行和独立64%提示音开启；不会捐赠、点菜单或发命令。

### 6.5 稀有Tree Gift

- **用途：**只提醒属于本人且在自定义列表中的稀有奖励。
- **读取内容：**客户端收到的原始游戏聊天 `Component` 与其 `SHOW_TEXT`，包括被兼容聊天压缩模组取消显示的消息。
- **实现：**`TreeGiftAlertSession` 接受普通或多行聊天 Component，打开的区块 15 秒后失效。只发给玩家本人的 `+N rewards gained!` 汇总是归属凭证，并可读取其 `SHOW_TEXT` hover；精确 Bonus/生物行会先缓冲，汇总无论先到还是后到都能完成确认。已经证明为本人的礼物在结束边框后保留 5 秒，且只接受精确 `-A wild <生物> appeared!`；没有本人汇总的公共生物行仍然无效。每项稀有物每个礼物只提示一次。
- **效果：**屏幕中央 `RARE TREE GIFT`、物品副标题和该功能自己的提示音。
- **默认/对外：**功能、10种稀有物品和音效全开，音量64%；不发聊天或命令。

## 7. 狩猎

### 7.1 Beeheemoth

- **用途：**更容易发现生成地点和听清生成/捕捉声音，但不自动交互。
- **读取内容：**已加载Bee、实体Scale/位置、本人精确捕捉确认和空间Bee声音实例。
- **实现：**只接受Scale约9.0的Bee；`EntityRendererMixin`使用原版轮廓；首次位置建立黄色光柱，进入10格、收到本人捕捉确认或实体消失即关闭。`BeeheemothSoundCustomizer`只改变实体或三秒最近位置12格内的非相对Bee事件/解析声音。
- **效果：**可调颜色轮廓、临时黄色光柱、按选择音量播放的原生Bee声音。
- **默认/对外：**辅助、轮廓、光柱、声音开启，声音64%；不自动捕捉。

### 7.2 Lasso REEL提示

- **用途：**提示玩家现在可以收Lasso。
- **读取内容：**本地玩家拴绳关系和附近精确 `REEL` Armor Stand标签。
- **实现：**把本人拴住的实体与附近标签关联，只在 false→true 状态变化时播放一次。
- **效果：**进入REEL时响一次，不会每tick重复。
- **默认/对外：**开启，64%；不模拟输入、不收Lasso。

### 7.3 Critter Behavior Assistant

- **用途：**提示Blue Jay、Goldolot、Dustybit、Hideonsun特殊机制。
- **读取内容：**已加载实体名、本地移动、手持捕捉工具名、进度标签和精确捕捉确认。
- **实现：**有限最近实体选择与行为状态计算站立/机制完成；收到捕捉确认后只对刚捕捉的同名Critter抑制三秒，防止旧实体重播。
- **效果：**中央显示站立、跟随跳跃、反弹投射物或就绪提示。
- **默认/对外：**全部行为和独立音效开启，64%；只建议，不操作。

### 7.4 Fairy Soul

- **用途：**按需显示Torrhus/Safari已知仙女魂。
- **读取内容：**文档固定坐标、本地位置、岛屿与收到的成功/已找到确认。
- **实现：**`HuntingWorldRenderer`提交粉色原版Beacon Beam；确认后只隐藏10格内最近坐标，并按Profile保存。
- **效果：**未确认收集的坐标显示粉色光柱；成功点击后立即消失。
- **默认/对外：**总开关关闭；开启后两岛子开关预选；纯渲染。

## 8. Critter Safari

### 8.1 Dashboard与Critterdex

- **用途：**总结本轮Safari和当前Biome收集。
- **读取内容：**捕捉/聊天、Tab/计分板Ticket与Biome、本地本轮时长。
- **实现：**本轮累加器只统计成功解析的本人捕捉和Shard；官方37种Critter表提供Biome与Shard品质。Loot Share可以更新Wumpa组队条件，但不会污染本人Critterdex。
- **效果：**时间、Shard、Ticket Tier、Biome进度、完整已捕捉/未捕捉名称。Safari Essence不在此重复。
- **默认/对外：**所有行开启；只读。

### 8.2 Cold与篝火

- **用途：**危险Cold前预警并指示附近恢复点。
- **读取内容：**收到的Cold数值和已加载篝火Block Entity。
- **实现：**默认严格高于80、90各提示一次；第一次跨线立即在有限范围已加载区块选择最近篝火。Cold保持高且未下降时显示，收到更低值确认下降即关闭。
- **效果：**两档中央预警与最近篝火红色光柱。
- **默认/对外：**开启，阈值可调，独立声音64%；不移动或交互方块。

### 8.3 Doomspiral与Warden

- **用途：**提示已有至少4个Soothing Incense，并判断可见Warden捕捉冷却结束。
- **读取内容：**本地物品栏精确物品数量、有限场地内Warden年龄/姿态与本人延迟。
- **实现：**Incense达到4个只提示一次；`WardenCooldownSupport`使用140客户端tick规则并补偿延迟，排除emerging/digging姿态。
- **效果：**中央就绪文字与各自提示音；每个Warden每次就绪只提示一次。
- **默认/对外：**两者开启，64%；不使用物品、不捕捉。

### 8.4 Critter与Sparkling高亮

- **用途：**区分可抓捕实体和Sparkling事件。
- **读取内容：**已加载真实实体、可见名称、收到的Sparkling聊天与Shard品质表。
- **实现：**`EntityRendererMixin`只把真实非Armor Stand Critter加入原版outline；捕捉道具和支撑Armor Stand明确排除。Sparkling使用独立颜色与中央提示。
- **效果：**真实Critter按品质色描边，不出现整个人形支架；Sparkling颜色可调。
- **默认/对外：**开启，Sparkling音量64%；纯渲染。

### 8.5 Floor Drop与Quest Item

- **用途：**追踪已经可见的附近掉落与本地任务物品。
- **读取内容：**附近已加载String方块、实体/名称和本地物品栏。
- **实现：**有限周期扫描更新最近距离和精确数量；持续对象去重后再提醒。
- **效果：**中央提示及/或综合HUD距离、数量行。
- **默认/对外：**开启，64%；不拾取、不寻路、不交互。

### 8.6 Wumpa

- **用途：**追踪组队前置并可选预测可见冲撞路线。
- **读取内容：**本人捕捉、队友 `LOOT SHARE ... catching a <Critter>`、Wumpa生成/阶段文字、名称载体、真实Ravager身体、移动和本地碰撞。
- **实现：**八种Icy前置与本人Critterdex分开；8/8或精确生成信号后列表替换为 `Wumpa: Spawned`。可选路线寻找附近真实Ravager，经过短时移动/静止确认，并用本地碰撞裁剪红色前向线。
- **效果：**生成前显示勾/叉清单，生成后只显示生成与阶段；可选红色路线。
- **默认/对外：**HUD/提醒开启，路线关闭，声音64%；不移动、不捕捉。

### 8.7 Snoozle可撞墙

- **用途：**只标记可能可撞的墙面，而不是整片洞穴。
- **读取内容：**附近已经加载的方块状态。
- **实现：**每秒有限Flood Fill，只接受同时含Cobbled Deepslate和Tuff的小型连通块；拒绝单一材质和过大地形；只提交邻接空气的表面半透明四边形。`ClientLevel.hasChunk(chunkX, chunkZ)`保证不请求新区块。
- **效果：**墙体外露面出现薄半透明颜色，默认绿色，可RGB修改。
- **默认/对外：**开启；纯本地渲染。

### 8.8 Safari Belt

- **用途：**在Belt Tooltip中集中显示四个Milestone及实际收到的属性。
- **读取内容：**玩家当前打开的Safari Milestone菜单与Belt Tooltip/lore。
- **实现：**`SafariMilestoneParser`支持合并行与标题/lore分行，拒绝Locked和捕捉进度假等级；Cavern/Forest/Haunted/Icy独立更新，只有更高确认值才按账号/Profile保存。`SafariBeltTooltip`走正常Tooltip管线并读取收到的Bonus文字，不硬编码总值。
- **效果：**原版物品说明中嵌入四个等级和属性增益。
- **默认/对外：**开启；不会打开或点击菜单。

## 9. Combat

### Ender Dragon高亮

- **用途：**在The End更容易看到龙。
- **读取内容：**已加载Ender Dragon和计分板识别出的The End/Dragon's Nest。
- **实现：**`EntityRendererMixin`使用原版发光轮廓通道并返回玩家选择的RGB颜色。
- **效果：**干净可调色描边，不改变模型或Hitbox。
- **默认/对外：**开启；纯渲染。

## 10. Pet HUD

### 用途

无需打开Pets菜单即可显示佩戴宠物与关键进度。

### 读取内容

- 收到的召唤、收起与Autopet聊天。
- 收到的 `Pet:` Tab小组件作为周期真值。
- 已打开Pets菜单与确实匹配的附近宠物Profile。
- 从已检查NEU仓库离线生成并打包的Profile/皮肤/配件索引。

### 实现

`PetTracker`维护身份、品质、等级、经验；`PetSkinTracker`只确认匹配的Profile/皮肤/配件，不复用完整无关ItemStack。`PetHeadResources`构造普通player head且不添加合成 `petInfo`；精确和最长皮肤家族前缀匹配动态帧。`PetLeveling`处理品质偏移的100级曲线与Golden/Jade/Rose Dragon 200级曲线。确认的皮肤、配件和总经验按宠物本地保留。

### 效果

清晰3D头颅、品质色 `[Lvl N] Pet Name`、当前等级经验/百分比、可选至满级经验、皮肤名、溢出等级，以及“图标+名字/仅图标/仅名字”的宠物用品。大数保留一位并用 `k/m/b/t`；没有省略号。满级只隐藏多余至满级行，不隐藏配件。

### 默认/对外

全部信息行开启，配件默认图标+名字；纯读取与渲染；运行时不下载材质/API，不依赖Firmament。

## 11. Chat Peek

- **用途：**不打开聊天输入框也能临时查看历史。
- **读取内容：**自定义按住热键/鼠标组合和滚轮。
- **实现：**`ChatComponentMixin`在 `ChatPeekManager.active()` 时使用聚焦高度渲染；`MouseHandlerMixin`按设置把滚轮给聊天或保留给快捷栏。
- **效果：**只在按住时扩展聊天；默认滚轮翻聊天。
- **默认/对外：**功能开启但按键未绑定；不发送消息。

## 12. 物品栏与菜单

### 12.1 物品时间戳

- **用途：**显示创建时间和支持的完成倒计时。
- **读取内容：**本地ItemStack已经拥有的组件/lore。
- **实现：**`ItemTimestampTooltip`把收到的时间格式化为24小时、12小时、ISO或RFC并追加Tooltip。
- **效果：**正常物品说明下增加时间/倒数行。
- **默认/对外：**开启；纯Tooltip。

### 12.2 光标记忆

- **用途：**短时间重开兼容界面时回到上次位置。
- **读取内容：**本地Screen身份、鼠标坐标和时间。
- **实现：**`CursorPositionSaver`在容差内记录/恢复，不生成点击。
- **效果：**重开匹配界面时光标回到保存点。
- **默认/对外：**开启，500ms；只移动本地指针。

### 12.3 AOTE/AOTV声音

- **用途：**自定义而非默认静音Instant Transmission和Etherwarp。
- **读取内容：**手持SkyBlock物品ID、本地声音事件/解析路径、声源坐标和设置。
- **实现：**`SoundEngineMixin`只在手持AOTE/AOTV时把附近匹配声音交给 `TeleportSoundCustomizer`；可保持原版或播放一次选择的原版声音，并用线程标记避免递归。
- **效果：**默认原声；可选Chorus、Enderman、Amethyst、XP Orb、End Portal Fill或Shulker，并调音量/音调。
- **默认/对外：**两类保持VANILLA；纯本地声音。

### 12.4 Attribute Shard Fusion Guide

- **用途：**完整提供反向配方查询与正向用途查询，避免玩家猜测存在输入顺序区别的 Attribute Fusion 组合。
- **打包数据来源：**`assets/qcloudy_addition/data/shard_fusions.json` 在构建前从当前 [Hypixel SkyBlock Wiki Attributes](https://hypixelskyblock.minecraft.wiki/w/Attributes) 效果/获取表与 [Attribute Fusion](https://hypixelskyblock.minecraft.wiki/w/Attribute_Fusion) 规则离线生成；Shard 身份用 [SkyShards](https://github.com/Campionnn/SkyShards)、[NotEnoughUpdates 物品仓库](https://github.com/NotEnoughUpdates/NotEnoughUpdates-REPO) 和 [Hypixel 官方 Bazaar 产品列表](https://api.hypixel.net/v2/skyblock/bazaar) 交叉检查。320 张本地 Shard PNG 来自 SkyShards 审核 MIT commit `9688031dbc4e726168ffceb0f44884ff26e6e728` 的 `public/shardIcons`；源集合共 321 张，生成时按目录允许列表筛选并排除 Rainbug。
- **数据校准：**运行时目录必须严格包含 320 个官方 Bazaar Shard。相对过时的 317 项快照，补入 Anteater、Zombuddy、Troodon 与 Ghost Crab；Goldolot 使用 `R92`；Rainbug 因不在官方 Bazaar Shard 允许列表中而排除。Wiki Attributes 列表页面明确标注不完整/可能过时，因此只作为规则和属性说明，不作为数量权威。
- **实现：**`ShardFusionCatalog` 一次载入并校验随模组提交的 JSON，包括规范化富文本效果片段、获取方式、生物类型和语义颜色；搜索覆盖名称/ID/属性/效果/品质/分类/家族/Skill/生物类型/获取文字。有序输入索引同时服务 Recipes 与 Uses，因此拥有自然来源的 Shard（例如 Queen Bee）仍会显示全部 Fusion 配方。特殊规则对两种输入顺序对称检查；其余 ID 输出保留第一/第二输入顺序。Chameleon 按数字 ID 递增并在需要时滚入下一品质。`ShardItemResolver` 使用整次会话共享的原生 ItemStack 缓存：已经在打开菜单/物品栏收到的匹配物品会覆盖内置模型；未观察到的每个目录条目都解析到自身离线 Shard 纹理，不再回退成紫水晶。QCA 不发起 HTTP 或纹理请求；已经收到的玩家头继续交由 Minecraft 正常物品渲染管线处理。
- **数量逻辑：**Chameleon 消耗 `1`；Reptile、Amphibian、Elemental 消耗 `2`；其他 Shard 消耗 `5`。ID/Chameleon 结果产出 `1`，特殊规则结果产出 `2`；Pure Reptile 显示按收到等级计算的 2–20% 双倍产出概率。最多三个可选输出按真实顺序显示，且不会等于任一输入。
- **界面：**`ShardFusionScreen` 提供详细信息/合成来源/可合成内容标签、搜索结果、前进/后退历史、分页、物品图标、输入数量、候选输出、产量及明确顺序提示。详情显示完整效果与获取行，单独标注 Fusion-only，并在存在配方时显示已验证 Fusion 配方数量。Epic 使用 Minecraft `§5`；其他品质/属性/分类/生物类型/获取文字使用已审核语义颜色。鼠标悬停可点击 Shard 文字时只让可见文字变深并添加下划线。点击搜索框外、按 `Esc` 或 `Tab` 释放文字焦点，点击搜索框重新获得焦点。输入组合与候选输出按实际内容宽度紧凑居中，点击区域由相同可见边界生成。文字换行或缩放，不使用省略号。
- **入口/默认/对外：**功能默认开启。二级设置只包含“打开指南”和默认未绑定的键盘/鼠标组合键。本地 `/qshard [英文查询]` 打开同一界面并预填搜索。它不会发送服务器命令、聊天、数据包、菜单输入，也不会请求 Wiki、Bazaar 或任何网络资源。

### 12.5 Shard Planner、价格桥接与 Hunting Box 仓库

- **用途：**在保留原 Guide 作为精确直接配方参考的前提下，为目标 Shard/数量生成有深度限制、能阻止循环的多步路线。
- **路线引擎：**`ShardFusionPlanner` 对目录全部 Shard 与有序产物配方进行有限深度动态松弛。路线可以终止于直接狩猎速率、可选 Bazaar 购买、已观察仓库数量，或继续 Fusion。选中路线会展开为不可变 Tree，并提供候选方案、Fusion 次数、预计成本/时间及狩猎/购买/仓库材料表。展开过程有循环、深度、算术和节点数保护。Materials Only 只改变显示，不改变计算。
- **速率与 Kraken：**`shard_rates.json` 是从已审核 SkyShards 速率数据转换的版本化离线基线，并被强制要求与 320 个目录 ID 一一对应；玩家保存的本地单 Shard 速率优先覆盖。Hunter Fortune 只缩放正狩猎速率。Kraken 可用 Kuudra Tier、通关秒数、coins/hour 机会成本、钥匙成本、对应 Tier 倍率及 25 秒停顿推导速率。本地 Crocodile 等级控制 2–20% Pure Reptile 期望倍率，但整数材料仍按保守需求显示。
- **价格：**`ShardPriceService` 不含 HTTP 客户端。运行时先检查 Skyblocker 是否加载，再反射解析其公开静态 `de.hysky.skyblocker.utils.ItemUtils.getItemPrice(String, boolean)`。QCA 只复制 Skyblocker 现有客户端缓存返回值，形成不可变本地快照；方法缺失、链接失败、条目缺失或畸形值都会安全变为不可用。没有 Skyblocker 编译/运行硬依赖。SkyHanni 与 Firmament 当前没有稳定公开跨模组 Bazaar API，因此 QCA 不读取其私有字段；没有兼容提供者时 Cheapest 禁用，非价格功能保持独立。
- **仓库：**`ShardWarehouseManager` 每秒最多检查一次当前显示、客户端已经收到的容器。标题必须精确为 `Hunting Box` 或 `(当前页/总页数) Hunting Box`；每个 Shard 必须能解析到目录 ID/名称与精确 `Owned: N Shards` lore。只更新当前可见页面；零个识别条目的过渡帧会被忽略。页面按当前本地 Profile 与 Shard ID 合并，并通过临时文件替换保存到 `config/qcloudy_addition_shard_warehouse.json`。QCA 不发送 `/hb`、不请求另一页、不点击槽位、不读取隐藏背包。
- **界面与保存：**`ShardPlanningScreen` 提供 Plan、Recipes、Shards、Fusion Lines、Warehouse 与 Settings。直接配方可分别输入输入/输出筛选；Fusion 图节点位置可本地拖动；模式、目标、数量、自定义速率、图节点、价格侧选择、Kuudra 参数与 Materials Only 通过 QCA 配置持久化。Planner 只能显示资料，无法执行任何路线步骤。

## 13. 本地保存内容

- `config/qcloudy_addition.json`：语言、功能开关、HUD外观/位置/缩放、宠物确认信息、Hunting资源/Chapter/Benefactor/Safari Belt状态、已确认Fairy Soul，以及 Shard Planner 设置/速率/图节点。旧 `autumecloudyaddition.json` 只用于迁移。
- `config/qcloudy_addition_shard_warehouse.json`：玩家实际打开的 Hunting Box 页面中观察到的按本地 Profile Shard 数量与最后观察时间；不包含隐藏背包或服务器拉取数据。
- 配置先写临时文件，再尽可能原子替换。

QCA不会在磁盘保存密码、Token、Hypixel API Key、聊天历史、远程账号数据或重连地址。

## 14. 完整对外操作清单

| 玩家触发 | 精确行为 | 是否自动 |
|---|---|---|
| `/aca`、`/qca`、`/ca`、`/qc` | 打开本地QCA设置 | 无服务器载荷 |
| `/qshard [英文查询]` | 打开本地离线 Shard Fusion Guide 并预填搜索 | 无服务器载荷 |
| 玩家输入 `/th` | `sendCommand("warp torrhus")` | 否 |
| 玩家输入 `/helia` | `sendCommand("chapter torrhus")` | 否 |
| 玩家点击“重新连接” | 对本次内存中记录的目标发起一次普通Minecraft连接 | 否 |

`sendChat`：无。自动生成聊天：无。自动命令：无。自动移动、战斗、捕捉、物品使用、方块交互或重连：无。

## 15. 应如何验收

自动测试覆盖解析器、默认值、设置路由、持久化修复、边界计算和归档结构；本地启动覆盖Fabric独立运行与四个指定参考模组联合初始化。但它们不能证明未来所有Hypixel文字、实服实体排列、玩家材质包、GUI Scale、延迟环境或规则解释。因此从Beta升级Stable前，应按本文“应呈现效果”逐项在登录Hypixel的环境进行回归，并以 `VALIDATION_zh_CN.md` 中列出的剩余边界为准。

# 客户端与规则边界

## 运行时数据流

| 功能 | 客户端输入 | 本地输出 | 对外发送 |
|---|---|---|---|
| 矮人矿洞地图 | 玩家 X/Y/Z/yaw、解析到的可见子地点 | 固定 PNG 与箭头 HUD | 无 |
| 冰川隧道地图 | 玩家 X/Y/Z/yaw、解析到的地点 | 固定分层 PNG 与箭头 HUD | 无 |
| 挖矿任务/粉尘/HOTM 配置 | 已收到的 Tab 文本及玩家已打开菜单中的物品名/说明 | 文字与进度条 HUD、缓存的当前配置名 | 无 |
| Crimson Isle 任务 | 已收到的 `Faction Quests:` Tab 文本 | 文字 HUD | 无 |
| Torrhus Chapter/资源/Contest/Benefactor | 已收到的计分板、Tab、聊天与已打开 HOTF/菜单文字 | 自动换行 HUD、本地档位计算、中央标题 | 无 |
| Tree Critter 计时 | 最近已加载实体中严格匹配 `Critter in: <时间>` 的显示名称 | 综合 Hunting HUD 中的一行 | 无 |
| Beeheemoth 辅助 | 已加载 Bee 类型/scale/UUID、本地玩家位置、已收到捕捉确认、附近已收到的 Bee 声音 | 可调原版轮廓/信标与本地声音音量缩放 | 无 |
| Lasso REEL 提示音 | 本地玩家手持 Lasso、已收到的绳索持有者关系、附近精确 `REEL` ArmorStand | 状态切换时一次本地音效 | 无 |
| Tree Gift 预警 | 本人奖励汇总 `SHOW_TEXT` 与同一经过归属证明的 Gift 区块精确行，包括被取消显示的聊天 | 中央标题与本地音效 | 无 |
| Safari Dashboard/Critterdex | 已收到的聊天、Tab/计分板、本地会话时钟 | 综合 HUD | 无 |
| Sparkling/Wumpa/行为辅助 | 已收到捕捉/生成聊天、可见自定义名称/实体运动、玩家本地移动与死亡 | 中央标题、本地音效、条件/阶段 HUD、可选原版轮廓 | 无 |
| Cold/篝火安全辅助 | 已收到的 Cold 文本与已加载区块中的篝火 Block Entity | 中央标题、本地音效、最近篝火信标 | 无 |
| Doomspiral 条件提示 | 本地背包内容 | 持有 4 个以上 Soothing Incense 时中央提示与本地音效 | 无 |
| Warden 可抓捕预警 | 已加载 Warden 类型/位置/姿态/客户端年龄及收到的本地玩家延迟 | 140 tick 就绪转换时一次中央大字与本地音效 | 无 |
| Fairy Souls | 官方 Wiki 固定坐标与已解析的当前岛屿 | 可选粉色信标 | 无 |
| Safari Critter 品质高亮 | 客户端可见的实体自定义名称与内置官方品质表 | 原版实体轮廓色 | 无 |
| Wumpa 路线预测 | 可见 Wumpa 位置/移动与本地方块碰撞射线 | 可选红线 | 无 |
| Snoozle 可撞墙覆盖 | 附近已加载的 Cobbled Deepslate/Tuff 方块状态 | 半透明暴露表面覆盖 | 无 |
| Floor Drop/Quest Item | 已加载的附近方块状态与本地背包 | 距离/物品 HUD 与中央标题 | 无 |
| Safari Belt | 已收到的物品 ID/说明和玩家已打开菜单中的物品 | Tooltip 与按账号/Profile 保存的本地配置缓存 | 无 |
| 末影龙高亮 | 已收到的末影龙实体和地点 | 原版轮廓渲染状态 | 无 |
| 宠物 HUD | 已收到的聊天与 Tab 文本 | 文字 HUD | 无 |
| 聊天偷窥 | 玩家真实按住按键及客户端已收到的聊天历史 | 临时改变本地聊天渲染与滚轮目标 | 无 |
| AOTE/AOTV 声音自定义 | 手持物品 ID 与客户端收到的附近声音事件 | 保留原声，或按设置的音量/音调替换为本地原版声音 | 无 |
| 配置 | 可改绑本地按键、本地 `/aca`/`/qca`/`/ca`/`/qc` 与鼠标输入 | JSON 配置文件 | 无 |
| 手动重连 | 上一次正常 `ConnectScreen` 目标与玩家在断线页的明确点击 | 打开新的原版连接界面 | 仅点击后向已记录目标发起一次正常服务器连接 |
| Torrhus 快捷命令 | 玩家主动输入本地 `/th` | 无 | 发送精确内容 `warp torrhus` |
| Helia 快捷命令 | 玩家主动输入本地 `/helia` | 无 | 发送精确内容 `chapter torrhus` |
| 菜单中键转换 | 玩家在已打开菜单中的真实鼠标输入 | 等效中键菜单激活 | 仅发送对应容器操作；中键替代默认关闭且默认只转换左键 |

## 命令与聊天

- 本地设置命令：`/aca`、`/qca`、`/ca`、`/qc`；若其他客户端命令已占用某个根名称，则跳过该别名。它们只打开 QCA 设置，不发送内容。
- 本地 Torrhus 快捷命令：`/th`；没有设置项且无法关闭。玩家明确输入时，QCA 发送精确内容 `warp torrhus`，等同手动输入 `/warp torrhus`；只有在其他客户端命令已经占用 `/th` 时才跳过注册。
- 本地 Helia 快捷命令：`/helia`；没有设置项。玩家明确输入时，QCA 发送精确内容 `chapter torrhus`，等同手动输入 `/chapter torrhus`；只有在其他客户端命令已经占用 `/helia` 时才跳过注册。
- 自动 `sendCommand` 调用：**没有**。
- `sendChat` 调用：**没有**。
- 自动生成的聊天内容：**没有**。

## 网络与自动化审计

QCA 不包含 Hypixel Mod API、Hypixel 公共 API、WebSocket、HTTP 客户端、遥测、坐标共享、远程更新器、宏、模拟输入、自动点击/移动或方块交互。唯一的对外动作是上面明确记录、由玩家触发的 `/th` 传送命令、`/helia` Chapter 命令、中键转换容器操作，以及玩家点击“重新连接”后的一次普通服务器连接；它们都不会自行运行。重连没有倒计时、重试循环、后台尝试或自动加入。

Hunting HUD 与追踪器没有任何对外发送路径：不会发送命令/聊天、请求区块、修改计分板 Objective、选取目标、投掷工具/Capsule、移动玩家或交互 Floor Drop、篝火、Critter、墙体、Fairy Soul。进度记忆只是本地 JSON，以本地账号 UUID 和收到的 Profile 标签为键，只保存客户端曾经收到的 Chapter/资源/Safari Milestone/Benefactor 值，并在观察值改变时更新。Chapter 会分别限制 Tab、计分板、已打开菜单及短时收到的聊天块，不扫描任意缓存文字；Benefactor 同样只读取有限的 Tab/计分板/聊天/菜单文字，其到期时间只是对收到时长进行本地计算，不会引发服务器动作。Tree Critter 计时只读取已经加载的实体显示名称，不检测点击、不消耗 Pot，也不合成本地倒数。Beeheemoth 使用指定参考模组相同的 scale-9 已加载 Bee 特征；固定光柱只由本地距离、已收到捕捉确认或实体消失移除，并且只在本地缩放空间相关的 Bee 系声音。Lasso 提示只读取已收到的拴绳关系与附近精确显示文字，然后播放本地声音。Wumpa 组队前置集合由本人锚定捕捉确认和客户端收到的队友 Loot Share 捕捉文字更新；单独的本人 Critterdex 仍排除 Loot Share。生成消息与 8/8 完成共用每轮一个提醒标记，路线只跟踪已加载 Ravager 身体和本地碰撞。Snoozle 覆盖每秒只检查附近已加载方块，拒绝过大或单一材质组件，只渲染本地暴露表面。Warden 就绪只读取有限场地内客户端可见的实体年龄/姿态和本地连接延迟，不修改实体，也不会发出捕捉动作。Tree Gift 接受本人精确汇总 hover；独立精确 Bonus 行只有在同一个有限收到区块证明本人贡献与奖励汇总后才有效，被取消显示的消息仍属于客户端已经收到的数据，附近玩家单独公开行依旧无效。Fairy Soul 光柱只会在收到成功/已经找到确认并通过有限的最近坐标匹配后隐藏。篝火搜索只检查原版已经加载区块中的 Block Entity。Miria 结果只在 QCA 综合 HUD 中显示，侧栏注入与竞赛倒计时重复显示均已删除；`/th` 与 `/helia` 是上方单独记录、必须由玩家输入的快捷命令。

## Hypixel 规则说明

实现严格限制为被动客户端数据与渲染，这可以降低反作弊和交互风险，但不等于获得 Hypixel 官方批准。Hypixel 当前说明强调：所有模组均由玩家自行承担风险；提供明显优势或未明确列出的功能不保证允许。请在使用前阅读最新官方规则，并关闭任何自己不确定的功能：

实体轮廓、信标点位、墙体覆盖与运动预测是本模组规则风险最高的部分，因为它们会让世界信息更容易观察。它们虽然只做被动渲染，但“只渲染”不等于必然允许。因此 Wumpa 路线和 Fairy Soul 信标默认关闭；按用户要求默认开启的 Critter 品质轮廓、Cold 篝火信标和 Snoozle 墙体覆盖也各自提供总开关。

- [Hypixel Allowed Modifications](https://support.hypixel.net/hc/en-us/articles/6472550754962-Hypixel-Allowed-Modifications)
- [Hypixel SkyBlock Rules](https://support.hypixel.net/hc/en-us/articles/4508088842898-Hypixel-SkyBlock-Rules)

# 发布检查清单

## 统一项目字段

| 字段 | 内容 |
|---|---|
| 名称 | QCloudy_Addition |
| 建议slug | `qcloudy-addition` |
| 版本 | `Alpha-2.6.14+26.1.2` |
| 发布通道 | Alpha；首次统一设置/HUD 提供方实现 |
| 环境 | 纯客户端 |
| Loader | Fabric |
| Minecraft | 26.1.2 |
| Java | 25 |
| 许可证 | LGPL-3.0-or-later |
| 必需依赖 | Fabric API 0.155.2+26.1.2或更高 |
| 可选依赖 | Mod Menu 18.0.0 |
| 不依赖 | Firmament、SkyHanni、Skyblocker、BabyZombieAddons；只提供可选精确版本集成 |

建议Modrinth简短描述：

> 面向 Fabric 26.1.2 的纯客户端 Hypixel SkyBlock 地图、追踪、Pet HUD、离线 Shard Fusion 配方与可自定义视觉提示。

建议 Modrinth 分类：Utility、Optimization、Game Mechanics。

建议GitHub Topics：`minecraft`、`fabric`、`hypixel-skyblock`、`skyblock`、`client-side`、`hud`、`minecraft-mod`、`java`。

## Modrinth

- 英文主描述使用 `docs/MODRINTH_DESCRIPTION.md`。
- 中文页面或英文描述下方使用 `docs/MODRINTH_DESCRIPTION_zh_CN.md`。
- 只把 `release/QCloudy_Addition-Alpha-2.6.14+26.1.2.jar` 标为可运行主文件。
- 版本更新日志使用 `CHANGELOG.md` 的 `2.6.14` 段落，中文配套内容使用 `CHANGELOG_zh_CN.md` 对应段落。
- Fabric API标为必需，Mod Menu标为可选。
- Client环境标为必需，Server标为不支持。
- 不要把Firmament、SkyHanni、Skyblocker或BabyzombieAddons标为依赖。
- 至少准备设置总览、HUD编辑器、Dwarven地图、Glacite地图、Mining HUD、Torrhus综合HUD、Safari HUD和Pet HUD截图。
- 截图不得暴露玩家UUID、私人聊天、服务器IP、Session或其他玩家隐私。

## GitHub仓库

- `README.md`作为默认英文首页，`README_zh_CN.md`作为中文版本。
- 保留 `LICENSE`、`THIRD_PARTY_NOTICES.md`、`CHANGELOG.md`、`CHANGELOG_zh_CN.md` 和完整 `docs/`。
- 创建真实仓库、Issue Tracker和Modrinth页面后再写入对应URL，不发布占位链接。
- 将元数据中的通用作者 `QCloudy_Addition contributors` 换成最终公开作者/团队名；只有用户确定后才加入公开联系方式与支持链接。
- 开启Issues，并要求Bug报告附Minecraft/Fabric/QCA版本、模组列表、日志、复现步骤和截图。
- 不提交 `run/`、`run-standalone/`、`.gradle/`、`.gradle-user-home/`、本地配置、日志、崩溃ZIP或四个参考JAR。
- 首次commit前确认 `.gitignore` 覆盖本地构建/运行文件。

## GitHub Alpha 2.6.14

- 标题：`QCloudy_Addition Alpha 2.6.14 for Minecraft 26.1.2`
- Tag：`v2.6.14-alpha+26.1.2`
- 正文以 `CHANGELOG.md` 和 `CHANGELOG_zh_CN.md` 的 `2.6.14` 段落为准。
- 上传二进制JAR，可选附加Sources JAR。
- 上传后重新下载一次，并与 `docs/VALIDATION.md` 中哈希比较。
- GitHub 勾选 **Pre-release**，因为这是 Alpha 而不是稳定 Release。
- Modrinth 的 Version type 选择 **Alpha**。

## 最终安全与质量门槛

- 任何代码、资源、元数据或版本变化后都用Java 25重跑 `clean test build prepareRelease`。
- 重跑 `jar --validate`、`unzip -t`、元数据、class major与release/build哈希检查。
- 从本次新生成的 XML 统计最终通过测试数，不复用 2.5.4 的数量。
- 确认恰好打包 320 张 Shard 纹理、320 个物品模型定义与 320 个物品定义；目录和图标 ID 集合完全一致且不存在 Rainbug。
- 确认点击外部、`Esc` 与 `Tab` 均可退出搜索焦点，点击搜索可恢复输入，而且配方导航快捷键不会被搜索框持续拦截。
- 在支持的 GUI Scale 下确认紧凑输入/输出边界与点击范围保持对齐。
- 确认 Epic 使用 `§5`、详情页完整换行显示效果/获取方式、悬停样式只作用于可见可点击文字，并且自然＋Fusion 双来源 Shard 同时显示两类来源。
- 至少启动一次独立实例和一次四参考模组实例。
- 四模组实例中逐项确认提供方切换、完全等价功能互斥、原生持久化、未知版本安全关闭，以及不同 GUI Scale 下每个第三方 HUD 的位置/缩放。
- 重新核对 `docs/COMPLIANCE_zh_CN.md` 中每个命令/聊天载荷。
- 确认上传图标四角透明，32×32下仍可识别。
- README不得声称获得Hypixel官方批准、绝对安全或完成全部登录实服验证。
- 确认公开作者名、源码URL、Issue URL和Modrinth URL已经最终确定；这些是当前唯一刻意没有替用户虚构的发布字段。

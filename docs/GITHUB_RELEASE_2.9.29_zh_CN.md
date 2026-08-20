# QCloudy_Addition Beta 2.9.29（Minecraft 26.1.2 与 26.2）

Beta 2.9.29 将 QCA 的两套兼容模组统一管理功能明确分开，并提供互相独立的总开关。

## 相比 Beta 2.9.28 的变化

- 在 **通用 -> 兼容模组** 中明确加入 **管理其他模组功能设置** 总开关，用于管理已安全识别的其他模组功能设置。
- **管理其他模组 HUD** 保持为独立总开关，只负责已安全识别的外部 HUD 位置。
- “兼容模组”分组默认展开，避免将这两个入口与 QCA 自身的 **编辑 HUD** 按钮混淆。
- 两套集成都仍为自愿启用，默认关闭；分别保留二次确认、扫描进度、Refresh 和未知成员失败关闭逻辑。
- 新增回归测试，确保两个独立入口以后不会再次遗漏。

## 兼容性与安全边界

- QCloudy_Addition 仍是可独立运行的纯客户端 Fabric 模组。
- SkyHanni、Skyblocker、Firmament、BabyZombieAddons 与 Feesh 是可选提供方，不是依赖。
- 只有在玩家确认后，兼容扫描才会读取已安装的客户端类与本地配置；不会发送服务器指令，也不会访问外部服务。
- 无法安全识别或已经变化的提供方成员会被省略并列入兼容缺口，不会猜测写入。

## 下载文件

可运行模组：

- `QCloudy_Addition-Beta-2.9.29+26.1.2.jar`
- `QCloudy_Addition-Beta-2.9.29+26.2.jar`

开发者 Sources：

- `QCloudy_Addition-Beta-2.9.29+26.1.2-sources.jar`
- `QCloudy_Addition-Beta-2.9.29+26.2-sources.jar`

只安装与你的 Minecraft 版本对应的可运行 JAR；不要把 `-sources.jar` 当作模组安装。

两个目标均通过 193 项自动测试，且没有失败，并完成元数据与归档验证。已登录 Hypixel 的实服测试和完整兼容模组组合的视觉回归仍需手动完成，详见 `docs/VALIDATION_zh_CN.md`。

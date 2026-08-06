# Storage 缓存崩溃分析 — 2026-08-04

## 证据

两份 ZIP 的 SHA-256 完全相同，均为 `8abff84c45b6b2ecb8ffada8de514a446755c70fc2d1ff6f853d47a24811a5d7`，因此它们是同一次崩溃的重复导出；分析时没有修改原文件。导出包含日志和崩溃报告，但没有 `qcloudy_addition-storage.nbt`，所以无法离线还原究竟是哪一个物品堆栈触发了问题。

崩溃环境是 Windows 11、Minecraft 26.1.2、Java 25.0.1 和 QCloudy_Addition 1.2.5。决定性的异常是：

```text
java.lang.IllegalStateException: Element Reference{ResourceKey[minecraft:enchantment / minecraft:efficiency]=Enchantment 效率} is not valid in current registry set
```

调用链从 `StorageCacheManager.encode` 进入 `saveAsync`、`tick`，最终在渲染线程的客户端 tick 回调中崩溃。客户端当时已运行约 30 分钟。它不是内存不足、显卡、材质包或 Mixin 应用失败。

## 根本原因

某个本地缓存 `ItemStack` 保留了属于旧动态注册表集合的“效率”附魔 Holder。ACA 保存缓存时，客户端已经在使用另一套当前注册表序列化环境；ACA 1.2.5 却让旧 Holder 通过这套新环境编码。`CompoundTag.store` 拒绝跨注册表引用，而未防护的异常直接逃出了渲染线程。日志能够证明注册表不匹配，但无法反推出此前究竟是哪一次世界/服务器生命周期事件最先让该缓存 Holder 失效。

## 1.2.8 中的修复

- 检测世界注册表集合变化，并按资源键重新绑定普通附魔与附魔书附魔。
- 每个物品在序列化前再次绑定到当前注册表。
- 在物品解码、搜索文本生成、内容指纹、编码和写盘路径逐物品隔离异常。
- 保留页面与槽位顺序；仍无法编码的物品只会变成空缓存槽位，其他物品与页面继续保存。
- 在整份快照编码外增加最后一道异常屏障，Storage 缓存序列化异常不能再终止渲染线程。
- 如果整份快照无法编码，仍保留当前内存缓存。
- 为正常与异常槽位编码加入回归测试。

升级时不需要手动删除旧缓存。如果遇到仍不受支持的旧物品，只可能暂时看到该缓存槽位为空；重新打开对应 Storage 页面后，会用客户端正常收到的背包内容刷新。

## 验证边界

修复已通过本地 Java 25 单元测试和构建检查。本地 macOS Loom 实例无法精确复现这份日志中的 Windows 已登录 Hypixel 注册表切换过程，因此仍建议在实服做一次相同的换岛、重连与 Storage 翻页回归。本修复没有增加数据包、命令、聊天、HTTP、自动化或服务器数据行为。

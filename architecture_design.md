# 架构设计文档 (Architecture Design)

## 1. 概述
本项目是一个适用于 Minecraft 1.21.11pre3 的纯客户端“一键整理容器”模组。模组旨在提供便捷的物品排序功能，无需服务端安装，仅在客户端运行。

## 2. 核心架构
本模组采用 Fabric 模组加载器，遵循纯客户端（Client-Only）架构设计。

### 2.1 模块划分
- **Client Entrypoint**: `name.modid.SortInventoryClient`
  - 负责模组的客户端初始化，包括配置加载和事件注册。
- **Core Logic**: `name.modid.client.SortInventoryLogic`
  - 封装了所有的排序算法和库存操作逻辑。
  - **Slot Filtering**: 过滤掉快捷栏（Hotbar，Slot 0-8），仅针对主背包（Slot 9-35）和外部容器进行排序。
  - **Sorting Algorithm**: 先按物品类别（工具 > 方块 > 食物 > 杂项），再按 Item ID 排序。
  - **Action Execution**: 通过模拟玩家点击（`handleInventoryMouseClick`）来执行物品交换。
- **Mixin Injection**: `name.modid.mixin.client.GuiButtonMixin`
  - 注入到 `AbstractContainerScreen` 的 `init` 方法。
  - 在容器界面右上角动态添加“S”排序按钮。
- **Configuration**: `name.modid.client.ConfigClient`
  - 管理客户端配置文件 `config/sortinventory-client-1.21.11pre3.json`。

### 2.2 关键设计决策
1.  **纯客户端兼容性**: 
    - 为了确保在任何服务器上都能安全使用，模组不发送自定义数据包，完全依赖原版库存操作协议（ClickWindow C2S）。
    - **稳定性优先**: 在 Pure Client 模式下，为了避免因网络延迟或服务端反作弊导致的物品同步问题，暂时禁用了复杂的堆叠（Merge）逻辑，采用“交换排序”（Swap Sort）策略。
2.  **安全性**:
    - 增加了 500ms 的操作冷却时间（Cooldown），防止因频繁点击被服务端判定为 Spam。
3.  **兼容性**:
    - 使用 Mojang Mappings 进行开发。
    - 适配 1.21.11pre3 版本的 API 变更（如 `DataComponents` 替代 NBT/TieredItem 检查）。

## 3. 数据流
1.  用户点击 GUI 按钮或按下快捷键（暂禁用）。
2.  `SortInventoryLogic.sort()` 被调用。
3.  检查冷却时间和当前容器状态。
4.  获取容器内的物品列表，进行内存排序。
5.  计算排序后的预期状态。
6.  通过一系列 `PICKUP` 点击操作，将实际库存调整为预期顺序。

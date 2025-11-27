# One-Click Container Sorting (Pure Client) / 一键整理容器 (纯客户端)

> **Disclaimer**: This mod was entirely written by an AI Assistant.
> **注意**：本模组完全由 AI 助手编写。

A lightweight, pure client-side Minecraft mod for Fabric 1.21.11-pre3 that allows you to organize your inventory and containers with a single click.
一个轻量级的 Minecraft Fabric 1.21.11-pre3 纯客户端模组，让你可以一键整理背包和容器。

## Features / 功能特性

- **Pure Client-Side / 纯客户端**:
  Works on multiplayer servers even if the server doesn't have the mod installed.
  即使服务器没有安装此模组，也可以在多人游戏中使用。

- **Smart Sorting / 智能排序**:
  - **Stack Merging / 堆叠合并**: Automatically merges incomplete stacks to save space.
    自动合并未堆叠满的物品以节省空间。
  - **Category Sorting / 分类排序**: Tools/Armor > Blocks > Consumables > Others.
    按优先级排序：工具/装备 > 方块 > 消耗品 > 其他。
  - **Advanced Tool Sorting / 高级工具排序**: Sorts by type (Mace > Trident > Sword...) and material (Netherite > Diamond...).
    按类型（重锤 > 三叉戟 > 剑...）和材质（下界合金 > 钻石...）精细排序。
  - **Durability Sorting / 耐久度排序**: High durability items come first.
    耐久度高的物品排在前面。

- **Convenient UI / 便捷 UI**:
  - Adds a sorting button "S" to supported containers (Chests, Shulker Boxes, Player Inventory).
    在支持的容器（箱子、潜影盒、玩家背包）中添加一个 "S" 整理按钮。
  - Button is unobtrusively placed in the container's right margin.
    按钮放置在容器界面的右侧空白处，不会遮挡内容。

- **Safety / 安全性**:
  - **Hotbar Protection / 快捷栏保护**: Does not sort items in your hotbar (slots 0-8).
    不会打乱你快捷栏（0-8格）中的物品。
  - **Container Isolation / 容器隔离**: Sorts player inventory and external containers separately to prevent mixing items.
    独立整理玩家背包和外部容器，防止物品混淆。
  - **Smart Filtering / 智能过滤**: Disabled for Dispensers, Hoppers, and other automation blocks to prevent accidents.
    在发射器、漏斗等自动化方块中禁用，防止意外操作。

## Installation / 安装指南

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.11-pre3.
   安装适用于 Minecraft 1.21.11-pre3 的 [Fabric Loader](https://fabricmc.net/)。
2. Download the `test-inv-1.0.0.jar` file.
   下载 `test-inv-1.0.0.jar` 文件。
3. Place the jar file in your `.minecraft/mods` folder.
   将 jar 文件放入你的 `.minecraft/mods` 文件夹中。
4. Launch the game!
   启动游戏！

## Usage / 使用方法

- Open any supported container (Chest, Barrel, Shulker Box) or your Inventory (E).
  打开任意支持的容器（箱子、木桶、潜影盒）或你的背包（E键）。
- Click the **"S"** button on the right side of the GUI.
  点击界面右侧的 **"S"** 按钮。

## Configuration / 配置

- The mod is client-side only. No server configuration required.
  本模组仅需客户端安装，无需服务器配置。

## Building from Source / 源码构建

1. Clone the repository:
   克隆仓库：
   ```bash
   git clone https://github.com/HaiTangBB/SimpleInventorySort-Client.git
   ```
2. Build with Gradle:
   使用 Gradle 构建：
   ```bash
   ./gradlew build
   ```
3. Find the jar in `build/libs`.
   在 `build/libs` 目录下找到生成的 jar 文件。

## License / 许可证

This project is open source.
本项目开源。

# One-Click Container Sorting (Pure Client)

A lightweight, pure client-side Minecraft mod for Fabric 1.21.11-pre3 that allows you to organize your inventory and containers with a single click.

## Features

- **Pure Client-Side**: Works on multiplayer servers even if the server doesn't have the mod installed.
- **Smart Sorting**:
  - **Stack Merging**: Automatically merges incomplete stacks to save space.
  - **Category Sorting**: Tools/Armor > Blocks > Consumables > Others.
  - **Advanced Tool Sorting**: Sorts by type (Mace > Trident > Sword...) and material (Netherite > Diamond...).
  - **Durability Sorting**: High durability items come first.
- **Convenient UI**:
  - Adds a sorting button "S" to supported containers (Chests, Shulker Boxes, Player Inventory).
  - Button is unobtrusively placed in the container's right margin.
- **Safety**:
  - **Hotbar Protection**: Does not sort items in your hotbar (slots 0-8).
  - **Container Isolation**: Sorts player inventory and external containers separately to prevent mixing items.
  - **Smart Filtering**: Disabled for Dispensers, Hoppers, and other automation blocks to prevent accidents.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.11-pre3.
2. Download the `test-inv-1.0.0.jar` file.
3. Place the jar file in your `.minecraft/mods` folder.
4. Launch the game!

## Usage

- Open any supported container (Chest, Barrel, Shulker Box) or your Inventory (E).
- Click the **"S"** button on the right side of the GUI.
- Alternatively, press **R** (configurable in Key Bindings) to sort.

## Configuration

- The mod is client-side only. No server configuration required.

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/SimpleInventorySort.git
   ```
2. Build with Gradle:
   ```bash
   ./gradlew build
   ```
3. Find the jar in `build/libs`.

## License

This project is open source.

package name.modid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

import java.util.*;

public class SortInventoryLogic {
    private static long lastSortTime = 0;

    public static void sort() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.player.containerMenu == null) return;

        long now = System.currentTimeMillis();
        if (now - lastSortTime < 500) return;
        lastSortTime = now;

        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
        doSort(client);
    }

    private static void doSort(Minecraft client) {
        var handler = client.player.containerMenu;
        boolean isInventoryMenu = handler instanceof InventoryMenu;
        
        List<Integer> externalSlots = new ArrayList<>();
        List<Integer> playerSlots = new ArrayList<>();
        
        // 1. Filter Slots
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            if (!slot.isActive()) continue;

            if (slot.container instanceof Inventory) {
                // Slot index in the Inventory object
                // 0-8 is Hotbar. 9-35 is Main Inventory.
                int invIndex = slot.index;
                if (invIndex >= 9 && invIndex <= 35) {
                    playerSlots.add(i);
                }
            } else {
                // If we are in the player's own inventory menu, do NOT treat non-player-inventory slots (crafting grid) as sortable external storage.
                if (isInventoryMenu) continue;

                // External Container (Chest, etc.)
                externalSlots.add(i);
            }
        }

        // Determine which set to sort
        List<Integer> targetSlotIds;

        if (!externalSlots.isEmpty()) {
            // If external container is present, sort ONLY the external container
            targetSlotIds = externalSlots;
        } else {
            // Otherwise (e.g. Player Inventory screen), sort the player inventory
            targetSlotIds = playerSlots;
        }

        if (targetSlotIds.isEmpty()) return;

        // 2. Pre-Merge Stacks (In-Place)
        mergeStacksInPlace(client, handler.containerId, targetSlotIds);

        // 3. Read Items (after merge)
        List<ItemStack> stacks = new ArrayList<>();
        // We need to fetch items again because merge changed them
        for (int id : targetSlotIds) {
            stacks.add(handler.getSlot(id).getItem().copy());
        }

        // 4. Sort
        List<ItemStack> sorted = new ArrayList<>(stacks);
        sorted.sort(new ItemComparator());

        // 5. Apply Changes (Swap Sort)
        applySort(client, handler.containerId, targetSlotIds, sorted);
    }

    private static void mergeStacksInPlace(Minecraft client, int syncId, List<Integer> slotIds) {
        var handler = client.player.containerMenu;
        
        for (int i = 0; i < slotIds.size(); i++) {
            int slotIndexA = slotIds.get(i);
            ItemStack stackA = handler.getSlot(slotIndexA).getItem();
            
            if (stackA.isEmpty() || stackA.getCount() >= stackA.getMaxStackSize()) continue;

            for (int j = i + 1; j < slotIds.size(); j++) {
                int slotIndexB = slotIds.get(j);
                ItemStack stackB = handler.getSlot(slotIndexB).getItem();

                if (ItemStack.isSameItemSameComponents(stackA, stackB)) {
                    // Click B (Pickup)
                    click(client, syncId, slotIndexB);
                    // Click A (Merge)
                    click(client, syncId, slotIndexA);
                    
                    // If we are holding something (remainder), put it back to B
                    ItemStack carried = client.player.containerMenu.getCarried();
                    if (!carried.isEmpty()) {
                        click(client, syncId, slotIndexB);
                    }
                    
                    // Update local stackA reference because it grew
                    stackA = handler.getSlot(slotIndexA).getItem();
                    if (stackA.getCount() >= stackA.getMaxStackSize()) break;
                }
            }
        }
    }

    private static void applySort(Minecraft client, int syncId, List<Integer> slotIds, List<ItemStack> sortedItems) {
        List<ItemStack> virtualInv = new ArrayList<>();
        for (int id : slotIds) {
            virtualInv.add(client.player.containerMenu.getSlot(id).getItem().copy());
        }

        while (sortedItems.size() < slotIds.size()) {
            sortedItems.add(ItemStack.EMPTY);
        }

        for (int i = 0; i < slotIds.size(); i++) {
            ItemStack wanted = sortedItems.get(i);
            ItemStack current = virtualInv.get(i);

            if (areStacksEqual(wanted, current)) continue;

            int foundIndex = -1;
            for (int j = i + 1; j < slotIds.size(); j++) {
                if (areStacksEqual(wanted, virtualInv.get(j))) {
                    foundIndex = j;
                    break;
                }
            }

            if (foundIndex != -1) {
                int slotA = slotIds.get(i);
                int slotB = slotIds.get(foundIndex);
                
                click(client, syncId, slotA);
                click(client, syncId, slotB);
                click(client, syncId, slotA);
                
                ItemStack temp = virtualInv.get(i);
                virtualInv.set(i, virtualInv.get(foundIndex));
                virtualInv.set(foundIndex, temp);
            }
        }
    }
    
    private static void click(Minecraft client, int syncId, int slotId) {
        client.gameMode.handleInventoryMouseClick(syncId, slotId, 0, ClickType.PICKUP, client.player);
    }

    private static boolean areStacksEqual(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) return true;
        if (a.isEmpty() || b.isEmpty()) return false;
        return ItemStack.isSameItemSameComponents(a, b) && a.getCount() == b.getCount();
    }

    static class ItemComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            if (o1.isEmpty() && o2.isEmpty()) return 0;
            if (o1.isEmpty()) return 1;
            if (o2.isEmpty()) return -1;

            // 1. Category Priority
            int cat1 = getCategory(o1);
            int cat2 = getCategory(o2);
            if (cat1 != cat2) return Integer.compare(cat1, cat2);

            // 2. Sub-sorting based on category
            if (cat1 == 0) { // Tools/Armor
                return compareTools(o1, o2);
            }
            
            // 3. General Sorting for others
            // Type -> Stack/Quality (Count) -> ID
            int idComp = BuiltInRegistries.ITEM.getKey(o1.getItem()).compareTo(BuiltInRegistries.ITEM.getKey(o2.getItem()));
            if (idComp != 0) return idComp;
            
            // Stack size (Descending)
            return Integer.compare(o2.getCount(), o1.getCount());
        }

        private int getCategory(ItemStack stack) {
            Item item = stack.getItem();
            String name = BuiltInRegistries.ITEM.getKey(item).getPath();
            
            // 0: Tool/Weapon/Armor
            if (isToolOrArmor(name) || stack.has(DataComponents.TOOL)) return 0;
            // 1: Block
            if (item instanceof BlockItem) return 1;
            // 2: Consumables
            if (stack.has(DataComponents.FOOD) || name.contains("potion") || name.contains("apple") || name.contains("bread")) return 2;
            // 3: Others
            return 3;
        }
        
        private boolean isToolOrArmor(String name) {
            return name.contains("sword") || name.contains("axe") || name.contains("pickaxe") || 
                   name.contains("shovel") || name.contains("hoe") || name.contains("bow") || 
                   name.contains("mace") || name.contains("trident") || name.contains("shield") ||
                   name.contains("helmet") || name.contains("chestplate") || name.contains("leggings") || name.contains("boots") ||
                   name.contains("elytra");
        }

        private int compareTools(ItemStack o1, ItemStack o2) {
            // Type Order: Mace -> Spear -> Sword -> Axe -> Pickaxe -> Shovel -> Hoe -> Bow -> Helmet -> Chest -> Legs -> Boots
            String name1 = BuiltInRegistries.ITEM.getKey(o1.getItem()).getPath();
            String name2 = BuiltInRegistries.ITEM.getKey(o2.getItem()).getPath();
            
            int type1 = getToolTypePriority(name1);
            int type2 = getToolTypePriority(name2);
            if (type1 != type2) return Integer.compare(type1, type2);
            
            // Material Order: Netherite -> Diamond -> Iron -> Stone -> Wood -> Gold
            int mat1 = getMaterialPriority(name1);
            int mat2 = getMaterialPriority(name2);
            if (mat1 != mat2) return Integer.compare(mat1, mat2);
            
            // Durability: High to Low (Damage: Low to High)
            int dam1 = o1.getDamageValue();
            int dam2 = o2.getDamageValue();
            return Integer.compare(dam1, dam2);
        }
        
        private int getToolTypePriority(String name) {
            if (name.equals("mace")) return 0;
            if (name.contains("trident")) return 1;
            if (name.contains("sword")) return 2;
            if (name.contains("axe") && !name.contains("pickaxe")) return 3; // "pickaxe" contains "axe" check? No, "pickaxe" contains "axe" substring? No. "pickaxe" vs "axe".
            // "pickaxe" contains "axe"? Yes. "pickaxe".contains("axe") is true.
            // So we must check pickaxe BEFORE axe or check !name.contains("pickaxe")
            
            if (name.contains("pickaxe")) return 4;
            if (name.contains("axe")) return 3; // Swapped order in logic: check pickaxe first?
            // Wait, if I check "pickaxe" first, it returns 4. If I check "axe" first (and it matches pickaxe), it returns 3.
            // User wants: Sword(2) -> Axe(3) -> Pickaxe(4).
            // So Axe comes BEFORE Pickaxe.
            // But "pickaxe" contains "axe". So if I check `if (name.contains("axe"))`, it will match "pickaxe" too.
            // So I must exclude "pickaxe" when checking "axe".
            
            if (name.contains("shovel")) return 5;
            if (name.contains("hoe")) return 6;
            if (name.contains("bow") || name.contains("crossbow")) return 7;
            
            if (name.contains("helmet")) return 8;
            if (name.contains("chestplate")) return 9;
            if (name.contains("leggings")) return 10;
            if (name.contains("boots")) return 11;
            
            return 12;
        }
        
        private int getMaterialPriority(String name) {
            // Netherite(0) -> Diamond(1) -> Iron(2) -> Stone(3) -> Wood(4) -> Gold(5) -> Other(6)
            if (name.contains("netherite")) return 0;
            if (name.contains("diamond")) return 1;
            if (name.contains("iron")) return 2;
            if (name.contains("stone") || name.contains("chainmail")) return 3;
            if (name.contains("wood") || name.contains("wooden") || name.contains("leather")) return 4;
            if (name.contains("gold") || name.contains("golden")) return 5;
            
            return 6;
        }
    }
}

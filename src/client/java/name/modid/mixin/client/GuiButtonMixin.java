package name.modid.mixin.client;

import name.modid.client.SortInventoryLogic;
import name.modid.client.ConfigClient;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, priority = 999)
public abstract class GuiButtonMixin {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected AbstractContainerMenu menu;

    // Use "init" but with a safer injection point
    @Inject(method = "init", at = @At("RETURN"))
    private void initSortButton(CallbackInfo ci) {
        // Debug Log
        System.out.println("[SortInventory] Init Sort Button. Screen: " + this.getClass().getSimpleName());

        if (!ConfigClient.get().showGuiButton) {
             System.out.println("[SortInventory] Button disabled in config.");
             return;
        }

        // Allowlist check: Only allow specific storage containers and player inventory
        // Allowed: Inventory (E key), Chests (Single/Double/Barrel/EnderChest), Shulker Boxes
        // Blocked: Furnace, Dispenser, Hopper, etc.
        boolean isAllowed = this.menu instanceof InventoryMenu ||
                            this.menu instanceof ChestMenu ||
                            this.menu instanceof ShulkerBoxMenu;

        if (!isAllowed) {
            System.out.println("[SortInventory] Button disabled for this menu type: " + this.menu.getClass().getSimpleName());
            return;
        }

        // Position: Right side of the container background (outside)
        // Align roughly with the first slot row (approx y + 17) or slightly lower as per user request
        int x = this.leftPos + this.imageWidth + 2; 
        int y = this.topPos + 20; 

        Button button = Button.builder(Component.literal("S"), (btn) -> {
            SortInventoryLogic.sort();
        })
        .pos(x, y)
        .size(20, 20)
        .tooltip(Tooltip.create(Component.literal("一键整理")))
        .build();

        // Use Accessor to call protected method addRenderableWidget
        ((ScreenAccessor) (Object) this).invokeAddRenderableWidget(button);
        System.out.println("[SortInventory] Button added at: " + x + ", " + y);
    }
}

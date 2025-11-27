package name.modid.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindingClient {
    public static KeyMapping sortKey;

    public static void register() {
        /*
        sortKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.sortinventory.sort", // Translation key
                GLFW.GLFW_KEY_R, // Default R
                "category.sortinventory.title" // Category
        ));
        */
    }
}

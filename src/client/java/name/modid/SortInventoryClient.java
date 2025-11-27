package name.modid;

import name.modid.client.ConfigClient;
import name.modid.client.KeyBindingClient;
import name.modid.client.SortInventoryLogic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SortInventoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigClient.load();
        KeyBindingClient.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBindingClient.sortKey != null) {
                while (KeyBindingClient.sortKey.consumeClick()) {
                    SortInventoryLogic.sort();
                }
            }
        });
    }
}

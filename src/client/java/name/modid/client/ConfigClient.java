package name.modid.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigClient {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "sortinventory-client-1.21.11pre3.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigData instance;

    public static class ConfigData {
        public boolean showGuiButton = true;
        public List<String> sortOrder = Arrays.asList("tool", "block", "food", "item", "misc");
        // Keybinding is handled by Fabric KeyBinding API, usually stored in options.txt, 
        // but we can store custom preferences here if needed. 
        // For this task, we'll stick to standard keybinding registration.
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
                instance = new ConfigData();
            }
        } else {
            instance = new ConfigData();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigData get() {
        if (instance == null) load();
        return instance;
    }
}

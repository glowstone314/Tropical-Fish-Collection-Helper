package gc.mc.tropical_fish.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    // --- 静态配置实例和文件路径 ---
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("tropical_fish.json").toFile();
    private static ModConfig INSTANCE;

    // --- 配置字段 ---
    public boolean enableGlowEffect = true;
    public boolean enableChatNotification = true;
    public int glowColor = 0xFFD700;

    // --- 实例方法：保存配置 ---
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    // --- 静态方法：加载或创建配置 ---
    public static void load() {
        if (INSTANCE == null) {
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    // 从文件加载
                    INSTANCE = GSON.fromJson(reader, ModConfig.class);
                } catch (IOException e) {
                    System.err.println("Failed to load config, creating default: " + e.getMessage());
                    INSTANCE = new ModConfig();
                }
            } else {
                // 文件不存在，创建默认配置并保存
                INSTANCE = new ModConfig();
                INSTANCE.save();
            }
        }
    }

    public static ModConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }
}
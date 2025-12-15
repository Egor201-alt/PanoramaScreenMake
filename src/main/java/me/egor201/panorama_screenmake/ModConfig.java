package me.egor201.panorama_screenmake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("panoramacraft.json");

    public int panoramaSize = 1024;
    public int keyCode = GLFW.GLFW_KEY_F4;
    
    private static ModConfig instance;

    public static ModConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static ModConfig load() {
        ModConfig config = new ModConfig();
        if (Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    if (loaded.panoramaSize <= 0) loaded.panoramaSize = 1024;
                    if (loaded.keyCode <= 0) loaded.keyCode = GLFW.GLFW_KEY_F4;
                    config = loaded;
                }
            } catch (Exception e) {
                PanoramaCraft.LOGGER.warn("Повреждённый конфиг, создаём новый", e);
            }
        }
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            PanoramaCraft.LOGGER.error("Не удалось сохранить конфиг", e);
        }
    }

    public InputUtil.Key getKey() {
        return InputUtil.fromTranslationKey("key.keyboard." + GLFW.glfwGetKeyName(keyCode, GLFW.glfwGetKeyScancode(keyCode)));
    }

    public void setKey(InputUtil.Key key) {
        this.keyCode = key.getCode();
    }
}

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
    public int keyModifier = 0;

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
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                PanoramaCraft.LOGGER.warn("Не удалось загрузить конфиг, используются значения по умолчанию", e);
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
        return InputUtil.fromKeyCode(keyCode, keyModifier);
    }

    public void setKey(InputUtil.Key key) {
        this.keyCode = key.getCode();
        this.keyModifier = key.getModifiers();
    }
}

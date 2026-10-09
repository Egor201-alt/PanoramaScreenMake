package me.egor201.panorama_screenmake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    public static final ModConfig INSTANCE = new ModConfig();

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "panoramascreenmake.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int resolution = 1024;
    public String savePath = "/panoramas";
    public int delaySeconds = 0;
    public int warmupTicks = 10;
    public int faceDelayTicks = 5;

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    INSTANCE.resolution = loaded.resolution > 0 ? loaded.resolution : 1024;
                    INSTANCE.savePath = (loaded.savePath == null || loaded.savePath.isEmpty()) ? "/panoramas" : loaded.savePath;
                    INSTANCE.delaySeconds = Math.max(0, Math.min(5, loaded.delaySeconds));
                    INSTANCE.warmupTicks = Math.max(0, Math.min(200, loaded.warmupTicks));
                    INSTANCE.faceDelayTicks = Math.max(1, Math.min(200, loaded.faceDelayTicks));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

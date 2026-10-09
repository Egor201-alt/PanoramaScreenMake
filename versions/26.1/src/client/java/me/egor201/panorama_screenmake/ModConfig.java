package me.egor201.panorama_screenmake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    public static final ModConfig INSTANCE = new ModConfig();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int warmupTicks = 10;
    public int faceDelayTicks = 5;

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("panoramascreenmake.json");
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    INSTANCE.warmupTicks = Math.max(0, Math.min(200, loaded.warmupTicks));
                    INSTANCE.faceDelayTicks = Math.max(1, Math.min(200, loaded.faceDelayTicks));
                }
            } catch (Exception e) {
                PanoramaCraft.LOGGER.error("Failed to read panoramascreenmake.json", e);
            }
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            PanoramaCraft.LOGGER.error("Failed to write panoramascreenmake.json", e);
        }
    }
}

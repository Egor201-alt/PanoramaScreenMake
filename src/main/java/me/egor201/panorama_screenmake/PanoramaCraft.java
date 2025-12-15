package me.egor201.panorama_screenmake;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import java.util.concurrent.Executor;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("panorama_craft");

    private static File PANO_DIR;

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
        Identifier.of("panoramascreenmake", "main")
    );

    @Override
    public void onInitializeClient() {
        PANO_DIR = new File(MinecraftClient.getInstance().runDirectory, "panoramas");

        KeyBinding panoramaKeyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.panoramascreenmake.take", 
                InputUtil.Type.KEYSYM,          
                GLFW.GLFW_KEY_F4,            
                CATEGORY                        
            )
        );
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (panoramaKeyBinding.wasPressed()) {
                if (client.player != null && !client.isPaused()) {
                    PANO_DIR.mkdirs();
        
                    Text resultMessage = client.takePanorama(PANO_DIR);
        
                    if (resultMessage != null) {
                        client.player.sendMessage(resultMessage, false);
        
                        Util.getIoWorkerExecutor().execute(() -> {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException ignored) {}
        
                            File screenshotsSubDir = new File(PANO_DIR, "screenshots");
                            if (!screenshotsSubDir.exists() || !screenshotsSubDir.isDirectory()) {
                                LOGGER.warn("Подпапка screenshots не найдена!");
                                return;
                            }
        
                            boolean allMoved = true;
                            for (int i = 0; i < 6; i++) {
                                File src = new File(screenshotsSubDir, "panorama_" + i + ".png");
                                File dest = new File(PANO_DIR, "panorama_" + i + ".png");
        
                                if (src.exists()) {
                                    if (src.renameTo(dest)) {
                                        LOGGER.info("Перемещён: panorama_" + i + ".png");
                                    } else {
                                        try {
                                            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                            if (src.delete()) {
                                                LOGGER.info("Скопирован и удалён: panorama_" + i + ".png");
                                            } else {
                                                LOGGER.warn("Не удалось удалить исходный: " + src);
                                                allMoved = false;
                                            }
                                        } catch (IOException e) {
                                            LOGGER.error("Ошибка копирования: " + src, e);
                                            allMoved = false;
                                        }
                                    }
                                } else {
                                    LOGGER.warn("Файл не найден: " + src);
                                    allMoved = false;
                                }
                            }
        
                            if (allMoved && screenshotsSubDir.listFiles().length == 0) {
                                if (screenshotsSubDir.delete()) {
                                    LOGGER.info("Удалена пустая папка screenshots/");
                                }
                            } else if (screenshotsSubDir.listFiles().length == 0) {
                                screenshotsSubDir.delete();
                            }
                        });
                    }
                }
            }
        });
    }
}

package me.egor201.panorama_screenmake;

import me.egor201.panorama_screenmake.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PanoramaCraft implements ClientModInitializer {

    private int tickCounter = 0;
    private boolean isTimerActive = false;

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
        Identifier.of("panoramascreenmake", "main")
    );

    @Override
    public void onInitializeClient() {
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
                if (client.player == null || client.isPaused()) return;

                int delaySec = ModConfig.INSTANCE.delaySeconds;

                if (delaySec > 0 && !isTimerActive) {
                    isTimerActive = true;
                    tickCounter = delaySec * 20;
                    client.player.sendMessage(Text.translatable("panorama.message.timer_start", delaySec), true);
                } else if (delaySec == 0) {
                    takePanoramaScreenshot(client);
                }
            }

            if (isTimerActive) {
                if (client.player == null) {
                    isTimerActive = false;
                    return;
                }

                tickCounter--;

                if (tickCounter % 20 == 0 && tickCounter > 0) {
                    client.player.sendMessage(Text.translatable("panorama.message.timer_tick", tickCounter / 20), true);
                }

                if (tickCounter <= 0) {
                    isTimerActive = false;
                    takePanoramaScreenshot(client);
                }
            }
        });
    }

    private void takePanoramaScreenshot(MinecraftClient client) {
        File runDir = client.runDirectory;
        String configPath = ModConfig.INSTANCE.savePath;
        
        File baseDir = new File(runDir, (configPath == null || configPath.trim().isEmpty()) ? "panoramas" : configPath);
        
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        File finalSessionDir = getNextFreeDirectory(baseDir);
        finalSessionDir.mkdirs();

        Text resultMessage = client.takePanorama(finalSessionDir);

        if (resultMessage != null) {
            client.player.sendMessage(resultMessage, false);

            Util.getIoWorkerExecutor().execute(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}

                File screenshotsSubDir = new File(finalSessionDir, "screenshots");
                
                if (screenshotsSubDir.exists() && screenshotsSubDir.isDirectory()) {
                    for (int i = 0; i < 6; i++) {
                        File src = new File(screenshotsSubDir, "panorama_" + i + ".png");
                        File dest = new File(finalSessionDir, "panorama_" + i + ".png");

                        if (src.exists()) {
                            try {
                                Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                try {
                                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    src.delete();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    if (screenshotsSubDir.list().length == 0) {
                        screenshotsSubDir.delete();
                    }
                }
            });
        }
    }

    private File getNextFreeDirectory(File baseDir) {
        int id = 1;
        while (true) {
            File check = new File(baseDir, "panorama_" + id);
            if (!check.exists()) {
                return check;
            }
            id++;
        }
    }
}
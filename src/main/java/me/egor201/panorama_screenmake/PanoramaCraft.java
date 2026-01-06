package me.egor201.panorama_screenmake;

import me.egor201.panorama_screenmake.ModConfig;
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
                    client.player.sendMessage(Text.literal("§eСнимок панорамы через " + delaySec + " сек..."), true);
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
                    client.player.sendMessage(Text.literal("§eПанорама через: " + (tickCounter / 20)), true);
                }

                if (tickCounter <= 0) {
                    isTimerActive = false;
                    takePanoramaScreenshot(client);
                }
            }
        });
    }

    private void takePanoramaScreenshot(MinecraftClient client) {
        File saveDir;
        String customPath = ModConfig.INSTANCE.savePath;

        if (customPath != null && !customPath.trim().isEmpty()) {
            saveDir = new File(customPath);
        } else {
            saveDir = new File(client.runDirectory, "panoramas");
        }

        saveDir.mkdirs();

        Text resultMessage = client.takePanorama(saveDir); 

        if (resultMessage != null) {
            client.player.sendMessage(resultMessage, false);

            Util.getIoWorkerExecutor().execute(() -> {
                try {
                    Thread.sleep(1500); 
                } catch (InterruptedException ignored) {}

                File screenshotsSubDir = new File(saveDir, "screenshots");
                if (!screenshotsSubDir.exists() || !screenshotsSubDir.isDirectory()) {
                    return;
                }

                boolean allMoved = true;
                for (int i = 0; i < 6; i++) {
                    File src = new File(screenshotsSubDir, "panorama_" + i + ".png");
                    File dest = new File(saveDir, "panorama_" + i + ".png");

                    if (src.exists()) {
                        if (!src.renameTo(dest)) {
                            try {
                                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                if (!src.delete()) {
                                    allMoved = false;
                                }
                            } catch (IOException e) {
                                e.printStackTrace(); 
                                allMoved = false;
                            }
                        }
                    } else {
                        allMoved = false;
                    }
                }

                if (screenshotsSubDir.listFiles() != null && screenshotsSubDir.listFiles().length == 0) {
                    screenshotsSubDir.delete();
                }
            });
        }
    }
}
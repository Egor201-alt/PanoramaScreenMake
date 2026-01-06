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
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PanoramaCraft implements ClientModInitializer {

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
                                return;
                            }
        
                            boolean allMoved = true;
                            for (int i = 0; i < 6; i++) {
                                File src = new File(screenshotsSubDir, "panorama_" + i + ".png");
                                File dest = new File(PANO_DIR, "panorama_" + i + ".png");
        
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
        });
    }
}

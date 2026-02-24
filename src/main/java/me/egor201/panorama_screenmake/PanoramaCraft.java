package me.egor201.panorama_screenmake;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {

    public static boolean isCapturingPanorama = false;
    public static int captureResolution = 1024;
    private static PanoramaCaptureTask captureTask = null;

    private int tickCounter = 0;
    private boolean isTimerActive = false;

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
        Identifier.of("panoramascreenmake", "main")
    );

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        KeyBinding panoramaKeyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.panoramascreenmake.take", 
                InputUtil.Type.KEYSYM,          
                GLFW.GLFW_KEY_F4,            
                CATEGORY                        
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (captureTask != null) {
                if (captureTask.tick(client)) {
                    captureTask = null;
                }
                return;
            }

            while (panoramaKeyBinding.wasPressed()) {
                if (client.player == null || client.isPaused()) return;

                int delaySec = ModConfig.INSTANCE.delaySeconds;

                if (delaySec > 0 && !isTimerActive) {
                    isTimerActive = true;
                    tickCounter = delaySec * 20;
                    client.player.sendMessage(Text.translatable("panorama.message.timer_start", delaySec), true);
                } else if (delaySec == 0) {
                    startPanoramaCapture(client);
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
                    startPanoramaCapture(client);
                }
            }
        });
    }

    private void startPanoramaCapture(MinecraftClient client) {
        File runDir = client.runDirectory;
        String configPath = ModConfig.INSTANCE.savePath;
        File baseDir = new File(runDir, (configPath == null || configPath.trim().isEmpty()) ? "panoramas" : configPath);
        
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        File finalSessionDir = getNextFreeDirectory(baseDir);
        finalSessionDir.mkdirs();

        int targetRes = ModConfig.INSTANCE.resolution;
        if (targetRes <= 0) targetRes = 1024;

        captureTask = new PanoramaCaptureTask(finalSessionDir, targetRes);
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

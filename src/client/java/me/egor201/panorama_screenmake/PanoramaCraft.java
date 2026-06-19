package me.egor201.panorama_screenmake;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("panorama_craft");

    public static volatile boolean panoramaCapturing = false;

    private static File PANO_DIR;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.parse("panoramascreenmake:main")
    );

    @Override
    public void onInitializeClient() {
        PANO_DIR = new File(Minecraft.getInstance().gameDirectory, "panoramas");

        KeyMapping panoramaKeyBinding = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                "key.panoramascreenmake.take",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                CATEGORY
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (panoramaKeyBinding.consumeClick()) {
                if (client.player != null && !client.isPaused()) {
                    PANO_DIR.mkdirs();
                    panoramaCapturing = true;
                    try {
                        Component resultMessage = client.grabPanoramixScreenshot(PANO_DIR);
                        if (resultMessage != null) {
                            client.player.sendSystemMessage(resultMessage);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Panorama capture failed", e);
                        client.player.sendSystemMessage(Component.literal(
                            "[PanoramaScreenMake] Capture failed: " + e.getMessage()
                        ));
                    } finally {
                        panoramaCapturing = false;
                    }
                }
            }
        });
    }
}

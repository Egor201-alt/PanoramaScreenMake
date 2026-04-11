package me.egor201.panorama_screenmake;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("panorama_craft");

    private static File PANO_DIR;

    private static final String CATEGORY = "panoramascreenmake.main";

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

                    Component resultMessage = client.grabPanorama(PANO_DIR, 1024, 1024);
                    
                    if (resultMessage != null) {
                        client.player.displayClientMessage(resultMessage, false);
                    }
                }
            }
        });
    }
}

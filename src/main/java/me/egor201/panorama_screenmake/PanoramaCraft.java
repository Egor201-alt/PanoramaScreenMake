package me.egor201.panorama_screenmake;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric/api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("panorama_craft");

    private static File PANO_DIR;

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
        ResourceLocation.parse("panoramascreenmake:main")
    );

    @Override
    public void onInitializeClient() {
        PANO_DIR = new File(Minecraft.getInstance().gameDirectory, "panoramas");

        KeyBinding panoramaKeyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.panoramascreenmake.take", 
                InputUtil.Type.KEYSYM,          
                GLFW.GLFW_KEY_F4,            
                CATEGORY                        
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (panoramaKeyBinding.consumeClick()) {
                if (client.player != null && !client.isPaused()) {
                    PANO_DIR.mkdirs();

                    Text resultMessage = client.takePanorama(PANO_DIR);
                    
                    if (resultMessage != null) {
                        client.player.sendMessage(resultMessage, false);
                    }
                }
            }
        });
    }
}

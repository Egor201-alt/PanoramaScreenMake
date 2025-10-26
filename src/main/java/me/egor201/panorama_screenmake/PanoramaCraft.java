package me.egor201.panorama_screenmake;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PanoramaCraft implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("panorama_craft");

    private static File PANO_DIR;

    @Override
    public void onInitializeClient() {
        PANO_DIR = new File(MinecraftClient.getInstance().runDirectory, "panoramas");

        KeyBinding panoramaKeyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding.Builder("key.panoramascreenmake.take")
                .setCategory("category.panoramascreenmake.main")
                .setType(InputUtil.Type.KEYSYM)
                .setKeyCode(GLFW.GLFW_KEY_F4)
                .build()
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (panoramaKeyBinding.wasPressed()) {
                if (client.player != null && !client.isPaused()) {
                    PANO_DIR.mkdirs();
                    //
                    Text resultMessage = client.takePanorama(PANO_DIR);
                    //
                    if (resultMessage != null) {
                        client.player.sendMessage(resultMessage, false);
                    }
                }
            }
        });
    }
}

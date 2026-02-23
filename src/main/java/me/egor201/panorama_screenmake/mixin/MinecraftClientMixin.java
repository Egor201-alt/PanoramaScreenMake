package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.ModConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @ModifyConstant(method = "takePanorama", constant = @Constant(intValue = 1024))
    private int injectCustomPanoramaResolution(int original) {
        int customRes = ModConfig.INSTANCE.resolution;
        return customRes > 0 ? customRes : original;
    }
}

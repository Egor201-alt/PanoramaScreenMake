package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.ModConfig;
import me.egor201.panorama_screenmake.PanoramaCraft;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @ModifyConstant(method = "takePanorama(Ljava/io/File;)Lnet/minecraft/text/Text;", constant = @Constant(intValue = 1024), require = 0)
    private int modifyPanoramaResolution(int original) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return original;
    }
}

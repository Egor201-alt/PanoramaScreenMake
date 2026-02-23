package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.ModConfig;
import me.egor201.panorama_screenmake.PanoramaCraft;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NativeImage.class)
public class NativeImageMixin {

    @ModifyVariable(method = "<init>(Lnet/minecraft/client/texture/NativeImage$Format;IIZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private int modifyNativeWidth1(int width) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return width;
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/client/texture/NativeImage$Format;IIZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private int modifyNativeHeight1(int height) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return height;
    }

    @ModifyVariable(method = "<init>(IIZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private int modifyNativeWidth2(int width) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return width;
    }

    @ModifyVariable(method = "<init>(IIZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private int modifyNativeHeight2(int height) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return height;
    }
}

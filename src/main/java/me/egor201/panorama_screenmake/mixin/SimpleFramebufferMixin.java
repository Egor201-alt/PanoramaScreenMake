package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.ModConfig;
import me.egor201.panorama_screenmake.PanoramaCraft;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SimpleFramebuffer.class)
public class SimpleFramebufferMixin {

    @ModifyVariable(method = "<init>(IIZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private int modifyWidth(int width) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return width;
    }

    @ModifyVariable(method = "<init>(IIZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private int modifyHeight(int height) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return height;
    }
}

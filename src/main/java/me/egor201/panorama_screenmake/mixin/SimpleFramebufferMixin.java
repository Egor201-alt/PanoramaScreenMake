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
    private int modifyWidth3Args(int width) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return width;
    }

    @ModifyVariable(method = "<init>(IIZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private int modifyHeight3Args(int height) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return height;
    }

    @ModifyVariable(method = "<init>(IIZZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private int modifyWidth4Args(int width) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return width;
    }

    @ModifyVariable(method = "<init>(IIZZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true, require = 0)
    private int modifyHeight4Args(int height) {
        if (PanoramaCraft.isCapturingPanorama && ModConfig.INSTANCE.resolution > 0) {
            return ModConfig.INSTANCE.resolution;
        }
        return height;
    }
}

package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.PanoramaCraft;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "getFramebufferWidth", at = @At("RETURN"), cancellable = true)
    private void overrideFbWidth(CallbackInfoReturnable<Integer> cir) {
        if (PanoramaCraft.isCapturingPanorama && PanoramaCraft.captureResolution > 0) {
            cir.setReturnValue(PanoramaCraft.captureResolution);
        }
    }

    @Inject(method = "getFramebufferHeight", at = @At("RETURN"), cancellable = true)
    private void overrideFbHeight(CallbackInfoReturnable<Integer> cir) {
        if (PanoramaCraft.isCapturingPanorama && PanoramaCraft.captureResolution > 0) {
            cir.setReturnValue(PanoramaCraft.captureResolution);
        }
    }

    @Inject(method = "getWidth", at = @At("RETURN"), cancellable = true)
    private void overrideWidth(CallbackInfoReturnable<Integer> cir) {
        if (PanoramaCraft.isCapturingPanorama && PanoramaCraft.captureResolution > 0) {
            cir.setReturnValue(PanoramaCraft.captureResolution);
        }
    }

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true)
    private void overrideHeight(CallbackInfoReturnable<Integer> cir) {
        if (PanoramaCraft.isCapturingPanorama && PanoramaCraft.captureResolution > 0) {
            cir.setReturnValue(PanoramaCraft.captureResolution);
        }
    }
}

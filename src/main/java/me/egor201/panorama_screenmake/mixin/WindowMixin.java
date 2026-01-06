package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.utils.ResolutionOverride;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "getFramebufferWidth", at = @At("HEAD"), cancellable = true)
    private void getFramebufferWidth(CallbackInfoReturnable<Integer> cir) {
        if (ResolutionOverride.active && ResolutionOverride.size > 0) {
            cir.setReturnValue(ResolutionOverride.size);
        }
    }

    @Inject(method = "getFramebufferHeight", at = @At("HEAD"), cancellable = true)
    private void getFramebufferHeight(CallbackInfoReturnable<Integer> cir) {
        if (ResolutionOverride.active && ResolutionOverride.size > 0) {
            cir.setReturnValue(ResolutionOverride.size);
        }
    }
    
    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    private void getWidth(CallbackInfoReturnable<Integer> cir) {
        if (ResolutionOverride.active && ResolutionOverride.size > 0) {
            cir.setReturnValue(ResolutionOverride.size);
        }
    }

    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
    private void getHeight(CallbackInfoReturnable<Integer> cir) {
        if (ResolutionOverride.active && ResolutionOverride.size > 0) {
            cir.setReturnValue(ResolutionOverride.size);
        }
    }
}
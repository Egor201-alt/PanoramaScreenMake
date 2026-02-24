package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.PanoramaCraft;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frustum.class)
public class FrustumMixin {

    @Inject(method = "isVisible(Lnet/minecraft/util/math/Box;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void forceVisibleForPanorama(Box box, CallbackInfoReturnable<Boolean> cir) {
        if (PanoramaCraft.isCapturingPanorama) {
            cir.setReturnValue(true);
        }
    }
}

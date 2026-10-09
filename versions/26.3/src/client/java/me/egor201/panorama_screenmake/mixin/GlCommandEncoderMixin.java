package me.egor201.panorama_screenmake.mixin;

import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlCommandEncoder", remap = false)
public class GlCommandEncoderMixin {

    @Inject(method = "awaitSubmit(JJ)Z", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void panoramaScreenmake$safeAwaitSubmit(long p1, long p2, CallbackInfoReturnable<Boolean> cir) {
        GL11C.glFinish();
        cir.setReturnValue(true);
    }
}

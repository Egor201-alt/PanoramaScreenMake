package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.PanoramaCraft;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// GlFence.awaitCompletion() calls GlCommandEncoder.awaitSubmit() which throws
// "Cannot wait on a fence for the current submit" when Sodium renders during panorama.
// Intercepting awaitCompletion() lets glFinish() do the actual GPU sync while the
// fence returns normally — MappableRingBuffer sees a clean fence and rotates its ring
// buffer correctly, leaving no corrupted state for the next normal frame.
@Mixin(targets = "com.mojang.blaze3d.opengl.GlFence", remap = false)
public class GlCommandEncoderMixin {

    @Inject(method = "awaitCompletion", at = @At("HEAD"), cancellable = true, remap = false)
    private void panoramaScreenmake$safeAwaitCompletion(CallbackInfo ci) {
        if (PanoramaCraft.panoramaCapturing) {
            GL11C.glFinish();
            ci.cancel();
        }
    }
}

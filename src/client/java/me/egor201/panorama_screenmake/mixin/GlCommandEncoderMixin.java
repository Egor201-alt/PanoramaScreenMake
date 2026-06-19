package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.PanoramaCraft;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// MC 26.2 forbids waiting on a fence while a command encoder submit is active.
// Sodium's MappableRingBuffer hits this restriction during panorama capture.
// We intercept the call and use glFinish() instead — only during panorama capture
// so normal rendering is unaffected.
@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder", remap = false)
public class GlCommandEncoderMixin {

    @Inject(method = "awaitSubmit", at = @At("HEAD"), cancellable = true, remap = false)
    private void panoramaScreenmake$safeAwaitSubmit(CallbackInfo ci) {
        if (PanoramaCraft.panoramaCapturing) {
            GL11C.glFinish();
            ci.cancel();
        }
    }
}

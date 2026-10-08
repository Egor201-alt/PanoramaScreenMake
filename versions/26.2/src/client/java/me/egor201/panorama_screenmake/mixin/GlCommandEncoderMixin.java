package me.egor201.panorama_screenmake.mixin;

import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MC 26.2 forbids awaitSubmit() while a command encoder submit is active.
// Sodium 0.9.0 hits this in normal rendering (FrameGraphBuilder.execute) and
// during panorama capture. glFinish() flushes all pending GPU work, making the
// fence condition trivially true, so we bypass the Java-level submit check.
@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder", remap = false)
public class GlCommandEncoderMixin {

    @Inject(method = "awaitSubmit", at = @At("HEAD"), cancellable = true, remap = false)
    private void panoramaScreenmake$safeAwaitSubmit(long p1, long p2, CallbackInfoReturnable<Boolean> cir) {
        GL11C.glFinish();
        cir.setReturnValue(true);
    }
}

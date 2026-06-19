package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.PanoramaCraft;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MC 26.2 forbids calling awaitSubmit() while a command encoder submit is active.
// During panorama capture Sodium hits this restriction when rotating its ring buffer.
// We intercept at HEAD: glFinish() resolves all GPU work, then we return the fence
// object back to GlFence.awaitCompletion() so it can update its internal tracking
// correctly and leave no corrupted state for the next normal frame.
@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder", remap = false)
public class GlCommandEncoderMixin {

    @Inject(method = "awaitSubmit", at = @At("HEAD"), cancellable = true, remap = false)
    private void panoramaScreenmake$safeAwaitSubmit(Object fence, CallbackInfoReturnable<Object> cir) {
        if (PanoramaCraft.panoramaCapturing) {
            GL11C.glFinish();
            cir.setReturnValue(fence);
        }
    }
}

package me.egor201.panorama_screenmake.mixin;

import org.lwjgl.opengl.GL11C;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

/**
 * Distant Horizons (and similar mods) need a few full render passes after the panorama
 * framebuffer resize before the first face is captured; vanilla only sleeps 10ms per face.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftPanoramaMixin {

    @Inject(method = "grabPanoramixScreenshot", at = @At("HEAD"))
    private void panoramaScreenmake$flushGPUBeforePanorama(File folder, CallbackInfoReturnable<Component> cir) {
        // Sodium's MappableRingBuffer cannot wait on GPU fences while a new command buffer
        // is being built (MC 26.2 restriction). glFinish() resolves all pending GPU submissions
        // upfront so no fence waits are needed during panorama rendering.
        GL11C.glFinish();
    }

    @Inject(
        method = "grabPanoramixScreenshot",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(II)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void panoramaScreenmake$warmupAfterPanoramaResize(File folder, CallbackInfoReturnable<Component> cir) {
        Minecraft self = (Minecraft) (Object) this;
        self.resizeGui();
        try {
            Thread.sleep(30L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Inject(
        method = "grabPanoramixScreenshot",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(II)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private void panoramaScreenmake$restoreGuiAfterPanorama(File folder, CallbackInfoReturnable<Component> cir) {
        ((Minecraft) (Object) this).resizeGui();
    }
}

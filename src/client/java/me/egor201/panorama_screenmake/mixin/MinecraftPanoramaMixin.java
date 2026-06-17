package me.egor201.panorama_screenmake.mixin;

import net.minecraft.client.DeltaTracker;
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
    private static final int WARMUP_PASSES_AFTER_PANORAMA_RESIZE = 3;

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
        for (int i = 0; i < WARMUP_PASSES_AFTER_PANORAMA_RESIZE; i++) {
            self.gameRenderer.update(DeltaTracker.ONE);
            self.gameRenderer.extract(DeltaTracker.ONE, true);
            self.gameRenderer.renderLevel(DeltaTracker.ONE);
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
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

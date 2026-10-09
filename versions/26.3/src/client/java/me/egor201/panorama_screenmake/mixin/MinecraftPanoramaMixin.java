package me.egor201.panorama_screenmake.mixin;

import me.egor201.panorama_screenmake.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(Minecraft.class)
public abstract class MinecraftPanoramaMixin {

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
            Thread.sleep(Math.max(30L, ModConfig.INSTANCE.warmupTicks * 50L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @ModifyArg(
        method = "grabPanoramixScreenshot",
        at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V"),
        index = 0,
        require = 0
    )
    private long panoramaScreenmake$faceDelay(long original) {
        return Math.max(original, ModConfig.INSTANCE.faceDelayTicks * 50L);
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

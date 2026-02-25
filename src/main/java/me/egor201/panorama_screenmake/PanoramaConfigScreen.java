package me.egor201.panorama_screenmake;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PanoramaConfigScreen {

    public enum ResolutionOption {
        RES_1024(1024, "1024x1024 (Standard)"),
        RES_2048(2048, "2048x2048"),
        RES_4096(4096, "4096x4096 (Warning: Lags!)"),
        RES_8192(8192, "8192x8192 (Warning: Lags!)");

        public final int value;
        public final String label;

        ResolutionOption(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public static ResolutionOption fromValue(int val) {
            for (ResolutionOption opt : values()) {
                if (opt.value == val) return opt;
            }
            return RES_1024;
        }
    }

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("config.panoramascreenmake.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("config.panoramascreenmake.category.general"))
                
                .option(Option.<ResolutionOption>createBuilder()
                    .name(Text.translatable("config.panoramascreenmake.option.resolution"))
                    .description(OptionDescription.of(Text.translatable("config.panoramascreenmake.tooltip.resolution")))
                    .binding(
                        ResolutionOption.RES_1024,
                        () -> ResolutionOption.fromValue(ModConfig.INSTANCE.resolution),
                        newVal -> ModConfig.INSTANCE.resolution = newVal.value
                    )
                    .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(ResolutionOption.class)
                        .formatValue(val -> Text.literal(val.label))
                    )
                    .build())
                
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("config.panoramascreenmake.option.delay"))
                    .description(OptionDescription.of(Text.translatable("config.panoramascreenmake.tooltip.delay")))
                    .binding(
                        0,
                        () -> ModConfig.INSTANCE.delaySeconds,
                        newVal -> ModConfig.INSTANCE.delaySeconds = newVal
                    )
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 5)
                        .step(1)
                        .formatValue(val -> val == 0 
                            ? Text.translatable("config.panoramascreenmake.value.instant") 
                            : Text.translatable("config.panoramascreenmake.value.seconds", val))
                    )
                    .build())

                .option(Option.<String>createBuilder()
                    .name(Text.translatable("config.panoramascreenmake.option.path"))
                    .description(OptionDescription.of(Text.translatable("config.panoramascreenmake.tooltip.path")))
                    .binding(
                        "/panoramas",
                        () -> ModConfig.INSTANCE.savePath,
                        newVal -> ModConfig.INSTANCE.savePath = newVal
                    )
                    .controller(StringControllerBuilder::create)
                    .build())
                
                .build())
            
            .save(ModConfig::save)
            .build()
            .generateScreen(parent);
    }
}

package me.egor201.panorama_screenmake;

import me.egor201.panorama_screenmake.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PanoramaConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.panoramascreenmake.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.panoramascreenmake.category.general"));

        general.addEntry(entryBuilder.startSelector(
                Text.translatable("config.panoramascreenmake.option.resolution"),
                new Integer[]{0, 1024, 2048, 4096, 8192},
                ModConfig.INSTANCE.resolution
            )
            .setDefaultValue(0)
            .setNameProvider(val -> {
                if (val == 0) return Text.translatable("config.panoramascreenmake.value.default");
                return Text.literal(val + "x" + val);
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.resolution = newValue)
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.resolution"))
            .build()
        );

        general.addEntry(entryBuilder.startIntSlider(
                Text.translatable("config.panoramascreenmake.option.delay"),
                ModConfig.INSTANCE.delaySeconds,
                0, 10
            )
            .setDefaultValue(0)
            .setTextGetter(val -> {
                if (val == 0) return Text.translatable("config.panoramascreenmake.value.instant");
                return Text.translatable("config.panoramascreenmake.value.seconds", val);
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.delaySeconds = newValue)
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.delay"))
            .build()
        );

        general.addEntry(entryBuilder.startStrField(
                Text.translatable("config.panoramascreenmake.option.path"),
                ModConfig.INSTANCE.savePath
            )
            .setDefaultValue("")
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.savePath = newValue)
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.path"))
            .build()
        );

        builder.setSavingRunnable(() -> {
            // TODO: Add Save Logic
        });

        return builder.build();
    }
}
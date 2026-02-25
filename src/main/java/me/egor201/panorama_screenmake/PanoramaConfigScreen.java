package me.egor201.panorama_screenmake;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.Set;

public class PanoramaConfigScreen {

    private static final Set<String> FORBIDDEN_FOLDERS = Set.of(
        "/mods", "/config", "/versions", "/saves", "/assets", "/logs", "/resourcepacks", "/shaderpacks"
    );

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.panoramascreenmake.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.panoramascreenmake.category.general"));

        general.addEntry(entryBuilder.startSelector(
                Text.translatable("config.panoramascreenmake.option.resolution"),
                new Integer[]{1024, 2048, 4096, 8192},
                ModConfig.INSTANCE.resolution
            )
            .setDefaultValue(1024)
            .setNameProvider(val -> {
                if (val == 1024) return Text.literal("1024x1024 (Standard)");
                return Text.literal(val + "x" + val);
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.resolution = newValue)
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.resolution"))
            .build()
        );

        general.addEntry(entryBuilder.startIntSlider(
                Text.translatable("config.panoramascreenmake.option.delay"),
                ModConfig.INSTANCE.delaySeconds,
                0, 5
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
            .setDefaultValue("/panoramas")
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.path"))
            .setErrorSupplier(val -> {
                if (!val.startsWith("/")) {
                    return Optional.of(Text.translatable("config.panoramascreenmake.error.slash").formatted(Formatting.RED));
                }
                if (val.length() < 2) {
                    return Optional.of(Text.translatable("config.panoramascreenmake.error.short").formatted(Formatting.RED));
                }
                String cleanVal = val.endsWith("/") ? val.substring(0, val.length() - 1) : val;
                if (FORBIDDEN_FOLDERS.contains(cleanVal.toLowerCase())) {
                    return Optional.of(Text.translatable("config.panoramascreenmake.error.forbidden").formatted(Formatting.RED));
                }
                return Optional.empty();
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.savePath = newValue)
            .build()
        );

        builder.setSavingRunnable(() -> {
            ModConfig.save();
        });

        return builder.build();
    }
}

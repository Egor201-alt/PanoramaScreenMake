package me.egor201.panorama_screenmake;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
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

        general.addEntry(entryBuilder.startDropdownMenu(
                Text.translatable("config.panoramascreenmake.option.resolution"),
                DropdownMenuBuilder.TopCellElementBuilder.of(
                    ModConfig.INSTANCE.resolution,
                    str -> { 
                        try { 
                            String num = str.split("x")[0].trim();
                            return Integer.parseInt(num); 
                        } catch (Exception e) { 
                            return 1024; 
                        } 
                    },
                    val -> Text.literal(val + "x" + val) 
                ),
                DropdownMenuBuilder.CellCreatorBuilder.of(val -> Text.literal(val + "x" + val))
            )
            .setDefaultValue(1024)
            .setSelections(Arrays.asList(1024, 2048, 4096, 8192))
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.resolution = newValue)
            .setTooltip(Text.translatable("config.panoramascreenmake.tooltip.resolution"))
            .build()
        );

        general.addEntry(entryBuilder.startLongSlider(
                Text.translatable("config.panoramascreenmake.option.delay"),
                (long) ModConfig.INSTANCE.delaySeconds, 
                0L, 
                5L  
            )
            .setDefaultValue(0L)
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.delaySeconds = newValue.intValue())
            .setTextGetter(val -> {
                if (val <= 0) return Text.translatable("config.panoramascreenmake.value.instant");
                return Text.translatable("config.panoramascreenmake.value.seconds", val);
            })
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
                if (val == null || val.trim().isEmpty()) return Optional.empty();

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

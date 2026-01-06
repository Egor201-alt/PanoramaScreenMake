package me.egor201.panorama_screenmake;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PanoramaConfigScreen {

    public static Screen create(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Настройки Panorama Make"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("Основное"));

        general.addEntry(entryBuilder.startSelector(
                Text.literal("Разрешение скриншотов"),
                new Integer[]{0, 1024, 2048, 4096, 8192},
                ModConfig.INSTANCE.resolution
            )
            .setDefaultValue(0)
            .setNameProvider(val -> {
                if (val == 0) return Text.literal("Как окно игры (По умолчанию)");
                return Text.literal(val + "x" + val);
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.resolution = newValue)
            .setTooltip(Text.literal("Фиксированный размер панорамы. Высокие значения могут нагружать ПК!"))
            .build()
        );


        general.addEntry(entryBuilder.startIntSlider(
                Text.literal("Задержка перед снимком"),
                ModConfig.INSTANCE.delaySeconds,
                0, 10
            )
            .setDefaultValue(0)
            .setTextGetter(val -> {
                if (val == 0) return Text.literal("Мгновенно");
                return Text.literal(val + " сек.");
            })
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.delaySeconds = newValue)
            .setTooltip(Text.literal("Дает время убрать руки с клавиатуры"))
            .build()
        );

        general.addEntry(entryBuilder.startStrField(
                Text.literal("Папка сохранения"),
                ModConfig.INSTANCE.savePath
            )
            .setDefaultValue("")
            .setSaveConsumer(newValue -> ModConfig.INSTANCE.savePath = newValue)
            .setTooltip(Text.literal("Оставьте пустым для сохранения в папку /panoramas"))
            .build()
        );

        builder.setSavingRunnable(() -> {
            System.out.println("Конфигурация сохранена в память!");
        });

        return builder.build();
    }
}
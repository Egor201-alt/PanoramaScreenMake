package me.egor201.panorama_screenmake;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> createConfigScreen(parent);
    }

    private static Screen createConfigScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Panorama Craft Настройки"))
                .setSavingRunnable(config::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("Общие"));

        ConfigEntryBuilder entry = builder.entryBuilder();

        general.addEntry(entry.startIntSlider(Text.literal("Размер панорамы"), config.panoramaSize, 256, 4096)
                .setDefaultValue(1024)
                .setTooltip(Text.literal("Размер каждого лица куба (квадрат)"))
                .setSaveConsumer(value -> config.panoramaSize = value)
                .build());

        general.addEntry(entry.startKeyCodeField(Text.literal("Клавиша съёмки"), config.getKey())
                .setDefaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_F4, 0))
                .setKeySaveConsumer(config::setKey)
                .build());

        return builder.build();
    }

    @Override
    public java.util.Optional<java.util.function.Supplier<Screen>> getConfigScreenFactory() {
        return java.util.Optional.of(() -> createConfigScreen(null));
    }
}

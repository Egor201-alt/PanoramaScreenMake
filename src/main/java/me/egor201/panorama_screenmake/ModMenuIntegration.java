package me.egor201.panorama_screenmake;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("PanoramaScreenMake Settings"))
                .setSavingRunnable(config::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("Общие"));

        ConfigEntryBuilder entry = builder.entryBuilder();

        general.addEntry(entry.startIntSlider(Text.literal("Размер панорамы"), config.panoramaSize, 256, 4096)
                .setDefaultValue(1024)
                .setTooltip(Text.literal("Разрешение скринов панорамы. Больше = лучше качество, но дольше съёмка"))
                .setSaveConsumer(value -> config.panoramaSize = value)
                .build());

        general.addEntry(entry.startKeyCodeField(Text.literal("Клавиша съёмки"), config.getKey())
                .setDefaultValue(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4))
                .setKeySaveConsumer(config::setKey)
                .build());

        return builder.build();
    }
}

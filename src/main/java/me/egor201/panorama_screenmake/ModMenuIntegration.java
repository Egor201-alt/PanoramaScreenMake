package me.egor201.panorama_screenmake;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DropdownMenuEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    private static final List<Integer> RESOLUTIONS = List.of(512, 1024, 2048);

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Panorama Craft").formatted(Formatting.GOLD, Formatting.BOLD))
                .setSavingRunnable(config::save);

        ConfigCategory main = builder.getOrCreateCategory(Text.literal("Настройки панорамы"));

        ConfigEntryBuilder entry = builder.entryBuilder();

        main.addEntry(entry.startDropdownMenu(
                        Text.literal("Разрешение панорамы"),
                        DropdownMenuEntry.TopCellElement.of(config.panoramaSize, integer -> integer.toString()),
                        DropdownMenuEntry.CellCreator.of(integer -> Text.literal(integer + "×" + integer))
                )
                .setSelections(RESOLUTIONS)
                .setDefaultValue(1024)
                .setTooltip(Text.literal("Выберите качество панорамы:\n")
                        .append(Text.literal("• 512×512 — быстро, низкое качество\n").formatted(Formatting.GRAY))
                        .append(Text.literal("• 1024×1024 — оптимально (как в ванилле)\n").formatted(Formatting.GRAY))
                        .append(Text.literal("• 2048×2048 — высокое качество, дольше съёмка").formatted(Formatting.GRAY)))
                .setSaveConsumer(newValue -> config.panoramaSize = newValue)
                .build());

        main.addEntry(entry.startKeyCodeField(Text.literal("Клавиша съёмки"), config.getKey())
                .setDefaultValue(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4))
                .setKeySaveConsumer(config::setKey)
                .build());

        return builder.build();
    }
}

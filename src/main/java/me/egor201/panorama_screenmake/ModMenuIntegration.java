package me.egor201.panorama_screenmake;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

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

        main.addEntry(entry.startEnumSelector(
                Text.literal("Разрешение панорамы"),
                PanoramaSize.class,
                PanoramaSize.fromSize(config.panoramaSize)
            )
            .setDefaultValue(PanoramaSize.MEDIUM)
            .setTooltip(Text.literal("Выберите качество панорамы:\n")
                    .append(Text.literal("• 512×512 — быстро, низкое качество\n").formatted(Formatting.GRAY))
                    .append(Text.literal("• 1024×1024 — оптимально (как в ванилле)\n").formatted(Formatting.GRAY))
                    .append(Text.literal("• 2048×2048 — высокое качество, дольше съёмка").formatted(Formatting.GRAY)))
            .setEnumNameProvider(enumValue -> ((PanoramaSize) enumValue).getDisplayName())
            .setSaveConsumer(newSize -> config.panoramaSize = newSize.getSize())
            .build());

        main.addEntry(entry.startKeyCodeField(Text.literal("Клавиша съёмки"), config.getKey())
            .setDefaultValue(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4))
            .setKeySaveConsumer(config::setKey)
            .build());

        builder.setFooterText(Text.literal("Нажмите клавишу в мире, чтобы сделать панораму.\n")
                .append(Text.literal("Файлы сохраняются в .minecraft/panoramas/").formatted(Formatting.ITALIC))
                .formatted(Formatting.DARK_GRAY));

        return builder.build();
    }

    public enum PanoramaSize {
        LOW(512, "512×512 — Быстро"),
        MEDIUM(1024, "1024×1024 — Оптимально"),
        HIGH(2048, "2048×2048 — Высокое качество");

        private final int size;
        private final String displayName;

        PanoramaSize(int size, String displayName) {
            this.size = size;
            this.displayName = displayName;
        }

        public int getSize() {
            return size;
        }

        public Text getDisplayName() {
            return Text.literal(displayName);
        }

        public static PanoramaSize fromSize(int size) {
            return switch (size) {
                case 512 -> LOW;
                case 2048 -> HIGH;
                default -> MEDIUM;
            };
        }
    }
}

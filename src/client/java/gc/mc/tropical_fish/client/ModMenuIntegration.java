package gc.mc.tropical_fish.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import gc.mc.tropical_fish.config.ModConfig;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        // 1. 获取当前配置实例
        ModConfig config = ModConfig.get();

        // 2. 初始化 ConfigBuilder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.tropical_fish.config_title"));

        // 3. 设置保存事件：当用户点击 "Save and Quit" 时触发
        builder.setSavingRunnable(config::save); // 调用 ModConfig.save() 方法

        // 4. 创建配置条目构建器
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // 5. 创建配置分类
        ConfigCategory general = builder.getOrCreateCategory(
                Text.translatable("text.tropical_fish.config_general"));

        // 6. 添加配置条目：开启/关闭粒子效果
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("text.tropical_fish.enable_particles"),
                        config.enableRareFishParticles)
                .setDefaultValue(true)
                .setTooltip(
                        Text.translatable("text.tropical_fish.tooltip.enable_particles"))
                .setSaveConsumer(newValue -> config.enableRareFishParticles = newValue)
                .build());

        // 7. 添加配置条目：粒子效果强度 (Float Slider)
        general.addEntry(entryBuilder.startDoubleField(
                        Text.translatable("text.tropical_fish.particle_intensity"),
                        config.particleIntensity)
                .setDefaultValue(1.0)
                .setMin(0.1)
                .setMax(5.0)
                .setTooltip(Text.translatable("text.tropical_fish.tooltip.particle_intensity"))
                .setSaveConsumer(newValue -> config.particleIntensity = newValue.floatValue())
                .build());

        // 8. 返回构建好的配置屏幕
        return builder.build();
    }
}
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

        // 6. 添加配置条目：开启/关闭发光效果
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("text.tropical_fish.enable_glow_effect"),
                        config.enableGlowEffect)
                .setDefaultValue(true)
                .setTooltip(
                        Text.translatable("text.tropical_fish.tooltip.enable_glow_effect"))
                .setSaveConsumer(newValue -> config.enableGlowEffect = newValue)
                .build());
        // 添加配置：开关聊天信息提示
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("text.tropical_fish.enable_chat_notification"),
                        config.enableChatNotification)
                .setDefaultValue(true)
                .setTooltip(
                        Text.translatable("text.tropical_fish.tooltip.enable_chat_notification"))
                .setSaveConsumer(newValue -> config.enableChatNotification = newValue)
                .build());
        general.addEntry(entryBuilder.startColorField(
                        Text.translatable("text.tropical_fish.glow_color"),
                        config.glowColor)
                .setDefaultValue(0xFFD700)
                .setTooltip(Text.translatable("text.tropical_fish.tooltip.glow_color"))
                .setAlphaMode(false)
                .setSaveConsumer(newValue -> config.glowColor = newValue)
                .build());

        // 8. 返回构建好的配置屏幕
        return builder.build();
    }
}
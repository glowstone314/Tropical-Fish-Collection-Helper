package gc.mc.tropical_fish.client;

import gc.mc.tropical_fish.RareFishHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import gc.mc.tropical_fish.config.ModConfig;
import gc.mc.tropical_fish.mixin.accessor.TropicalFishEntityAccessor;


public class FishListener {

    private static final Set<UUID> processedRareFish = new HashSet<>();

    public static void registerListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            ClientWorld world = client.world;
            if (world == null || client.player == null) {
                return;
            }
            // 每次检查时都获取最新的配置
            ModConfig config = ModConfig.get();

            // 检查功能是否开启
            if (!config.enableRareFishParticles || client.world == null || client.player == null) {
                return;
            }

            ClientWorld clientWorld = client.world;
            for (Entity entity : world.getEntities()) {

                if (entity instanceof TropicalFishEntity fish) {

                    TropicalFishEntityAccessor fishAccessor = (TropicalFishEntityAccessor) fish;
                    int variantId = fishAccessor.invokeGetTropicalFishVariant();

                    if (RareFishHelper.isRareTropicalFish(variantId)) {
                        UUID fishUUID = fish.getUuid();

                        // 获取配置强度
                        double intensity = config.particleIntensity;

                        // 计算基础粒子数量 (强度大于1时的整数部分)
                        int baseCount = (int) intensity;

                        // 计算额外的概率 (强度小于1时的分数部分，或大于1时的分数部分)
                        double fractionalChance = intensity - baseCount;

                        // 首先，生成整数部分的粒子
                        int finalCount = baseCount;

                        // 其次，根据分数部分的概率，决定是否生成额外的一个粒子
                        if (world.random.nextDouble() < fractionalChance) {
                            finalCount++;
                        }

                        // 如果强度为 0.5: baseCount=0, fractionalChance=0.5. 有 50% 概率 finalCount=1。
                        // 如果强度为 1.5: baseCount=1, fractionalChance=0.5. finalCount 至少为 1，有 50% 概率 finalCount=2。

                        for (int i = 0; i < finalCount; i++) {
                            // 使用随机偏移量，使粒子看起来更自然
                            clientWorld.addParticle(
                                    ParticleTypes.GLOW, // 粒子类型
                                    false,              // isImmediate - 通常设为 false
                                    fish.getX() + (clientWorld.random.nextDouble() - 0.5) * 0.5,
                                    fish.getBodyY(0.5) + 0.3,
                                    fish.getZ() + (clientWorld.random.nextDouble() - 0.5) * 0.5,
                                    0.0, 0.0, 0.0       // 粒子速度参数
                            );
                        }

                        // 2. 打印消息（仅处理一次）
                        if (processedRareFish.add(fishUUID)) {

                            // 1. 获取包含格式化参数的翻译文本
                            // %s 是占位符
                            Text translatedFormat = Text.translatable(
                                    "message.tropical_fish.fish_found",
                                    // 2. 传入格式化参数
                                    String.format("%.1f", fish.getX()),
                                    String.format("%.1f", fish.getY()),
                                    String.format("%.1f", fish.getZ()),
                                    variantId
                            );

                            // 3. 发送给玩家
                            client.player.sendMessage(translatedFormat, false);
                        }
                    }
                }
            }
        });
    }
}
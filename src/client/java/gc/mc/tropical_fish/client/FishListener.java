package gc.mc.tropical_fish.client;

import gc.mc.tropical_fish.RareFishHelper;
import gc.mc.tropical_fish.config.ModConfig;
import gc.mc.tropical_fish.mixin.accessor.TropicalFishEntityAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class FishListener {
    // 已提醒过的鱼：按插入顺序存储 UUID -> 通知时的 tick
    private static final LinkedHashMap<UUID, Integer> NOTIFIED = new LinkedHashMap<>();
    // 记录每条鱼最后一次出现在客户端世界的 tick
    private static final Map<UUID, Integer> LAST_SEEN = new HashMap<>();

    private static int clientTick = 0;

    // 配置
    private static final int DESPAWN_TTL = 200; // 超过这个 tick 未见则认为已消失
    private static final int MAX_NOTIFIED_ENTRIES = 512;

    public static void registerListener() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            clientTick++;

            // 遍历世界中所有实体，更新 LAST_SEEN，并在未提醒时进行提醒
            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof TropicalFishEntity fish) {
                    TropicalFishEntityAccessor accessor = (TropicalFishEntityAccessor) fish;
                    int variantId = accessor.invokeGetTropicalFishVariant();
                    UUID uuid = fish.getUuid();

                    // 标记最后一次看到该鱼的时间
                    LAST_SEEN.put(uuid, clientTick);

                    if (ModConfig.get().enableChatNotification) {
                        if (RareFishHelper.isRareTropicalFish(variantId)) {
                            // 仅当未提醒过时进行提醒
                            if (!NOTIFIED.containsKey(uuid)) {
                                client.player.sendMessage(
                                        Text.translatable("message.tropical_fish.fish_found",
                                                String.format("%.1f", fish.getX()),
                                                String.format("%.1f", fish.getY()),
                                                String.format("%.1f", fish.getZ()),
                                                String.format("#%08X", variantId)),
                                        false
                                );
                                NOTIFIED.put(uuid, clientTick);
                            }
                        }
                    }
                }
            }

            // 清理：对于长时间未见的实体，认为已消失，移除记录
            Iterator<Map.Entry<UUID, Integer>> lastSeenIt = LAST_SEEN.entrySet().iterator();
            while (lastSeenIt.hasNext()) {
                Map.Entry<UUID, Integer> e = lastSeenIt.next();
                if (clientTick - e.getValue() > DESPAWN_TTL) {
                    UUID uuid = e.getKey();
                    lastSeenIt.remove();
                    NOTIFIED.remove(uuid);
                }
            }

            // 当 NOTIFIED 超过上限时，从最旧的开始移除，直到不超过上限
            if (NOTIFIED.size() > MAX_NOTIFIED_ENTRIES) {
                Iterator<UUID> it = NOTIFIED.keySet().iterator();
                while (NOTIFIED.size() > MAX_NOTIFIED_ENTRIES && it.hasNext()) {
                    it.next();
                    it.remove();
                }
            }
        });
    }
}
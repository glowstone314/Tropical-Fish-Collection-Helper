package gc.mc.tropical_fish;

import net.minecraft.entity.passive.TropicalFishEntity;
import java.util.Set;
import java.util.stream.Collectors;

public class RareFishHelper {

    private static final Set<Integer> COMMON_IDS = TropicalFishEntity.COMMON_VARIANTS.stream()
            .map(TropicalFishEntity.Variant::getId)
            .collect(Collectors.toSet());

    public static boolean isRareTropicalFish(int variantId) {
        return !COMMON_IDS.contains(variantId);
    }
}
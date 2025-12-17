package gc.mc.tropical_fish;

import net.minecraft.entity.passive.TropicalFishEntity.Variant;
import net.minecraft.entity.passive.TropicalFishEntity;
import java.util.Set;
import java.util.stream.Collectors;

public class RareFishHelper {

    private static final Set<Integer> COMMON_VARIANT_IDS = TropicalFishEntity.COMMON_VARIANTS.stream()
            .map(Variant::getId)
            .collect(Collectors.toUnmodifiableSet());

    public static boolean isRareTropicalFish(int variantId) {
        return !COMMON_VARIANT_IDS.contains(variantId);
    }
}
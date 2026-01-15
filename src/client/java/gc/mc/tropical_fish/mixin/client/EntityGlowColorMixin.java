package gc.mc.tropical_fish.mixin.client;

import gc.mc.tropical_fish.RareFishHelper;
import gc.mc.tropical_fish.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGlowColorMixin {

    @Inject(
            method = "getTeamColorValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;

        if (entity.getEntityWorld().isClient() && entity instanceof TropicalFishEntity fish) {
            if (ModConfig.get().enableGlowEffect) {
                int variantId = fish.getVariety().getIndex();

                if (RareFishHelper.isRareTropicalFish(variantId)) {
                    cir.setReturnValue(ModConfig.get().glowColor);
                }
            }
        }
    }
}
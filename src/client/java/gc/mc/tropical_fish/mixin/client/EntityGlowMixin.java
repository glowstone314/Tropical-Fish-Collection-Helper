package gc.mc.tropical_fish.mixin.client;

import gc.mc.tropical_fish.RareFishHelper;
import gc.mc.tropical_fish.config.ModConfig;
import gc.mc.tropical_fish.mixin.accessor.TropicalFishEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(
            method = "isGlowing",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        // 如果原本就已经在发光，则不处理
        if (cir.getReturnValue()) return;

        Entity entity = (Entity) (Object) this;

        // 仅在客户端且为热带鱼时判断
        if (entity.getEntityWorld().isClient() && entity instanceof TropicalFishEntity fish) {
            if (ModConfig.get().enableGlowEffect) {
                int variantId = ((TropicalFishEntityAccessor) fish).invokeGetTropicalFishVariant();
                if (RareFishHelper.isRareTropicalFish(variantId)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.NestweaverOrigin;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "canBeLeashed", at = @At(value = "HEAD"), cancellable = true)
    public void canBeLeashed(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (NestweaverOrigin.SPIDER_KINSHIP.isActive(player)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}

package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.OwnableSpider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow
    protected Player lastHurtByPlayer;

    @Shadow
    protected int lastHurtByPlayerTime;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER))
    public void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        Entity other = damageSource.getEntity();

        if (other instanceof OwnableSpider spider && spider.hasOwner()) {
            this.lastHurtByPlayerTime = 100;

            if (spider.getOwner() instanceof Player player) {
                this.lastHurtByPlayer = player;
            } else {
                this.lastHurtByPlayer = null;
            }
        }
    }
}

package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.NestweaverOrigin;
import net.capsey.nestweaver.OwnableSpider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HurtByTargetGoal.class)
abstract class HurtByTargetGoalMixin extends TargetGoal {
    public HurtByTargetGoalMixin(Mob mob, boolean bl) {
        super(mob, bl);
    }

    @Inject(method = "canUse", at = @At(value = "HEAD"), cancellable = true)
    public void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob instanceof OwnableSpider spider) {
            LivingEntity target = this.mob.getLastHurtByMob();
            if (spider.getOwner() == target) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}

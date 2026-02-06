package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.NestweaverOrigin;
import net.capsey.nestweaver.OwnableSpider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderTargetGoal")
abstract class SpiderTargetGoalMixin<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public SpiderTargetGoalMixin(Mob mob, Class<T> class_, boolean bl) {
        super(mob, class_, bl);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(Spider spider, Class<T> class_, CallbackInfo ci) {
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(entity -> !((OwnableSpider) spider).hasOwner() && !NestweaverOrigin.SPIDER_KINSHIP.isActive(entity));
    }
}

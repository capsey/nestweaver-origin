package net.capsey.nestweaver.ai.goal;

import java.util.EnumSet;

import net.capsey.nestweaver.OwnableSpider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SpiderOwnerHurtTargetGoal extends TargetGoal {
    private final OwnableSpider spider;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public SpiderOwnerHurtTargetGoal(OwnableSpider spider) {
        super((Mob) spider, false);
        this.spider = spider;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.spider.hasOwner()) {
            LivingEntity owner = this.spider.getOwner();
            if (owner != null) {
                this.ownerLastHurt = owner.getLastHurtMob();
                int i = owner.getLastHurtMobTimestamp();
                return i != this.timestamp
                        && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)
                        && this.spider.wantsToAttack(this.ownerLastHurt, owner);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.spider.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }
}

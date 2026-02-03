package net.capsey.nestweaver.ai.goal;

import java.util.EnumSet;

import net.capsey.nestweaver.OwnableSpider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SpiderOwnerHurtByTargetGoal extends TargetGoal {
    private final OwnableSpider spider;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public SpiderOwnerHurtByTargetGoal(OwnableSpider spider) {
        super((Mob) spider, false);
        this.spider = spider;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.spider.hasOwner()) {
            LivingEntity owner = this.spider.getOwner();

            if (owner == null) {
                return false;
            } else {
                this.ownerLastHurtBy = owner.getLastHurtByMob();
                int i = owner.getLastHurtByMobTimestamp();
                return i != this.timestamp
                        && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT)
                        && this.spider.wantsToAttack(this.ownerLastHurtBy, owner);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.spider.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}

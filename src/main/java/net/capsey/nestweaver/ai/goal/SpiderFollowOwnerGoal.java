package net.capsey.nestweaver.ai.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class SpiderFollowOwnerGoal extends Goal {
    private final Spider spider;
    private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    public SpiderFollowOwnerGoal(Spider spider, double speedModifier, float startDistance, float stopDistance) {
        this.spider = spider;
        this.speedModifier = speedModifier;
        this.navigation = spider.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(spider.getNavigation() instanceof GroundPathNavigation) && !(spider.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = ((OwnableEntity) this.spider).getOwner();
        if (owner == null) {
            return false;
        } else if (owner.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.spider.distanceToSqr(owner) < this.startDistance * this.startDistance) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(this.spider.distanceToSqr(this.owner) <= this.stopDistance * this.stopDistance);
        }
    }

    private boolean unableToMove() {
        return this.spider.isPassenger() || this.spider.isLeashed();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.spider.getPathfindingMalus(BlockPathTypes.WATER);
        this.spider.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.spider.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.spider.getLookControl().setLookAt(this.owner, 10.0F, this.spider.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.navigation.moveTo(this.owner, this.speedModifier);
        }
    }
}

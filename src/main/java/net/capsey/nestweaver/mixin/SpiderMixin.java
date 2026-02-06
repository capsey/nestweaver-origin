package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.OwnableSpider;
import net.capsey.nestweaver.ai.goal.SpiderFollowOwnerGoal;
import net.capsey.nestweaver.ai.goal.SpiderOwnerHurtByTargetGoal;
import net.capsey.nestweaver.ai.goal.SpiderOwnerHurtTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Spider.class)
abstract class SpiderMixin extends Monster implements OwnableSpider {
    @Unique
    @Nullable
    private UUID owner;

    protected SpiderMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.owner;
    }

    @Unique
    public void setOwnerUUID(@Nullable UUID uUID) {
        this.owner = uUID;
    }

    @Inject(method = "registerGoals", at = @At(value = "TAIL"))
    private void registerGoals(CallbackInfo ci) {
        Spider spider = (Spider)(Object)this;
        this.goalSelector.addGoal(8, new SpiderFollowOwnerGoal(spider, 1.0, 10.0F, 2.0F));
        this.targetSelector.addGoal(1, new SpiderOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SpiderOwnerHurtTargetGoal(this));
    }

    @Override
    public boolean wantsToAttack(LivingEntity entity, LivingEntity owner) {
        if (entity instanceof Creeper || entity instanceof Ghast) {
            return false;
        } else if (entity instanceof OwnableSpider spider) {
            return !spider.hasOwner() || spider.getOwner() != owner;
        } else if (entity instanceof Player && owner instanceof Player && !((Player) owner).canHarmPlayer((Player) entity)) {
            return false;
        } else if (entity instanceof AbstractHorse && ((AbstractHorse) entity).isTamed()) {
            return false;
        } else {
            return !(entity instanceof TamableAnimal) || !((TamableAnimal) entity).isTame();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        UUID uUID;
        if (compoundTag.hasUUID("Owner")) {
            uUID = compoundTag.getUUID("Owner");
        } else {
            String string = compoundTag.getString("Owner");
            uUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
        }

        if (uUID != null) {
            this.setOwnerUUID(uUID);
        }
    }
}

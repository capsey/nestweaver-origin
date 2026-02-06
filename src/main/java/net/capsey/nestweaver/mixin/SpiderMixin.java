package net.capsey.nestweaver.mixin;

import net.capsey.nestweaver.NestweaverOrigin;
import net.capsey.nestweaver.OwnableSpider;
import net.capsey.nestweaver.ai.goal.SpiderFollowOwnerGoal;
import net.capsey.nestweaver.ai.goal.SpiderOwnerHurtByTargetGoal;
import net.capsey.nestweaver.ai.goal.SpiderOwnerHurtTargetGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        Item item = itemStack.getItem();

        if (this.level().isClientSide) {
            boolean bl = NestweaverOrigin.SPIDER_KINSHIP.isActive(player) && this.isFood(itemStack) && this.getTarget() != player;
            return bl ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (NestweaverOrigin.SPIDER_KINSHIP.isActive(player)) {
                if (this.isFood(itemStack) && this.getTarget() != player && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    this.level().broadcastEntityEvent(this, (byte)18);
                    this.heal(item.getFoodProperties().getNutrition());
                    return InteractionResult.SUCCESS;
                }
            }

            return super.mobInteract(player, interactionHand);
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }

        super.die(damageSource);
    }

    public boolean isFood(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item.isEdible() && item.getFoodProperties().isMeat();
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 18) {
            for (int i = 0; i < 7; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }
}

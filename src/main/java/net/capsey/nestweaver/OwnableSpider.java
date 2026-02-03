package net.capsey.nestweaver;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

public interface OwnableSpider extends OwnableEntity {
    default boolean hasOwner() {
        return getOwnerUUID() != null;
    }

    boolean wantsToAttack(LivingEntity entity, LivingEntity owner);
}

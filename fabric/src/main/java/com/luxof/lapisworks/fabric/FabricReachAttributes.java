package com.luxof.lapisworks.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.luxof.lapisworks.platform.ReachAttributes;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;

/** Fabric implementation of the reach seam, backed by the reach-entity-attributes mod. */
public class FabricReachAttributes implements ReachAttributes {

    @Override
    public EntityAttribute reach() {
        return ReachEntityAttributes.REACH;
    }

    @Override
    public EntityAttribute attackRange() {
        return ReachEntityAttributes.ATTACK_RANGE;
    }

    @Override
    public double getReachDistance(LivingEntity entity, double base) {
        return ReachEntityAttributes.getReachDistance(entity, base);
    }

    @Override
    public double getAttackRange(LivingEntity entity, double base) {
        return ReachEntityAttributes.getAttackRange(entity, base);
    }
}

package com.luxof.lapisworks.neoforge;

import com.luxof.lapisworks.platform.ReachAttributes;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraftforge.common.ForgeMod;

/**
 * NeoForge implementation of the reach seam, backed by Forge's own attributes.
 * <p>
 * Fabric gets these from the third-party {@code reach-entity-attributes} mod, which has no NeoForge
 * build; Forge ships {@code ForgeMod.BLOCK_REACH} and {@code ForgeMod.ENTITY_REACH} for the same two
 * concepts, so the numbers map one-to-one.
 */
public class NeoForgeReachAttributes implements ReachAttributes {

    @Override
    public EntityAttribute reach() {
        return ForgeMod.BLOCK_REACH.get();
    }

    @Override
    public EntityAttribute attackRange() {
        return ForgeMod.ENTITY_REACH.get();
    }

    @Override
    public double getReachDistance(LivingEntity entity, double base) {
        return totalOf(entity, reach(), base);
    }

    @Override
    public double getAttackRange(LivingEntity entity, double base) {
        return totalOf(entity, attackRange(), base);
    }

    /**
     * The attribute's current value, or {@code base} when the entity does not have it.
     * <p>
     * The Fabric mod's {@code getReachDistance(entity, base)} returns the attribute's total value and
     * falls back to {@code base} only when the modifier is absent, so using the vanilla total here
     * reproduces it exactly.
     */
    private static double totalOf(LivingEntity entity, EntityAttribute attribute, double base) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance == null ? base : instance.getValue();
    }
}

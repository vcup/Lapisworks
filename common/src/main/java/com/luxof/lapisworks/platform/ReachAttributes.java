package com.luxof.lapisworks.platform;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;

/**
 * The seam over the "reach" attributes.
 * <p>
 * Lapisworks' Amel imbuement can extend how far a player reaches and how far they can attack. On
 * Fabric those are the {@code reach-entity-attributes} mod's {@code REACH} and {@code ATTACK_RANGE}
 * attributes — and that mod has no NeoForge build at all (it is a {@code ModInitializer} compiled
 * against intermediary names), so linking it from shared code crashes the NeoForge server during
 * mixin application. Forge/NeoForge ships {@code ForgeMod.BLOCK_REACH} and
 * {@code ForgeMod.ENTITY_REACH} instead, so each platform supplies its own pair here.
 */
public interface ReachAttributes {
    ReachAttributes INSTANCE = Seams.load(ReachAttributes.class);

    /** The attribute governing block interaction distance. */
    EntityAttribute reach();

    /** The attribute governing attack distance. */
    EntityAttribute attackRange();

    /** The entity's current reach distance, including any active modifiers. */
    double getReachDistance(LivingEntity entity, double base);

    /** The entity's current attack range, including any active modifiers. */
    double getAttackRange(LivingEntity entity, double base);
}

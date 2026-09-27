package com.luxof.lapisworks.items.shit;

import com.luxof.lapisworks.platform.AccessorySlot;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

/** lets you implement custom behaviour for your totem of undying item.
 * <p>the lapisworks totem item tag lets lapisworks know your item is a totem of undying. */
public interface ITotem {
    /** <code>slot</code> may be null if the item was found in the hands instead of an
     * accessory slot.
     * <p>item stack damage/decrement as well as sending a status update to the client is on you.
     * <p>view default implementation for an example. (it's how vanilla totems do this.) */
    default void revive(
        LivingEntity entity,
        ItemStack stack,
        @Nullable AccessorySlot slot
    ) {
        entity.setHealth(1.0f);
        entity.clearStatusEffects();
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
        entity.getWorld().sendEntityStatus(entity, (byte)35);
        stack.decrement(1);
    }

    /** on a status update packet with id 35 sent, the client shows the player a floating item.
     * <p>this lets you control that. */
    default ItemStack getFloatingItemToShowClientPlayerOnRevive(
        ItemStack stack,
        @Nullable AccessorySlot slot
    ) {
        return stack;
    }

    /** if false then does not try to revive the player with this item. */
    default boolean canWork(
        LivingEntity entity,
        ItemStack item,
        @Nullable AccessorySlot slot
    ) {
        return true;
    }
}

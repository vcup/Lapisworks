package com.luxof.lapisworks.platform;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * The accessory behaviour an item declares, expressed without naming either loader's API.
 * <p>
 * An item cannot implement both Trinkets' {@code Trinket} and Curios' {@code ICurioItem}, so items
 * implement this instead and each platform module wraps it: the Fabric module adapts it to
 * {@code Trinket}, the NeoForge one to {@code ICurioItem}. This is the same shape as Hex Casting's
 * {@code HexBaubleItem} + {@code TrinketsApiInterop}/{@code CuriosApiInterop} pair.
 * <p>
 * All parameters referring to a slot use {@link AccessorySlot}, never {@code SlotReference} or
 * {@code SlotContext}.
 */
public interface PlatformAccessoryItem {
    /** Called when the item is equipped onto {@code entity}. */
    default void onEquip(ItemStack stack, AccessorySlot slot, LivingEntity entity) {}

    /** Called when the item is removed from {@code entity}. */
    default void onUnequip(ItemStack stack, AccessorySlot slot, LivingEntity entity) {}

    /** Called every tick while worn. */
    default void accessoryTick(ItemStack stack, AccessorySlot slot, LivingEntity entity) {}

    /** Whether this item may be equipped into {@code slot} right now. */
    default boolean canEquip(ItemStack stack, AccessorySlot slot, LivingEntity entity) {
        return true;
    }

    /** Attribute modifiers this accessory grants while worn. */
    default Multimap<EntityAttribute, EntityAttributeModifier> getAccessoryModifiers(
        ItemStack stack,
        AccessorySlot slot,
        LivingEntity entity,
        UUID uuid
    ) {
        return ImmutableMultimap.of();
    }
}

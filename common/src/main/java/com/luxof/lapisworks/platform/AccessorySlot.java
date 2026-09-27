package com.luxof.lapisworks.platform;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * A loader-agnostic view of one equipped accessory slot.
 * <p>
 * Stands in for Trinkets' {@code SlotReference} on Fabric and Curios' {@code SlotContext} on
 * NeoForge, so shared code never mentions either loader's accessory types.
 */
public interface AccessorySlot {
    /** The stack currently in this slot. */
    ItemStack getStack();

    /** The entity wearing this slot. */
    LivingEntity getEntity();

    /** The slot identifier, e.g. {@code "necklace"}. */
    String getSlotName();

    /** The index of this slot within its slot type. */
    int getIndex();
}

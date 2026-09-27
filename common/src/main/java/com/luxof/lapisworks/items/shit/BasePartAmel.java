package com.luxof.lapisworks.items.shit;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface BasePartAmel {
    /** ran every time the Imbue Amel spell imbues amel into this item.
     * <br>the NBT tag "lapisworks:infused_amel" is set already when this is called. */
    public void onImbue(ItemStack stack, int addedAmel);

    /** not implemented and thus isn't actually used yet. */
    default void onAttack(LivingEntity user, World world, Hand hand, Entity attacked) {};
}

package com.luxof.lapisworks.mixinsupport;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface GetStacks {
    public List<HeldItemInfo> getHeldStacks();
    /** other hand comes first. */
    public List<HeldItemInfo> getHeldStacksOtherFirst();
    public List<ItemStack> getHeldItemStacks();
    /** other hand comes first. */
    public List<ItemStack> getHeldItemStacksOtherFirst();
}

package com.luxof.lapisworks.mixinsupport;

import net.minecraft.item.ItemStack;

public interface WispCanIntoItem {
    public ItemStack getStack();
    /** returns the stack before this one. */
    public ItemStack setStack(ItemStack stack);
}

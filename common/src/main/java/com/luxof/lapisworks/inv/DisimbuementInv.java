package com.luxof.lapisworks.inv;

import java.util.List;

import net.minecraft.item.ItemStack;

/** "what's the difference between this and <code>HandsInv</code>?"
 * <p>there's almost no difference. you just use this one when you want to reverse
 * amel imbuement (or match an imbuement recipe by the partial-amel/full-amel item). */
public class DisimbuementInv extends HandsInv {
    public DisimbuementInv(List<ItemStack> items) { super(items); }
}

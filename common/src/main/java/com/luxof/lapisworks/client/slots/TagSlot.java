package com.luxof.lapisworks.client.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;

public class TagSlot extends Slot {
    public TagKey<Item> tag;

    public TagSlot(TagKey<Item> tag, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.tag = tag;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return matches(stack);
    }

    public boolean matches(ItemStack stack) {
        return stack.isIn(tag);
    }
}

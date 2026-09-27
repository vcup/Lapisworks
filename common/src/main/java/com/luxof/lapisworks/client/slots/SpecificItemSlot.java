package com.luxof.lapisworks.client.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SpecificItemSlot extends Slot {
    public Item item;

    public SpecificItemSlot(Item item, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.item = item;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return matches(stack);
    }

    public boolean matches(ItemStack stack) {
        return stack.isOf(item);
    }
}

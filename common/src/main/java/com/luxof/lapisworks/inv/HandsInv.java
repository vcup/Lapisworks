package com.luxof.lapisworks.inv;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/** I use this for some recipes. */
public class HandsInv implements Inventory {
    public static final int MAINHAND = 0;
    public static final int OFFHAND = 1;
    private final List<ItemStack> items;
    public HandsInv(List<ItemStack> items) { this.items = items; }
    public List<ItemStack> getHands() { return this.items; }

    @Override public void clear() {}
    @Override public boolean canPlayerUse(PlayerEntity player) { return false; }
    @Override public ItemStack getStack(int slot) { return ItemStack.EMPTY; }
    @Override public boolean isEmpty() { return true; }
    @Override public void markDirty() {}
    @Override public ItemStack removeStack(int slot) { return ItemStack.EMPTY; }
    @Override public ItemStack removeStack(int slot, int amount) { return ItemStack.EMPTY; }
    @Override public void setStack(int slot, ItemStack stack) {}
    @Override public int size() { return 0; }
}

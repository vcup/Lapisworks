package com.luxof.lapisworks.inv;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import static net.minecraft.item.ItemStack.EMPTY;

public class EnchBrewerInv extends BrewerInv {
    public static int IDX_DUST = 5;

    public ItemStack dust;
    public EnchBrewerInv() {
        super();
        dust = EMPTY.copy();
    }

    public EnchBrewerInv(
        ItemStack input,
        ItemStack blaze, // "SET YOUR HEART ABLAZE!" (if you got that, can we be friends?)
        List<ItemStack> brewingInto,
        ItemStack dust
    ) {
        super(input, blaze, brewingInto);
        this.dust = dust;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        dust = ItemStack.fromNbt(nbt.getCompound("dust"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("dust", dust.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override public boolean isEmpty() { return super.isEmpty() && dust.isEmpty(); }
    @Override public int size() { return 6; }
    @Override public void clear() { super.clear(); dust = EMPTY.copy(); }
    @Override public ItemStack getStack(int slot) {
        return slot == 5 ? dust : super.getStack(slot);
    }
    @Override public ItemStack removeStack(int slot) {
        return slot == 5 ? dust.copyAndEmpty() : super.removeStack(slot);
    }
    @Override public ItemStack removeStack(int slot, int amount) {
        return slot == 5 ? dust.split(amount) : super.removeStack(slot, amount);
    }
    @Override public void setStack(int slot, ItemStack set) {
        if (slot == 5) dust = set;
        else super.setStack(slot, set);
    }
}

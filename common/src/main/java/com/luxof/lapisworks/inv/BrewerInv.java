package com.luxof.lapisworks.inv;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import static net.minecraft.item.ItemStack.EMPTY;

public class BrewerInv implements Inventory {
    public static int IDX_BLAZE = 0;
    public static int IDX_INPUT = 1;
    public static int IDX_BREWINGINTO_1 = 2;
    public static int IDX_BREWINGINTO_2 = 3;
    public static int IDX_BREWINGINTO_3 = 4;

    public ItemStack input;
    public ItemStack blaze;
    public List<ItemStack> brewingInto;
    public BrewerInv() {
        this.input = EMPTY.copy();
        this.blaze = EMPTY.copy();
        this.brewingInto = new ArrayList<>(List.of(
            EMPTY.copy(),
            EMPTY.copy(),
            EMPTY.copy()
        ));
    }
    /** <code>brewingInto</code> MUST be of length 3. */
    public BrewerInv(
        ItemStack input,
        ItemStack blaze, // "SET YOUR HEART ABLAZE!" (if you got that, can we be friends?)
        List<ItemStack> brewingInto
    ) {
        if (brewingInto.size() != 3)
            throw new RuntimeException("brewingInto.size() must be 3!");
        this.input = input;
        this.blaze = blaze;
        this.brewingInto = brewingInto;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound inventoryNBT = new NbtCompound();

        inventoryNBT.put("blaze", blaze.writeNbt(new NbtCompound()));
        inventoryNBT.put("input", input.writeNbt(new NbtCompound()));

        NbtCompound brewingInto = new NbtCompound();
        brewingInto.put("0", this.brewingInto.get(0).writeNbt(new NbtCompound()));
        brewingInto.put("1", this.brewingInto.get(1).writeNbt(new NbtCompound()));
        brewingInto.put("2", this.brewingInto.get(2).writeNbt(new NbtCompound()));
        inventoryNBT.put("brewingInto", brewingInto);

        nbt.put("brewerinv", inventoryNBT);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        NbtCompound inv = nbt.getCompound("brewerinv");

        blaze = ItemStack.fromNbt(inv.getCompound("blaze"));
        input = ItemStack.fromNbt(inv.getCompound("input"));

        NbtCompound brewingInto = inv.getCompound("brewingInto");
        this.brewingInto.set(0, ItemStack.fromNbt(brewingInto.getCompound("0")));
        this.brewingInto.set(1, ItemStack.fromNbt(brewingInto.getCompound("1")));
        this.brewingInto.set(2, ItemStack.fromNbt(brewingInto.getCompound("2")));
    }


    @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
    // :troll:
    @Override public boolean isEmpty() {
        return blaze.isEmpty() && input.isEmpty()
            && brewingInto.get(0).isEmpty()
            && brewingInto.get(1).isEmpty()
            && brewingInto.get(2).isEmpty()
            ? true : false;
    }
    @Override public void markDirty() {}
    @Override public int size() { return 5; }


    @Override public void clear() {
        blaze = EMPTY.copy();
        input = EMPTY.copy();
        brewingInto.set(0, EMPTY.copy());
        brewingInto.set(1, EMPTY.copy());
        brewingInto.set(2, EMPTY.copy());
    }
    // if only.
    /*protected ItemStack slotHelper(int slot, Function<ItemStack, ItemStack> applier) {
        return switch (slot) {
            case 0 -> applier.apply(input);
            case 1 -> applier.apply(blaze);
            case 2 -> applier.apply(brewingInto.get(0));
            case 3 -> applier.apply(brewingInto.get(1));
            case 4 -> applier.apply(brewingInto.get(2));
            default -> EMPTY.copy();
        };
    }*/
    @Override public ItemStack getStack(int slot) {
        return slot < 2 ? switch (slot) {
            case 0 -> blaze;
            case 1 -> input;
            default -> EMPTY.copy();
        } : brewingInto.get(slot - 2);
    }
    @Override public ItemStack removeStack(int slot) {
        return slot < 2 ? switch (slot) {
            case 0 -> blaze.copyAndEmpty();
            case 1 -> input.copyAndEmpty();
            default -> EMPTY.copy();
        } : brewingInto.get(slot - 2).copyAndEmpty();
    }
    @Override public ItemStack removeStack(int slot, int amount) {
        return slot < 2 ? switch (slot) {
            case 0 -> blaze.split(amount);
            case 1 -> input.split(amount);
            default -> EMPTY.copy();
        } : brewingInto.get(slot - 2).split(amount);
    }
    @Override public void setStack(int slot, ItemStack set) {
        switch (slot) {
            case 0 -> blaze = set;
            case 1 -> input = set;
            case 2 -> brewingInto.set(0, set);
            case 3 -> brewingInto.set(1, set);
            case 4 -> brewingInto.set(2, set);
            default -> EMPTY.copy();
        };
    }

    private static final int BASE = IDX_BREWINGINTO_1;
    public ItemStack getBrewingInto(int index) { return getStack(BASE + index); }
    public ItemStack removeBrewingInto(int index) { return removeStack(BASE + index); }
    public ItemStack removeBrewingInto(int idx, int am) { return removeStack(BASE + idx, am); }
    public void setBrewingInto(int idx, ItemStack to) { setStack(BASE + idx, to); }
}

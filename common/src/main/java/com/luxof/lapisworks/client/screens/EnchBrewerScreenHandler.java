package com.luxof.lapisworks.client.screens;

import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.client.slots.LapisPotionIngredientSlot;
import com.luxof.lapisworks.client.slots.SpecificItemSlot;
import com.luxof.lapisworks.client.slots.TagSlot;
import com.luxof.lapisworks.init.ModScreens;
import com.luxof.lapisworks.inv.EnchBrewerInv;

import static com.luxof.lapisworks.LapisworksIDs.POTION_TAG;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BLAZE;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_1;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_2;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_3;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_INPUT;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_DUST;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import static net.minecraft.item.ItemStack.EMPTY;

public class EnchBrewerScreenHandler extends ScreenHandler {
    public final EnchBrewerInv inventory;
    private final PropertyDelegate state;

    public EnchBrewerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new EnchBrewerInv(), new ArrayPropertyDelegate(7));
    }

    public EnchBrewerScreenHandler(
        int syncId,
        PlayerInventory plrInv,
        EnchBrewerInv blockInv,
        PropertyDelegate propertyDelegate
    ) {
        super(ModScreens.ENCH_BREWER_SCREEN_HANDLER, syncId);
        checkSize(blockInv, 6);
        this.inventory = blockInv;
        this.state = propertyDelegate;

        World world = plrInv.player.getWorld();
        this.addSlot(new SpecificItemSlot(Items.BLAZE_POWDER, inventory, IDX_BLAZE, 79, 45));
        this.addSlot(new LapisPotionIngredientSlot(world, inventory, IDX_INPUT, 79, 17));
        this.addSlot(new TagSlot(POTION_TAG, inventory, IDX_BREWINGINTO_1, 56, 57));
        this.addSlot(new TagSlot(POTION_TAG, inventory, IDX_BREWINGINTO_2, 79, 64));
        this.addSlot(new TagSlot(POTION_TAG, inventory, IDX_BREWINGINTO_3, 102, 57));
        this.addSlot(new SpecificItemSlot(HexItems.AMETHYST_DUST, inventory, IDX_DUST, 56, 31));
        this.addProperties(propertyDelegate);

        // inv & hotbar respectively
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(plrInv, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(plrInv, m, 8 + m * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIdx) {
        // i have little clue why this is the way it is.

        Slot slot = this.getSlot(slotIdx);
        if (slot == null || !slot.hasStack()) return EMPTY.copy();
        ItemStack ogStack = slot.getStack();
        ItemStack newStack = ogStack.copy();
        if (ogStack.isEmpty()) return ogStack;

        if (slotIdx < this.inventory.size()) {
            if (!insertItem(newStack, this.inventory.size(), this.slots.size(), true))
                return EMPTY.copy();

        } else if (!insertItem(newStack, 0, this.inventory.size(), false))
            return EMPTY.copy();


        if (newStack.isEmpty()) slot.setStack(EMPTY.copy());
        else slot.markDirty();

        slot.onTakeItem(player, ogStack);
        return ogStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return this.inventory.canPlayerUse(player); }

    public int getFuel() { return this.state.get(0); }
    public int getBrewTime() { return this.state.get(1); }
    public boolean isBrewing() { return this.state.get(2) == 1; }
    public int getPotion1Color() { return this.state.get(3); }
    public int getPotion2Color() { return this.state.get(4); }
    public int getPotion3Color() { return this.state.get(5); }
}

package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import static com.luxof.lapisworks.Lapisworks.handToString;
import static com.luxof.lapisworks.LapisworksIDs.GENERIC_BADITEM;
import static com.luxof.lapisworks.LapisworksIDs.SPECHAND_BADITEM;
import static com.luxof.lapisworks.LapisworksIDs.SPECHAND_NOITEM;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

/** Yes, if you pass an empty ItemStack for item, it still works. */
public class MishapBadHandItem extends Mishap {
    public final ItemStack item;
    public final Text wanted;
    public final Hand hand;
    public Text gotItemDesc;
    
    private ItemStack emptyIfNull(ItemStack stack) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public MishapBadHandItem(@Nullable ItemStack item, Text wanted, @Nullable Hand hand) {
        this.item = emptyIfNull(item);
        this.wanted = wanted;
        this.hand = hand;
    }
    public MishapBadHandItem(@Nullable ItemStack item, Item wanted, @Nullable Hand hand) {
        this.item = emptyIfNull(item);
        this.wanted = wanted.getName();
        this.hand = hand;
    }
    /** Replaces the period usually at the end of bad_item (not no_item) with gotItemDesc. */
    public MishapBadHandItem(@Nullable ItemStack item, Text wanted, Text gotItemDesc, @Nullable Hand hand) {
        this.item = emptyIfNull(item);
        this.wanted = wanted;
        this.hand = hand;
        this.gotItemDesc = gotItemDesc;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment ctx, Context errorCtx) {
        return dyeColor(DyeColor.BROWN);
    }

    @Override
    protected Text errorMessage(CastingEnvironment ctx, Context errorCtx) {
        if (this.item.isEmpty()) {
            return Text.translatable(
                SPECHAND_NOITEM,
                this.wanted,
                handToString(this.hand)
            );
        } else {
            return Text.translatable(
                SPECHAND_BADITEM,
                this.wanted,
                handToString(this.hand),
                // i could do another way with appending Text but then translator guy
                // would have to look at my code since it's not obvious in en_us.json
                this.gotItemDesc == null ? GENERIC_BADITEM : this.gotItemDesc,
                this.item.getCount(),
                this.item.getName()
            );
        }
    }

    @Override
    public void execute(CastingEnvironment ctx, Context errorCtx, List<Iota> stack) {
        ctx.getMishapEnvironment().dropHeldItems();
    }
}

package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import static com.luxof.lapisworks.LapisworksIDs.NOT_ENOUGH;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapNotEnoughItems extends Mishap {
    private final Text itemName;
    private final int required;
    private final int got;

    public MishapNotEnoughItems(ItemStack itemsInOffhand, int requiredAmount) {
        this.itemName = itemsInOffhand.getName();
        this.got = itemsInOffhand.getCount();
        this.required = requiredAmount;
    }
    public MishapNotEnoughItems(Text item, int got, int required) {
        this.itemName = item;
        this.got = got;
        this.required = required;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment ctx, Context errorCtx) {
        return dyeColor(DyeColor.BROWN);
    }

    @Override
    public void execute(CastingEnvironment env, Context errorCtx, List<Iota> stack) {
        env.getMishapEnvironment().dropHeldItems();
    }

    @Override
    public Text errorMessage(CastingEnvironment ctx, Context errorCtx) {
        return Text.translatable(
            NOT_ENOUGH,
            this.required,
            this.itemName,
            this.got,
            this.itemName
        );
    }
}

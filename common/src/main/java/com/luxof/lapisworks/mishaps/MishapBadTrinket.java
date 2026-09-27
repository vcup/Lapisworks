package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import static com.luxof.lapisworks.LapisworksIDs.WRONG_EQUIPPED;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapBadTrinket extends Mishap {
    public final Item trinket;
    public final Text wanted;

    public MishapBadTrinket(Item trinket, Text wanted) {
        this.trinket = trinket;
        this.wanted = wanted;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment arg0, Context arg1) {
        return dyeColor(DyeColor.BLUE);
    }

    @Override
    public void execute(CastingEnvironment arg0, Context arg1, List<Iota> arg2) {}

    @Override
    protected Text errorMessage(CastingEnvironment arg0, Context arg1) {
        return Text.translatable(
            WRONG_EQUIPPED,
            trinket.getName(),
            wanted
        );
    }
}

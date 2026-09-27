package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import static com.luxof.lapisworks.LapisworksIDs.NOT_EQUIPPED;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import org.jetbrains.annotations.NotNull;

public class MishapNotWearingTrinket extends Mishap {
    private final Text trinket;

    public MishapNotWearingTrinket(@NotNull Item requiredTrinket) {
        this.trinket = requiredTrinket.getName();
    }
    public MishapNotWearingTrinket(@NotNull Text requiredTrinket) {
        this.trinket = requiredTrinket;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment arg0, Context arg1) {
        return dyeColor(DyeColor.LIGHT_BLUE);
    }

    @Override
    public void execute(CastingEnvironment arg0, Context arg1, List<Iota> arg2) {}

    @Override
    protected Text errorMessage(CastingEnvironment arg0, Context arg1) {
        return Text.translatable(
            NOT_EQUIPPED,
            this.trinket
        );
    }
}

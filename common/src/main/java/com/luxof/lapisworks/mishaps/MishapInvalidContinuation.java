package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.List;

import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapInvalidContinuation extends Mishap {
    public final Text wanted;

    public MishapInvalidContinuation(Text wanted) {
        this.wanted = wanted;
    }

    public MishapInvalidContinuation(String wanted) {
        this.wanted = Text.translatable(wanted);
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment arg0, Context arg1) {
        return dyeColor(DyeColor.MAGENTA);
    }

    @Override
    public void execute(CastingEnvironment arg0, Context arg1, List<Iota> arg2) {}

    @Override
    protected Text errorMessage(CastingEnvironment arg0, Context arg1) {
        return Text.translatable("mishaps.lapisworks.invalid_cont", wanted);
    }
}

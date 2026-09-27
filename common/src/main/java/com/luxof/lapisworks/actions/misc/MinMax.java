package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class MinMax extends ConstMediaActionNCT {
    private final boolean min;
    public MinMax(boolean min) {
        this.min = min;
    }

    public int argc = 2;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        double num1 = stack.getDouble(0);
        double num2 = stack.getDouble(1);

        return List.of(new DoubleIota(
            (min
                ? num1 < num2
                : num1 > num2
            )
                ? num1
                : num2
        ));
    }
}

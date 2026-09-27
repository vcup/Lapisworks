package com.luxof.lapisworks.interop.hierophantics.patterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class SetAmalgamRange extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Amalgamation amalgam = stack.getAmalgamation(0);
        amalgam.range = stack.getPositiveDoubleUnder(1, amalgam.getMaxRange());
        amalgam.updateOrigin(world);
        return List.of(new Amalgamation.AmalgamationIota(amalgam));
    }
}

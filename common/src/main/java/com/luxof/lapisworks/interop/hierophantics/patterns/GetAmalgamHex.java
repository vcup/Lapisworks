package com.luxof.lapisworks.interop.hierophantics.patterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class GetAmalgamHex extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        return List.of(stack.get(0), new ListIota(stack.getAmalgamation(0).hex));
    }
}

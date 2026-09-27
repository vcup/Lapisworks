package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertInRitual;

import java.util.List;

public class GetRitualTuning extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Iota tuned = assertInRitual(ctx).getTunedFrequency();
        return List.of(tuned == null ? new NullIota() : tuned);
    }
}

package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import java.util.List;
import java.util.Optional;

import ram.talia.hexal.api.OperatorUtilsKt;
import ram.talia.hexal.common.entities.BaseCastingWisp;
import ram.talia.hexal.api.casting.mishaps.MishapOthersWisp;

public class LapisalHexIotaStack {
    public static Optional<BaseCastingWisp> getBaseCastingWisp(
        List<? extends Iota> stack, int idx, int argc
    ) {
        return Optional.of(OperatorUtilsKt.getBaseCastingWisp(stack, idx, argc));
    }

    public static Optional<BaseCastingWisp> getBaseCastingWispOwnedByThis(
        List<? extends Iota> stack, int idx, int argc, CastingEnvironment ctx
    ) {
        BaseCastingWisp wisp = getBaseCastingWisp(stack, idx, argc).get();

        if (wisp.getCaster() != ctx.getCastingEntity())
            throw new MishapOthersWisp(wisp.getCaster());

        return Optional.of(wisp);
    }
}

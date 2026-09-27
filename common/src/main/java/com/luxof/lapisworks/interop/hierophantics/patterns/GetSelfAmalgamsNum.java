package com.luxof.lapisworks.interop.hierophantics.patterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;

import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;

public class GetSelfAmalgamsNum extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        if (!(ctx.getCastingEntity() instanceof ServerPlayerEntity sp))
            throw new MishapBadCaster();
        return List.of(new DoubleIota(((ChariotServerPlayer)sp).getFusedAmalgamations().size()));
    }
}

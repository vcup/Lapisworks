package com.luxof.lapisworks.interop.hierophantics.patterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;

public class RemoveSelfAmalgam extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        if (!(ctx.getCastingEntity() instanceof ServerPlayerEntity sp))
            throw new MishapBadCaster();
        ArrayList<Amalgamation> amalgams = ((ChariotServerPlayer)sp).getFusedAmalgamations();
        amalgams.remove(stack.getPositiveIntUnder(0, amalgams.size()));
        return List.of();
    }
}

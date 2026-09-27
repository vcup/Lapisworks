package com.luxof.lapisworks.interop.hierophantics.patterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GetAmalgamation extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        if (!(world.getBlockEntity(pos) instanceof ChariotMindEntity chariotMind))
            throw new MishapBadBlock(pos, Chariot.CHARIOT_MIND.getName());

        return List.of(new Amalgamation.AmalgamationIota(chariotMind.getAmalgamation(world)));
    }
}

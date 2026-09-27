package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.mod.HexTags.Blocks;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class IsConjureable extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = dust(0.01);

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        return List.of(new BooleanIota(
            world.getBlockState(stack.getBlockPosInRange(0)).isIn(Blocks.CHEAP_TO_BREAK_BLOCK)
        ));
    }
}

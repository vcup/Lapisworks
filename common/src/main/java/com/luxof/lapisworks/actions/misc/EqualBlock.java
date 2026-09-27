package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EqualBlock extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockState a = ctx.getWorld().getBlockState(stack.getBlockPosInRange(0));
        BlockState b = ctx.getWorld().getBlockState(stack.getBlockPosInRange(1));
        // i have no fucking clue how an identity check ever returns true for these
        return List.of(new BooleanIota(a == b));
    }

    public BlockPos confirmInAmbit(BlockPos pos, CastingEnvironment ctx) {
        ctx.assertPosInRange(pos);
        return pos;
    }
}

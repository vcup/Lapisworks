package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.LapisworksIDs.SIMP_IMP_BLOCK;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class AskSImp extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPos(0);
        ctx.assertPosInRange(pos);
        if (!(ctx.getWorld().getBlockEntity(pos) instanceof SimpleImpetusEntity simpleImpetus))
            throw new MishapBadBlock(pos, SIMP_IMP_BLOCK);

        return List.of(
            simpleImpetus.getIsTuned() ?
                new PatternIota(simpleImpetus.getTunedPattern()) :
                new NullIota()
        );
    }
}

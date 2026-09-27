package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;

import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertIsThisBlock;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GetAmethystTuning extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        assertIsThisBlock(
            world,
            pos,
            ModBlocks.TUNEABLE_AMETHYST
        );
        Iota tuned = ((TuneableAmethystEntity)world.getBlockEntity(pos)).getTunedFrequency();
        return List.of(tuned == null ? new NullIota() : tuned);
    }
}

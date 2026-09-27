package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.LapisworksIDs.LIVE_JUKEBOX_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class SongPrfn extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);

        LiveJukeboxEntity liveJukeBox = throwIfEmpty(
            ctx.getWorld().getBlockEntity(pos, ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE),
            new MishapBadBlock(pos, LIVE_JUKEBOX_BLOCK)
        );

        return List.of(
            //                                                   vvvvvv fuck you too Java
            new ListIota(liveJukeBox.notes.stream().map(integ -> (Iota)new DoubleIota(integ)).toList()),
            new DoubleIota(liveJukeBox.frequency)
        );
    }
}

package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;

import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GetLinkableLinks extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        LinkableMediaBlock linkable = assertLinkableThere(pos, ctx);

        return List.of(
            new ListIota(
                linkable.getLinks()
                    .stream()
                    .map(p -> (Iota)new Vec3Iota(p.toCenterPos()))
                    .toList()
            )
        );
    }
}

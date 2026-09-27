package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class GetThatMedia extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        MediaTransferInterface mti = stack.getMediaTransferInterface(0);

        return List.of(
            new DoubleIota(
                mti.isMTIAtThisTime(ctx)
                    ? mti.getMediaHere() / (double)MediaConstants.DUST_UNIT
                    : 0.0
            )
        );
    }
}

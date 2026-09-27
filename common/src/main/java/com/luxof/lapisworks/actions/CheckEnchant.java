package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class CheckEnchant extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = (long)(MediaConstants.DUST_UNIT * 0.01);

    @Override
    public List<Iota> execute(HexIotaStack args, CastingEnvironment ctx) {
        LapisworksInterface ent = (LapisworksInterface)args.getLivingEntityButNotArmorStand(0);
        return List.of(
            new DoubleIota(
                (ent).getEnchantments().get(
                    args.getIntBetween(1, 0, ent.getEnchantments().size() - 1)
                )
            )
        );
    }    
}

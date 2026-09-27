package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.Lapisworks.intToHand;
import static com.luxof.lapisworks.LapisworksIDs.READABLE;
import static com.luxof.lapisworks.init.Mutables.Mutables.maxHands;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ReadFromHand extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        int hand = stack.getIntBetween(0, 0, maxHands - 1);
        final Hand HAND = intToHand(hand);

        ItemStack heldStack = getStackFromHand(ctx, hand);
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(heldStack);

        if (
            iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null && iotaHolder.emptyIota() == null)
        )
            throw new MishapBadHandItem(
                heldStack,
                READABLE,
                HAND
            );
        return List.of(iotaHolder.readIota(ctx.getWorld()));
    }
}

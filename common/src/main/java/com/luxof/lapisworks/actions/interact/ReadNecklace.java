package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadTrinket;
import com.luxof.lapisworks.mishaps.MishapNotWearingTrinket;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getEquippedAccessoriesIn;
import static com.luxof.lapisworks.LapisworksIDs.READABLE;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ReadNecklace extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack args, CastingEnvironment ctx) {
        LivingEntity ent = throwIfNull(ctx.getCastingEntity(), new MishapBadCaster());

        List<ItemStack> trinkets = getEquippedAccessoriesIn(ent, "necklace");
        ADIotaHolder iotaHolder = trinkets.size() == 1
            ? IXplatAbstractions.INSTANCE.findDataHolder(trinkets.get(0))
            : trinkets.stream().flatMap((ItemStack stack) -> {

                ADIotaHolder holder = IXplatAbstractions.INSTANCE.findDataHolder(stack);

                return holder != null && (
                            holder.readIota(world) != null ||
                            holder.emptyIota() != null
                        ) ? Stream.of(holder) : Stream.of();
                })
                .findFirst()
                .orElseThrow(() -> new MishapNotWearingTrinket(READABLE));

        if (
            iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null & iotaHolder.emptyIota() == null)
        )
            throw !trinkets.get(0).isEmpty()
                ? new MishapBadTrinket(
                    trinkets.get(0).getItem(),
                    READABLE
                )
                : new MishapNotWearingTrinket(READABLE);

        return List.of(iotaHolder.readIota(ctx.getWorld()));
    }
}

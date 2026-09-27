package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadTrinket;
import com.luxof.lapisworks.mishaps.MishapNotWearingTrinket;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getEquippedAccessoriesIn;
import static com.luxof.lapisworks.LapisworksIDs.WRITEABLE;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WriteNecklace extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Iota iota = stack.get(0);
        LivingEntity ent = throwIfNull(ctx.getCastingEntity(), new MishapBadCaster());

        List<ItemStack> trinkets = getEquippedAccessoriesIn(ent, "necklace");
        ADIotaHolder iotaHolder = trinkets.size() == 1
            ? IXplatAbstractions.INSTANCE.findDataHolder(trinkets.get(0))
            : trinkets.stream().flatMap((ItemStack itemStack) -> {

                ADIotaHolder holder = IXplatAbstractions.INSTANCE.findDataHolder(itemStack);

                return holder != null && holder.writeIota(iota, true)
                        ? Stream.of(holder) : Stream.of();
                })
                .findFirst()
                .orElseThrow(() -> new MishapNotWearingTrinket(WRITEABLE));

        if (
            iotaHolder == null || !iotaHolder.writeIota(iota, true)
        )
            throw !trinkets.get(0).isEmpty()
                ? new MishapBadTrinket(
                    trinkets.get(0).getItem(),
                    WRITEABLE
                )
                : new MishapNotWearingTrinket(WRITEABLE);

        PlayerEntity truename = MishapOthersName
            .getTrueNameFromDatum(iota, (PlayerEntity)ctx.getCastingEntity());
        if (truename != null) throw new MishapOthersName(truename);

        iotaHolder.writeIota(iota, false);

        return List.of();
    }
}

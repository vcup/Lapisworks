package com.luxof.lapisworks.actions.wizard.enchsent;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LocateMySent extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        if (!(ctx.getCastingEntity() instanceof ServerPlayerEntity caster))
            throw new MishapBadCaster();

        Vec3d sentPos = ((EnchSentInterface)caster).getEnchantedSentinel();
        return List.of(
            sentPos == null
                ? new NullIota()
                : new Vec3Iota(sentPos)
        );
    }
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.luxof.lapisworks.mixinsupport.WispCanIntoItem;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import ram.talia.hexal.api.casting.eval.env.WispCastEnv;
import ram.talia.hexal.common.entities.BaseCastingWisp;

@Mixin(value = WispCastEnv.class, remap = false)
public abstract class WispCastEnvMixin {

    @Shadow @Final private BaseCastingWisp wisp;

    @ModifyReturnValue(method = "getPrimaryStacks", at = @At("RETURN"))
    protected List<HeldItemInfo> lapisworks$gibThisWispGooberItsItem_pleading_face_(
        List<HeldItemInfo> og
    ) {
        // ik the og impl returns an ArrayList
        // but i'd rather NOT catch an unimpl err for something like this cuz of another mod
        var ret = og instanceof ArrayList ? og : new ArrayList<>(og);
    
        ret.add(new HeldItemInfo(
            ((WispCanIntoItem)wisp).getStack(),
            Hand.MAIN_HAND
        ));

        return ret;
    }
}

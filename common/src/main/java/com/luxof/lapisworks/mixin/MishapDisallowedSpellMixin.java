package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell;

import com.luxof.lapisworks.LapisworksServer;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MishapDisallowedSpell.class, remap = false)
public abstract class MishapDisallowedSpellMixin extends Mishap {
    @Shadow
    @Final
    private String type;
    @Shadow
    @Final
    private Identifier actionKey;

    @Inject(
        method = "errorMessage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void remilia$robbiesExaltNameShit(
        CastingEnvironment ctx,
        Mishap.Context errorCtx,
        CallbackInfoReturnable<Text> cir
    ) {
        if (actionKey == null) return;
        else if (actionKey.toString().equals("lapisworks:robbie_exalt")) {
            cir.setReturnValue(
                error(
                    type,
                    "hexcasting.action.lapisworks:robbie_exalt"
                        + String.valueOf(LapisworksServer.ROBBIES_EXALT_VARIANT)
                )
            );
        }
    }
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.interop.patchouli.PatternProcessor;

import com.luxof.lapisworks.client.LapisworksClient;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.api.IVariableProvider;

@Mixin(value = PatternProcessor.class, remap = false)
public class PatternProcessorMixin {
    @Shadow
    private String translationKey;

    @Inject(
        method = "setup",
        at = @At("TAIL")
    )
    private void lapisworks$doRobbiesExalt(World world, IVariableProvider vars, CallbackInfo ci) {
        if (vars.has("header")) return;

        String opName = vars.get("op_id").asString();
        if (opName.equals("lapisworks:robbie_exalt")) {
            translationKey += String.valueOf(LapisworksClient.ROBBIES_EXALT_VARIANT_CLIENT);
        }
    }
}

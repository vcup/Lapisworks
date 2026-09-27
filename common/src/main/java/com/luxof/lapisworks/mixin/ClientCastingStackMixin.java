package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.client.ClientCastingStack;
import at.petrak.hexcasting.api.client.HexPatternRenderHolder;

import com.luxof.lapisworks.mixinsupport.SpiralPatternsClearable;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientCastingStack.class, remap = false)
public abstract class ClientCastingStackMixin implements SpiralPatternsClearable {

    @Unique
    private boolean doSpirals = false;

    @Shadow
    private ArrayList<HexPatternRenderHolder> patterns;

    @Override
    public void setSpiralPatternsClearing(boolean yesOrNo) {
        if (yesOrNo && patterns.size() > 0)
            patterns.forEach(it -> it.setLifetime(0));
        doSpirals = !yesOrNo;
    }

    @Override
    public boolean getSpiralPatternsClearing() {
        return doSpirals;
    }

    @Inject(
        at = @At("HEAD"),
        method = "addPattern",
        cancellable = true
    )
    public void lapisworks$preventAddingPatternsIfYeh(HexPattern pat, int lifetime, CallbackInfo ci) {
        if (getSpiralPatternsClearing()) ci.cancel();
    }
}

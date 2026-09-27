package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.ControlCircleTickSpeed;

import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// i am NOT dealing with remap=true on the other mixin!
@Mixin(value = CircleExecutionState.class, remap = false)
public class CircleExecutionStateMixin2 implements ControlCircleTickSpeed {
    @Unique private Integer forcedTPT = null;

    @Unique @Override @Nullable public Integer getForcedTPT() { return this.forcedTPT; }
    @Unique @Override public void setForcedTPT(int tpt) { this.forcedTPT = tpt; }
    @Unique @Override public void clearForcedTPT() { this.forcedTPT = null; }

    @Inject(at = @At("HEAD"), method = "getTickSpeed", cancellable = true)
    protected void getTickSpeed(CallbackInfoReturnable<Integer> cir) {
        if (this.forcedTPT == null) return;
        cir.setReturnValue(this.forcedTPT);
    }
}

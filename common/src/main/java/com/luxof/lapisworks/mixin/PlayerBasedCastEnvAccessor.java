package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public interface PlayerBasedCastEnvAccessor {
    @Invoker("canOvercast")
    boolean lapisworks$invokeCanOvercast();
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Iota.class, remap = false)
public interface IotaAccessor {
    @Accessor("payload")
    Object lapisworks$getPayload();
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.impl.HexAPIImpl;

import com.luxof.lapisworks.LapisworksServer;

import net.minecraft.registry.RegistryKey;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Appends the per-world "Robbie's Exalt" variant to the pattern's translation key.
 * <p>
 * Mixin cannot apply an injector to an interface's {@code default} method at all ("is not supported
 * on interface mixin method"), which is what the original interface mixin relied on. Instead this
 * mixes into the concrete implementation and <em>overrides</em> the default method, delegating to
 * the interface's own body via {@code HexAPI.super} and then appending the suffix. That is a
 * supported mixin shape on both loaders and produces identical behaviour.
 */
@Mixin(value = HexAPIImpl.class, remap = false)
public abstract class HexAPIMixin implements HexAPI {

    @Override
    public String getActionI18nKey(RegistryKey<ActionRegistryEntry> key) {
        String original = HexAPI.super.getActionI18nKey(key);
        return original +
            (original.endsWith("lapisworks:robbie_exalt")
                ? String.valueOf(LapisworksServer.ROBBIES_EXALT_VARIANT) : "");
    }
}

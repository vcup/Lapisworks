package com.luxof.lapisworks.fabric;

import com.luxof.lapisworks.platform.LapisworksRegistry;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Fabric implementation of the registry seam.
 * <p>
 * Fabric never locks its registries, so a plain {@code Registry.register} is all that is needed --
 * exactly the behaviour shared code had before the port.
 */
public class FabricRegistry implements LapisworksRegistry {

    @Override
    public <T> T register(Registry<T> registry, Identifier id, T value) {
        return Registry.register(registry, id, value);
    }

    @Override
    public <T> T registerUnlocked(Registry<T> registry, Identifier id, T value) {
        // Fabric never locks registries, so both paths are the same write.
        return Registry.register(registry, id, value);
    }
}

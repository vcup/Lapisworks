package com.luxof.lapisworks.fabric;

import com.luxof.lapisworks.Lapisworks;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric entrypoint.
 * <p>
 * Shared code reaches the platform through {@code com.luxof.lapisworks.platform.Seams}, which
 * resolves implementations from {@code META-INF/services}; this module ships the Fabric ones, so
 * there is nothing to install by hand here. This class only has to kick off shared initialization.
 */
public class LapisworksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Fabric never locks its registries, so both phases can run straight through here. They are
        // still kept separate so the shared API matches NeoForge's much stricter ordering.
        Lapisworks.initEarly();
        Lapisworks.initLate();
    }
}

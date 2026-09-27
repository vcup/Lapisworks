package com.luxof.lapisworks.init;

import at.petrak.hexcasting.common.lib.HexBlocks;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.Set;

import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPOIs {
    public static final RegistryKey<PointOfInterestType> SLATES_KEY = RegistryKey.of(
        RegistryKeys.POINT_OF_INTEREST_TYPE,
        id("slates_poi")
    );
    // TODO: remove this in the 1.21 port, i have my own system now and this is kept around to not break old worlds
    public static final PointOfInterestType SLATES_POI_TYPE = registerPOI(
        "slates_poi",
        Set.copyOf(HexBlocks.SLATE.getStateManager().getStates())
    );

    public static final RegistryKey<PointOfInterestType> SIMP_IMPETUS_KEY = RegistryKey.of(
        RegistryKeys.POINT_OF_INTEREST_TYPE,
        id("simple_impetus_poi")
    );
    public static final PointOfInterestType SIMP_IMPETUS_POI_TYPE = registerPOI(
        "simple_impetus_poi",
        Set.copyOf(ModBlocks.SIMPLE_IMPETUS.getStateManager().getStates())
    );

    /**
     * Fabric's {@code PointOfInterestHelper} is a thin wrapper that registers a fresh
     * {@code PointOfInterestType} (1 ticket, 1 block of search distance) and returns it, so doing
     * that directly is both equivalent and loader-agnostic.
     */
    private static PointOfInterestType registerPOI(String name, Set<net.minecraft.block.BlockState> states) {
        return LapisworksRegistry.INSTANCE.register(
            Registries.POINT_OF_INTEREST_TYPE,
            id(name),
            new PointOfInterestType(states, 1, 1)
        );
    }

    public static void crawlOutOfHell() {}
}

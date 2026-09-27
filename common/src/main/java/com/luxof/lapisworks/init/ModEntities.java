package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.village.VillagerType;

public class ModEntities {
    public static final VillagerType JACK = new VillagerType("jack");
    /** :clueless: */
    public static void doSomethingFun() {
        register("jack", JACK);
    }

    public static void register(
        String name,
        VillagerType type
    ) {
        LapisworksRegistry.INSTANCE.register(Registries.VILLAGER_TYPE, id(name), type);
    }
}

package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class LapisSounds {
    public static final SoundEvent COLLAR_BELL = register("collar_bell");

    public static void imagineArfingCouldntBeMe() {}

    public static SoundEvent register(
        String name
    ) {
        return LapisworksRegistry.INSTANCE.register(Registries.SOUND_EVENT, id(name), SoundEvent.of(id(name)));
    }
}

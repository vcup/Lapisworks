package com.luxof.lapisworks.fabric;

import java.util.function.Function;

import com.luxof.lapisworks.platform.ClientRegistrations;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 * Fabric implementation of the client-registration seam, backed by the Fabric API registries.
 * <p>
 * Fabric's registries accept these calls at any point during client initialization, so this is a
 * thin forwarding layer; only NeoForge needs the request to be buffered until its event fires.
 */
public class FabricClientRegistrations implements ClientRegistrations {

    @Override
    public <T extends ParticleEffect> void registerParticleFactory(
        ParticleType<T> type,
        Function<SpriteProvider, ParticleFactory<T>> factory
    ) {
        ParticleFactoryRegistry.getInstance().register(type, factory::apply);
    }

    @Override
    public void registerKeyBinding(KeyBinding binding) {
        KeyBindingHelper.registerKeyBinding(binding);
    }
}

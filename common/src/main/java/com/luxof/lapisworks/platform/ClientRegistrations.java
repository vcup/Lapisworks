package com.luxof.lapisworks.platform;

import java.util.function.Function;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 * The seam over client registrations that must happen inside a loader-specific window.
 * <p>
 * Particle factories and key bindings cannot be registered at an arbitrary time: Fabric and NeoForge
 * each open a window for them and then close it. Registering outside that window is not merely
 * ignored -- Fabric API's particle registrar falls back to a dead one, and NeoForge's key-mapping
 * registrar appends to the options list too late. Done that way, startup logged "Something is
 * attempting to register particle providers at a later point than intended!" and "Key mapping ...
 * registered after event", and the particles and key bind were not usable.
 * <p>
 * Shared code therefore declares what it wants here, and each platform module performs the
 * registration from its own loader's client-registration callback.
 */
public interface ClientRegistrations {
    ClientRegistrations INSTANCE = Seams.load(ClientRegistrations.class);

    /**
     * Registers the client factory that draws a particle type.
     * <p>
     * The factory takes the sprite set the loader resolves for the particle, matching both loaders'
     * native particle-factory shape.
     */
    <T extends ParticleEffect> void registerParticleFactory(
        ParticleType<T> type,
        Function<SpriteProvider, ParticleFactory<T>> factory
    );

    /** Registers a key binding so it appears in the controls screen and can be pressed. */
    void registerKeyBinding(KeyBinding binding);
}

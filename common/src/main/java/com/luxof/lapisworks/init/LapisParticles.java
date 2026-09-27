package com.luxof.lapisworks.init;

import com.luxof.lapisworks.client.particles.*;
import com.luxof.lapisworks.platform.ClientRegistrations;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;

public class LapisParticles {
    /**
     * Local stand-in for Fabric's {@code FabricParticleTypes.simple()}.
     * <p>
     * {@code DefaultParticleType}'s only constructor is {@code protected}, so the common module cannot
     * use {@code new DefaultParticleType(false)} directly (it does not compile -- verified with javac
     * against the Yarn-mapped 1.20.1 jar), and Architectury does not ship a particle-type factory or
     * widen the constructor. Fabric's own helper works by returning an anonymous subclass, so this
     * subclass reproduces exactly that: same class hierarchy, same {@code alwaysShow=false} argument,
     * no added behaviour.
     */
    public static class SimpleParticleType extends DefaultParticleType {
        public SimpleParticleType() { super(false); }
    }

    public static final DefaultParticleType FLOATING_ENCHANT = new SimpleParticleType();
    public static final DefaultParticleType AMETHYST_DUST = new SimpleParticleType();

    public static void pawtickle() {
        register(FLOATING_ENCHANT, "floating_enchant");
        register(AMETHYST_DUST, "amethyst_dust");
    }
    public static void clientTicklesPaw() {
        // Through the seam: each loader needs these registered from inside its own particle-provider
        // window, and doing it afterwards leaves them unroutable.
        registerClient(FLOATING_ENCHANT, FloatingEnchant.FloatingEnchantFactory::new);
        registerClient(AMETHYST_DUST, AmethystDust.AmethystDustFactory::new);
    }

    private static void register(
        ParticleType<?> particleType,
        String name
    ) {
        LapisworksRegistry.INSTANCE.register(Registries.PARTICLE_TYPE, id(name), particleType);
    }
    private static <T extends ParticleEffect> void registerClient(
        ParticleType<T> type,
        java.util.function.Function<net.minecraft.client.particle.SpriteProvider,
            net.minecraft.client.particle.ParticleFactory<T>> factory
    ) {
        ClientRegistrations.INSTANCE.registerParticleFactory(type, factory);
    }
}

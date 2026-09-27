package com.luxof.lapisworks.neoforge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.luxof.lapisworks.platform.ClientRegistrations;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * NeoForge implementation of the client-registration seam.
 * <p>
 * Measured event order on this loader is: mod constructor, {@code RegisterParticleProvidersEvent},
 * {@code RegisterKeyMappingsEvent}, {@code FMLClientSetupEvent}. Shared client initialization runs
 * from the last of those, i.e. after both registration windows have closed, so the two registrations
 * are buffered here and drained by the listeners. The platform entrypoint calls
 * {@code LapisworksClient.initClientEarly()} from the mod constructor (before the events), which is
 * what queues these requests in the first place -- a request made after an event has fired cannot be
 * honoured and is reported as an error rather than silently dropped.
 */
public class NeoForgeClientRegistrations implements ClientRegistrations {

    private record ParticleRequest(
        ParticleType<?> type,
        Function<SpriteProvider, ? extends ParticleFactory<?>> factory
    ) {}

    private static final List<ParticleRequest> PENDING_PARTICLES = new ArrayList<>();
    private static final List<KeyBinding> PENDING_KEYS = new ArrayList<>();

    private static boolean particleWindowClosed = false;
    private static boolean keyWindowClosed = false;

    /** Subscribes the drain listeners. Called from mod construction. */
    public static void init(IEventBus modBus) {
        modBus.addListener((RegisterParticleProvidersEvent event) -> {
            for (ParticleRequest request : PENDING_PARTICLES) {
                registerParticle(request, event);
            }
            PENDING_PARTICLES.clear();
            particleWindowClosed = true;
        });
        modBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (KeyBinding binding : PENDING_KEYS) {
                event.register(binding);
            }
            PENDING_KEYS.clear();
            keyWindowClosed = true;
        });
    }

    @Override
    public <T extends ParticleEffect> void registerParticleFactory(
        ParticleType<T> type,
        Function<SpriteProvider, ParticleFactory<T>> factory
    ) {
        if (particleWindowClosed) {
            throw new IllegalStateException(
                "Particle factory for " + type + " was requested after RegisterParticleProvidersEvent; "
                    + "call LapisworksClient.initClientEarly() from the mod constructor instead."
            );
        }
        PENDING_PARTICLES.add(new ParticleRequest(type, factory));
    }

    @Override
    public void registerKeyBinding(KeyBinding binding) {
        if (keyWindowClosed) {
            throw new IllegalStateException(
                "Key binding " + binding.getTranslationKey() + " was requested after "
                    + "RegisterKeyMappingsEvent; call LapisworksClient.initClientEarly() from the mod "
                    + "constructor instead."
            );
        }
        PENDING_KEYS.add(binding);
    }

    /**
     * Forge hands the factory a {@code SpriteSet}, which is what these factories take, so
     * {@code registerSpriteSet} is the Forge-native entry point (no Architectury involvement).
     */
    private static <T extends ParticleEffect> void registerParticle(
        ParticleType<T> type,
        Function<SpriteProvider, ParticleFactory<T>> factory,
        RegisterParticleProvidersEvent event
    ) {
        event.registerSpriteSet(type, spriteSet -> factory.apply(spriteSet));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerParticle(ParticleRequest request, RegisterParticleProvidersEvent event) {
        registerParticle(
            (ParticleType) request.type(),
            (Function) request.factory(),
            event
        );
    }
}

package com.luxof.lapisworks.neoforge;

import java.util.ArrayList;
import java.util.List;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.platform.LapisworksRegistry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;

/**
 * NeoForge implementation of the registry seam.
 * <p>
 * Forge freezes every registry before mods run and reopens them one at a time, only for the
 * duration of that registry's {@code RegisterEvent}. Two consequences shape this class:
 * <ul>
 *   <li>{@code Registry.register} works only for the registry whose event is currently firing;
 *       the sanctioned way in is {@code event.register(...)}, which writes to that same registry.</li>
 *   <li>A Block or Item must be <em>constructed</em> during the window too: their constructors create
 *       an "intrusive holder" on their registry, and one created outside the window is never
 *       registered, which Forge then reports as "Some intrusive holders were not registered".</li>
 * </ul>
 * So shared content initialization runs on the very first {@code RegisterEvent} (capturing every
 * write, since no registry's event has been missed yet), and each registry's queued writes are then
 * flushed during its own event.
 */
public class NeoForgeRegistry implements LapisworksRegistry {

    private record Write(Registry<?> registry, Identifier id, Object value) {}

    private static final List<Write> PENDING = new ArrayList<>();

    private static boolean contentInitialized = false;

    public static void init(IEventBus modBus) {
        // HIGHEST so shared content initialization happens before any other mod's listener could
        // register anything, i.e. as early as the window allows.
        modBus.addListener(EventPriority.HIGHEST, false, RegisterEvent.class, NeoForgeRegistry::onRegister);
    }

    @Override
    public <T> T register(Registry<T> registry, Identifier id, T value) {
        PENDING.add(new Write(registry, id, value));
        return value;
    }

    @Override
    public <T> T registerUnlocked(Registry<T> registry, Identifier id, T value) {
        // Registries owned by other mods (Hex Casting's action / iota-type ones) are not managed by
        // Forge and are never locked, so a direct write works -- and is required, because the
        // deferred queue is only replayed during Forge's RegisterEvent, which happens before Hex
        // creates those registries.
        return Registry.register(registry, id, value);
    }

    private static void onRegister(RegisterEvent event) {
        if (!contentInitialized) {
            // First event: build all content now, while the window is open, so every Block/Item's
            // intrusive holder is created inside it. Everything they register lands in PENDING.
            contentInitialized = true;
            Lapisworks.initEarly();
        }
        flush(event);
    }

    /** Applies the queued writes that belong to the registry this event is for. */
    private static void flush(RegisterEvent event) {
        if (PENDING.isEmpty()) return;

        RegistryKey<? extends Registry<?>> eventKey = event.getRegistryKey();
        List<Write> matching = new ArrayList<>();
        for (Write write : PENDING) {
            RegistryKey<? extends Registry<?>> key = write.registry().getKey();
            if (key != null && key.equals(eventKey)) matching.add(write);
        }
        if (matching.isEmpty()) return;

        PENDING.removeAll(matching);
        for (Write write : matching) {
            registerViaEvent(event, write);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerViaEvent(RegisterEvent event, Write write) {
        event.register(
            (RegistryKey) event.getRegistryKey(),
            write.id(),
            () -> write.value()
        );
    }
}

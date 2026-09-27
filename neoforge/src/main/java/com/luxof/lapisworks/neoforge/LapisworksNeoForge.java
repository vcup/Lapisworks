package com.luxof.lapisworks.neoforge;

import com.luxof.lapisworks.Lapisworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * NeoForge entrypoint.
 * <p>
 * On Minecraft 1.20.1 NeoForge is a fork of Forge that keeps the {@code net.minecraftforge.*}
 * package names and the {@code forge} mod id, so this is written against the Forge API; what makes
 * it a NeoForge build rather than a Forge one is the loader artifact it compiles against
 * ({@code net.neoforged:forge}) and the dependency ranges in {@code META-INF/mods.toml}.
 * <p>
 * Shared code reaches the platform through {@code com.luxof.lapisworks.platform.Seams}, which
 * resolves implementations from {@code META-INF/services}; this module ships the NeoForge ones, so
 * there is nothing to install by hand here.
 */
@Mod(Lapisworks.MOD_ID)
public class LapisworksNeoForge {
    public LapisworksNeoForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // Registers the RegisterEvent listener that both runs shared content initialization inside
        // Forge's registry window and flushes the writes the seam captured. See NeoForgeRegistry for
        // why the ordering has to be this way.
        NeoForgeRegistry.init(modBus);
        NeoForgeCapabilities.init(modBus, forgeBus);

        // Client pieces that need Forge's registration windows have to be queued from here: the
        // windows close before FMLClientSetupEvent runs.
        //
        // The distribution is checked *before* naming the client classes: they reference client-only
        // Minecraft types (ParticleEngine and friends), and merely loading them on a dedicated server
        // throws "Attempted to load class ... for invalid dist DEDICATED_SERVER" during mod
        // construction. Reading FMLEnvironment.dist directly avoids that.
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            NeoForgeClientSetup.initEarly(modBus);
            NeoForgeClientSetup.register(modBus);
        }

        // The mod's own network channel is registered lazily by NeoForgeNetworking.init(), which runs
        // the first time shared code sends or receives on any channel.

        // Hex Casting creates its own registries from NewRegistryEvent, which fires after mod
        // construction, so the half of initialization that reads them waits for common setup.
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(Lapisworks::initLate));
    }
}

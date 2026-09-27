package com.luxof.lapisworks.neoforge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.luxof.lapisworks.client.LapisworksClient;
import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Wires the shared client initializer into NeoForge.
 * <p>
 * The work is split across three points because Forge's client registries open and close at
 * different times. The measured order is:
 * <pre>
 * mod constructor
 * RegisterParticleProvidersEvent
 * RegisterKeyMappingsEvent
 * RegisterClientReloadListenersEvent
 * FMLCommonSetupEvent
 * FMLClientSetupEvent
 * EntityRenderersEvent.AddLayers
 * </pre>
 * So:
 * <ul>
 *   <li>{@code initClientEarly} (particle factories, key binding) runs from the mod constructor --
 *       the only point before those two events, which is what produced "Something is attempting to
 *       register particle providers at a later point than intended!" and "Key mapping ... registered
 *       after event" when they were registered from client setup;</li>
 *   <li>{@code initClientRenderers} (accessory renderers, the collar's item renderer) runs from
 *       {@code RegisterClientReloadListenersEvent}: after the item registry exists (naming an item
 *       forces {@code ModItems}' static initializer, which constructs Items and would otherwise
 *       throw "Registry is already frozen") but still before Curios snapshots its renderer registry
 *       at {@code AddLayers} -- registering from client setup instead left the worn collar and
 *       necklaces undrawn; and</li>
 *   <li>everything else runs from {@code FMLClientSetupEvent}.</li>
 * </ul>
 * This class is only touched when a client is starting, so a dedicated server never loads it (nor the
 * client-only classes shared code reaches through it).
 * <p>
 * It also carries the two client hooks that reach shared code through the event seam but cannot be
 * subscribed on Forge's main bus: item colours (a mod-bus registration event) and the client
 * join/quit pair (Forge has no equivalent on the main bus, so it is driven from the client tick
 * instead, which is what {@link #onClientPlayerJoin} arranges).
 */
public final class NeoForgeClientSetup {
    private NeoForgeClientSetup() {}

    private static final List<PlatformEvents.ItemColorProvider> PENDING_COLORS = new ArrayList<>();
    private static final List<Item> PENDING_COLOR_ITEMS = new ArrayList<>();

    /** Called from the mod constructor: registers the drain listeners and queues the early work. */
    public static void initEarly(IEventBus modBus) {
        NeoForgeClientRegistrations.init(modBus);
        // Item colours are a mod-bus registration event, so they cannot be subscribed from the
        // events seam (which only has the forge bus). Queued here and drained by the listener.
        modBus.addListener((RegisterColorHandlersEvent.Item event) -> {
            for (int i = 0; i < PENDING_COLORS.size(); i++) {
                PlatformEvents.ItemColorProvider provider = PENDING_COLORS.get(i);
                event.register(
                    (stack, tintIndex) -> provider.getColor(stack, tintIndex),
                    PENDING_COLOR_ITEMS.get(i)
                );
            }
            PENDING_COLORS.clear();
            PENDING_COLOR_ITEMS.clear();
        });
        LapisworksClient.initClientEarly();
    }

    /** Called from the mod constructor; the remaining phases run later, from their own events. */
    public static void register(IEventBus modBus) {
        modBus.addListener(NeoForgeClientSetup::onClientSetup);
        modBus.addListener((RegisterClientReloadListenersEvent event) ->
            LapisworksClient.initClientRenderers());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        LapisworksClient.initClient();
    }

    // --- hooks the events seam forwards here ---------------------------------------------------

    static void registerItemColors(PlatformEvents.ItemColorProvider provider, Item[] items) {
        // One provider may cover several items; RegisterColorHandlersEvent.Item#register takes them
        // together, so they are queued as a unit and replayed per registration call.
        for (Item item : items) {
            PENDING_COLORS.add(provider);
            PENDING_COLOR_ITEMS.add(item);
        }
    }

    static void onClientPlayerJoin(Consumer<MinecraftClient> callback) {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) ->
            callback.accept(MinecraftClient.getInstance()));
    }

    static void onClientPlayerQuit(Consumer<MinecraftClient> callback) {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) ->
            callback.accept(MinecraftClient.getInstance()));
    }
}

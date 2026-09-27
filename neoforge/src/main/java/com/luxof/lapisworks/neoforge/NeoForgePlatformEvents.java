package com.luxof.lapisworks.neoforge;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * NeoForge implementation of the events and client hooks, backed by Forge events.
 * <p>
 * NeoForge has no separate event-bus library, so these subscribe directly. Two need translation:
 * <ul>
 *   <li>{@link #onPlayerClone} -- Forge exposes {@code isWasDeath()}, which is the inverse of the
 *       {@code alive} flag the seam documents.</li>
 *   <li>{@link #onClientPlayerJoin} -- Forge's client has no join event on the forge bus, only the
 *       mod-bus {@code EntityRenderersEvent.AddLayers} equivalent, so the shared client initializer's
 *       {@code initClient} handle is used instead (see {@link NeoForgeClientSetup}).</li>
 * </ul>
 * The client hooks that are mod-bus events (item colours) are registered from
 * {@link NeoForgeClientSetup} instead of here, because the forge bus cannot carry them.
 */
public class NeoForgePlatformEvents implements PlatformEvents {

    @Override
    public void onServerTickStart(Consumer<MinecraftServer> callback) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ServerTickEvent.class, event -> {
            if (event.phase != TickEvent.Phase.START) return;
            callback.accept(event.getServer());
        });
    }

    @Override
    public void onServerTickEnd(Consumer<MinecraftServer> callback) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ServerTickEvent.class, event -> {
            if (event.phase != TickEvent.Phase.END) return;
            callback.accept(event.getServer());
        });
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> callback) {
        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> callback.accept(event.getServer()));
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> callback) {
        MinecraftForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> callback.accept(event.getServer()));
    }

    @Override
    public void onPlayerJoin(Consumer<ServerPlayerEntity> callback) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayerEntity player) callback.accept(player);
        });
    }

    @Override
    public void onPlayerClone(PlayerClone callback) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
            if (!(event.getOriginal() instanceof ServerPlayerEntity oldPlayer)) return;
            if (!(event.getEntity() instanceof ServerPlayerEntity newPlayer)) return;
            // Forge reports wasDeath; the seam reports alive. Inverting here keeps the one meaning
            // shared code relies on (true = dimension change, false = death).
            callback.onClone(oldPlayer, newPlayer, !event.isWasDeath());
        });
    }

    @Override
    public void onChunkUnload(BiConsumer<ServerWorld, WorldChunk> callback) {
        MinecraftForge.EVENT_BUS.addListener((ChunkEvent.Unload event) -> {
            // Forge fires this for any level; filter to server worlds so it matches Fabric's
            // ServerChunkEvents.CHUNK_UNLOAD exactly. Forge hands the base Chunk type, so it is
            // narrowed here (a server world only ever holds WorldChunks).
            if (!(event.getLevel() instanceof ServerWorld serverWorld)) return;
            if (!(event.getChunk() instanceof WorldChunk worldChunk)) return;
            callback.accept(serverWorld, worldChunk);
        });
    }

    @Override
    public void onModifyLootTable(ModifyLootTable callback) {
        // Forge has no loot-table modify event: only LootTableLoadEvent, which fires for built-in
        // tables as they load. Datapack-supplied tables are therefore not seen, which is what the
        // seam documents.
        MinecraftForge.EVENT_BUS.addListener((LootTableLoadEvent event) -> {
            callback.modify(event.getName(), new LootTableContext() {
                @Override
                public void addPool(LootPool pool) {
                    net.minecraft.loot.LootTable table = event.getTable();
                    // LootTable.EMPTY is shared, so appending to it would leak across tables.
                    if (table == net.minecraft.loot.LootTable.EMPTY) return;
                    table.addPool(pool);
                }
            }, true);
        });
    }

    @Override
    public void onClientTickEnd(Consumer<MinecraftClient> callback) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, event -> {
            if (event.phase != TickEvent.Phase.END) return;
            callback.accept(MinecraftClient.getInstance());
        });
    }

    @Override
    public void onClientLevelTickEnd(Consumer<MinecraftClient> callback) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.LevelTickEvent.class, event -> {
            if (event.phase != TickEvent.Phase.END) return;
            callback.accept(MinecraftClient.getInstance());
        });
    }

    @Override
    public void onClientPlayerJoin(Consumer<MinecraftClient> callback) {
        // Deferred to NeoForgeClientSetup, which drives it from client setup; Forge has no forge-bus
        // client-join event to subscribe to here.
        NeoForgeClientSetup.onClientPlayerJoin(callback);
    }

    @Override
    public void onClientPlayerQuit(Consumer<MinecraftClient> callback) {
        NeoForgeClientSetup.onClientPlayerQuit(callback);
    }

    @Override
    public void setBlockRenderLayer(Block block, RenderLayer layer) {
        // Forge patches RenderLayers with setRenderLayer; plain Fabric has no equivalent and needs
        // Fabric API's BlockRenderLayerMap, which is what the Fabric implementation uses.
        RenderLayers.setRenderLayer(block, layer);
    }

    @Override
    public void registerItemColors(ItemColorProvider provider, Item... items) {
        NeoForgeClientSetup.registerItemColors(provider, items);
    }

    @Override
    public void onAfterTranslucentWorldRender(BiConsumer<MatrixStack, Float> callback) {
        NeoForgeRenderHooks.onAfterTranslucent(callback);
    }

    @Override
    public void registerItemRenderer(Item item, ItemRenderer renderer) {
        // NeoForge has no builtin-item-renderer registry and no event for one on 1.20.1: an item
        // whose model is builtin/entity must supply its renderer from Item#initializeClient, which
        // Forge calls during the item's construction. Collar overrides that through a mixin, so the
        // renderer is looked up when the item is drawn rather than when it is registered.
        NeoForgeItemRendererExtensions.register(item, renderer);
    }
}

package com.luxof.lapisworks.fabric;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.luxof.lapisworks.platform.PlatformEvents;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Fabric implementation of the events and client hooks, backed by the Fabric API.
 * <p>
 * Fabric API is already a hard dependency of this mod, so these are direct forwards. The only one
 * needing translation is {@link #onPlayerClone}: Fabric passes vanilla's {@code alive} flag straight
 * through, which is the meaning the seam documents.
 */
public class FabricPlatformEvents implements PlatformEvents {

    @Override
    public void onServerTickStart(Consumer<MinecraftServer> callback) {
        ServerTickEvents.START_SERVER_TICK.register(callback::accept);
    }

    @Override
    public void onServerTickEnd(Consumer<MinecraftServer> callback) {
        ServerTickEvents.END_SERVER_TICK.register(callback::accept);
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> callback) {
        ServerLifecycleEvents.SERVER_STARTED.register(callback::accept);
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> callback) {
        ServerLifecycleEvents.SERVER_STOPPING.register(callback::accept);
    }

    @Override
    public void onPlayerJoin(Consumer<ServerPlayerEntity> callback) {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> callback.accept(handler.getPlayer()));
    }

    @Override
    public void onPlayerClone(PlayerClone callback) {
        // Fabric's third argument is vanilla's `alive`: true when the old player is still alive (a
        // dimension change), false on death. That already matches the seam's contract.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
            callback.onClone(oldPlayer, newPlayer, alive));
    }

    @Override
    public void onChunkUnload(BiConsumer<ServerWorld, WorldChunk> callback) {
        ServerChunkEvents.CHUNK_UNLOAD.register(callback::accept);
    }

    @Override
    public void onModifyLootTable(ModifyLootTable callback) {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
            callback.modify(id, new LootTableContext() {
                @Override public void addPool(LootPool pool) { tableBuilder.pool(pool); }
            }, source.isBuiltin()));
    }

    @Override
    public void onClientTickEnd(Consumer<MinecraftClient> callback) {
        ClientTickEvents.END_CLIENT_TICK.register(callback::accept);
    }

    @Override
    public void onClientLevelTickEnd(Consumer<MinecraftClient> callback) {
        ClientTickEvents.END_WORLD_TICK.register(world -> callback.accept(MinecraftClient.getInstance()));
    }

    @Override
    public void onClientPlayerJoin(Consumer<MinecraftClient> callback) {
        // Matches what the original registered: the handler reads client.player itself.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> callback.accept(client));
    }

    @Override
    public void onClientPlayerQuit(Consumer<MinecraftClient> callback) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> callback.accept(client));
    }

    @Override
    public void setBlockRenderLayer(Block block, RenderLayer layer) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
    }

    @Override
    public void registerItemColors(ItemColorProvider provider, Item... items) {
        ColorProviderRegistry.ITEM.register(
            (stack, tintIndex) -> provider.getColor(stack, tintIndex),
            items
        );
    }

    @Override
    public void onAfterTranslucentWorldRender(BiConsumer<MatrixStack, Float> callback) {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> callback.accept(ctx.matrixStack(), ctx.tickDelta()));
    }

    @Override
    public void registerItemRenderer(Item item, ItemRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(
            item,
            (stack, mode, matrices, vertexConsumers, light, overlay) ->
                renderer.render(stack, mode, matrices, vertexConsumers, light, overlay)
        );
    }
}

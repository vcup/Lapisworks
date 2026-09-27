package com.luxof.lapisworks.platform;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

/**
 * The seam over loader events and the client hooks the two loaders spell differently.
 * <p>
 * Each platform registers against its own loader's events: Fabric API on Fabric, Forge events on
 * NeoForge. Nothing here pulls in a third-party event library, so no extra mod is needed at runtime.
 * <p>
 * Where the loaders disagree on meaning, the platform implementation normalises it, so shared code
 * sees one contract. {@link #onPlayerClone} is the main example.
 */
public interface PlatformEvents {
    PlatformEvents INSTANCE = Seams.load(PlatformEvents.class);

    // --- server ------------------------------------------------------------------------

    /** Just before each server tick. */
    void onServerTickStart(Consumer<MinecraftServer> callback);

    /** Just after each server tick. */
    void onServerTickEnd(Consumer<MinecraftServer> callback);

    /** Once the server has started. */
    void onServerStarted(Consumer<MinecraftServer> callback);

    /** As the server begins stopping. */
    void onServerStopping(Consumer<MinecraftServer> callback);

    /** When a player joins. */
    void onPlayerJoin(Consumer<ServerPlayerEntity> callback);

    /**
     * When a player is cloned: on death, or on a dimension change.
     * <p>
     * {@code alive} is {@code true} when the old player is still alive, i.e. a dimension change, and
     * {@code false} on death. The loaders express this differently -- Fabric's clone callback passes
     * vanilla's {@code alive} flag while Forge's passes {@code !wasDeath} -- so each implementation
     * converts to this single meaning.
     */
    void onPlayerClone(PlayerClone callback);

    /** When a chunk leaves a server world, so per-chunk caches can be dropped. */
    void onChunkUnload(BiConsumer<ServerWorld, WorldChunk> callback);

    /**
     * While a loot table is being built, so extra pools can be added.
     * <p>
     * On NeoForge this only fires for built-in tables, because Forge's hook is
     * {@code LootTableLoadEvent}; a datapack-supplied table will not be seen there.
     */
    void onModifyLootTable(ModifyLootTable callback);

    // --- client ------------------------------------------------------------------------

    /** After each client tick. */
    void onClientTickEnd(Consumer<MinecraftClient> callback);

    /** After each client-world tick. */
    void onClientLevelTickEnd(Consumer<MinecraftClient> callback);

    /** When the client player joins a world. */
    void onClientPlayerJoin(Consumer<MinecraftClient> callback);

    /** When the client player leaves a world. */
    void onClientPlayerQuit(Consumer<MinecraftClient> callback);

    /** Draws a block in the given render layer. */
    void setBlockRenderLayer(Block block, RenderLayer layer);

    /** Registers a per-stack tint provider. */
    void registerItemColors(ItemColorProvider provider, Item... items);

    /**
     * After translucent world geometry is drawn, for overlays that live in world space.
     * <p>
     * Replaces Fabric's {@code WorldRenderEvents.AFTER_TRANSLUCENT}. The callback gets the matrix
     * stack and tick delta directly, since the two loaders hand those over in different shapes.
     */
    void onAfterTranslucentWorldRender(BiConsumer<MatrixStack, Float> callback);

    /**
     * Registers a custom renderer for an item, used when it is drawn in the world or in a GUI.
     * <p>
     * Replaces Fabric's {@code BuiltinItemRendererRegistry}. The callback gets the stack, the
     * transform mode, the matrix stack, the vertex consumers, the light and the overlay.
     */
    void registerItemRenderer(Item item, ItemRenderer renderer);

    @FunctionalInterface
    interface PlayerClone {
        void onClone(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
    }

    @FunctionalInterface
    interface ModifyLootTable {
        void modify(Identifier id, LootTableContext context, boolean builtin);
    }

    /** Appends pools to the table being built. */
    interface LootTableContext {
        void addPool(LootPool pool);
    }

    @FunctionalInterface
    interface ItemColorProvider {
        int getColor(ItemStack stack, int tintIndex);
    }

    @FunctionalInterface
    interface ItemRenderer {
        void render(
            ItemStack stack,
            net.minecraft.client.render.model.json.ModelTransformationMode mode,
            MatrixStack matrices,
            net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
        );
    }
}

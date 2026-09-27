package com.luxof.lapisworks.client;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.blocks.bers.*;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkCenterRenderer;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkPart;
import com.luxof.lapisworks.client.trinkets.*;
import com.luxof.lapisworks.init.*;
import com.luxof.lapisworks.interop.hexcessible.LapiscessibleInterface;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.AcceleratableEntity;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.platform.Accessories;
import com.luxof.lapisworks.collar.CollarItemRenderer;
import com.luxof.lapisworks.platform.Networking;
import com.luxof.lapisworks.platform.PlatformEvents;

import static com.luxof.lapisworks.Lapisworks.FULL_HEXICAL_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HEXAL_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HEXCESSIBLE_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HIEROPHANTICS_INTEROP;
import static com.luxof.lapisworks.Lapisworks.dim;
import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.log;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.LapisworksIDs.APPLY_PULL_FOR_TIME;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_RESULT;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_TS;
import static com.luxof.lapisworks.LapisworksIDs.GIB_DUST;
import static com.luxof.lapisworks.LapisworksIDs.ROBBIES_EXALT_PACKET;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.LapisworksIDs.UNLOCK_SHIT_FOR_HEXCESSIBLE;
import static com.luxof.lapisworks.init.ModItems.AMEL_JAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR_WITH_MODEL;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2_WORN;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE_WORN;
import static com.luxof.lapisworks.init.ModItems.IRON_SWORD;
import static com.luxof.lapisworks.init.ModItems.TOTEM_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.TOTEM_NECKLACE_WORN;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;

/**
 * The shared, loader-agnostic client initializer.
 * <p>
 * This is deliberately a plain class with a static {@code initClient()} rather than an
 * implementation of any loader's client-entrypoint interface: the same class is compiled into the
 * Fabric and the NeoForge jar, and {@code net.fabricmc.api.ClientModInitializer} does not exist on
 * the NeoForge classpath. Each platform module has its own client entrypoint that calls
 * {@link #initClient()}.
 */
public class LapisworksClient {
    public Vec3d bufferSentinelPos = null;
    public Double bufferSentinelAmbit = null;
    public boolean playerHasJoined = false;

    /** The instance whose join/sentinel state the packet handlers below mutate. */
    private static final LapisworksClient INSTANCE = new LapisworksClient();

    public LapisworksClient() {}

    public static void registerMPPs() {
        ModelPredicateProviderRegistry.register(
            IRON_SWORD,
            // first person doesn't work and i don't fucking know why
            id("blocking"),
            (stack, world, entity, seed) -> {
                return entity != null
                    && entity.isUsingItem()
                    && entity.getActiveItem() == stack
                        ? 1.0F : 0.0F;
            }
        );
        if (Lapisworks.HEXTENDED_INTEROP) {
            ModelPredicateProviderRegistry.register(
                com.luxof.lapisworks.interop.hextended.Lapixtended.AMEL_ORB,
                id("amel_orb_is_filled"),
                (stack, world, entity, seed) -> {
                    AmelOrb orb = (AmelOrb)stack.getItem();
                    return orb.getPlaceInAmbit(stack) == null ? 0.0F : 1.0F;
                }
            );
        }
    }

    public static void overlayWorld(MatrixStack ms, float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d sentinel = ((EnchSentInterface)player).getEnchantedSentinel();
            if (sentinel != null) { THEGRANDROTATER.renderEnchantedSentinel(sentinel, ms, tickDelta); }
        }
    }

    public static void initInterop() {
        if (FULL_HEXICAL_INTEROP) {
            com.luxof.lapisworks.interop.hexical.FullLapixicalClient.initTheFullLapixicalClient();
        }
        if (HEXAL_INTEROP) {
            com.luxof.lapisworks.interop.hexal.LapisalClient.beCoolOnTheClient();
        }
        if (HIEROPHANTICS_INTEROP) {
            com.luxof.lapisworks.interop.hierophantics.LapisphanticsClient.doMyShitTwin();
        }
    }

    /**
     * Client registrations that must happen inside the loader's own registration window.
     * <p>
     * Particle factories and key bindings are only accepted while the loader has those registries
     * open, and both loaders close them before ordinary client setup runs. Measured NeoForge order
     * is: mod constructor, {@code RegisterParticleProvidersEvent}, {@code RegisterKeyMappingsEvent},
     * {@code FMLClientSetupEvent} -- so registering them from client setup is too late and they are
     * dropped, with "Something is attempting to register particle providers at a later point than
     * intended!" and "Key mapping ... registered after event". Each platform entrypoint calls this as
     * early as its loader permits.
     */
    public static void initClientEarly() {
        LapisParticles.clientTicklesPaw();
        KeyEvents.registerKeyBinding();
    }

    /**
     * Accessory renderers.
     * <p>
     * Split out from both other phases because of a two-sided timing squeeze:
     * <ul>
     *   <li>they must run <em>before</em> Curios snapshots its renderer registry, which happens in
     *       {@code EntityRenderersEvent.AddLayers} -- earlier than {@code FMLClientSetupEvent}, so
     *       registering from {@link #initClient()} left them pending and the worn collar never drew;
     *       and</li>
     *   <li>they must run <em>after</em> the item registry is open, because naming
     *       {@code ModItems.FOCUS_NECKLACE} forces {@code ModItems}' static initializer, which
     *       constructs the items and throws "Registry is already frozen" if that happens during mod
     *       construction.</li>
     * </ul>
     * {@code RegisterClientReloadListenersEvent} sits between the two on NeoForge, and Fabric accepts
     * it at any point during client init.
     */
    public static void initClientRenderers() {
        Accessories.INSTANCE.registerRenderer(AMEL_JAR, new JarTrinketRenderer());
        Accessories.INSTANCE.registerRenderer(
            FOCUS_NECKLACE,
            new NecklaceTrinketRenderer(new ItemStack(FOCUS_NECKLACE_WORN))
        );
        Accessories.INSTANCE.registerRenderer(
            FOCUS_NECKLACE2,
            new NecklaceTrinketRenderer(new ItemStack(FOCUS_NECKLACE2_WORN))
        );
        Accessories.INSTANCE.registerRenderer(
            TOTEM_NECKLACE,
            new NecklaceTrinketRenderer(new ItemStack(TOTEM_NECKLACE_WORN))
        );
        Accessories.INSTANCE.registerRenderer(
            COLLAR,
            new CollarTrinketRenderer()
        );

        // The collar's item model is builtin/entity, so it needs a custom item renderer. Same
        // timing constraint: the platform hook has to know before anything is drawn.
        PlatformEvents.INSTANCE.registerItemRenderer(COLLAR, new CollarItemRenderer());
    }

    public static void initClient() {
        // the eternal fucking grammar battle with this simple Markiplier ass log will drive me insane
        // thankful i won't have to edit this file anymore
        // ^^^^ what was that, chief?
        log("Hello everybody my name is LapisworksClient and today what we are going to do is: scrying lens tooltips, make blocks transparent, keybinds, networking, Model Predicate Providers, make blocks translucent, spin 4D hypercubes for the FUNNY, Block Entity Renderers (shudder), render trinkets, make particles, and client-side rendering!");
        log("Does NONE of that sound fun? Well, that's because it isn't. So let's get started, shall we?");

        ModScreens.registerOnClient();
        Dowser.registerMyCuteness();

        initInterop();

        BlockEntityRendererFactories.register(
            ModBlocks.ENCH_BREWER_ENTITY_TYPE,
            EnchBrewerRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.CHALK_ENTITY_TYPE,
            ChalkRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.CHALK_WITH_PATTERN_ENTITY_TYPE,
            ChalkWithPatternRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.BIG_CHALK_CENTER_ENTITY_TYPE,
            BigChalkCenterRenderer::new
        );

        ScryingOverlaysClient.addOverlays();

        // World-space overlay drawing has no shared API, so it goes through the platform seam and
        // each loader supplies its own world-render event.
        PlatformEvents.INSTANCE.onAfterTranslucentWorldRender(
            (matrices, tickDelta) -> overlayWorld(matrices, tickDelta)
        );

        PlatformEvents.INSTANCE.setBlockRenderLayer(ModBlocks.MIND_BLOCK, RenderLayer.getTranslucent());
        PlatformEvents.INSTANCE.setBlockRenderLayer(ModBlocks.TUNEABLE_AMETHYST, RenderLayer.getCutout());

        // The key binding itself is registered in initClientEarly(), before the loader's
        // key-mapping window closes; only the tick listener is wired up here.
        PlatformEvents.INSTANCE.onClientTickEnd(KeyEvents::endClientTick);

        // These are client-side receivers: the server sends them to us. The seam names the side that
        // RUNS the handler, so there is no sender/receiver confusion to get backwards.
        //
        // Threading matches Fabric's ClientPlayNetworking.registerGlobalReceiver, which also ran these
        // on the network thread, so the direct reads of MinecraftClient.getInstance().player and the
        // buffer reads below behave exactly as before. The one place the original hopped to the main
        // thread, client.execute(...), is still a hop: context.queue(...).
        Networking.INSTANCE.registerClientReceiver( SEND_SENT,
            (buf, context) -> {
                boolean banishSentinel = buf.readBoolean();
                if (banishSentinel) {
                    if (!INSTANCE.playerHasJoined) {
                        INSTANCE.bufferSentinelPos = null;
                        INSTANCE.bufferSentinelAmbit = null;
                    } else
                        ((EnchSentInterface)MinecraftClient.getInstance().player).setEnchantedSentinel(null, null);
                    return;
                }
                Vec3d newPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                Double newAmbit = buf.readDouble();
                if (!INSTANCE.playerHasJoined) {
                    INSTANCE.bufferSentinelPos = newPos;
                    INSTANCE.bufferSentinelAmbit = newAmbit;
                } else
                    ((EnchSentInterface)MinecraftClient.getInstance().player).setEnchantedSentinel(newPos, newAmbit);
            }
        );

        Networking.INSTANCE.registerClientReceiver( SEND_PWSHAPE_PATS,
            (buf, context) -> {
                NbtCompound nbt = buf.readNbt();
                for (String flag : chosenFlags.keySet()) {
                    chosenFlags.put(flag, nbt.getInt(flag));
                    PatchouliAPI.get().setConfigFlag(
                        flag + String.valueOf(nbt.getInt(flag)),
                        true
                    );
                }

                if (HEXCESSIBLE_INTEROP)
                    context.queue(LapiscessibleInterface::recalibratePWShapeUnlocksInHexcessible);
            }
        );

        Networking.INSTANCE.registerClientReceiver( ROBBIES_EXALT_PACKET,
            (buf, context) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                ROBBIES_EXALT_VARIANT_CLIENT = client.player != null
                    && client.player.getUuidAsString().equals(
                        // Fel will have estrogen, it has to be this way
                        "b14b3e5c-7405-48ac-84dc-5c0925de44f2"
                    )
                    ? 0
                    : buf.readInt();
            }
        );

        Networking.INSTANCE.registerClientReceiver( DOWSE_TS,
            (buf, context) -> {
                PacketByteBuf sendBuf = new PacketByteBuf(Unpooled.buffer());
                sendBuf.writeString(buf.readString());

                Block find = Registries.BLOCK.get(buf.readIdentifier());
                Pair<BlockPos, Double> result = Dowser.dowse(find);

                sendBuf.writeBoolean(result != null);
                if (result != null) {
                    sendBuf.writeBlockPos(result.getLeft());
                    sendBuf.writeDouble(result.getRight());
                }
                Networking.INSTANCE.sendToServer(DOWSE_RESULT, sendBuf);
            }
        );

        Networking.INSTANCE.registerClientReceiver( GIB_DUST,
            (buf, context) -> {
                BlockPos pos = buf.readBlockPos();
                Direction attachedTo = Direction.byName(buf.readString());

                // WHY DOES THIS KEEP FUCKING HAPPENING
                World world = MinecraftClient.getInstance().player.getWorld();
                try {
                    BigChalkPart.spawnDust(world, pos, attachedTo);
                } catch (Exception e) {
                    err("Error while spawning dust on big chalk break:");
                    e.printStackTrace();
                }
            }
        );

        Networking.INSTANCE.registerClientReceiver( APPLY_PULL_FOR_TIME,
            (buf, context) -> {
                Vec3d pull = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                PlayerEntity player = MinecraftClient.getInstance().player;
                player.addVelocity(pull);
                ((AcceleratableEntity)player).applyLingeringAccel(
                    pull,
                    buf.readInt() - 1
                );
            }
        );

        Networking.INSTANCE.registerClientReceiver( UNLOCK_SHIT_FOR_HEXCESSIBLE,
            (buf, context) -> {
                if (HEXCESSIBLE_INTEROP)
                    LapiscessibleInterface.unlockPWShapeInHexcessibleByAdvancement(buf.readIdentifier());
            }
        );

        PlatformEvents.INSTANCE.onClientPlayerJoin(client -> {
            INSTANCE.playerHasJoined = true;
            // The seam hands over the client, matching what the original handler received; the player
            // is read from it here so the cast target is the same entity as before.
            ((EnchSentInterface)client.player).setEnchantedSentinel(
                INSTANCE.bufferSentinelPos,
                INSTANCE.bufferSentinelAmbit
            );
        });

        PlatformEvents.INSTANCE.onClientPlayerQuit(player -> {
            INSTANCE.playerHasJoined = false;
            INSTANCE.bufferSentinelPos = null;
            INSTANCE.bufferSentinelAmbit = null;
            nullConfigFlags();
        });

        PlatformEvents.INSTANCE.registerItemColors(
            (stack, tint) -> switch (tint) {
                case 0 -> COLLAR.getColor(stack);
                case 1 -> dim(COLLAR.getColor(stack));
                default -> 0x808080;
            },
            COLLAR_WITH_MODEL
        );
    }

    public static int ROBBIES_EXALT_VARIANT_CLIENT = 0;
}

package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.PersistentStateRituals;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.pickUsingSeed;
import static com.luxof.lapisworks.Lapisworks.pickConfigFlags;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.LapisworksIDs.GEODE_DOWSER_REQUEST;
import static com.luxof.lapisworks.LapisworksIDs.ROBBIES_EXALT_PACKET;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.LapisworksIDs.SET_PATTERNS_ON_CHALK;
import static com.luxof.lapisworks.init.ModItems.GEODE_DOWSER;
import static com.luxof.lapisworks.init.ThemConfigFlags.turnChosenIntoNbt;

import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import kotlin.Pair;

import com.luxof.lapisworks.platform.Networking;
import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LapisworksServer {
    public static void onJoinEnchSentStuff(
        ServerPlayerEntity player
    ) {
        Vec3d sentPos = ((EnchSentInterface)player).getEnchantedSentinel();
        Double sentAmbit = ((EnchSentInterface)player).getEnchantedSentinelAmbit();
        if (sentPos == null) { return; }
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(false);
        buf.writeDouble(sentPos.x);
        buf.writeDouble(sentPos.y);
        buf.writeDouble(sentPos.z);
        buf.writeDouble(sentAmbit);
        Networking.INSTANCE.sendToPlayer(player, SEND_SENT, buf);
    }

    public static void onJoinPWShapeStuff(
        ServerPlayerEntity player
    ) {
        PacketByteBuf patsBuf = new PacketByteBuf(Unpooled.buffer());
        // hell naw i'm not dealing with the two extra args to writeMap() (i dunno wtf those are)
        patsBuf.writeNbt(turnChosenIntoNbt());
        Networking.INSTANCE.sendToPlayer(player, SEND_PWSHAPE_PATS, patsBuf);
    }

    public static void onJoinRobbiesStuff(
        ServerPlayerEntity player
    ) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(ROBBIES_EXALT_VARIANT);
        Networking.INSTANCE.sendToPlayer(player, ROBBIES_EXALT_PACKET, buf);
    }

    public static void handleCastingGridPacket(
        ServerPlayerEntity player,
        PacketByteBuf buf
    ) {
        boolean clearGrid = buf.readBoolean();
        if (clearGrid) {
            IXplatAbstractions.INSTANCE.clearCastingData(player);
            // i don't know why, it's just in the itemstaff code
            MsgClearSpiralPatternsS2C packet = new MsgClearSpiralPatternsS2C(player.getUuid());
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(player, packet);
        }

        CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, Hand.MAIN_HAND);
        List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player);
        Pair<List<NbtCompound>, NbtCompound> descs = vm.generateDescs();

        IXplatAbstractions.INSTANCE.sendPacketToPlayer(
            player,
            new MsgOpenSpellGuiS2C(
                Hand.MAIN_HAND,
                patterns,
                descs.getFirst(),
                descs.getSecond(),
                0 // it says "todo fix" in the hex casting github 'round here, wonder why
            )
        );
    }

    /** public so anyone can easily fw it */
    public static Map<String, BiConsumer<ServerPlayerEntity, PacketByteBuf>> dowseResultTakers = new HashMap<>();
    private static int configRefreshCountdown = 100;

    public static void lockIn() {
        dowseResultTakers.put(GEODE_DOWSER_REQUEST, GEODE_DOWSER::serverHandleDowseResult);

        PlatformEvents.INSTANCE.onServerTickStart((server) -> {
            configRefreshCountdown--;
            if (configRefreshCountdown < 0) {
                LapisConfig.renewCurrentConfig();
                configRefreshCountdown++;
            }
        });
        Networking.INSTANCE.registerServerReceiver( LapisworksIDs.OPEN_CASTING_GRID,
            (buf, context) -> handleCastingGridPacket(
                (ServerPlayerEntity)context.getPlayer(),
                buf
            )
        );
        Networking.INSTANCE.registerServerReceiver( LapisworksIDs.DOWSE_RESULT,
            (buf, context) -> {
                BiConsumer<ServerPlayerEntity, PacketByteBuf> dowseResultTaker = dowseResultTakers.get(buf.readString());
                if (dowseResultTaker == null) return;
                dowseResultTaker.accept((ServerPlayerEntity)context.getPlayer(), buf);
            }
        );
        Networking.INSTANCE.registerServerReceiver( SET_PATTERNS_ON_CHALK,
            (buf, context) -> {
                ServerPlayerEntity player = (ServerPlayerEntity)context.getPlayer();
                BlockPos position = buf.readBlockPos();

                int sentPatterns = buf.readInt();
                List<HexPattern> newPatterns = new ArrayList<>();
                for (int i = 0; i < sentPatterns; i++) {
                    newPatterns.add(HexPattern.fromNBT(buf.readNbt()));
                }

                ServerWorld sw = (ServerWorld)player.getWorld();

                context.queue(() -> {

                    ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)sw.getBlockEntity(position);

                    chalk.pats = newPatterns;

                    chalk.markDirty();
                    sw.updateListeners(
                        position,
                        chalk.getCachedState(),
                        chalk.getCachedState(),
                        Block.NOTIFY_LISTENERS
                    );

                });
            }
        );

        PlatformEvents.INSTANCE.onPlayerJoin((player) -> {
            onJoinEnchSentStuff(player);
            onJoinPWShapeStuff(player);
            onJoinRobbiesStuff(player);
        });
        PlatformEvents.INSTANCE.onServerStarted(server -> {
            pickConfigFlags(pickUsingSeed(server.getOverworld().getSeed()));
            ROBBIES_EXALT_VARIANT = new Random(server.getOverworld().getSeed()).nextInt(2);
        });
        PlatformEvents.INSTANCE.onServerStopping(server -> nullConfigFlags());

        PlatformEvents.INSTANCE.onServerTickEnd(server -> {
            server.getWorlds().forEach(
                world -> PersistentStateRituals.getState(world).tick(world)
            );
        });
    }
    
    public static int ROBBIES_EXALT_VARIANT = 0;
}

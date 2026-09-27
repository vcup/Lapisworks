package com.luxof.lapisworks.neoforge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.platform.Networking;

import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * NeoForge implementation of the networking seam, backed by Forge's {@code SimpleChannel}.
 * <p>
 * Forge's channel API is class-based: each message type gets one integer id and a serializer. The
 * seam is identifier-based and server code sends many different channels, so one envelope message
 * carries the channel id plus the payload, and a single registered id routes to whichever receiver
 * was registered for that channel. The direction comes from the network context, so one id serves
 * both server-bound and client-bound traffic.
 */
public class NeoForgeNetworking implements Networking {

    private static final String PROTOCOL = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new Identifier(Lapisworks.MOD_ID, "main"),
        () -> PROTOCOL,
        PROTOCOL::equals,
        PROTOCOL::equals
    );

    /** Registrations keyed by channel, split by which side runs the handler. */
    private static final Map<Identifier, ServerReceiver> SERVER_RECEIVERS = new HashMap<>();
    private static final Map<Identifier, ClientReceiver> CLIENT_RECEIVERS = new HashMap<>();

    private static boolean registered = false;

    /** The wire format: which channel this is, and the payload that channel expects. */
    public record Envelope(Identifier channel, byte[] payload) {
        public static void write(Envelope msg, PacketByteBuf buf) {
            buf.writeIdentifier(msg.channel());
            buf.writeBytes(msg.payload());
        }

        public static Envelope read(PacketByteBuf buf) {
            Identifier channel = buf.readIdentifier();
            byte[] payload = new byte[buf.readableBytes()];
            buf.readBytes(payload);
            return new Envelope(channel, payload);
        }
    }

    /** Registers the single envelope id. Called during mod construction. */
    public static void init() {
        if (registered) return;
        registered = true;
        CHANNEL.registerMessage(
            0,
            Envelope.class,
            Envelope::write,
            Envelope::read,
            NeoForgeNetworking::handle
        );
    }

    private static PacketByteBuf wrap(byte[] payload) {
        return new PacketByteBuf(Unpooled.wrappedBuffer(payload));
    }

    private static void handle(Envelope msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        NetworkDirection dir = ctx.getDirection();
        ctx.setPacketHandled(true);

        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerReceiver receiver = SERVER_RECEIVERS.get(msg.channel());
            if (receiver == null) return;
            ServerPlayerEntity player = ctx.getSender();
            if (player == null) return;
            ctx.enqueueWork(() -> receiver.receive(wrap(msg.payload()), new ServerContext() {
                @Override public ServerPlayerEntity getPlayer() { return player; }
                @Override public void queue(Runnable task) { ctx.enqueueWork(task); }
            }));
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            ClientReceiver receiver = CLIENT_RECEIVERS.get(msg.channel());
            if (receiver == null) return;
            ctx.enqueueWork(() -> receiver.receive(wrap(msg.payload()), new ClientContext() {
                @Override public void queue(Runnable task) { ctx.enqueueWork(task); }
            }));
        }
    }

    @Override
    public void registerServerReceiver(Identifier channel, ServerReceiver handler) {
        init();
        SERVER_RECEIVERS.put(channel, handler);
    }

    @Override
    public void registerClientReceiver(Identifier channel, ClientReceiver handler) {
        init();
        CLIENT_RECEIVERS.put(channel, handler);
    }

    @Override
    public void sendToPlayer(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
        init();
        byte[] payload = new byte[buf.readableBytes()];
        buf.readBytes(payload);
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new Envelope(channel, payload));
    }

    @Override
    public void sendToServer(Identifier channel, PacketByteBuf buf) {
        init();
        byte[] payload = new byte[buf.readableBytes()];
        buf.readBytes(payload);
        CHANNEL.sendToServer(new Envelope(channel, payload));
    }
}

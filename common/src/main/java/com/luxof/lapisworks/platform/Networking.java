package com.luxof.lapisworks.platform;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * The seam over mod-message networking.
 * <p>
 * Replaces Architectury's {@code NetworkManager}. Each platform talks to its own API: Fabric API on
 * Fabric, Forge's {@code SimpleChannel} on NeoForge.
 * <p>
 * The receiver methods are named after <em>which side runs the handler</em>. Architectury named them
 * after the direction the packet travels, where {@code c2s()} means "handled on the server" -- easy
 * to invert by accident, and a mistake that fails silently because the receiver never fires.
 */
public interface Networking {
    Networking INSTANCE = Seams.load(Networking.class);

    /** Registers a handler that runs on the <b>server</b> for packets a client sent. */
    void registerServerReceiver(Identifier channel, ServerReceiver handler);

    /** Registers a handler that runs on the <b>client</b> for packets the server sent. */
    void registerClientReceiver(Identifier channel, ClientReceiver handler);

    /** Sends a packet from the server to one player. */
    void sendToPlayer(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf);

    /** Sends a packet from the client to the server. */
    void sendToServer(Identifier channel, PacketByteBuf buf);

    @FunctionalInterface
    interface ServerReceiver {
        void receive(PacketByteBuf buf, ServerContext context);
    }

    @FunctionalInterface
    interface ClientReceiver {
        void receive(PacketByteBuf buf, ClientContext context);
    }

    /** Server side of a received packet. */
    interface ServerContext {
        /** The player who sent it. */
        ServerPlayerEntity getPlayer();

        /** Runs {@code task} on the server thread. */
        void queue(Runnable task);
    }

    /** Client side of a received packet. */
    interface ClientContext {
        /** Runs {@code task} on the client thread. */
        void queue(Runnable task);
    }
}

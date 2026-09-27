package com.luxof.lapisworks.fabric;

import com.luxof.lapisworks.platform.Networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Fabric implementation of the networking seam, backed by the Fabric API.
 * <p>
 * Fabric API is a required dependency of this mod already, so there is nothing extra to install.
 */
public class FabricNetworking implements Networking {

    @Override
    public void registerServerReceiver(Identifier channel, ServerReceiver handler) {
        ServerPlayNetworking.registerGlobalReceiver(channel, (server, player, netHandler, buf, sender) ->
            handler.receive(buf, new ServerContext() {
                @Override public ServerPlayerEntity getPlayer() { return player; }
                @Override public void queue(Runnable task) { server.execute(task); }
            })
        );
    }

    @Override
    public void registerClientReceiver(Identifier channel, ClientReceiver handler) {
        ClientPlayNetworking.registerGlobalReceiver(channel, (client, netHandler, buf, sender) ->
            handler.receive(buf, new ClientContext() {
                @Override public void queue(Runnable task) { client.execute(task); }
            })
        );
    }

    @Override
    public void sendToPlayer(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, channel, buf);
    }

    @Override
    public void sendToServer(Identifier channel, PacketByteBuf buf) {
        ClientPlayNetworking.send(channel, buf);
    }
}

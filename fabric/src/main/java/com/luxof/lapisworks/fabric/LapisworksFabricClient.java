package com.luxof.lapisworks.fabric;

import com.luxof.lapisworks.client.LapisworksClient;

import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric client entrypoint; the work lives in the shared client initializer.
 * <p>
 * Fabric's particle-factory, key-binding and accessory-renderer registries all accept calls at any
 * point during client initialization, so the phases run back to back here. The split exists to match
 * NeoForge, where each of them has its own window: particle factories and key bindings must be queued
 * before Forge's registration events, and accessory renderers must land after the item registry
 * exists but before Curios snapshots its renderer registry.
 */
public class LapisworksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LapisworksClient.initClientEarly();
        LapisworksClient.initClientRenderers();
        LapisworksClient.initClient();
    }
}

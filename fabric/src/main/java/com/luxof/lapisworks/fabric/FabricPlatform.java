package com.luxof.lapisworks.fabric;

import java.nio.file.Path;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import com.luxof.lapisworks.platform.LapisworksPlatform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

import net.minecraft.entity.mob.MobEntity;

import org.jetbrains.annotations.Nullable;

/** Fabric implementation of the shared platform seam, backed by FabricLoader. */
public class FabricPlatform implements LapisworksPlatform {

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public @Nullable Integer verDifference(String modid, String targetVersion) {
        try {
            Version currentVer = FabricLoader.getInstance().getModContainer(modid).get()
                .getMetadata().getVersion();
            Version targetVer = Version.parse(targetVersion);
            return currentVer.compareTo(targetVer);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void clearBrainsweep(MobEntity mob) {
        // Fabric keeps this in a Cardinal Components component, which syncs itself to clients.
        HexCardinalComponents.BRAINSWEPT.get(mob).setBrainswept(false);
    }

    @Override
    public boolean isBrainswept(MobEntity mob) {
        return HexCardinalComponents.BRAINSWEPT.get(mob).isBrainswept();
    }

    @Override
    public void registerResourceCondition(String path, java.util.function.Predicate<com.google.gson.JsonObject> test) {
        net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions.register(
            new net.minecraft.util.Identifier("lapisworks", path),
            json -> test.test(json)
        );
    }
}

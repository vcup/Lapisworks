package com.luxof.lapisworks.neoforge;

import java.nio.file.Path;

import com.luxof.lapisworks.platform.LapisworksPlatform;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import org.jetbrains.annotations.Nullable;

/** NeoForge implementation of the shared platform seam. */
public class NeoForgePlatform implements LapisworksPlatform {

    @Override
    public boolean isModLoaded(String modid) {
        // Mixin config plugins run BEFORE ModList is populated, and ModSpecificMCP calls this from
        // shouldApplyMixin, so asking ModList that early throws
        // "Cannot invoke ModList.isLoaded(String) because the return value of ModList.get() is null".
        // FMLLoader's loading mod list exists from the very start of mod discovery, so fall back to
        // it whenever the real ModList is not up yet.
        var modList = ModList.get();
        if (modList != null) return modList.isLoaded(modid);

        var loadingModList = net.minecraftforge.fml.loading.FMLLoader.getLoadingModList();
        return loadingModList != null && loadingModList.getModFileById(modid) != null;
    }

    @Override
    public @Nullable Integer verDifference(String modid, String targetVersion) {
        try {
            IModInfo info = modInfo(modid);
            if (info == null) return null;
            ArtifactVersion current = info.getVersion();
            ArtifactVersion target = new DefaultArtifactVersion(targetVersion);
            return current.compareTo(target);
        } catch (Exception e) {
            return null;
        }
    }

    /** The mod's metadata, from the real ModList or, before it exists, the loading mod list. */
    @Nullable
    private static IModInfo modInfo(String modid) {
        var modList = ModList.get();
        if (modList != null) {
            return modList.getModContainerById(modid).orElseThrow().getModInfo();
        }
        var loadingModList = net.minecraftforge.fml.loading.FMLLoader.getLoadingModList();
        if (loadingModList == null) return null;
        var file = loadingModList.getModFileById(modid);
        if (file == null || file.getMods().isEmpty()) return null;
        return file.getMods().get(0);
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public void clearBrainsweep(MobEntity mob) {
        // On Forge/NeoForge Hex Casting keeps this flag in the mob's persistent data, and clearing
        // it does not need to be synced by hand: nothing on the client acts on the flag except when
        // Hex sends its own ack packet, which only ever sets it.
        //
        // Note: Hex's ForgeXplatImpl has a public clear-like entry point only via
        // setBrainsweepAddlData (which SETS the flag), so the persistent-data key is what we touch.
        mob.getPersistentData().remove("hexcasting:brainswept");
    }

    @Override
    public boolean isBrainswept(MobEntity mob) {
        // Same storage as clearBrainsweep: a persistent-data flag on the mob.
        return mob.getPersistentData().getBoolean("hexcasting:brainswept");
    }

    @Override
    public void registerResourceCondition(String path, java.util.function.Predicate<com.google.gson.JsonObject> test) {
        Identifier id = new Identifier("lapisworks", path);
        // Forge has no equivalent of Fabric's ResourceConditions.register(id, predicate): a
        // condition is a type with its own serializer, so the predicate is wrapped in one here.
        CraftingHelper.register(new IConditionSerializer<ForgeJsonCondition>() {
            @Override
            public void write(com.google.gson.JsonObject json, ForgeJsonCondition value) {
                // The body is authored by hand in the data files, so there is nothing to emit;
                // Forge only needs this when it re-serializes a condition, which we never do.
            }

            @Override
            public ForgeJsonCondition read(com.google.gson.JsonObject json) {
                return new ForgeJsonCondition(id, json, test);
            }

            @Override
            public Identifier getID() {
                return id;
            }
        });
    }
}

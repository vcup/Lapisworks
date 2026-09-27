package com.luxof.lapisworks.platform;

import java.nio.file.Path;

import net.minecraft.entity.mob.MobEntity;

import org.jetbrains.annotations.Nullable;

/**
 * Loader/environment services. Fabric implements this over FabricLoader, NeoForge over
 * FMLPaths/FMLLoader. Resolved through {@link Seams}, so there is no initialization ordering
 * requirement beyond the platform entrypoint existing on the classpath.
 */
public interface LapisworksPlatform {
    LapisworksPlatform INSTANCE = Seams.load(LapisworksPlatform.class);

    /** Whether a mod id is present in this instance. */
    boolean isModLoaded(String modid);

    /**
     * {@code current - target} as a comparable integer, or {@code null} when the mod is absent or
     * the version cannot be parsed.
     */
    @Nullable
    Integer verDifference(String modid, String targetVersion);

    /** The instance's {@code config} directory. */
    Path getConfigDir();

    /** The instance's game directory. */
    Path getGameDir();

    /** True when running inside a development environment. */
    boolean isDevelopmentEnvironment();

    /** True when this is the physical client. */
    boolean isClient();

    /**
     * Clears Hex Casting's "brainswept" flag on a mob, syncing the change to clients.
     * <p>
     * Hex Casting's cross-platform API can only <em>set</em> this flag (and read it); clearing it
     * needs the loader's own storage, which is a Cardinal Components component on Fabric and
     * persistent entity data on NeoForge. Hence a seam method.
     */
    void clearBrainsweep(MobEntity mob);

    /**
     * Whether Hex Casting considers {@code mob} brainswept.
     * <p>
     * Hex's own cross-platform API can answer this, but only where Hex is loadable at compile time;
     * routing it here keeps the platform modules the only place that knows how each loader stores
     * the flag, exactly as {@link #clearBrainsweep} does.
     */
    boolean isBrainswept(MobEntity mob);

    /**
     * Registers a custom datapack resource condition under {@code lapisworks:<path>}.
     * <p>
     * Fabric and NeoForge have entirely separate condition systems (Fabric's
     * {@code ResourceConditions} vs Forge's {@code ICondition} serializers), and each loader only
     * understands its own, so the registration is platform-specific even though the predicate is not.
     */
    void registerResourceCondition(String path, java.util.function.Predicate<com.google.gson.JsonObject> test);
}

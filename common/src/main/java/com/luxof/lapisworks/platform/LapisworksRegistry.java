package com.luxof.lapisworks.platform;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * The seam over writing to a game registry.
 * <p>
 * On Fabric {@code Registry.register} may be called at any time, so shared code used it directly.
 * NeoForge instead <em>locks</em> its registries and requires mods to register from the
 * {@code RegisterEvent} callback; calling {@code Registry.register} there throws
 * "Can not register to a locked registry. Modder should use NeoForge Register methods." Shared code
 * therefore goes through this seam, and the NeoForge implementation registers the value when the
 * target registry is next open.
 */
public interface LapisworksRegistry {
    LapisworksRegistry INSTANCE = Seams.load(LapisworksRegistry.class);

    /**
     * Registers {@code value} under {@code id} in {@code registry}, where {@code registry} is one of
     * the <em>game's own</em> registries (blocks, items, sounds, ...).
     * <p>
     * NeoForge defers these until the registry's registration window opens; Fabric registers
     * immediately.
     */
    <T> T register(Registry<T> registry, Identifier id, T value);

    /**
     * Registers {@code value} into a registry owned by another mod, such as Hex Casting's action and
     * iota-type registries.
     * <p>
     * Those are not managed by the loader and are never locked, so this always writes immediately on
     * both platforms. It matters that it does: deferred writes are replayed during the loader's
     * registry event, which runs <em>before</em> Hex creates its registries, so a deferred pattern
     * would never be applied at all.
     */
    <T> T registerUnlocked(Registry<T> registry, Identifier id, T value);
}

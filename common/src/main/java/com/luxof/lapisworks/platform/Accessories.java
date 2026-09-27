package com.luxof.lapisworks.platform;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

/**
 * The seam over the loader's accessory/bauble system.
 * <p>
 * Fabric implements this over Trinkets, NeoForge over Curios. Shared code must go through here
 * rather than touching either API: Trinkets has no NeoForge build at all and Curios no Fabric build,
 * so a direct call from shared code is a guaranteed crash on the other platform.
 * <p>
 * The design mirrors Hex Casting's own {@code DiscoveryHandlers}: a loader-agnostic interface each
 * platform module implements, so shared code never learns what a slot system is.
 */
public interface Accessories {
    Accessories INSTANCE = Seams.load(Accessories.class);

    /**
     * Makes {@code item} wearable in {@code slot}, delegating its behaviour to
     * {@code accessory}. Called during mod initialization on both platforms.
     */
    void registerWearable(Item item, String slot, PlatformAccessoryItem accessory);

    /**
     * Every equipped accessory on the entity, in no particular order.
     * <p>
     * This is the one method most shared code needs; it stands in for Trinkets'
     * {@code getAllEquipped()} and for walking Curios' slot map.
     */
    List<Pair<AccessorySlot, ItemStack>> getAllEquipped(LivingEntity entity);

    /** The equipped stacks in one named slot, e.g. {@code "necklace"}. */
    List<ItemStack> getEquippedIn(LivingEntity entity, String slot);

    /** Whether the entity has any stack of {@code item} equipped. */
    boolean isEquipped(LivingEntity entity, Item item);

    /** The first equipped slot holding {@code item}, or {@code null}. */
    Pair<AccessorySlot, ItemStack> getFirstEquipped(LivingEntity entity, Item item);

    /**
     * Registers the client-side renderer used to draw {@code item} on the wearer's model. Client
     * only; must be called during client initialization.
     */
    void registerRenderer(Item item, AccessoryRenderer renderer);
}

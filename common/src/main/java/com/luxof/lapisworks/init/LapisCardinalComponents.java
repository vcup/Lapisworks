package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;

import com.luxof.lapisworks.mixinsupport.CollarControllable;

import net.minecraft.entity.Entity;

/**
 * Exposes tameable mobs' collars as Hex Casting iota holders.
 * <p>
 * Hex Casting attaches an iota-holder for every item implementing {@code IotaHolderItem}, but for
 * entities it only covers item entities, item frames, wall scrolls and players, so an addon has to
 * register its own holder for any other entity type. That registration used to be a Cardinal
 * Components {@code EntityComponentInitializer} here, which is Fabric-only; it now lives in each
 * platform module's seam implementation, which wraps {@link ToCollarable} in whichever holder type
 * its loader provides ({@code CCEntityIotaHolder.Wrapper} on Fabric,
 * {@code CapEntityIotaHolder.Wrapper} on NeoForge).
 */
public final class LapisCardinalComponents {
    private LapisCardinalComponents() {}

    /**
     * Makes an entity's worn collar readable and writable by Hex Casting, by delegating to
     * {@link CollarControllable}, so the data stays on the mob's own collar instead of being copied.
     */
    public static class ToCollarable extends ItemDelegatingEntityIotaHolder {
        public ToCollarable(Entity entity) {
            super(
                ((CollarControllable) entity)::getCollar,
                stack -> { ((CollarControllable) entity).setCollar(stack); }
            );
        }
    }
}

package com.luxof.lapisworks.neoforge;

import at.petrak.hexcasting.forge.cap.ForgeCapabilityHandler;
import at.petrak.hexcasting.forge.cap.HexCapabilities;
import at.petrak.hexcasting.forge.cap.adimpl.CapEntityIotaHolder;

import com.luxof.lapisworks.init.LapisCardinalComponents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * NeoForge half of the collar-as-iota-holder registration.
 * <p>
 * Hex Casting attaches an iota holder for every item implementing {@code IotaHolderItem}, but for
 * entities it only covers item entities, item frames, wall scrolls and players, so an addon
 * registers its own for any other entity type. On Fabric that is a Cardinal Components component
 * (see the fabric module); here it is a Forge capability, wrapped in Hex's own
 * {@code CapEntityIotaHolder.Wrapper} so it behaves identically to Hex's built-in ones.
 */
public final class NeoForgeCapabilities {
    private NeoForgeCapabilities() {}

    /** Our capability's id; only has to be unique, not to match Hex's. */
    private static final Identifier LAPISWORKS_IOTA_CAP = new Identifier("lapisworks", "collar_iota_holder");

    public static void init(IEventBus modBus, IEventBus forgeBus) {
        // Note: deliberately NOT registering ADIotaHolder on RegisterCapabilitiesEvent. Hex Casting
        // already registers it, and Forge treats a second registration of the same capability as
        // fatal ("Cannot register capability implementation multiple times"), which aborted Hex's
        // common setup. addCapability below is all an addon needs.
        forgeBus.addGenericListener(Entity.class, NeoForgeCapabilities::attachEntityCaps);
    }

    private static void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        // Only the cat, matching the Fabric build: the collar is readable/writable on cats so Hex
        // patterns can read and write the collar's stored iota.
        if (!(event.getObject() instanceof CatEntity cat)) return;

        event.addCapability(
            LAPISWORKS_IOTA_CAP,
            ForgeCapabilityHandler.makeProvider(
                HexCapabilities.IOTA,
                new CapEntityIotaHolder.Wrapper(new LapisCardinalComponents.ToCollarable(cat))
            )
        );
    }
}

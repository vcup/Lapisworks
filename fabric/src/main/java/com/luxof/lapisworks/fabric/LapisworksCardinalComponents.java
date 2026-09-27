package com.luxof.lapisworks.fabric;

import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;

import static at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER;

import com.luxof.lapisworks.init.LapisCardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;

/**
 * Fabric half of the collar-as-iota-holder registration.
 * <p>
 * Cardinal Components attaches per-entity data through components, so the shared
 * {@link LapisCardinalComponents.ToCollarable} delegate is wrapped in CCA's
 * {@code CCEntityIotaHolder.Wrapper} here. The NeoForge module does the equivalent with a Forge
 * capability instead.
 */
public class LapisworksCardinalComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(
            CatEntity.class,
            IOTA_HOLDER,
            wrapItemEntityDelegate(LapisCardinalComponents.ToCollarable::new)
        );
    }

    private <E extends Entity> ComponentFactory<E, CCEntityIotaHolder.Wrapper> wrapItemEntityDelegate(
        java.util.function.Function<E, at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder> make
    ) {
        return e -> new CCEntityIotaHolder.Wrapper(make.apply(e));
    }
}

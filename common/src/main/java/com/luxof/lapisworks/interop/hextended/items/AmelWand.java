package com.luxof.lapisworks.interop.hextended.items;

import com.luxof.lapisworks.items.AmelStaff;

import java.util.UUID;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class AmelWand extends AmelStaff {
    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        /* hextended's UUID */
        UUID.fromString("a370ec84-ea18-4de6-8730-4271516dcf9c"),
        "Amel Staff Zoom",
        0.28,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );
    @Override public EntityAttributeModifier getGridZoom() { return this.GRID_ZOOM; }
}

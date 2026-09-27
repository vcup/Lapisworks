package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.UUID;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;

public class AmelStaff extends ItemStaff {
    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        /* "What the fuck are you doing?"
         * Staves or something. You need to generate a random UUID once
         * and use it for every EntityAttributeModifier.
         * "why??"
         * i don't know either.
         * "what the fuck."
         * me too.
         */
        UUID.fromString("a370ec84-ea18-4de6-8730-4271516dcf9c"),
        "Amel Staff Zoom",
        0.20,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );

    public AmelStaff() { super(new Item.Settings().maxCount(1)); }
    public AmelStaff(Item.Settings props) { super(props); }

    public EntityAttributeModifier getGridZoom() { return this.GRID_ZOOM; }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        HashMultimap<EntityAttribute, EntityAttributeModifier> out = HashMultimap.create(
            super.getAttributeModifiers(slot)
        );
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
            out.put(HexAttributes.GRID_ZOOM, this.getGridZoom());
        return out;
    }
}

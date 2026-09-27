package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.luxof.lapisworks.items.shit.DurabilityPartAmel;

import java.util.UUID;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PartiallyAmelStaff extends ItemStaff implements DurabilityPartAmel {
    private static Item.Settings static_settings = new Item.Settings().maxCount(1).maxDamage(200);

    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        // same UUID as hextended to not stack on them
        UUID.fromString("a370ec84-ea18-4de6-8730-4271516dcf9c"),
        "Partially Amel Staff Zoom",
        0.35,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );
    public EntityAttributeModifier _getGridZoom() { return this.GRID_ZOOM; }

    public PartiallyAmelStaff() { super(static_settings); }
    public PartiallyAmelStaff(Item.Settings props) { super(props); }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        HashMultimap<EntityAttribute, EntityAttributeModifier> out = HashMultimap.create(
            super.getAttributeModifiers(slot)
        );
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
            out.put(HexAttributes.GRID_ZOOM, this._getGridZoom());
        return out;
    }

    @Override
    public int getAmelWorthInDurability() { return 20; }

    @Override
    public boolean isDamageable() { return true; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.damageWRTAmelCount(user, world, hand, 1);
        return super.use(world, user, hand);
    }
}

package com.luxof.lapisworks.items;

import com.luxof.lapisworks.items.shit.AmelSword;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.world.World;

public class IronSword extends AmelSword {
    public IronSword() {
        super(ToolMaterials.IRON, 4, -2.4F, new Item.Settings().maxDamage(500), Items.IRON_INGOT);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity)) { return stack; }
        PlayerEntity player = (PlayerEntity)user;
        // set cooldown for 0.5s when you stop using it
        player.getItemCooldownManager().set(this, 10);
        return stack;
    }
}

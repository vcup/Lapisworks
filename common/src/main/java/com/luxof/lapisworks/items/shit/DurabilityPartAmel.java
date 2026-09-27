package com.luxof.lapisworks.items.shit;

import static com.luxof.lapisworks.Lapisworks.getInfusedAmel;
import static com.luxof.lapisworks.Lapisworks.setInfusedAmel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface DurabilityPartAmel extends BasePartAmel {
    int getAmelWorthInDurability();

    default void makeAmelCountAppropriate(ItemStack stack) {
        setInfusedAmel(
            stack,
            // hope intdiv saves me ass
            (stack.getMaxDamage() - stack.getDamage()) / getAmelWorthInDurability()
        );
    }

    default void damageWRTAmelCount(PlayerEntity user, World world, Hand hand, int dmg) {
        ItemStack stack = user.getStackInHand(hand);
        stack.damage(dmg, user, any -> {});
        makeAmelCountAppropriate(stack);
    }

    default void makeDurabilityAppropriate(ItemStack stack) {
        stack.setDamage(
            stack.getMaxDamage() -
            getInfusedAmel(stack) * getAmelWorthInDurability()
        );
    }

    @Override
    default void onImbue(ItemStack stack, int addedAmel) {
        makeDurabilityAppropriate(stack);
    }
}

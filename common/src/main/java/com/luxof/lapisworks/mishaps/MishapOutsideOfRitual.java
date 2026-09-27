package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class MishapOutsideOfRitual extends Mishap {
    private final boolean oneTimeRitual;
    public MishapOutsideOfRitual(boolean oneTimeRitual) {
        this.oneTimeRitual = oneTimeRitual;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment arg0, Context arg1) {
        // wish i could return hot pink but oh well
        // code in case i can later: #e30574
        return dyeColor(DyeColor.LIGHT_BLUE);
    }

    @Override
    protected Text errorMessage(CastingEnvironment arg0, Context arg1) {
        if (!oneTimeRitual)
            return Text.translatable("mishaps.lapisworks.ritual.cast_outside_ritual");

        return Text.translatable("mishaps.lapisworks.ritual.cast_outside_one_time_ritual");
    }

    private void dropAll(LivingEntity entity, List<ItemStack> stacks) {
        ItemScatterer.spawn(
            entity.getWorld(),
            BlockPos.ofFloored(entity.getPos()),
            DefaultedList.copyOf(
                ItemStack.EMPTY,
                (ItemStack[])stacks.toArray()
            )
        );
    }
    private boolean DoesntHaveBindingCurse(ItemStack stack) {
        // y u no !EnchantmentHelper::hasBindingCurse or even Stream.filterOut
        return !EnchantmentHelper.hasBindingCurse(stack);
    }
    @Override
    public void execute(CastingEnvironment env, Context ctx, List<Iota> stack) {
        LivingEntity castingEntity = env.getCastingEntity();
        if (castingEntity instanceof ServerPlayerEntity sp) {
            PlayerInventory inv = sp.getInventory();
            dropAll(sp, inv.main);
            dropAll(sp, inv.offHand);
            dropAll(sp, inv.armor.stream().filter(this::DoesntHaveBindingCurse).toList());
        }
    }
}

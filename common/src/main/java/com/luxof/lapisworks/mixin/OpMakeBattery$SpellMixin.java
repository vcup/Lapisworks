package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.init.ModItems;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
    targets = {"at/petrak/hexcasting/common/casting/actions/spells/OpMakeBattery$Spell"},
    remap = false
)
public abstract class OpMakeBattery$SpellMixin {
    @Shadow public abstract ItemEntity getItemEntity();
    @Shadow public abstract ItemStack getHandStack();
    @Shadow public abstract Hand getHand();

    @WrapMethod(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V")
    public void cast(
        CastingEnvironment env,
        Operation<Void> og
    ) {
        if (!getHandStack().isOf(ModItems.UNCRAFTED_CONDENSER)) {
            og.call(env);
            return;
        }

        if (!getItemEntity().isAlive()) return;
        // copy because i may not want it to change
        ItemStack entityStack = getItemEntity().getStack().copy();
        ItemStack handStack = getHandStack();

        ADMediaHolder entityStackMedia = IXplatAbstractions.INSTANCE.findMediaHolder(entityStack);
        long mediamount = entityStackMedia.withdrawMedia(entityStackMedia.getMedia(), false);
        if (mediamount <= 0L) return;

        ItemStack newStack = new ItemStack(ModItems.MEDIA_CONDENSER);
        NBTHelper.putLong(newStack, "media", mediamount);
        NBTHelper.putLong(newStack, "max", mediamount);
        if (!env.replaceItem(it -> it == handStack, newStack, getHand())) return;
        
        if (entityStack.isEmpty()) {
            getItemEntity().kill();
        }
        getItemEntity().setStack(entityStack);
    }
}

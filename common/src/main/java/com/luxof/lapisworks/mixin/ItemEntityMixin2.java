package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.mixinsupport.ItemEntityMinterface;

import static com.luxof.lapisworks.Lapisworks.putInCradle;
import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin2 implements ItemEntityMinterface {
    @Shadow public abstract ItemStack getStack();

    @Unique private BlockPos cradlePos = null;

    @Unique @Override public void setBlockPosOfCradle(BlockPos blockPos) { cradlePos = blockPos; }
    @Unique @Override public BlockPos getBlockPosOfCradle() { return cradlePos; }
    
    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound compound, CallbackInfo ci) {
        if (!compound.contains("cradlePosX")) return;
        cradlePos = new BlockPos(
            compound.getInt("cradlePosX"),
            compound.getInt("cradlePosY"),
            compound.getInt("cradlePosZ")
        );

        if (compound.getBoolean(IS_IN_CRADLE)) {
            putInCradle(getStack());
        }
    }
    
    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound compound, CallbackInfo ci) {
        if (cradlePos == null) return;
        compound.putInt("cradlePosX", cradlePos.getX());
        compound.putInt("cradlePosY", cradlePos.getY());
        compound.putInt("cradlePosZ", cradlePos.getZ());

        if (NBTHelper.contains(getStack(), IS_IN_CRADLE)) {
            putInCradle(getStack());
        }
    }
}

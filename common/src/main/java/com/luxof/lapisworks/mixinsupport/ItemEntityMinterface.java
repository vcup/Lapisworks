package com.luxof.lapisworks.mixinsupport;

import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public interface ItemEntityMinterface {
    public void setBlockPosOfCradle(BlockPos blockPos);
    @Nullable public BlockPos getBlockPosOfCradle();
}

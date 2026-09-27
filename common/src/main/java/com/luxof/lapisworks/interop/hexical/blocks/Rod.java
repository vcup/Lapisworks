package com.luxof.lapisworks.interop.hexical.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class Rod extends Block {
    public static final VoxelShape SHAPE = Block.createCuboidShape(
        6, 0, 6,
        10, 16, 10
    );

    public Rod() {
        super(Settings.copy(Blocks.LIGHTNING_ROD));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos bp, ShapeContext ctx) {
        return SHAPE;
    }
}

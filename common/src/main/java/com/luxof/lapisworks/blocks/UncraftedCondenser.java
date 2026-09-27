package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.common.lib.HexBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class UncraftedCondenser extends Block {
    public UncraftedCondenser() {
        super(Settings.copy(HexBlocks.SLATE_BLOCK));
    }

    public static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1, 0, 0, 15, 14, 16),
        Block.createCuboidShape(0, 0, 1, 16, 14, 15),
        Block.createCuboidShape(6, 14, 6, 10, 16, 10)
    );
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    public VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
        return SHAPE;
    }
}

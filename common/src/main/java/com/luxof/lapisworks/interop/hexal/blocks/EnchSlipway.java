package com.luxof.lapisworks.interop.hexal.blocks;

import com.luxof.lapisworks.interop.hexal.Lapisal;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnchSlipway extends BlockWithEntity {
    public EnchSlipway(Settings settings) { super(settings); }

    @Override
    public VoxelShape getOutlineShape(
        BlockState pState,
        BlockView pLevel,
        BlockPos pPos,
        ShapeContext pContext
    ) { return VoxelShapes.empty(); }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.INVISIBLE; }

    @Override
    public BlockEntity createBlockEntity(BlockPos bp, BlockState bs) {
        return new EnchSlipwayEntity(bp, bs);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (type == Lapisal.ENCH_SLIPWAY_ENTITY_TYPE) { return EnchSlipwayEntity::tick; }
        else { return null; }
    }
}

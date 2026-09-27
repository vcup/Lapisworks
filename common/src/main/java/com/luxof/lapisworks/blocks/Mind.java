package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Mind extends BlockWithEntity {
    public Mind() {
        super(
            Settings.create().sounds(BlockSoundGroup.DEEPSLATE_TILES).breakInstantly().nonOpaque()
        );
    }

    // i REFUSE to use BERs (scawy)
    public static final IntProperty FILLED = IntProperty.of("filled", 0, 14);

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MindEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        // checkType() makes me required to do an "unsafe cast" for whatever reason
        if (type == ModBlocks.MIND_ENTITY_TYPE) { return MindEntity::tick; }
        else { return null; }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }
}

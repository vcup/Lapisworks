package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;
import com.luxof.lapisworks.init.ModBlocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LiveJukebox extends BlockWithEntity {
    public LiveJukebox() {
        // strong as deepslate
        super(AbstractBlock.Settings.create()
            .mapColor(MapColor.DIRT_BROWN)
            .instrument(Instrument.BASS)
            .strength(2.0F, 6.0F)
            .requiresTool()
            .burnable()
        );
    }

    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        LiveJukeboxEntity blockEntity = (LiveJukeboxEntity)world.getBlockEntity(pos);
        blockEntity.startPlaying();
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LiveJukeboxEntity(pos, state);
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
        if (type == ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE) { return LiveJukeboxEntity::tick; }
        else { return null; }
    }
}

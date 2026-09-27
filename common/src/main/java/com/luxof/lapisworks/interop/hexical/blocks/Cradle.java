package com.luxof.lapisworks.interop.hexical.blocks;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.interop.hexical.Lapixical;

import static com.luxof.lapisworks.Lapisworks.putInCradle;
import static com.luxof.lapisworks.Lapisworks.removeFromCradle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Cradle extends BlockWithEntity {
    public static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0, 0, 6, 16, 16, 10),
        Block.createCuboidShape(6, 0, 0, 10, 16, 16)

        // unnecessary detail makes raycasting hard
        /*Block.createCuboidShape(0, 0, 6, 16, 2, 10),
        Block.createCuboidShape(0, 0, 6, 2, 16, 10),
        Block.createCuboidShape(0, 14, 6, 16, 16, 10),
        Block.createCuboidShape(14, 0, 6, 16, 16, 10),

        Block.createCuboidShape(6, 0, 0, 10, 2, 16),
        Block.createCuboidShape(6, 0, 0, 10, 16, 2),
        Block.createCuboidShape(6, 14, 0, 10, 16, 16),
        Block.createCuboidShape(6, 0, 14, 10, 16, 16)*/
    );

    public Cradle() { super(Settings.copy(Blocks.LIGHTNING_ROD)); }
 
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {}

    @Override
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos bp, ShapeContext ctx) {
        return SHAPE;
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
        // actual fucking magic that fixes EVERYTHING LMAO
        if (world.isClient) return ActionResult.SUCCESS;

        CradleEntity bE = (CradleEntity)world.getBlockEntity(pos);

        ItemStack prevStack = bE.getHeldStack();
        ItemStack playerStack = player.getStackInHand(hand);

        removeFromCradle(prevStack);
        putInCradle(playerStack);

        bE.setHeldStack(playerStack);
        player.setStackInHand(hand, prevStack);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState bs) {
        return new CradleEntity(pos, bs);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        // checkType() makes me required to do an "unsafe cast" for whatever reason
        if (type == Lapixical.CRADLE_ENTITY_TYPE) return CradleEntity::tick;
        if (type == ModBlocks.MIND_ENTITY_TYPE) { return MindEntity::tick; }
        else { return null; }
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) return;
        CradleEntity bE = (CradleEntity)world.getBlockEntity(pos);
        if (bE.heldEntity != null) {
            bE.heldEntity.discard();
            bE.heldEntity = null;
        }

        ItemStack stack = bE.getHeldStack();
        removeFromCradle(stack);

        if (!stack.isEmpty()) {
            world.spawnEntity(
                new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack)
            );
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}

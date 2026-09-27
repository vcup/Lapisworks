package com.luxof.lapisworks.interop.hexical.blocks;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import static com.luxof.lapisworks.LapisworksIDs.HEXICAL_IMPETUS_HAND;
import static com.luxof.lapisworks.LapisworksIDs.RH_HOLDER;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

public class Holder extends BlockCircleComponent implements BlockEntityProvider {
    public static final VoxelShape SHAPE = Block.createCuboidShape(
        2, 0, 2,
        14, 16, 14
    );
    public static final IntProperty STATE = IntProperty.of("state", 0, 1);
    public Holder() {
        super(Settings.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f));
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(STATE, 0)
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
        HolderEntity blockEntity = (HolderEntity)world.getBlockEntity(pos);
        HeldItemInfo item = blockEntity.heldInfo;
        ItemStack prevStack = item.stack().copy();
        Hand newHand;
        if (player.getStackInHand(hand).isEmpty()) {
            newHand = item.hand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        } else {
            newHand = item.hand();
        }
        blockEntity.heldInfo = new HeldItemInfo(
            player.getStackInHand(hand),
            newHand
        );
        player.setStackInHand(hand, prevStack);
        world.setBlockState(pos, state.with(STATE, newHand.ordinal()));
        blockEntity.markDirty();
        if (!world.isClient) {
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public int getComparatorOutput(
        @NotNull BlockState state,
        @NotNull World world,
        @NotNull BlockPos pos
    ) {
        HolderEntity blockEntity = (HolderEntity)world.getBlockEntity(pos);
        return ScreenHandler.calculateComparatorOutput((Inventory)blockEntity);
    }

    /*
    // no need to do all this
    // mc apparently handles dropping blocks from `Inventory`s just fine by itself
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        this.spawnBreakParticles(world, player, pos, state);
        HolderEntity bE = (HolderEntity)world.getBlockEntity(pos);

        ItemEntity items = new ItemEntity(
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            bE.heldInfo.stack()
        );
        world.spawnEntity(items);

        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, Emitter.of(player, state));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(
        @NotNull BlockState state,
        @NotNull World world,
        @NotNull BlockPos pos,
        @NotNull BlockState newState,
        boolean moved
    ) {
        HolderEntity bE = (HolderEntity)world.getBlockEntity(pos);
        ItemEntity items = new ItemEntity(
            world, pos.getX(), pos.getY(), pos.getZ(), bE.heldInfo.stack()
        );
        world.spawnEntity(items);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    */

    @Override
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos bp, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public ControlFlow acceptControlFlow(
        CastingImage imgIn,
        CircleCastEnv env,
        Direction enterDir,
        BlockPos pos,
        BlockState bs,
        ServerWorld world
    ) {
        CastingImage imgNow = null;
        HolderEntity holder = (HolderEntity)world.getBlockEntity(pos);

        ItemStack stack = holder.heldInfo.stack();
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
        if (iotaHolder != null) {
            Iota iota = iotaHolder.readIota(world);
            if (iota != null) {
                List<Iota> newStack = new ArrayList<>(imgIn.getStack());
                newStack.add(iota);
                List<ParenthesizedIota> newParens = new ArrayList<>(imgIn.getParenthesized());
                newParens.add(new CastingImage.ParenthesizedIota(iota, false));
                // idk why miyu does it this way but i may as well follow her lead
                imgNow = imgIn.getParenCount() == 0 ? imgIn.copy(
                        newStack,
                        imgIn.getParenCount(),
                        imgIn.getParenthesized(),
                        imgIn.getEscapeNext(),
                        imgIn.getOpsConsumed(),
                        imgIn.getUserData()
                    ) : imgIn.copy(
                        imgIn.getStack(),
                        imgIn.getParenCount(),
                        newParens,
                        imgIn.getEscapeNext(),
                        imgIn.getOpsConsumed(),
                        imgIn.getUserData()
                    );
            }
        } else {
            int[] position = {pos.getX(), pos.getY(), pos.getZ()}; // can't init inside the call fsr
            boolean isMain = holder.heldInfo.hand() == Hand.MAIN_HAND;
            imgIn.getUserData().putIntArray(
                isMain ? RH_HOLDER : HEXICAL_IMPETUS_HAND,
                position
            );
        }
        if (imgNow == null) imgNow = imgIn;

        EnumSet<Direction> exitDirsSet = this.possibleExitDirections(pos, bs, world);
        exitDirsSet.remove(enterDir.getOpposite());
        return new ControlFlow.Continue(
            imgNow,
            exitDirsSet.stream().map((dir) -> this.exitPositionFromDirection(pos, dir)).toList()
        );
    }

    @Override
    public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerWorld world) {
        return enterDir != this.normalDir(pos, bs, world).getOpposite();
    }

    @Override
    public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, World world) {
        EnumSet<Direction> dirs = EnumSet.allOf(Direction.class);
        dirs.remove(this.normalDir(pos, bs, world));
        return dirs;
    }

    @Override
    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft) {
        return Direction.UP;
    }

    @Override
    public float particleHeight(BlockPos pos, BlockState bs, World world) {
        return 0.5f - 15f / 16f;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(STATE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HolderEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(
        BlockState state, World world, BlockPos pos, BlockState newState, boolean moved
    ) {
        if (state.getBlock() == newState.getBlock()) return;
        if (!(world.getBlockEntity(pos) instanceof HolderEntity bE)) return;
        ItemScatterer.spawn(world, pos, bE);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}

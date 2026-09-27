package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.bigchalk.BigChalkCenter;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkCenterEntity;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkPart;
import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.blocks.stuff.ChalkBlockInterface;
import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.platform.Menus;

import static com.luxof.lapisworks.LapisworksIDs.CHALK_CONNECTABLE_TAG;

import io.netty.buffer.Unpooled;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.SOUTH;
import static net.minecraft.util.math.Direction.UP;
import static net.minecraft.util.math.Direction.WEST;

import java.util.List;

// ImbueAmel.java ass code bro
public class ChalkWithPattern extends BlockWithEntity implements ChalkBlockInterface {
    public ChalkWithPattern() {
        super(Settings.copy(ModBlocks.CHALK));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new ChalkWithPatternEntity(arg0, arg1);
    }

    private static VoxelShape DOWN_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static VoxelShape UP_SHAPE = Block.createCuboidShape(0, 15, 0, 16, 16, 16);
    private static VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 1);
    private static VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 15, 16, 16, 16);
    private static VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 0, 0, 1, 16, 16);
    private static VoxelShape EAST_SHAPE = Block.createCuboidShape(15, 0, 0, 16, 16, 16);
    @Override
    public VoxelShape getOutlineShape(
        BlockState pState,
        BlockView pLevel,
        BlockPos pPos,
        ShapeContext pContext
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)pLevel.getBlockEntity(pPos);
        if (chalk == null) return DOWN_SHAPE;

        return switch (chalk.attachedTo) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    public Pair<Direction, Direction> getForwardAndLeft(Direction down) {
        Direction forward = down == Direction.NORTH || down == Direction.SOUTH ?
            Direction.UP : Direction.NORTH;
        Vec3i left = down.getVector().crossProduct(forward.getVector());

        return new Pair<>(forward, Direction.getFacing(left.getX(), left.getY(), left.getZ()));
    }
    public List<BlockPos> get3x3(
        BlockPos pos,
        Direction down
    ) {
        var forwardAndLeft = getForwardAndLeft(down);
        Direction forward = forwardAndLeft.getLeft();
        Direction backward = forward.getOpposite();

        Direction left = forwardAndLeft.getRight();
        Direction right = left.getOpposite();

        return List.of(
            pos.offset(forward).offset(left),
            pos.offset(forward),
            pos.offset(forward).offset(right),
            pos.offset(left),
            pos,
            pos.offset(right),
            pos.offset(backward).offset(left),
            pos.offset(backward),
            pos.offset(backward).offset(right)
        );
    }
    /** returns success. */
    public boolean tryMakeChalkMultiblock(
        World world,
        BlockPos center,
        ItemStack stack,
        PlayerEntity player
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(center);
        Direction down = chalk.attachedTo;
        List<BlockPos> multiblockArea = get3x3(center, down);

        for (BlockPos pos : multiblockArea) {
            if (
                !(world.getBlockEntity(pos) instanceof ChalkWithPatternEntity leChalk) ||
                leChalk.attachedTo != down
            )
                return false;
        }

        if (!player.isCreative()) stack.damage(10, player, couldntCareEnough -> {});
        for (BlockPos pos : multiblockArea) {
            world.setBlockState(
                pos,
                pos.equals(center)
                    ? ModBlocks.BIG_CHALK_CENTER
                        .getDefaultState()
                        .with(BigChalkCenter.ATTACHED, down)
                        .with(BigChalkCenter.FACING, player.getHorizontalFacing())
                    : ModBlocks.BIG_CHALK_PART
                        .getDefaultState()
                        .with(BigChalkPart.ATTACHED, down)
            );
            BigChalkPart.spawnDust(world, pos, down);
        }
        world.playSound(null, center, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS);
        // *why?*
        ((BigChalkCenterEntity)world.getBlockEntity(center))._firstTime = false;

        return true;
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
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(ModItems.CHALK))
            return ChalkBlockInterface.super.onUse(
                state,
                world,
                pos,
                player,
                hand,
                hit,
                ((ChalkWithPatternEntity)world.getBlockEntity(pos)).attachedTo
            );

        if (tryMakeChalkMultiblock(world, pos, stack, player)) {
            return ActionResult.SUCCESS;
        }

        if (world.isClient) return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        // This menu carries extra opening data (the block position), so it must be opened through the
        // platform's extended path. Vanilla's openHandledScreen makes Fabric throw
        // "Extended screen handler ... must be opened with an ExtendedScreenHandlerFactory".
        if (screenHandlerFactory instanceof Menus.ExtendedProvider provider) {
            Menus.INSTANCE.openExtended((ServerPlayerEntity)player, provider);
        } else if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.SUCCESS;
    }

    // "left and front, according to WHAT?" i hear you ask.
    // whatever makes render gud.
    // use a compass. north is front, almost always.
    // ^ that comment is so fucked and untrue
    // was i on cracked
    private Direction findLeftVector(Direction down) {
        return switch (down) {
            case UP -> WEST;
            case DOWN -> WEST;
            case NORTH -> WEST;
            case WEST -> UP;
            case SOUTH -> WEST;
            case EAST -> DOWN;
        };
    }
    private Direction findFrontVector(Direction down) {
        return switch (down) {
            case UP -> SOUTH;
            case DOWN -> NORTH;
            case NORTH -> UP;
            case WEST -> NORTH;
            case SOUTH -> DOWN;
            case EAST -> NORTH;
        };
    }
    @Override
    public void neighborUpdate(
        BlockState state,
        World world,
        BlockPos pos,
        Block fromBlock,
        BlockPos fromPos,
        boolean notify
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(pos);
        BlockState fromState = world.getBlockState(fromPos);

        Direction comingFrom = Direction.fromVector(
            fromPos.getX() - pos.getX(),
            fromPos.getY() - pos.getY(),
            fromPos.getZ() - pos.getZ()
        );
        if (comingFrom == chalk.attachedTo) {
            world.breakBlock(pos, false);
            return;
        }

        updateBracketRendering(world, pos, chalk);

        if (
            !fromState.isIn(CHALK_CONNECTABLE_TAG) ||
            (!chalk.renderLeftBracket || !chalk.renderRightBracket)
        ) {
            chalk.save();
            return;
        }

        Direction left = findLeftVector(chalk.attachedTo);
        if (comingFrom == left || comingFrom == left.getOpposite())
            chalk.rotated = false;
        else
            chalk.rotated = true;

        updateBracketRendering(world, pos, chalk);

        chalk.save();
    }

    private int assignPointsTo(
        World world,
        BlockPos ourPos,
        Direction dir,
        Direction attachedTo
    ) {
        int ret = 0;

        BlockPos targetPos = ourPos.offset(dir);
        BlockState state = world.getBlockState(targetPos);

        if (state.isIn(CHALK_CONNECTABLE_TAG))
            ret += 1;
        else
            return 0;

        if (
            world.getBlockEntity(targetPos) instanceof AttachedBE attachedConnectable &&
            attachedConnectable.getAttachedTo() == attachedTo
        )
            ret += 1;
        else
            return 0;

        if (world.getBlockEntity(targetPos) instanceof ChalkWithPatternEntity) {
            // if it's chalk we wouldn't join in to our chalk,
            // it's chalk we don't want to be facing.
            if (shouldRenderBracketOnSide(world, ourPos, dir, attachedTo))
                return 0;
            // if it IS though, we ABSOLUTELY want to be facing it.
            ret += 3;
        }

        return ret;
    }
    @Override
    public void onPlaced(
        World world,
        BlockPos pos,
        BlockState state,
        LivingEntity placer,
        ItemStack itemStack
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(pos);

        Direction left = findLeftVector(chalk.attachedTo);
        Direction front = findFrontVector(chalk.attachedTo);
        Direction attachedTo = chalk.attachedTo;

        int onLeftAndRight = assignPointsTo(world, pos, left, attachedTo) +
            assignPointsTo(world, pos, left.getOpposite(), attachedTo);

        int onFrontAndBack = assignPointsTo(world, pos, front, attachedTo) +
            assignPointsTo(world, pos, front.getOpposite(), attachedTo);

        if (onFrontAndBack > onLeftAndRight) chalk.rotated = true;
        else if (onFrontAndBack == onLeftAndRight) chalk.rotated = Math.random() < 0.5;
        else chalk.rotated = false;

        updateBracketRendering(world, pos, chalk);
        chalk.save();
    }

    private boolean shouldRenderBracketOnSide(
        World world,
        BlockPos pos,
        Direction sideDir,
        Direction attachedTo
    ) {
        BlockPos side = pos.offset(sideDir);
        if (
            !(world.getBlockEntity(side) instanceof ChalkWithPatternEntity chalk) ||
            chalk.attachedTo != attachedTo
        ) return true;

        Direction horizontal = chalk.rotated ?
            findFrontVector(chalk.attachedTo).getOpposite() : findLeftVector(chalk.attachedTo);

        if (chalk.renderLeftBracket && chalk.renderRightBracket)
            return false;
        else if (sideDir == horizontal || sideDir == horizontal.getOpposite())
            return false;
        else
            return true;
    }
    public void updateBracketRendering(
        World world,
        BlockPos pos,
        ChalkWithPatternEntity chalk
    ) {
        Direction horizontal = chalk.rotated ?
            findFrontVector(chalk.attachedTo).getOpposite() : findLeftVector(chalk.attachedTo);
        Direction attachedTo = chalk.attachedTo;

        chalk.renderLeftBracket = shouldRenderBracketOnSide(world, pos, horizontal, attachedTo);
        chalk.renderRightBracket = shouldRenderBracketOnSide(world, pos, horizontal.getOpposite(), attachedTo);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        world.playSoundAtBlockCenter(
            pos,
            SoundEvents.BLOCK_SAND_BREAK,
            SoundCategory.BLOCKS,
            1f,
            1f,
            false
        );
    }
}

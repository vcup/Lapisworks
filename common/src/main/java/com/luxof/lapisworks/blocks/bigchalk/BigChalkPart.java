package com.luxof.lapisworks.blocks.bigchalk;

import com.luxof.lapisworks.init.LapisParticles;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.get3x3;
import static com.luxof.lapisworks.LapisworksIDs.CANT_PLACE_CHALK_ON_TAG;
import static com.luxof.lapisworks.LapisworksIDs.GIB_DUST;

import io.netty.buffer.Unpooled;

import com.luxof.lapisworks.platform.Networking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

// big chalk is after me!
public class BigChalkPart extends Block {
    public BigChalkPart() {
        super(
            Settings.copy(ModBlocks.CHALK)
        );
        setDefaultState(
            getDefaultState()
                .with(ATTACHED, Direction.DOWN)
        );
    }

    public static final EnumProperty<Direction> ATTACHED = EnumProperty.of(
        "attached",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED);
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
        Direction attachedTo = pState.get(ATTACHED);

        return switch (attachedTo) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        // always delegate onUse to the center of the multiblock
        Direction attached = state.get(ATTACHED);

        for (BlockPos otherPos : get3x3(pos, attached, false)) {
            BlockState otherState = world.getBlockState(otherPos);
            if (!otherState.isOf(ModBlocks.BIG_CHALK_CENTER)) continue;
            return otherState.getBlock().onUse(otherState, world, otherPos, player, hand, hit);
        }

        return ActionResult.PASS;
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
        Direction comingFrom = Direction.fromVector(
            fromPos.getX() - pos.getX(),
            fromPos.getY() - pos.getY(),
            fromPos.getZ() - pos.getZ()
        );
        Direction attached = state.get(ATTACHED);
        BlockState fromState = world.getBlockState(fromPos);
        if (
            comingFrom == attached &&
            (!fromState.isSideSolidFullSquare(world, fromPos, attached.getOpposite()) ||
            fromState.isIn(CANT_PLACE_CHALK_ON_TAG))
        ) {
            spawnDust(world, pos, comingFrom);
            world.removeBlock(pos, false);
        }
    }

    protected boolean isCenter() { return false; }
    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(
        BlockState state,
        World world,
        BlockPos pos,
        BlockState newState,
        boolean moved
    ) {
        // WHY DOES THIS FUCKING METHOD NEVER RUN ON THE CLIENT
        // IT FUCKING SHOULD
        // BUT IT NEVER FUCKING DOES
        // WHAT IS THE POINT OF GIVING ME A WORLD INSTEAD OF A SERVERWORLD
        // IS IT TO GIVE ME PARANOIA
        // BECAUSE IT SURE FUCKING HAS
        // HOLY FUCKING SHIT
        if (state.isOf(newState.getBlock())) return;

        Direction attached = state.get(ATTACHED);

        for (BlockPos otherPos : get3x3(pos, attached, false)) {
            BlockState otherState = world.getBlockState(otherPos);
            if (
                isCenter() && otherState.isOf(ModBlocks.BIG_CHALK_PART) ||
                !isCenter() && otherState.isOf(ModBlocks.BIG_CHALK_CENTER)
            ) {
                if (world instanceof ServerWorld sw)
                    spawnDustServer(sw, otherPos, attached);
                world.removeBlock(otherPos, false);
            }
        }
        if (isCenter()) {
            world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void spawnBreakParticles(
        World world,
        PlayerEntity player,
        BlockPos pos,
        BlockState state
    ) {
        spawnDust(world, pos, state.get(ATTACHED));
    }

    public void spawnDustServer(
        ServerWorld sw,
        BlockPos pos,
        Direction attachedTo
    ) {
        for (ServerPlayerEntity sp : sw.getPlayers()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(pos);
            buf.writeString(attachedTo.toString());

            Networking.INSTANCE.sendToPlayer(sp, GIB_DUST, buf);
        }
    }
    public static void spawnDust(
        World world,
        BlockPos pos,
        Direction attachedTo
    ) {
        for (int i = 0; i < 15; i++) {
            spawnDustParticle(world, pos, attachedTo);
        }
    }
    private static void spawnDustParticle(
        World world,
        BlockPos pos,
        Direction attachedTo
    ) {
        // i'll smoke a million litres of 'caine b4 i explain this gl
        boolean posX = attachedTo == Direction.WEST;
        boolean flipXSpawn = attachedTo == Direction.EAST;
        boolean xAxis = posX || flipXSpawn;

        boolean posY = attachedTo == Direction.DOWN;
        boolean flipYSpawn = attachedTo == Direction.UP;
        boolean yAxis = posY || flipYSpawn;

        boolean posZ = attachedTo == Direction.NORTH;
        boolean flipZSpawn = attachedTo == Direction.SOUTH;
        boolean zAxis = posZ || flipZSpawn;

        // trust me :)
        double x = (double)pos.getX() + 0.5 +
            (xAxis ? (posX ? -0.2 : -0.8) : Math.random() * 1.25 - 0.75) * (flipXSpawn ? -1 : 1);
        double y = (double)pos.getY() + 0.5 +
            (yAxis ? (posY ? -0.2 : -0.8) : Math.random() * 1.25 - 0.75)  * (flipYSpawn ? -1 : 1);
        double z = (double)pos.getZ() + 0.5 +
            (zAxis ? (posZ ? -0.2 : -0.8) : Math.random() * 1.25 - 0.75)  * (flipZSpawn ? -1 : 1);

        double vX = xAxis ? 0.0 : Math.random() * 0.2 - 0.1;
        double vY = yAxis ? 0.0 : Math.random() * 0.2 - 0.1;
        double vZ = zAxis ? 0.0 : Math.random() * 0.2 - 0.1;

        world.addParticle(LapisParticles.AMETHYST_DUST, x, y, z, vX, vY, vZ);
    }
}

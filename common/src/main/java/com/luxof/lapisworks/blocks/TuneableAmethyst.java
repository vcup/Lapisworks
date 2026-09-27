package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class TuneableAmethyst extends BlockWithEntity {
    public TuneableAmethyst() {
        super(
            Settings.create()
                .solid()
                .nonOpaque()
                .strength(1.5f)
                .mapColor(DyeColor.PINK)
                .pistonBehavior(PistonBehavior.DESTROY)
                .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
        );
        setDefaultState(
            this.stateManager.getDefaultState()
                .with(STAGE, 0)
                .with(ATTACHED, Direction.DOWN)
        );
    }

    public static final IntProperty STAGE = IntProperty.of("stage", 0, 2);
    public static final EnumProperty<Direction> ATTACHED = EnumProperty.of(
        "attached",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
        builder.add(ATTACHED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new TuneableAmethystEntity(arg0, arg1);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
            .with(ATTACHED, ctx.getSide().getOpposite());
    }

    private final VoxelShape DOWN_SHAPE = Block.createCuboidShape(3, 0, 3, 13, 12, 13);
    private final VoxelShape UP_SHAPE = Block.createCuboidShape(3, 4, 3, 13, 16, 13);
    private final VoxelShape NORTH_SHAPE = Block.createCuboidShape(3, 3, 0, 13, 13, 12);
    private final VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 3, 3, 12, 13, 13);
    private final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(3, 3, 4, 13, 13, 16);
    private final VoxelShape EAST_SHAPE = Block.createCuboidShape(4, 3, 3, 16, 13, 13);
    @Override
    public VoxelShape getOutlineShape(
        BlockState state,
        BlockView world,
        BlockPos pos,
        ShapeContext context
    ) {
        return switch (state.get(ATTACHED)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }
}

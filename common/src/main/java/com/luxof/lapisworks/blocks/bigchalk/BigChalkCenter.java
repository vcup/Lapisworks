package com.luxof.lapisworks.blocks.bigchalk;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.items.AmelJar;
import com.luxof.lapisworks.items.Stamp;

import static com.luxof.lapisworks.Lapisworks.get3x3;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class BigChalkCenter extends BigChalkPart implements BlockEntityProvider {
    public BigChalkCenter() {
        super();
        setDefaultState(
            getDefaultState()
                .with(ATTACHED, Direction.DOWN)
                .with(FACING, Direction.NORTH)
        );
    }

    public static final EnumProperty<Direction> FACING = EnumProperty.of(
        "facing",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED);
        builder.add(FACING);
    }

    @Override
    protected boolean isCenter() { return true; }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new BigChalkCenterEntity(arg0, arg1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (type == ModBlocks.BIG_CHALK_CENTER_ENTITY_TYPE)
            return (a, b, stateNow, bE) -> ((BigChalkCenterEntity)bE).tick(stateNow);
        else
            return null;
    }

    private ActionResult onUseWithChalk(
        World world,
        BlockPos pos
    ) {
        BigChalkCenterEntity bE = (BigChalkCenterEntity)world.getBlockEntity(pos);
        if (bE.isPowered()) return ActionResult.PASS;
        bE.altTexture = !bE.altTexture;
        bE.save();

        Direction down = bE.attachedTo;
        for (BlockPos aPos : get3x3(pos, down, true)) {
            spawnDust(world, aPos, down);
        }
        world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS);

        return ActionResult.SUCCESS;
    }
    private ActionResult onUseMustCast(
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        boolean disappear
    ) {
        BigChalkCenterEntity bE = (BigChalkCenterEntity)world.getBlockEntity(pos);

        if (bE.isPowered()) return ActionResult.PASS;
        else if (disappear) bE.disappearAfterCasting = true;

        bE.power(true);
        bE.playerWhoTouchedMe = player.getUuid();
        bE.handThatTouchedMe = hand;

        return ActionResult.SUCCESS;
    }
    private ActionResult onUseWithStamp(
        World world,
        BlockPos pos,
        ItemStack stampStack,
        PlayerEntity player
    ) {
        BigChalkCenterEntity bE = (BigChalkCenterEntity)world.getBlockEntity(pos);
        if (bE.isPowered()) return ActionResult.FAIL;
        else if (!NBTHelper.contains(stampStack, Stamp.TAG_PATTERN)) return ActionResult.FAIL;
        bE.stamp(
            HexPattern.fromNBT(NBTHelper.getCompound(stampStack, Stamp.TAG_PATTERN)),
            player.getHorizontalFacing()
        );
        Stamp.playStampSound(world, pos);
        return ActionResult.SUCCESS;
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
        if (stack.isOf(ModItems.CHALK))
            return onUseWithChalk(world, pos);
        else if (stack.isEmpty())
            return onUseMustCast(world, pos, player, hand, false);
        else if (Mutables.isAmel(stack))
            return onUseMustCast(world, pos, player, hand, true);
        else if (stack.getItem() instanceof AmelJar jar)
            return onUseMustCast(world, pos, player, hand, jar.getStored(stack) > 0);
        else if (stack.isOf(ModItems.STAMP))
            return onUseWithStamp(world, pos, stack, player);
        else
            return ActionResult.PASS;
    }
}

package com.luxof.lapisworks.mindinfusions;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.blocks.JumpSlate;
import com.luxof.lapisworks.blocks.ReboundSlate;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MakeSlateBoomerang extends SMindInfusion {
    @Override
    public SMindInfusion setUp(
        BlockPos blockPos,
        CastingEnvironment ctx,
        List<? extends Iota> hexStack,
        VAULT vault
    ) {
        return super.setUp(blockPos, ctx, hexStack, vault);
    }

    @Override public boolean testBlock() {
        return ctx.isEnlightened()
            && (
                ctx.getWorld().getBlockState(blockPos).isOf(ModBlocks.JUMP_SLATE_AM1) ||
                ctx.getWorld().getBlockState(blockPos).isOf(ModBlocks.JUMP_SLATE_AM2) ||
                ctx.getWorld().getBlockState(blockPos).isOf(ModBlocks.JUMP_SLATE_AMETH) ||
                ctx.getWorld().getBlockState(blockPos).isOf(ModBlocks.JUMP_SLATE_LAPIS)
            );
    }

    @Override
    public void mishapIfNeeded() {}

    @Override
    public void accept() {
        ServerWorld world = ctx.getWorld();
        BlockState state = world.getBlockState(blockPos);
        world.setBlockState(
            blockPos,
            ModBlocks.REBOUND_SLATE_1.getDefaultState()
                .with(
                    ReboundSlate.FACING,
                    state.get(JumpSlate.FACING)
                ).with(
                    ReboundSlate.ATTACH_FACE,
                    state.get(JumpSlate.ATTACH_FACE)
                ).with(
                    ReboundSlate.WATERLOGGED,
                    state.get(JumpSlate.WATERLOGGED)
                ).with(
                    ReboundSlate.ENERGIZED,
                    state.get(JumpSlate.ENERGIZED)
                )
        );
    }
}

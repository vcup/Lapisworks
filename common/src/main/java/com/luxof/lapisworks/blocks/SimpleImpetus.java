package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class SimpleImpetus extends BlockAbstractImpetus {
    public SimpleImpetus() {
        super(
            Settings.copy(Blocks.DEEPSLATE_TILES)
                .strength(4f, 4f)
                .pistonBehavior(PistonBehavior.BLOCK)
                .luminance(bs -> bs.get(BlockAbstractImpetus.ENERGIZED) ? 15 : 0)
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new SimpleImpetusEntity(arg0, arg1);
    }

    @Override
    public void onPlaced(
        World world,
        BlockPos pos,
        BlockState state,
        @Nullable LivingEntity placer,
        ItemStack itemStack
    ) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof SimpleImpetusEntity simpleImpetus)) return;
        if (placer == null || !(placer instanceof ServerPlayerEntity plr)) return;
        // can't do if-else above or this statement below gets fucked in an alley
        simpleImpetus.setPlayer(plr);
        simpleImpetus.markDirty();
    }
}

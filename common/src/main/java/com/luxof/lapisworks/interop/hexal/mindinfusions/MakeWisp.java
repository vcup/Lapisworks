package com.luxof.lapisworks.interop.hexal.mindinfusions;

import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import net.minecraft.block.Blocks;

import ram.talia.hexal.common.entities.WanderingWisp;

public class MakeWisp extends SMindInfusion {
    @Override
    public boolean testBlock() {
        return ctx.getWorld().getBlockState(blockPos).isOf(Blocks.AIR);
    }

    @Override
    public void accept() {
        WanderingWisp wisp = new WanderingWisp(ctx.getWorld(), blockPos.toCenterPos());
        ctx.getWorld().spawnEntity(wisp);
    }
}

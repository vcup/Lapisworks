package com.luxof.lapisworks.client.screens;

import com.luxof.lapisworks.init.ModScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class ChalkWithPatternScreenHandler extends ScreenHandler {
    private final BlockPos blockPos;

    public ChalkWithPatternScreenHandler(
        int syncId,
        PlayerInventory plrInv,
        PacketByteBuf buf
    ) {
        this(syncId, buf.readBlockPos());
    }

    public ChalkWithPatternScreenHandler(
        int syncId,
        BlockPos pos
    ) {
        super(ModScreens.CHALK_WITH_PATTERN_SCREEN_HANDLER, syncId);
        this.blockPos = pos;
    };

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockPos.getSquaredDistance(player.getPos()) < 25.0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) { return ItemStack.EMPTY.copy(); }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}

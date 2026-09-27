package com.luxof.lapisworks.inv;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class SMindInfusionSetupInv implements Inventory {
    public final boolean isBlockInfusion;
    private final World world;
    private final CastingEnvironment ctx;
    private final List<? extends Iota> iotaStack;
    private final VAULT vault;

    private final BlockState bState;
    @Nullable private final BlockEntity bEntity;
    private final BlockPos bPos;

    private final Entity entity;

    public SMindInfusionSetupInv(
        BlockPos bp, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
    ) {
        this.isBlockInfusion = true;
        this.world = ctx.getWorld();
        this.ctx = ctx;
        this.iotaStack = iotaStack;
        this.vault = vault;

        bState = world.getBlockState(bp);
        bEntity = world.getBlockEntity(bp);
        bPos = bp;
        entity = null;
    }

    public SMindInfusionSetupInv(
        Entity ent, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
    ) {
        this.isBlockInfusion = false;
        this.world = ctx.getWorld();
        this.ctx = ctx;
        this.iotaStack = iotaStack;
        this.vault = vault;

        bState = null;
        bEntity = null;
        bPos = null;
        entity = ent;
    }

    public SMindInfusion setUp(SMindInfusion infusion) {
        return infusion.setUp(bPos, ctx, iotaStack, vault);
    }

    public World getWorld() { return world; }
    public CastingEnvironment getEnv() { return ctx; }
    public List<? extends Iota> getStack() { return iotaStack; }
    public VAULT grabVAULT() { return vault; }

    @Nullable public BlockState getTargetBlockState() { return bState; }
    @Nullable public BlockPos getTargetBlockPos() { return bPos; }
    @Nullable public BlockEntity getTargetBlockEntity() { return bEntity; }

    @Nullable public Entity getTargetEntity() { return entity; }

    @Override public void clear() {}
    @Override public boolean canPlayerUse(PlayerEntity player) { return false; }
    @Override public ItemStack getStack(int slot) { return ItemStack.EMPTY.copy(); }
    @Override public boolean isEmpty() { return true; }
    @Override public void markDirty() {}
    @Override public ItemStack removeStack(int slot) { return ItemStack.EMPTY.copy(); }
    @Override public ItemStack removeStack(int slot, int amount) { return ItemStack.EMPTY.copy(); }
    @Override public void setStack(int slot, ItemStack stack) {}
    @Override public int size() { return 0; }
}

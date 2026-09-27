package com.luxof.lapisworks.init.Mutables;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.VAULT.VAULT;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class SMindInfusion {
    protected boolean infusingBlock = false;
    protected BlockPos blockPos = null;
    protected Entity entity = null;
    protected CastingEnvironment ctx = null;
    protected List<? extends Iota> hexStack = null;
    protected VAULT vault = null;
    public SMindInfusion() {}

    public SMindInfusion setUp(
        BlockPos blockPos,
        CastingEnvironment ctx,
        List<? extends Iota> hexStack,
        VAULT vault
    ) {
        this.infusingBlock = true;
        this.blockPos = blockPos;
        this.entity = null;
        this.ctx = ctx;
        this.hexStack = hexStack;
        this.vault = vault;
        return this;
    }

    public SMindInfusion setUp(
        Entity entity,
        CastingEnvironment ctx,
        List<? extends Iota> hexStack,
        VAULT vault
    ) {
        this.infusingBlock = false;
        this.blockPos = null;
        this.entity = entity;
        this.ctx = ctx;
        this.hexStack = hexStack;
        this.vault = vault;
        return this;
    }

    public boolean testBlock() { return false; }
    public boolean testEntity() { return false; }
    public void mishapIfNeeded() {}
    public void accept() {}
}

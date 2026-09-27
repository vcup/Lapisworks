package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class RitualMishapEnv extends MishapEnvironment {
    protected final RitualExecutionState ritual;

    protected RitualMishapEnv(ServerWorld world, RitualExecutionState ritual) {
        super(world, null);
        this.ritual = ritual;
    }

    @Override
    public void yeetHeldItemsTowards(Vec3d targetPos) {}

    @Override
    public void dropHeldItems() {}

    @Override
    public void drown() {}

    @Override
    public void damage(float healthProportion) {}

    @Override
    public void removeXp(int amount) {}

    @Override
    public void blind(int ticks) {}
}

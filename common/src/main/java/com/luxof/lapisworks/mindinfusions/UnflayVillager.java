package com.luxof.lapisworks.mindinfusions;

import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.init.ModEntities;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.platform.LapisworksPlatform;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public class UnflayVillager extends SMindInfusion {
    @Override
    public boolean testEntity() {
        return ctx.isEnlightened()
            && entity instanceof VillagerEntity villager
            && IXplatAbstractions.INSTANCE.isBrainswept(villager);
    }

    @Override
    public void accept() {
        VillagerEntity villager = (VillagerEntity)entity;
        // Hex's xplat seam can read the brainswept flag but not clear it, so that goes through the
        // platform seam instead of the old reflective mixin into ForgeXplatImpl.
        LapisworksPlatform.INSTANCE.clearBrainsweep(villager);
        villager.setExperience(0);
        // setting profession to nil makes trades regenerate too. nice.
        villager.setVillagerData(
            villager.getVillagerData()
                .withLevel(1)
                .withProfession(VillagerProfession.NONE)
                .withType(ModEntities.JACK)
        );
        villager.reinitializeBrain(ctx.getWorld());
    }
}

package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.init.ModEntities;
import com.luxof.lapisworks.mixinsupport.JackMinterface;

import net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LoseJobOnSiteLossTask.class)
public class LoseJobOnSiteLossTaskMixin {
    // not even lambdas are safe from my murderous fucking talons!
    @Inject(
        method = "method_47038",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void lapisworks$YouDontHaveToBeTheEmployedGuyWeCanStayHomeAndDoNothingAllDayTogether(
        ServerWorld world,
        VillagerEntity villager,
        long time,
        CallbackInfoReturnable<Boolean> cir
    ) {
        VillagerData villagerData = villager.getVillagerData();
        if (villagerData.getType() != ModEntities.JACK) return;

        VillagerProfession prof = villagerData.getProfession();
        if (
            prof != VillagerProfession.NONE &&
            prof != VillagerProfession.NITWIT &&
            villager.getExperience() <= ((JackMinterface)villager).getDefaultExpForProfession(prof)
        ) {
            villager.setVillagerData(villagerData.withProfession(VillagerProfession.NONE));
            villager.reinitializeBrain(world);
            cir.setReturnValue(true);
        }
    }
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBrainsweep;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep;

import com.llamalad7.mixinextras.sugar.Local;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OpBrainsweep.class, remap = false)
public class OpBrainsweepMixin {
    @Inject(at = @At("TAIL"), method = "execute")
    private void execute(
        List<Iota> args,
        CastingEnvironment ctx,
        CallbackInfoReturnable<SpellAction.Result> cir,
        @Local MobEntity sacrifice,
        @Local BlockPos pos,
        @Local BlockState state
    ) {
        if (!state.isOf(ModBlocks.MIND_BLOCK))
            return;
        MindEntity mind = (MindEntity)ctx.getWorld().getBlockEntity(pos);
        if (mind.mindCompletion < 100f)
            throw new MishapBadBrainsweep(sacrifice, pos);
    }
}

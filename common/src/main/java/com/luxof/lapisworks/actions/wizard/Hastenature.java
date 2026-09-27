package com.luxof.lapisworks.actions.wizard;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import java.util.List;

import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

// name from chatgpt, sorry but i'm bad at naming things
// and Hastenature is a name that kicks ass you have to admit
public class Hastenature extends SpellActionNCT {
    public int argc = 2;
    public boolean requiresEnlightenment = true;

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPos(0);
        int times = stack.getPositiveInt(1);

        BlockState state = ctx.getWorld().getBlockState(pos);
        int buddingAmethystPenalty = state.isOf(Blocks.BUDDING_AMETHYST) ? 3 : 1;

        return new SpellAction.Result(
            new Spell(pos, times),
            MediaConstants.SHARD_UNIT * buddingAmethystPenalty * times,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            times
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final int times;

        public Spell(BlockPos pos, int times) {
            this.pos = pos;
            this.times = times;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ServerWorld world = ctx.getWorld();

            for (int i = 0; i < times; i++) {
                world.getBlockState(pos).randomTick(
                    world,
                    pos,
                    world.random
                );
            }
		}
    }
}

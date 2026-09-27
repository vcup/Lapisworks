package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.MishapThrowerJava.assertIsLinked;
import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;

import java.util.List;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class UnlinkCondensers extends SpellActionNCT {
    public int argc = 2;

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos1 = stack.getBlockPosInRange(0);
        BlockPos pos2 = stack.getBlockPosInRange(1);

        LinkableMediaBlock linkable1 = assertLinkableThere(pos1, ctx);
        assertLinkableThere(pos2, ctx);

        assertIsLinked(linkable1, pos2);

        return new Result(
            new Spell(pos1, pos2),
            MediaConstants.CRYSTAL_UNIT * 3,
            List.of(
                ParticleSpray.burst(pos1.toCenterPos(), 5, 30),
                ParticleSpray.burst(pos2.toCenterPos(), 5, 30)
            ),
            1
        );
    }
    
    public class Spell implements RenderedSpellNCT {
        public final BlockPos pos1;
        public final BlockPos pos2;
        public Spell(BlockPos pos1, BlockPos pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            ServerWorld world = ctx.getWorld();
            ((LinkableMediaBlock)world.getBlockEntity(pos1)).removeLink(pos2);
            ((LinkableMediaBlock)world.getBlockEntity(pos2)).removeLink(pos1);
        }
    }
}

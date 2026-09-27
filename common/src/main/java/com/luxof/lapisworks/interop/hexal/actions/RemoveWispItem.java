package com.luxof.lapisworks.interop.hexal.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.mixinsupport.WispCanIntoItem;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import ram.talia.hexal.common.entities.BaseCastingWisp;

public class RemoveWispItem extends SpellActionNCT {
    public int argc = 1;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BaseCastingWisp wisp = stack.getBaseCastingWispOwnedByThis(0).get();

        return new Result(
            new Spell(wisp),
            dust(2.5),
            List.of(
                ParticleSpray.burst(ctx.mishapSprayPos(), 2, 10),
                ParticleSpray.burst(wisp.getPos(), 2, 10)
            ),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BaseCastingWisp wisp;

        public Spell(BaseCastingWisp wisp) {
            this.wisp = wisp;
        }

        public void cast(CastingEnvironment ctx) {
            Vec3d pos = wisp.getPos();
            ItemScatterer.spawn(
                world,
                pos.x,
                pos.y,
                pos.z,
                ((WispCanIntoItem)wisp).setStack(ItemStack.EMPTY)
            );
        }
    }
}

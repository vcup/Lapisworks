package com.luxof.lapisworks.interop.hexal.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.mixinsupport.WispCanIntoItem;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import java.util.List;

import net.minecraft.entity.ItemEntity;

import ram.talia.hexal.common.entities.BaseCastingWisp;

public class GibWispItem extends SpellActionNCT {
    public int argc = 2;

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BaseCastingWisp wisp = stack.getBaseCastingWispOwnedByThis(0).get();
        ItemEntity item = stack.getItemEntity(1);


        return new Result(
            new Spell(wisp, item),
            dust(2.5),
            List.of(
                ParticleSpray.burst(ctx.mishapSprayPos(), 2, 10),
                ParticleSpray.burst(wisp.getPos(), 2, 10),
                ParticleSpray.burst(item.getPos(), 2, 10)
            ),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BaseCastingWisp wisp;
        public final ItemEntity item;

        public Spell(BaseCastingWisp wisp, ItemEntity item) {
            this.wisp = wisp;
            this.item = item;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            item.setStack(((WispCanIntoItem)wisp).setStack(item.getStack()));
            if (item.getStack().isEmpty()) {
                item.discard();
            }
        }
    }
}

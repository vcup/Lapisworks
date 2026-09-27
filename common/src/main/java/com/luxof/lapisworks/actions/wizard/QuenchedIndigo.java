package com.luxof.lapisworks.actions.wizard;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;

import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.List;

import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class QuenchedIndigo extends SpellActionNCT {
    public int argc = 0;
    public boolean requiresEnlightenment = true;

    private boolean isLapis(ItemStack stack) {
        return stack.isOf(Items.LAPIS_LAZULI);
    }
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        ItemStack items = throwIfNull(
            ctx.getHeldItemToOperateOn(this::isLapis),
            new MishapBadOffhandItem(ItemStack.EMPTY, Items.LAPIS_LAZULI.getName())
        ).stack();

        if (items.getCount() < 2)
            throw new MishapNotEnoughItems(items, 2);

        return new Result(
            new Spell(items),
            0L,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 3, 10 + items.getCount())),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final ItemStack stack;

        public Spell(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(
                contender -> contender == stack,
                new ItemStack(Items.AMETHYST_SHARD, stack.getCount() / 2),
                null
            );
        }
    }
}

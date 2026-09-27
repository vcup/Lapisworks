package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/** you could make like 10 / (6.666.. * 60) = 0.025 dust per second per mind from this
 * so hexal wisp eating isn't overran */
public class MindLiquefaction extends SpellActionNCT {
    public int argc = 1;

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos mindPos = stack.getBlockPos(0);
        ctx.assertPosInRange(mindPos);

        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );
        ItemStack heldStack = throwIfNull(
            ctx.getHeldItemToOperateOn(MindLiquefaction::isMediaHolder),
            MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "rechargeable")
        ).stack();

        return new SpellAction.Result(
            new Spell(blockEntity, heldStack),
            0L,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final MindEntity blockEntity;
        public final ItemStack heldStack;

        public Spell(MindEntity blockEntity, ItemStack heldStack) {
            this.blockEntity = blockEntity;
            this.heldStack = heldStack;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ADMediaHolder media = IXplatAbstractions.INSTANCE.findMediaHolder(this.heldStack);
            media.insertMedia(
                Math.min(
                    media.insertMedia(-1, true),
                    this.blockEntity.getMaxMediaGainFromAbsorption() * (
                        (long)blockEntity.mindCompletion / 100L
                    )
                ),
                false
            );
            this.blockEntity.mindCompletion = 0;
            this.blockEntity.markDirty();
		}
    }

    private static boolean isMediaHolder(ItemStack stack) {
        if (stack == null || stack.isEmpty()) { return false; }
        ADMediaHolder mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
        return mediaHolder == null || !mediaHolder.canRecharge();
    }
}

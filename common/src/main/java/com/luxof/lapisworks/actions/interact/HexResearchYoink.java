package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.storage.ItemScroll;
import at.petrak.hexcasting.server.ScrungledPatternsSave;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.matchShape;
import static com.luxof.lapisworks.LapisworksIDs.FULL_SIMPLE_MIND;
import static com.luxof.lapisworks.LapisworksIDs.GREAT_SCROLL;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.LapisworksIDs.PAT_SCROLL;
import static com.luxof.lapisworks.LapisworksIDs.SCROLL;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

/** yes lmao */
public class HexResearchYoink extends SpellActionNCT {
    public int argc = 1;

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos mindPos = stack.getBlockPos(0);

        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );
        if (blockEntity.mindCompletion < 100f) throw new MishapBadBlock(mindPos, FULL_SIMPLE_MIND);


        ItemStack offHandItems = throwIfNull(
            ctx.getHeldItemToOperateOn(itemStack -> (itemStack.getItem() instanceof ItemScroll)),
            new MishapBadOffhandItem(ItemStack.EMPTY.copy(), SCROLL)
        ).stack();
        ItemScroll scroll = (ItemScroll)offHandItems.getItem();

        Iota iota = scroll.readIota(offHandItems, ctx.getWorld());
        // imagine someone funny says "what if scroll stores number"
        // unlikely to happen but if it does..
        if (!(iota instanceof PatternIota)) throw new MishapBadOffhandItem(offHandItems, PAT_SCROLL);

        HexPattern realPattern = getPerWorldPatternByShape(((PatternIota)iota).getPattern(), ctx);
        if (realPattern == null) {
            throw new MishapBadOffhandItem(
                offHandItems,
                GREAT_SCROLL
            );
        }

        return new SpellAction.Result(
            new Spell(offHandItems, blockEntity, realPattern, scroll),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final ItemStack stack;
        public final MindEntity blockEntity;
        public final HexPattern pattern;
        public final ItemScroll scroll;

        public Spell(ItemStack stack, MindEntity blockEntity, HexPattern pattern, ItemScroll scroll) {
            this.stack = stack;
            this.blockEntity = blockEntity;
            this.pattern = pattern;
            this.scroll = scroll;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.mindCompletion -= 50.0f;
            this.blockEntity.markDirty();
            if (ctx.getWorld().random.nextInt(4) > 2) { return; } // 3/5 chance (number is 0-4 inclusive)
            this.scroll.writeDatum(stack, new PatternIota(this.pattern));
		}
    }

    @Nullable
    public HexPattern getPerWorldPatternByShape(HexPattern pat, CastingEnvironment ctx) {
        ScrungledPatternsSave perWorldPatterns = ScrungledPatternsSave.open(
            ctx.getWorld().getServer().getOverworld()
        );
        Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();

        for (RegistryKey<ActionRegistryEntry> key : registry.getKeys()) {

            var actualStrokeOrderAndPWEntry = perWorldPatterns.lookupReverse(key);
            if (actualStrokeOrderAndPWEntry == null) continue;

            HexPattern actualStrokeOrderPat = HexPattern.fromAngles(
                actualStrokeOrderAndPWEntry.getFirst(),
                actualStrokeOrderAndPWEntry.getSecond().canonicalStartDir()
            );
            if (!matchShape(pat, actualStrokeOrderPat)) continue;

            return actualStrokeOrderPat;

        };
        return null;
    }
}

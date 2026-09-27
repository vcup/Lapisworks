package com.luxof.lapisworks.interop.hexal;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.interop.hexal.blocks.EnchSlipway;
import com.luxof.lapisworks.interop.hexal.blocks.EnchSlipwayEntity;
import com.luxof.lapisworks.interop.hexal.mindinfusions.MakeWisp;
import com.luxof.lapisworks.interop.hexal.mindinfusions.OpenSlipway;

import static com.luxof.lapisworks.LapisworksIDs.OPEN_DIMENSIONAL_RIFT;
import static com.luxof.lapisworks.LapisworksIDs.TURN_MIND_TO_WISP;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;

// Lapisal, then like, maybe Hexaworks or Hexalapis for Hexal's Lapisworks interop?
// coming up with names regardless of if they'll ever be used is fun
public class Lapisal {
    public static EnchSlipway ENCH_SLIPWAY = new EnchSlipway(
        Settings.create()
            .pistonBehavior(PistonBehavior.BLOCK)
            .dropsNothing()
			.strength(-1.0f, 3600000.0f)
            .noCollision()
            .nonOpaque()
            .luminance(state -> 15)
    );
    @SuppressWarnings("null")
    public static BlockEntityType<EnchSlipwayEntity> ENCH_SLIPWAY_ENTITY_TYPE = new BlockEntityType<EnchSlipwayEntity>(
        EnchSlipwayEntity::new,
        ImmutableSet.of(ENCH_SLIPWAY),
        null
    );
    public static void beCool() {
        ModBlocks.pickACropTop("amel_constructs/enchslipway", ENCH_SLIPWAY);
        ModBlocks.dontForgetStockings("ench_slipway_entity_type", ENCH_SLIPWAY_ENTITY_TYPE);
        LapisalPatterns.imRunningOutOfMethodNames();
        Mutables.SMindInfusions.put(OPEN_DIMENSIONAL_RIFT, new OpenSlipway());
        Mutables.SMindInfusions.put(TURN_MIND_TO_WISP, new MakeWisp());
    }
}

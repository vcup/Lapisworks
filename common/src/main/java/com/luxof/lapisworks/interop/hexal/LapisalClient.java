package com.luxof.lapisworks.interop.hexal;

import com.luxof.lapisworks.interop.hexal.blocks.EnchSlipwayRenderer;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class LapisalClient {
    public static void beCoolOnTheClient() {
        BlockEntityRendererFactories.register(
            Lapisal.ENCH_SLIPWAY_ENTITY_TYPE,
            EnchSlipwayRenderer::new
        );
    }
}

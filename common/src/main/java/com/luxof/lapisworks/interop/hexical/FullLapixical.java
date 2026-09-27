package com.luxof.lapisworks.interop.hexical;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.hexical.blocks.Holder;
import com.luxof.lapisworks.interop.hexical.blocks.HolderEntity;

import static com.luxof.lapisworks.interop.hexical.Lapixical.fullStack;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;

import vazkii.patchouli.api.PatchouliAPI;

/** the rest of Lapixical which can only be loaded when Hexical >= 2.0.0 */
public class FullLapixical {
    public static Holder HOLDER = new Holder();
    public static BlockEntityType<BlockEntity> HOLDER_ENTITY_TYPE = new BlockEntityType<BlockEntity>(
        HolderEntity::new,
        ImmutableSet.of(HOLDER),
        null
    );
    public static BlockItem HOLDER_ITEM = ModItems.registerItem(
        "amel_constructs/holder",
        new BlockItem(HOLDER, fullStack)
    );

    public static void initHexical200Interop() {
        Lapisworks.FULL_HEXICAL_INTEROP = true;
        PatchouliAPI.get().setConfigFlag("lapisworks:fulllapixical", true);
        ModBlocks.pickACropTop("amel_constructs/holder", HOLDER);
        ModBlocks.dontForgetStockings("holder_entity_type", HOLDER_ENTITY_TYPE);
    }
}

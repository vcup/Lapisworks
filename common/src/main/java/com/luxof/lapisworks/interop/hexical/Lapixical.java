package com.luxof.lapisworks.interop.hexical;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.hexical.blocks.Cradle;
import com.luxof.lapisworks.interop.hexical.blocks.CradleEntity;
import com.luxof.lapisworks.interop.hexical.blocks.Rod;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.verDifference;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import vazkii.patchouli.api.PatchouliAPI;

/** uhh, Lapixical and Hexisworks? I dunno Hexical is a weird ass name to work with
 * but I like how Hexisworks looks */
public class Lapixical {
    public static Settings fullStack = new Item.Settings().maxCount(64);
    public static Rod ROD = new Rod();
    public static Cradle CRADLE = new Cradle();
    public static BlockEntityType<BlockEntity> CRADLE_ENTITY_TYPE = new BlockEntityType<BlockEntity>(
        CradleEntity::new,
        ImmutableSet.of(CRADLE),
        null
    );
    public static BlockItem ROD_ITEM = new BlockItem(ROD, fullStack);
    public static BlockItem CRADLE_ITEM = new BlockItem(CRADLE, fullStack);

    public static void initHexicalInterop() {
        Integer verDiff = verDifference("hexical", "1.5.0");
        if (verDiff == null) {
            err("How in the actual fuck did I cuck up Hexical interop?");
            verDiff = 0;
        }
        if (verDiff > 0) {
            // the only version after hexical 1.5.0 is 2.0.0
            FullLapixical.initHexical200Interop();
        } else {
            PatchouliAPI.get().setConfigFlag("lapisworks:fulllapixical", false);
        }
        ModBlocks.pickACropTop("rod", ROD);
        ModBlocks.pickACropTop("cradle", CRADLE);
        ModBlocks.dontForgetStockings("cradle_entity_type", CRADLE_ENTITY_TYPE);
        ModItems.registerItem("rod", ROD_ITEM);
        ModItems.registerItem("cradle", CRADLE_ITEM);
    }
}

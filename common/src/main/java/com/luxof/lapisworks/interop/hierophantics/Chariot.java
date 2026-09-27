package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMind;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation.AmalgamationIota;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;

public class Chariot {
    /**
     * Registered on first use rather than at class-init.
     * <p>
     * This writes into Hex Casting's own iota-type registry, which on NeoForge is created from
     * {@code NewRegistryEvent} -- after mod construction -- so touching it from a static initializer
     * throws "Registry is already frozen". {@link #readTarotCards()} is only called from the late
     * init phase, by which point Hex's registry exists.
     */
    private static IotaType<AmalgamationIota> amalgamIotaType = null;

    public static IotaType<AmalgamationIota> amalgamIotaType() {
        if (amalgamIotaType == null) {
            amalgamIotaType = AmalgamationIota.TYPE;
        }
        return amalgamIotaType;
    }

    public static ChariotMind CHARIOT_MIND = new ChariotMind();
    public static BlockEntityType<ChariotMindEntity> CHARIOT_MIND_ENTITY_TYPE =
        beType(ChariotMindEntity::new, CHARIOT_MIND);
    public static BlockItem CHARIOT_MIND_ITEM =
        new BlockItem(CHARIOT_MIND, new Item.Settings().maxCount(64));



    /** Blocks/items: must run before the loader freezes its registries. */
    public static void registerContent() {
        block("chariotmind", CHARIOT_MIND);
        registerBeType("chariotmind", CHARIOT_MIND_ENTITY_TYPE);
        ModItems.registerItem("chariotmind", CHARIOT_MIND_ITEM);
    }

    /** Hex's own registries plus the patterns: must run once Hex has created them. */
    public static void readTarotCards() {
        iotaType("amalgamation", AmalgamationIota.TYPE);
        ChariotPatterns.conjoinDasTwins();
    }



    private static <IOTA extends Iota> IotaType<IOTA> iotaType(
        String name,
        IotaType<IOTA> type
    ) {
        // Register, then return the value: the seam's type parameter is inferred from the registry's
        // element type (a supertype of IOTA), so using its result directly does not typecheck.
        LapisworksRegistry.INSTANCE.registerUnlocked(HexIotaTypes.REGISTRY, id(name), type);
        return type;
    }

    private static <BLOCK extends Block> BLOCK block(
        String name,
        BLOCK block
    ) {
        // Same reasoning as iotaType above.
        LapisworksRegistry.INSTANCE.register(Registries.BLOCK, id(name), block);
        return block;
    }
    // mark, this is *good news*. we can finally be bees.
    private static <BE extends BlockEntity> BlockEntityType<BE> beType(
        BlockEntityFactory<BE> factory,
        Block block
    ) {
        return new BlockEntityType<>(factory, ImmutableSet.of(block), null);
    }

    private static void registerBeType(
        String name,
        BlockEntityType<? extends BlockEntity> beType
    ) {
        LapisworksRegistry.INSTANCE.register(
            Registries.BLOCK_ENTITY_TYPE,
            id(name),
            beType
        );
    }
}

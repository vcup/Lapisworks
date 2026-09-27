package com.luxof.lapisworks.interop.hexical;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;

import com.luxof.lapisworks.interop.hexical.blocks.HolderEntity;

import static com.luxof.lapisworks.LapisworksIDs.HOLDER_MAINHAND;
import static com.luxof.lapisworks.LapisworksIDs.HOLDER_OFFHAND;

import java.util.Optional;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class FullLapixicalClient {
    public static void initTheFullLapixicalClient() {
        ScryingLensOverlayRegistry.addDisplayer(
            FullLapixical.HOLDER,
            (lines, state, pos, observer, world, direction) -> {
                Optional<BlockEntity> bEOpt = world.getBlockEntity(pos, FullLapixical.HOLDER_ENTITY_TYPE);
                if (bEOpt.isEmpty()) return;
                HolderEntity bE = (HolderEntity)bEOpt.get();
                Hand hand = bE.heldInfo.hand();
                if (hand == Hand.MAIN_HAND) {
                    lines.add(
                        new Pair<ItemStack, Text>(
                            new ItemStack(Items.AMETHYST_SHARD),
                            HOLDER_MAINHAND
                        )
                    );
                } else if (hand == Hand.OFF_HAND) {
                    lines.add(
                        new Pair<ItemStack, Text>(
                            new ItemStack(Items.LAPIS_LAZULI),
                            HOLDER_OFFHAND
                        )
                    );
                }
                ItemStack stack = bE.getStack(0);
                if (stack.isEmpty()) return;
                lines.add(
                    new Pair<ItemStack, Text>(
                        stack,
                        Text.translatable(
                            "render.lapisworks.scryinglens.holder.items",
                            stack.getCount(),
                            stack.getName()
                        )
                    )
                );
            }
        );
    }
}

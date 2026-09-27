package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation.AmalgamationIota;

import com.mojang.datafixers.util.Pair;

import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

// is Lapisphantics a cooler name than Chariot?
// hard to decide
public class LapisphanticsClient {

    private static Text inlineifyIotas(NbtList iotas) {
        MutableText buf = null;
        for (NbtElement iota : iotas) {
            Text display = IotaType.getDisplay((NbtCompound)iota);
            buf = buf == null ? display.copy() : buf.append(display);
        }
        return buf;
    }
    
    public static void doMyShitTwin() {
        PlatformEvents.INSTANCE.setBlockRenderLayer(Chariot.CHARIOT_MIND, RenderLayer.getTranslucent());


        ScryingLensOverlayRegistry.addDisplayer(
            Chariot.CHARIOT_MIND,
            (lines, state, pos, observer, world, direction) -> {
                ChariotMindEntity chariotMind = (ChariotMindEntity)world.getBlockEntity(pos);

                lines.add(
                    new Pair<>(
                        new ItemStack(Chariot.CHARIOT_MIND_ITEM),
                        AmalgamationIota.display(chariotMind.storedAmalgamationNbt)
                    )
                );

                NbtList hex = chariotMind.getHexClient();
                lines.add(
                    new Pair<>(
                        new ItemStack(HexItems.CHARGED_AMETHYST),
                        hex.size() > 0 ? inlineifyIotas(hex)
                            : Text.translatable(
                                "render.lapisworks.scryinglens.chalk.no_patterns"
                            )
                    )
                );
            }
        );
    }
}

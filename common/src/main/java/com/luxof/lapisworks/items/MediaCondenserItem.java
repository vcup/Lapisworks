package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;

import com.luxof.lapisworks.init.ModBlocks;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class MediaCondenserItem extends BlockItem {

    public MediaCondenserItem(Settings settings) {
        super(ModBlocks.MEDIA_CONDENSER, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        double media = (double)NBTHelper.getLong(stack, "media", 0L);
        double max = (double)NBTHelper.getLong(stack, "media", 640000L);

        tooltip.add(
            Text.translatable(
                "tooltips.lapisworks.condenser.media",
                Text.literal(String.valueOf(media / 10000.0)).styled(s -> s.withColor(ItemMediaHolder.HEX_COLOR)),
                Text.literal(String.valueOf(max / 10000.0)).styled(s -> s.withColor(ItemMediaHolder.HEX_COLOR)),
                Text.literal(String.valueOf(media / max)).styled(s -> s.withColor(TextColor.fromRgb(MediaHelper.mediaBarColor((long)media, (long)max))))
            ).formatted(Formatting.LIGHT_PURPLE)
        );
    }
}

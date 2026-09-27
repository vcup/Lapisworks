package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.HexBaubleItem;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class FocusNecklace extends Item implements HexBaubleItem, IotaHolderItem {
    public FocusNecklace(Settings settings) { super(settings); }
    
    public static final String TAG_DATA = "data";

    @Override
    public @Nullable NbtCompound readIotaTag(ItemStack stack) {
        return NBTHelper.getCompound(stack, TAG_DATA);
    }

    @Override
    public boolean writeable(ItemStack stack) { return true; }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) { return true; }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        if (iota == null) {
            stack.removeSubNbt(TAG_DATA);
        } else {
            NBTHelper.put(stack, TAG_DATA, IotaType.serialize(iota));
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getHexBaubleAttrs(ItemStack stack) {
        return ImmutableMultimap.of();
    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World pLevel, List<Text> pTooltipComponents,
        TooltipContext pIsAdvanced) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
    }
}

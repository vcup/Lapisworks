package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import at.petrak.hexcasting.common.misc.PatternTooltip;

import com.luxof.lapisworks.blocks.stuff.StampableBE;

import java.util.Optional;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class Stamp extends Item implements IotaHolderItem {
    public static void playStampSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(
            pos,
            SoundEvents.BLOCK_STONE_PLACE,
            SoundCategory.BLOCKS,
            1f,
            3f,
            false
        );
    }

    public Stamp() {
        super(
            new Item.Settings()
                .maxCount(1)
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        ItemStack stack = ctx.getStack();

        if (!(world.getBlockEntity(pos) instanceof StampableBE stampable))
            return ActionResult.FAIL;
        if (!NBTHelper.contains(stack, TAG_PATTERN))
            return ActionResult.FAIL;
        else if (NBTHelper.getCompound(stack, TAG_PATTERN) == null) // HOW
            return ActionResult.FAIL;

        HexPattern pattern = HexPattern.fromNBT(NBTHelper.getCompound(stack, TAG_PATTERN));
        stampable.stamp(pattern, ctx.getHorizontalPlayerFacing());

        playStampSound(world, pos);

        return ActionResult.SUCCESS;
    }


    public static final String TAG_DATA = "data";
    public static final String TAG_PATTERN = "pattern";

    @Override public boolean writeable(ItemStack stack) {
        return true;
    }

    @Override public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return iota == null || iota instanceof PatternIota;
    }

    @Override public @Nullable NbtCompound readIotaTag(ItemStack stack) {
        return NBTHelper.getCompound(stack, TAG_DATA);
    }

    @Override public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        if (!canWrite(stack, iota)) return;
        else if (iota == null) {
            NBTHelper.remove(stack, TAG_DATA);
            NBTHelper.remove(stack, TAG_PATTERN);
        }
        else {
            NBTHelper.put(stack, TAG_DATA, IotaType.serialize(iota));
            NBTHelper.put(stack, TAG_PATTERN, ((PatternIota)iota).getPattern().serializeToNBT());
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (!NBTHelper.contains(stack, TAG_PATTERN))
            return Optional.empty();

        NbtCompound nbt = NBTHelper.getCompound(stack, TAG_PATTERN);
        HexPattern pattern = HexPattern.fromNBT(nbt);

        return Optional.of(new PatternTooltip(pattern, PatternTooltipComponent.SLATE_BG));
    }
}

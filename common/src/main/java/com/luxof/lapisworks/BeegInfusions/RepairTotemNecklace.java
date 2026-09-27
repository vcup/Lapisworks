package com.luxof.lapisworks.BeegInfusions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import com.luxof.lapisworks.VAULT.Flags;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class RepairTotemNecklace extends BeegInfusion {
    private ItemStack stack;

    @Override
    public boolean test() {
        for (var itemInfo : heldInfos) {
            if (itemInfo.stack().isOf(ModItems.TOTEM_NECKLACE)) {
                stack = itemInfo.stack();
                return true;
            }
        }
        return false;
    }

    @Override
    public void mishapIfNeeded() {
        int allowedMax = OperatorUtils.getPositiveInt(hexStack, 0, hexStack.size());
        if (allowedMax < 64) {
            throw new MishapNotEnoughItems(AMEL, allowedMax, 64);
        }
        assertItemAmount(ctx, Mutables::isAmel, AMEL, 64);
        if (stack.getDamage() == 0)
            throw new MishapBadHandItem(
            stack,
            Text.translatable("mishaps.lapisworks.descs.repairable_totem"),
            Text.translatable("mishaps.lapisworks.descs.unrepairable_totem"),
            null
        );
    }

    @Override
    public long getCost() { return MediaConstants.CRYSTAL_UNIT * 20L; }

    @Override
    public void accept() {
        vault.drain(Mutables::isAmel, 64, false, Flags.PRESET_UpToHotbar);
        stack.setDamage(stack.getDamage() - 1);
    }
}

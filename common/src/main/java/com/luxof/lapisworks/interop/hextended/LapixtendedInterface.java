package com.luxof.lapisworks.interop.hextended;

import abilliontrillionstars.hextended.LanisHextendedStaves;
import abilliontrillionstars.hextended.items.ItemExtendedStaff;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.DurabilityPartAmel;

import static com.luxof.lapisworks.Lapisworks.getInfusedAmel;
import static com.luxof.lapisworks.init.ModItems.AMEL_STAFF;
import static com.luxof.lapisworks.init.ModItems.PARTAMEL_STAFF;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

/** Not a literal <code>interface</code>, but an interface to do stuff that'd be added to by Lapixtended. */
public class LapixtendedInterface {

    public static boolean isExtended(Item item) {
        return item instanceof ItemExtendedStaff;
    }

    public static Optional<ItemStack> getDisimbuedFormOfGenericPartAmelWand(ItemStack stack) {
        return stack.isOf(Lapixtended.PARTAMEL_WAND)
            ? Optional.of(new ItemStack(ModItems.AMEL_ITEM, getInfusedAmel(stack)))
            : Optional.empty();
    }

    /** Returns <code>PARTAMEL_STAFF</code> if <code>fromitem</code> is an <code>ItemStaff</code>, otherwise <code>PARTAMEL_WAND</code> if it's an <code>ItemExtendedStaff</code>.
     * <p>returns <code>null</code> if it's neither.
    */
    @Nullable
    public static DurabilityPartAmel getAppropriatePartAmelGeneric(Item forItem) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (forItem instanceof ItemExtendedStaff) {
                return (DurabilityPartAmel)(Lapixtended.PARTAMEL_WAND);
            }
        }
        return forItem instanceof ItemStaff ? PARTAMEL_STAFF : null;
    }

    /** if it's an extended staff, gives you an amel wand. else, amel staff. */
    @Nullable
    public static Item getAppropriateFullAmel(Item ofItem) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (ofItem instanceof ItemExtendedStaff) {
                return Lapixtended.AMEL_WAND;
            }
        }
        return ofItem instanceof ItemStaff ? AMEL_STAFF : null;
    }

    // what is this code's purpose?
    /** gives a staff that is known to have a recipe. (oak, to be precise.)
     * <p>if you pass in an extended staff, gives you an extended oak. else, regular oak.
     * <p>Yes, this means the mod completely and utterly fucking dies if the recipe is disabled.
     * <p>Do I care? */
    public static Item getGenericStaffKnownToHaveRecipe(ItemStaff item) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (item instanceof ItemExtendedStaff) {
                return Registries.ITEM.get(
                    LanisHextendedStaves.id("staff/long/oak")
                );
            }
        }
        return HexItems.STAFF_OAK;
    }
}

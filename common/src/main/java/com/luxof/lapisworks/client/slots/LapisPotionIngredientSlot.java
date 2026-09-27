package com.luxof.lapisworks.client.slots;

import com.luxof.lapisworks.recipes.BreweryRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class LapisPotionIngredientSlot extends Slot {
    public World world;

    public LapisPotionIngredientSlot(World world, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.world = world;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return matches(stack);
    }

    public boolean matches(ItemStack stack) {
        for (BreweryRecipe rec : world.getRecipeManager().listAllOfType(BreweryRecipe.Type.INSTANCE)) {
            if (rec.catalystMatches(stack)) return true;
        }
        return BrewingRecipeRegistry.isValidIngredient(stack);
    }
}

package com.luxof.lapisworks.recipes;

import java.util.List;

import com.luxof.lapisworks.inv.BrewerInv;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BrewItemRec implements BreweryRecipe {
    private final Identifier id;
    private final IngredientWithCount from;
    private final List<IngredientWithCount> catalysts;
    private final ItemStack out;

    public BrewItemRec(
        Identifier id,
        IngredientWithCount from,
        List<IngredientWithCount> catalysts,
        ItemStack out
    ) {
        this.id = id;
        this.from = from;
        this.catalysts = catalysts;
        this.out = out;
    }

    @Override
    public boolean matches(BrewerInv inventory, World world) {
        boolean validInput = catalysts.stream()
            .filter(catalyst -> catalyst.testWithCount(inventory.input))
            .count() > 0;
        boolean atLeastOneValidBrewingInto = inventory.brewingInto.stream()
            .filter(from::testWithCount)
            .count() > 0;
        return validInput && atLeastOneValidBrewingInto;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) { return out.copy(); }

    @Override public Identifier getId() { return id; }
    public IngredientWithCount getFrom() { return from; }
    public ItemStack getOutput() { return out.copy(); }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return BrewItemRecSerializer.INSTANCE; }


    @Override public List<IngredientWithCount> getPossibleCatalysts() { return catalysts; }
    @Override public boolean inputMatches(ItemStack stack) { return from.testWithCount(stack); }
    @Override public ItemStack craft(ItemStack stack) {
        return PotionUtil.setPotion(getOutput(), PotionUtil.getPotion(stack));
    }
}

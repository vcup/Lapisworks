package com.luxof.lapisworks.recipes;

import com.luxof.lapisworks.inv.BrewerInv;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

// convenience and shit
public interface BreweryRecipe extends Recipe<BrewerInv> {
    public static class Type implements RecipeType<BreweryRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    public static class BrewingRecipeRegistryDerivedBreweryRecipe implements BreweryRecipe {

        private final ItemStack ing;
        private final IngredientWithCount ingWC;
        private static final Identifier RECIPE_ID = id("recipes/normalrecipesupport");

        private BrewingRecipeRegistryDerivedBreweryRecipe(ItemStack ing) {
            this.ing = ing;
            this.ingWC = new IngredientWithCount(Ingredient.ofStacks(ing), 1);
        }

        /** returns null if it couldn't find a valid recipe. */
        @Nullable
        public static BrewingRecipeRegistryDerivedBreweryRecipe of(ItemStack input, ItemStack ing) {
            if (input.isEmpty() || ing.isEmpty() || !BrewingRecipeRegistry.hasRecipe(input, ing))
                return null;
            return new BrewingRecipeRegistryDerivedBreweryRecipe(ing);
        }

        @Override public boolean fits(int width, int height) { return true; }
        @Override public Identifier getId() { return RECIPE_ID; }
        @Override public RecipeSerializer<?> getSerializer() { return null; }
        @Override public boolean matches(BrewerInv inventory, World world) { return false; }
        @Override public ItemStack getOutput(DynamicRegistryManager registryManager) {
            return ItemStack.EMPTY;
        }
        @Override public List<IngredientWithCount> getPossibleCatalysts() {
            return List.of(ingWC);
        }
        @Override public boolean inputMatches(ItemStack stack) {
            return BrewingRecipeRegistry.hasRecipe(stack, ing);
        }
        @Override public ItemStack craft(ItemStack stack) {
            // why is this order of arguments specifically flipped?
            return BrewingRecipeRegistry.craft(ing, stack);
        }
    }

    public static BrewingRecipeRegistryDerivedBreweryRecipe findBrewingRecipeRegistryRecipe(
        BrewerInv inv,
        World world
    ) {
        ItemStack catalyst = inv.input;

        for (ItemStack brewingInto : inv.brewingInto) {
            var recipe = BrewingRecipeRegistryDerivedBreweryRecipe.of(
                brewingInto,
                catalyst
            );
            if (recipe != null) return recipe;
        }

        return null;
    }

    public List<IngredientWithCount> getPossibleCatalysts();
    public boolean inputMatches(ItemStack stack);
    public ItemStack craft(ItemStack stack);
    /** <code>craft</code> but it selects the leftmost brewingInto. Don't use this. */
    @Override
    default ItemStack craft(BrewerInv inventory, DynamicRegistryManager registryManager) {
        return craft(inventory.brewingInto.get(0));
    }

    /** attempts to return the amount of this specific catalyst this recipe takes for one batch. */
    @Nullable
    default public Integer getNeededAmountOfCatalyst(ItemStack catalystInQuestion) {
        Optional<IngredientWithCount> maybeIng = getPossibleCatalysts().stream()
            .filter(catalyst -> catalyst.testWithCount(catalystInQuestion))
            .findFirst();
        return maybeIng.isPresent() ? maybeIng.get().getCount() : null;
    }

    /** returns whether or not this catalyst is useable in this recipe. */
    default public boolean catalystMatches(ItemStack catalystInQuestion) {
        return getNeededAmountOfCatalyst(catalystInQuestion) != null;
    }

    /** Basically a macro.
     * <p><code>return inputMatches(stack) ? craft(stack) : stack</code> */
    default public ItemStack craftIfMatchesInput(ItemStack stack) {
        return inputMatches(stack) ? craft(stack) : stack;
    }
    default public RecipeType<?> getType() { return BreweryRecipe.Type.INSTANCE; }
}

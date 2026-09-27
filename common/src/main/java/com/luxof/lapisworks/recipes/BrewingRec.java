package com.luxof.lapisworks.recipes;

import com.luxof.lapisworks.inv.BrewerInv;

import static com.luxof.lapisworks.Lapisworks.potionEquals;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BrewingRec implements BreweryRecipe {
    private final Identifier id;

    private final String input;
    private final String out;
    private final List<IngredientWithCount> catalysts;

    private final Potion potionInput;
    private final Potion potionOut;

    public BrewingRec(
        Identifier id,
        Identifier input,
        List<IngredientWithCount> catalysts,
        Identifier out
    ) { this(id, input.toString(), catalysts, out.toString()); }

    public BrewingRec(
        Identifier id,
        String input,
        List<IngredientWithCount> catalysts,
        String out
    ) {
        this.id = id;

        this.input = input;
        this.out = out;
        this.catalysts = catalysts;

        this.potionInput = Potion.byId(input);
        this.potionOut = Potion.byId(out);
    }

    @Override public Identifier getId() { return this.id; }

    public String getInputStr() { return this.input; }
    public Potion getInputPotion() { return this.potionInput; }
    public String getOutputStr() { return this.out; }
    public Potion getOutputPotion() { return this.potionOut; }


    /** returns a potion with the potion output of this recipe. not splash, not lingering. */
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return PotionUtil.setPotion(new ItemStack(Items.POTION), potionOut);
    }

    @Override
    public boolean matches(BrewerInv inventory, World world) {
        ItemStack catalystStack = inventory.input;

        boolean validCatalyst = this.catalysts.stream()
            .filter(ing -> ing.testWithCount(catalystStack))
            .count() > 0;

        boolean validBrewingInto = inventory.brewingInto.stream()
            .filter(this::inputMatches)
            .count() > 0;

        return validCatalyst && validBrewingInto;
    }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return BrewingRecSerializer.INSTANCE; }


    @Override public List<IngredientWithCount> getPossibleCatalysts() { return this.catalysts; }
    @Override public boolean inputMatches(ItemStack stack) { return potionEquals(input, stack); }
    @Override public ItemStack craft(ItemStack stack) {
        return PotionUtil.setPotion(stack.copy(), potionOut);
    }
}

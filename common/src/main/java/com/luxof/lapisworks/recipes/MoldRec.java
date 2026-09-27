package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;

import com.luxof.lapisworks.inv.HandsInv;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MoldRec implements Recipe<HandsInv> {
    private final Identifier id;
    private final Ingredient input;
    private final Item output;
    private final boolean enabled;

    public static class Type implements RecipeType<MoldRec> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }
    
    protected class MoldRecJsonFormat {
        List<String> requiredmod;
        JsonObject input;
        String output;
    }

    public MoldRec(
        Identifier id,
        Ingredient input,
        Item output,
        boolean enabled
    ) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.enabled = enabled;
    }

    @Override
    public Identifier getId() { return this.id; }
    /** Only returns false when this recipe has been disabled due to required mod(s) not being present. */
    public boolean getEnabled() { return this.enabled; }
    public Ingredient getInput() { return this.input; }
    public Item getOutput() { return this.output; }
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(this.getOutput());
    }

    @Override
    public ItemStack craft(HandsInv inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean matches(HandsInv inventory, World world) {
        return this.enabled ?
            inventory.getHands().stream().map(stack -> this.input.test(stack)).toList().contains(true)
            : false;
    }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return MoldRecSerializer.INSTANCE; }
    @Override public RecipeType<?> getType() { return Type.INSTANCE; }
}

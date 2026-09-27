package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class CollarCombinationRecipeSerializer implements RecipeSerializer<CollarCombinationRecipe> {

    private CollarCombinationRecipeSerializer() {}

    public static CollarCombinationRecipeSerializer INSTANCE = new CollarCombinationRecipeSerializer();

    @Override
    public CollarCombinationRecipe read(Identifier id, JsonObject json) {
        return new CollarCombinationRecipe(id);
    }

    @Override
    public CollarCombinationRecipe read(Identifier id, PacketByteBuf buf) {
        return new CollarCombinationRecipe(id);
    }

    @Override
    public void write(PacketByteBuf buf, CollarCombinationRecipe recipe) {}
}

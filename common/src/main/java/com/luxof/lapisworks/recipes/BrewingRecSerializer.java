package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BrewingRecSerializer implements RecipeSerializer<BrewingRec> {
    private BrewingRecSerializer() {}
    public static final BrewingRecSerializer INSTANCE = new BrewingRecSerializer();

    // i'm just a chill guy.
    @Override
    public BrewingRec read(Identifier id, JsonObject json) {
        List<IngredientWithCount> input = JsonHelper.getArray(json, "input").asList()
            .stream()
            .map(IngredientWithCount::fromJson)
            .toList();

        Identifier from = new Identifier(JsonHelper.getString(json, "from"));
        Identifier to = new Identifier(JsonHelper.getString(json, "to"));
        return new BrewingRec(id, from, input, to);
    }

    @Override
    public BrewingRec read(Identifier id, PacketByteBuf buf) {
        return new BrewingRec(
            id,
            buf.readString(),
            buf.readList(IngredientWithCount::read),
            buf.readString()
        );
    }

    @Override
    public void write(PacketByteBuf buf, BrewingRec recipe) {
        List<IngredientWithCount> catalysts = recipe.getPossibleCatalysts();

        buf.writeString(recipe.getInputStr());
        buf.writeCollection(catalysts, IngredientWithCount::write);
        buf.writeString(recipe.getOutputStr());
    }
}

package com.luxof.lapisworks.recipes;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BrewItemRecSerializer implements RecipeSerializer<BrewItemRec> {
    private BrewItemRecSerializer() {}
    public static final BrewItemRecSerializer INSTANCE = new BrewItemRecSerializer();

    @Override
    public BrewItemRec read(Identifier id, JsonObject json) {
        List<IngredientWithCount> input = JsonHelper.getArray(json, "input").asList()
            .stream()
            .map(IngredientWithCount::fromJson)
            .toList();
        
        IngredientWithCount from = IngredientWithCount.fromJson(json.get("from"));
        JsonObject toObject = json.get("to").getAsJsonObject();
        ItemStack to = new ItemStack(
            Registries.ITEM.get(new Identifier(JsonHelper.getString(toObject, "item"))),
            JsonHelper.getInt(toObject, "count")
        );

        return new BrewItemRec(id, from, input, to);
    }

    @Override
    public BrewItemRec read(Identifier id, PacketByteBuf buf) {
        return new BrewItemRec(
            buf.readIdentifier(),
            IngredientWithCount.read(buf),
            buf.readList(IngredientWithCount::read),
            buf.readItemStack()
        );
    }

    @Override
    public void write(PacketByteBuf buf, BrewItemRec recipe) {
        buf.writeIdentifier(recipe.getId());
        recipe.getFrom().write(buf);
        buf.writeCollection(recipe.getPossibleCatalysts(), IngredientWithCount::write);
        buf.writeItemStack(recipe.getOutput());
    }
}

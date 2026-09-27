package com.luxof.lapisworks.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.recipes.MoldRec.MoldRecJsonFormat;

import java.util.List;

import com.luxof.lapisworks.platform.LapisworksPlatform;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MoldRecSerializer implements RecipeSerializer<MoldRec> {
    private MoldRecSerializer() {}
    public static final MoldRecSerializer INSTANCE = new MoldRecSerializer();

    private static boolean modsLoaded(List<String> mods) {
        for (String id : mods) { if (!LapisworksPlatform.INSTANCE.isModLoaded(id)) return false; }
        return true;
    }

    @Override
    public MoldRec read(Identifier id, JsonObject json) {
        MoldRecJsonFormat recipeJson = new Gson().fromJson(json, MoldRecJsonFormat.class);
        if (recipeJson.input == null) {
            throw new JsonSyntaxException("Unspecified input for Mold Amel recipe: " + id);
        } else if (recipeJson.output == null) {
            throw new JsonSyntaxException("Unspecified output for Mold Amel recipe: " + id);
        }
        if (recipeJson.requiredmod != null &&
            !modsLoaded(recipeJson.requiredmod)) {
            return new MoldRec(
                id,
                Ingredient.ofItems(Items.AIR),
                Items.AIR,
                false
            );
        }
        Ingredient input = Ingredient.fromJson(recipeJson.input);
        Item output = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.output))
            .orElseThrow(() -> new JsonSyntaxException("No such item for output: " + recipeJson.output));

        return new MoldRec(id, input, output, true);
    }

    @Override
    public MoldRec read(Identifier id, PacketByteBuf buf) {
        return new MoldRec(
            id,
            Ingredient.fromPacket(buf),
            Registries.ITEM.get(buf.readIdentifier()),
            buf.readBoolean()
        );
    }

    @Override
    public void write(PacketByteBuf buf, MoldRec recipe) {
        recipe.getInput().write(buf);
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getOutput()));
        buf.writeBoolean(recipe.getEnabled());
    }
}

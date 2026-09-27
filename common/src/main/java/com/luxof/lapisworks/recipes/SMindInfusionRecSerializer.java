package com.luxof.lapisworks.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SMindInfusionRecSerializer implements RecipeSerializer<SMindInfusionRec> {
    private SMindInfusionRecSerializer() {}
    public static SMindInfusionRecSerializer INSTANCE = new SMindInfusionRecSerializer();

    private void jsonExc(Identifier id, String msg) {
        throw new JsonSyntaxException(id.toString() + msg);
    }
    
    public List<IngredientWithCount> getCosts(JsonArray array) {
        List<IngredientWithCount> ret = new ArrayList<>();
        array.forEach(ele -> {
            ret.add(IngredientWithCount.fromJson(ele));
        });
        return List.copyOf(ret);
    }

    @Override
    public SMindInfusionRec read(Identifier id, JsonObject json) {
        Block inputBlock = null;
        Ingredient displayInput = null;
        Block outputBlock = null;
        Item displayOutput = null;
        boolean needsEnlightenment = false;
        try {
            inputBlock = Registries.BLOCK.get(
                new Identifier(JsonHelper.getString(json, "input_block"))
            );
        } catch (NullPointerException e) {
            jsonExc(id, "\"input_item\" is a required ID (of a block) field.");
        }
        try {
            displayInput = Ingredient.fromJson(JsonHelper.getObject(json, "display_input_item"));
        } catch (NullPointerException e) {
            jsonExc(id, "\"display_input_item\" is a required ingredient field.");
        }
        try {
            outputBlock = Registries.BLOCK.get(
                new Identifier(JsonHelper.getString(json, "output_block"))
            );
        } catch (NullPointerException e) {
            jsonExc(id, "\"output_block\" is a required ID (of a block) field.");
        }
        try {
            displayOutput = Registries.ITEM.get(
                new Identifier(JsonHelper.getString(json, "display_output_item"))
            );
        } catch (NullPointerException e) {
            jsonExc(id, "\"display_output_item\" is a required ID (of an item) field.");
        }

        List<IngredientWithCount> costs;
        try {
            costs = getCosts(JsonHelper.getArray(json, "additional_costs"));
            if (costs.size() > 3) throw new RuntimeException(); // trigger the parse exception
        } catch (Exception e) {
            throw new JsonParseException(
                "Improperly formatted \"additional_costs\" (missing, more than 3 or else)"
            );
        }
        try {
            needsEnlightenment = JsonHelper.getBoolean(json, "needs_enlightenment");
        } catch (JsonSyntaxException e) {
            jsonExc(id, "\"needs_enlightenment\" is a required boolean field.");
        }

        return new SMindInfusionRec(
            id, inputBlock, displayInput, outputBlock, displayOutput, needsEnlightenment
        );
    }

    @Override
    public SMindInfusionRec read(Identifier id, PacketByteBuf buf) {
        return new SMindInfusionRec(
            id,
            Registries.BLOCK.get(buf.readIdentifier()),
            Ingredient.fromPacket(buf),
            Registries.BLOCK.get(buf.readIdentifier()),
            Registries.ITEM.get(buf.readIdentifier()),
            buf.readList(IngredientWithCount::read)
        );
    }

    @Override
    public void write(PacketByteBuf buf, SMindInfusionRec recipe) {
        buf.writeIdentifier(Registries.BLOCK.getId(recipe.getInput()));
        recipe.getDisplayInput().write(buf);
        buf.writeIdentifier(Registries.BLOCK.getId(recipe.getOutput()));
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getDisplayOutput()));
        buf.writeCollection(recipe.getAdditionalCosts(), IngredientWithCount::write);
    }
}

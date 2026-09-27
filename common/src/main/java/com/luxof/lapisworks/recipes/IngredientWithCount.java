package com.luxof.lapisworks.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.emi.emi.api.stack.EmiIngredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.jetbrains.annotations.Nullable;

public class IngredientWithCount {
    @Nullable private final Ingredient ingredient;
    @Nullable private final TagKey<Item> tag;
    private final int amount;
    public IngredientWithCount(Ingredient ing, int am) {
        this.ingredient = ing;
        this.tag = null;
        this.amount = am;
    }
    public IngredientWithCount(Ingredient ing) {
        this.ingredient = ing;
        this.tag = null;
        this.amount = 1;
    }
    public IngredientWithCount(TagKey<Item> tag, int am) {
        this.ingredient = null;
        this.tag = tag;
        this.amount = am;
    }
    public IngredientWithCount(TagKey<Item> tag) {
        this.ingredient = null;
        this.tag = tag;
        this.amount = 1;
    }
    public static IngredientWithCount fromJson(JsonObject json) {
        return new IngredientWithCount(
            Ingredient.fromJson(json),
            JsonHelper.getInt(json, "count")
        );
    }

    public static IngredientWithCount fromJson(JsonElement json) {
        return fromJson(json.getAsJsonObject());
    }

    public static IngredientWithCount read(PacketByteBuf buf) {
        if (buf.readBoolean())
            return new IngredientWithCount(Ingredient.fromPacket(buf), buf.readInt());
        else
            return new IngredientWithCount(
                TagKey.of(RegistryKeys.ITEM, buf.readIdentifier()),
                buf.readInt()
            );
        // :anger:
        /*
        return new IngredientWithCount(
            buf.readBoolean() ? Ingredient.fromPacket(buf) : TagKey.of(RegistryKeys.ITEM, buf.readIdentifier()),
            buf.readInt()
        );
        */
    }

    public void write(PacketByteBuf buf) {
        write(buf, this);
    }

    public static void write(PacketByteBuf buf, IngredientWithCount ing) {
        buf.writeBoolean(ing.tag == null);
        if (ing.tag == null)
            ing.ingredient.write(buf);
        else
            buf.writeIdentifier(ing.tag.id());
        buf.writeInt(ing.amount);
    }

    public int getCount() { return this.amount; }

    public boolean test(Item item) { return test(new ItemStack(item)); }
    public boolean test(ItemStack stack) {
        if (tag == null)
            return this.ingredient.test(stack);
        else
            return stack.isIn(tag);
    }
    public boolean testWithCount(ItemStack stack) {
        return test(stack) && stack.getCount() >= getCount();
    }

    public boolean equals(IngredientWithCount other) {
        if (other == null) return false;

        if (tag == null && other.tag == null)
            return ingredient.equals(other.ingredient) && amount == other.amount;
        else if (tag != null && other.tag != null)
            return tag.equals(other.tag) && amount == other.amount;
        else
            return false;
    }

    public EmiIngredient toEmiIng() {
        if (tag == null)
            return EmiIngredient.of(tag, amount);
        else
            return EmiIngredient.of(ingredient, amount);
    }

    private Text getTagName(TagKey<Item> tag) {
        Identifier tagId = tag.id();
        return Text.translatable(
            "tag.item." + tagId.getNamespace() + "." + tagId.getPath().replace("/", ".")
        );
    }
    /** the only way this can return null is if you make this with an <code>Ingredient</code> that has
     * <code>Ingredient.entries.length > 1</code>. */
    @Nullable
    public Text getName() {
        if (tag == null) {
            Ingredient.Entry[] entries = ingredient.entries;
            // literally physically impossible if gotten from JSON, which is the use case :relieved:
            // ^^ "what the hell are you talking about unc"
            // basically it's either { "tag": "thing" } or { "item": "thing" }
            if (entries.length > 1) return null;
            if (entries[0] instanceof Ingredient.TagEntry tagEntry) {
                return getTagName(tagEntry.tag);
            } else if (entries[0] instanceof Ingredient.StackEntry stackEntry) {
                return stackEntry.stack.getName();
            } else {
                throw new IllegalArgumentException("We've got an ingredient here that's neither tag nor stack. This state is unreachable, unless the actual dumbest of shit happens. Still, the exception is here if the actual dumbest of shit does indeed happen.");
            }
        } else {
            return getTagName(tag);
        }
    }
}

package com.luxof.lapisworks.neoforge;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraftforge.common.crafting.conditions.ICondition;

/**
 * Bridges Lapisworks' loader-agnostic condition predicate into Forge's condition system.
 * <p>
 * Forge conditions are types with their own serializer rather than registered predicates (as on
 * Fabric), so the JSON body is carried through to the predicate, which is
 * {@code LapisResourceCons.test}.
 */
public class ForgeJsonCondition implements ICondition {
    private final Identifier id;
    private final JsonObject json;
    private final Predicate<JsonObject> test;

    public ForgeJsonCondition(Identifier id, JsonObject json, Predicate<JsonObject> test) {
        this.id = id;
        this.json = json;
        this.test = test;
    }

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public boolean test(IContext context) {
        return test.test(json);
    }
}

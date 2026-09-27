package com.luxof.lapisworks.interop.emi.recipes;

import com.luxof.lapisworks.recipes.ImbuementRec;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.AMEL_TAG;
import static com.luxof.lapisworks.interop.emi.LapisworksEmiPlugin.IMBUEMENT_CATEGORY;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class EmiImbuementRecipe implements EmiRecipe {
    public static final EmiTexture BACKGROUND = new EmiTexture(
        id("textures/gui/emi/imbuement_bg.png"),
        0,
        0,
        98,
        44
    );
    public static final EmiTexture BACKGROUND_NO_PARTAMEL = new EmiTexture(
        id("textures/gui/emi/imbuement_bg_no_partamel.png"),
        0,
        0,
        98,
        44
    );
    private final Identifier id;
    private final EmiIngredient normal;
    @Nullable private final EmiStack partAmel;
    private final EmiStack fullAmel;

    private final EmiIngredient fullAmelsCostStack;
    private final int fullAmelsCost;

    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EmiImbuementRecipe(ImbuementRec recipe) {
        this.id = recipe.getId();
        this.normal = EmiIngredient.of(recipe.getNormal());
        this.partAmel = recipe.getPartAmel() != null ? EmiStack.of(recipe.getPartAmel()) : null;
        this.fullAmel = EmiStack.of(recipe.getFullAmel());
        this.fullAmelsCost = recipe.getFullAmelsCost();
        this.fullAmelsCostStack = EmiIngredient.of(AMEL_TAG, fullAmelsCost);

        this.inputs = new ArrayList<EmiIngredient>(
            List.of(
                normal, fullAmelsCostStack
            )
        );
        this.outputs = new ArrayList<EmiStack>(List.of(fullAmel));
        if (partAmel != null) {
            inputs.add(
                EmiIngredient.of(
                    Ingredient.ofStacks(partAmel.getItemStack())
                )
            );
            outputs.add(partAmel);
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // hope this works
        if (partAmel != null) {
            widgets.addTexture(BACKGROUND, -4, -4);
            widgets.addSlot(partAmel, 36, 18).drawBack(false).recipeContext(this);
        } else {
            widgets.addTexture(BACKGROUND_NO_PARTAMEL, -4, -4);
        }
        widgets.addSlot(normal, 0, 0).drawBack(false);
        widgets.addSlot(fullAmelsCostStack, 0, 18).drawBack(false);
        widgets.addSlot(fullAmel, 72, 0).recipeContext(this).drawBack(false);
    }

    @Override public EmiRecipeCategory getCategory() { return IMBUEMENT_CATEGORY; }
    @Override public @Nullable Identifier getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Nullable public EmiStack getPartAmel() { return partAmel; }
    public int getAmelCost() { return fullAmelsCost; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 90; }
    @Override public int getDisplayHeight() { return 36; }
}

package com.luxof.lapisworks.interop.emi.recipes;

import com.luxof.lapisworks.recipes.SMindInfusionRec;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.interop.emi.LapisworksEmiPlugin.SMINDINFUSION_CATEGORY;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

public class EmiSMindInfusionRecipe extends BasicEmiRecipe {
    public static final EmiTexture BACKGROUND = new EmiTexture(
        id("textures/gui/emi/smindinfusion_bg.png"),
        0, 0, 64, 96
    );
    
    private final EmiIngredient input;
    private final List<EmiIngredient> additionalCosts;
    private final EmiStack output;

    public EmiSMindInfusionRecipe(
        SMindInfusionRec recipe
    ) {
        super(SMINDINFUSION_CATEGORY, recipe.getId(), 56, 88);
        this.input = EmiIngredient.of(recipe.getDisplayInput());
        this.additionalCosts = recipe.getAdditionalCosts().stream().map(c -> c.toEmiIng()).toList();
        this.output = EmiStack.of(recipe.getDisplayOutput());

        this.inputs = List.of(input);
        this.outputs = List.of(output);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, -4, -4);
        widgets.addSlot(input, 19, 28).drawBack(false);
        for (int i = 0; i < additionalCosts.size(); i++) {
            widgets.addSlot(additionalCosts.get(i), 19*i, 70).drawBack(false);
        }
        widgets.addSlot(output, 19, 0).drawBack(false).recipeContext(this);
    }
    
}

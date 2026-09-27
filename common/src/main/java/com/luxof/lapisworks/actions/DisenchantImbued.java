package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.inv.DisimbuementInv;
import com.luxof.lapisworks.items.shit.BasePartAmel;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;
import com.luxof.lapisworks.recipes.ImbuementRec;

import static com.luxof.lapisworks.Lapisworks.getInfusedAmel;

import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Ingredient.StackEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class DisenchantImbued extends SpellActionNCT {
    public int argc = 0;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {

        ItemStack[] disimbuement = { new ItemStack(Items.AIR) };

        CastingEnvironment.HeldItemInfo performOn = ctx.getHeldItemToOperateOn(
            itemstack -> {
                Optional<ItemStack> possible = getDisimbuementFor(world, itemstack);
                if (possible.isEmpty()) return false;
                disimbuement[0] = possible.get();
                return true;
            }
        );

        if (performOn == null)
            throw new MishapBadOffhandItem(
                ItemStack.EMPTY,
                Text.translatable("mishaps.lapisworks.descs.imbued")
            );

        ItemStack transform = performOn.stack();

        return new Result(
            new Spell(disimbuement[0], transform),
            dust(1),
            List.of(
                transform.getItem() instanceof BasePartAmel
                    ? ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)
                    : ParticleSpray.burst(ctx.mishapSprayPos(), 5, 50)
            ),
            1
        );
    }

    // his code's irreversability was his downfall.
    public Optional<ItemStack> getDisimbuementFor(
        ServerWorld world,
        ItemStack stack
    ) {
        List<ImbuementRec> recipes = getDisimbuementRecipesFor(world, stack);
        Optional<ItemStack> specific = getSpecificDisimbuementFor(world, stack, recipes);
        Optional<ItemStack> general = getGeneralDisimbuementFor(stack, recipes);

        return specific.isPresent()
            ? specific
            : general;
    }

    private Optional<ItemStack> getSpecificDisimbuementFor(
        ServerWorld world,
        ItemStack stack,
        List<ImbuementRec> recipes
    ) {
        if (recipes.size() != 1) return Optional.empty();
        ImbuementRec recipe = recipes.get(0);

        Ingredient ing = recipe.getNormal();

        if (ing.entries.length > 1) return Optional.empty();
        if (!(ing.entries[0] instanceof StackEntry entry)) return Optional.empty();

        return Optional.of(entry.stack.copyWithCount(stack.getCount()));
    }

    private Optional<ItemStack> getGeneralDisimbuementFor(
        ItemStack stack,
        List<ImbuementRec> recipes
    ) {
        if (stack.getItem() instanceof BasePartAmel)
            return Optional.of(new ItemStack(ModItems.AMEL_ITEM, getInfusedAmel(stack)));

        boolean anyRecipes = false;
        int lowestAmelCount = 0;
        for (ImbuementRec recipe : recipes) {
            if (!anyRecipes) {
                anyRecipes = true;
                lowestAmelCount = recipe.getFullAmelsCost();
            } else if (recipe.getFullAmelsCost() < lowestAmelCount)
                lowestAmelCount = recipe.getFullAmelsCost();
        }

        if (!anyRecipes) return Optional.empty();

        return Optional.of(new ItemStack(ModItems.AMEL_ITEM, lowestAmelCount));
    }

    private List<ImbuementRec> getDisimbuementRecipesFor(
        ServerWorld world,
        ItemStack stack
    ) {
        return world.getRecipeManager().getAllMatches(
            ImbuementRec.Type.INSTANCE,
            new DisimbuementInv(List.of(stack, stack)),
            world
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final ItemStack into;
        public final ItemStack stack;

        public Spell(
            ItemStack into,
            ItemStack stack
        ) {
            this.into = into;
            this.stack = stack;
        }

        public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(test -> test == stack, into, null);
        }
    }
}

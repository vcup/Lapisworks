package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;

import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.inv.DisimbuementInv;
import com.luxof.lapisworks.inv.HandsInv;
import com.luxof.lapisworks.items.shit.BasePartAmel;

import static com.luxof.lapisworks.Lapisworks.getInfusedAmel;
import static com.luxof.lapisworks.Lapisworks.setInfusedAmel;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class ImbuementRec implements Recipe<HandsInv> {
    private final Identifier id;
    private final Ingredient normal;
    private final Item partAmel;
    private final Item fullAmel;
    private final int cost;

    public static class Type implements RecipeType<ImbuementRec> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }
    
    protected class ImbuementRecJsonFormat {
        JsonObject normal;
        String partamel;
        String fullamel;
        Integer cost;
    }

    public ImbuementRec(
        Identifier id,
        Ingredient norm,
        Item partAmel,
        Item fullAmel,
        int cost
    ) {
        this.id = id;
        this.normal = norm;
        this.partAmel = partAmel;
        this.fullAmel = fullAmel;
        this.cost = cost;
    }

    @Override
    public Identifier getId() { return this.id; }
    public Ingredient getNormal() { return this.normal; }
    public Item getPartAmel() { return this.partAmel; }
    public Item getFullAmel() { return this.fullAmel; }
    /** the cost (in amel) to make the full amel product from <code>normal</code>.
     * you could also call it the "base cost".
     * <p>for oak staff, it'd return 10 since you need 10 amel to make an amel staff from an oak staff. */
    public int getFullAmelsCost() { return this.cost; }
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(this.getFullAmel());
    }

    private ItemStack withInfusedAmel(Item item, int count, int infused) {
        ItemStack ret = new ItemStack(item, count);
        setInfusedAmel(ret, infused);
        ((BasePartAmel)item).onImbue(ret, infused);
        return ret;
    }
    /** returns Pair<TurnInputInto, DrainThisMuchAmel> or null if it can't infuse a single item. */
    @Nullable
    public Pair<List<ItemStack>, Integer> craft(
        ItemStack item,
        VAULT vault,
        int tryAmel
    ) {
        int costToInfuseOne = cost - getInfusedAmel(item);

        int batches = (int)Math.floor(Math.min(
            (double)item.getCount(),
            (double)tryAmel / Math.max(1.0, (double)costToInfuseOne) // pls no div by zero
        ));
        int amel = batches * costToInfuseOne;
        if (batches < 1 && partAmel == null) return null;

        if (partAmel == null) {
            return new Pair<>(
                List.of(
                    new ItemStack(item.getItem(), item.getCount() - batches),
                    new ItemStack(fullAmel, batches)
                ),
                amel
            );

        } else {
            return new Pair<>(
                List.of(
                    new ItemStack(
                        item.getItem(),
                        item.getCount() - 1
                    ),
                    withInfusedAmel(
                        partAmel,
                        item.getCount() > batches && tryAmel % costToInfuseOne > 0 ? 1 : 0,
                        getInfusedAmel(item) + tryAmel % costToInfuseOne
                    ),
                    new ItemStack(fullAmel, batches)
                ),
                tryAmel / Math.max(costToInfuseOne, 1) + tryAmel % costToInfuseOne
            );
        }
    }

    /** use the other one, not this. */
    @Override
    public ItemStack craft(HandsInv inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return ImbuementRecSerializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    @Override
    public boolean matches(HandsInv inventory, World world) {
        if (inventory instanceof DisimbuementInv) {
            boolean ret = false;
            for (ItemStack stack : inventory.getHands()) {
                ret = ret || stack.getItem() == fullAmel || stack.getItem() == partAmel;
            }
            return ret;
        }


        boolean ret = false;
        for (ItemStack stack : inventory.getHands()) {
            ret = ret || normal.test(stack) || stack.isOf(partAmel);
        }
        return ret;
    }
}

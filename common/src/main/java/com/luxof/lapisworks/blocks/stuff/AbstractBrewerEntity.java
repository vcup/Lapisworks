package com.luxof.lapisworks.blocks.stuff;

import com.luxof.lapisworks.inv.BrewerInv;
import com.luxof.lapisworks.recipes.BreweryRecipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.item.ItemStack.EMPTY;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractBrewerEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    // logic similar to og brewing stand. no guarantee tho
    public int fuel = 0;
    public int brewTime = -1; // -1 means not brewing btw!
    public final int maxFuel;
    public final int maxBrewTime;
    protected List<BreweryRecipe> currentRecipes = new ArrayList<>();
    public BrewerInv inv = new BrewerInv(
        EMPTY.copy(), EMPTY.copy(), List.of(EMPTY.copy(), EMPTY.copy(), EMPTY.copy())
    );

    // these are handles for a screenhandler to use type shit
    public int getBrewerFuel() { return fuel; }
    public int getBrewerBrewTime() { return brewTime; }
    public List<BreweryRecipe> getBrewerRecs() { return currentRecipes; }

    public AbstractBrewerEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState state,
        int maxFuel,
        int maxBrewTime
    ) {
        super(type, pos, state);
        this.maxFuel = maxFuel;
        this.maxBrewTime = maxBrewTime;
    }
    public AbstractBrewerEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState state,
        int maxFuel,
        int maxBrewTime,
        BrewerInv inv
    ) {
        this(type, pos, state, maxFuel, maxBrewTime);
        this.inv = inv;
    }

    /** called from fuelTick(). */
    protected void refillFuelIfNeededAndCan() {
        if (fuel == 0) {
            if (inv.blaze.isEmpty() || inv.blaze.getCount() <= 0) return;
            inv.blaze.decrement(1);
            fuel = maxFuel;
        }
    }
    /** called from fuelTick(). */
    protected void refillBrewTimeIfNeededAndCan() {
        if (brewTime <= 0 && fuel > 0) {
            fuel -= 1;
            brewTime = maxBrewTime;
        }
    }
    protected void fuelTick() {
        if (currentRecipes.size() <= 0) return;
        refillFuelIfNeededAndCan();
        refillBrewTimeIfNeededAndCan();
    }

    protected List<BreweryRecipe> updateRecipes(BrewerInv inv) {
        var recs = new ArrayList<>(
            world.getRecipeManager().getAllMatches(BreweryRecipe.Type.INSTANCE, inv, world)
        );

        // what could possibly go wrong
        // the mfs making five hundred enchanted breweries:
        var generic = BreweryRecipe.findBrewingRecipeRegistryRecipe(inv, world);
        if (generic != null)
            recs.add(generic);

        return recs;
    }

    /** returns the amount of catalyst that recipe required. */
    protected int craft(BreweryRecipe recipe, BrewerInv inv) {
        List<ItemStack> crafted = inv.brewingInto.stream()
            .map(recipe::craftIfMatchesInput)
            .toList();

        inv.brewingInto = new ArrayList<>(crafted);
        return recipe.getNeededAmountOfCatalyst(inv.input);
    }

    /** called from tick() when brewTime == 0. */
    protected void brewPotions() {
        int ingCost = 0;
        for (BreweryRecipe rec : currentRecipes) {
            ingCost = Math.max(ingCost, craft(rec, inv));
        }
        inv.input.decrement(ingCost);
        this.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (brewTime == 0) brewPotions();
        fuelTick();
        currentRecipes = updateRecipes(inv);
        if (currentRecipes.size() <= 0) return;

        if (brewTime >= 0) brewTime -= 1;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inv.writeNbt(nbt);
        nbt.putInt("fuel", fuel);
        nbt.putInt("brewTime", brewTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inv.readNbt(nbt);
        fuel = nbt.getInt("fuel");
        brewTime = nbt.getInt("brewTime");
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override public void clear() { inv.clear(); }
    @Override public boolean canPlayerUse(PlayerEntity player) { return inv.canPlayerUse(player); }
    @Override public ItemStack getStack(int slot) { return inv.getStack(slot); }
    @Override public boolean isEmpty() { return inv.isEmpty(); }
    @Override public ItemStack removeStack(int slot) { return inv.removeStack(slot); }
    @Override public ItemStack removeStack(int slot, int amount) { return inv.removeStack(slot, amount); }
    @Override public void setStack(int slot, ItemStack stack) { inv.setStack(slot, stack); }
    @Override public int size() { return inv.size(); }
    /** returns the color of the potion this stack in brewingInto will be turned into.
     * -1 if not present. */
    public int getColorOfStackPostBrewing(int index) {
        ItemStack stack = inv.brewingInto.get(index);
        for (BreweryRecipe rec : currentRecipes) {
            if (rec.inputMatches(stack)) {
                return PotionUtil.getColor(rec.craft(stack));
            }
        }
        return -1;
    }
    public boolean isBrewing() {
        return fuel > 0 || brewTime >= 0;
    }
}

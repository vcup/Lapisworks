package com.luxof.lapisworks.blocks.entities;

import com.luxof.lapisworks.blocks.stuff.AbstractBrewerEntity;
import com.luxof.lapisworks.client.screens.EnchBrewerScreenHandler;
import com.luxof.lapisworks.init.LapisParticles;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.inv.EnchBrewerInv;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchBrewerEntity extends AbstractBrewerEntity {
    public static final int MAX_FUEL = 15;
    public static final int MAX_BREW_TIME = 200;
    public EnchBrewerEntity(BlockPos pos, BlockState state) {
        // 15 maxfuel because normal brewing stand has it as 20
        // 200 brewtime to brew twice as fast (normal is 400)
        super(
            ModBlocks.ENCH_BREWER_ENTITY_TYPE,
            pos,
            state,
            MAX_FUEL,
            MAX_BREW_TIME,
            new EnchBrewerInv()
        );
    }

    public final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            EnchBrewerEntity brewer = EnchBrewerEntity.this;
            return switch (index) {
                case 0 -> brewer.fuel;
                case 1 -> brewer.brewTime;
                case 2 -> isBrewing() ? 1 : 0;
                case 3 -> brewer.getColorOfStackPostBrewing(0);
                case 4 -> brewer.getColorOfStackPostBrewing(1);
                case 5 -> brewer.getColorOfStackPostBrewing(2);
                case 6 -> brewer.getDustCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            EnchBrewerEntity brewer = EnchBrewerEntity.this;
            switch (index) {
                case 0: brewer.fuel = value;
                case 1: brewer.brewTime = value;
                case 6: brewer.getDust().setCount(value);
                default: return;
            }
        }

        @Override
        public int size() {
            return 6;
        }
    };

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.lapisworks.enchbrewer.name");
    }

    @Override
    public ScreenHandler createMenu(int syncID, PlayerInventory plrInv, PlayerEntity plr) {
        return new EnchBrewerScreenHandler(
            syncID,
            plrInv,
            (EnchBrewerInv)inv,
            propertyDelegate
        );
    }

    @Override
    protected void refillBrewTimeIfNeededAndCan() {
        if (this.brewTime <= 0 && this.fuel > 0 && getDustCount() > 0) {
            this.fuel -= 1;
            this.brewTime = maxBrewTime;
            getDust().decrement(1);
        }
    }

    private int ticksToParticle = 0;
    private int ticksToParticleWhenInactive = 5;
    private int ticksToParticleWhenActive = 1;
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);
        boolean isBrewing = isBrewing();

        if (ticksToParticle > 0) {
            ticksToParticle -= 1;
            return;
        }
        ticksToParticle = isBrewing ? ticksToParticleWhenActive : ticksToParticleWhenInactive;
		double x = pos.getX() + 0.4 + world.random.nextFloat() * 0.2;
		double y = pos.getY() + 0.7 + world.random.nextFloat() * 0.3;
		double z = pos.getZ() + 0.4 + world.random.nextFloat() * 0.2;

        double vX = isBrewing ? 0.05 - world.random.nextDouble() * 0.1 : 0.0;
        double vY = isBrewing ? 0.15 - world.random.nextDouble() * 0.3 : 0.0;
        double vZ = isBrewing ? 0.05 - world.random.nextDouble() * 0.1 : 0.0;
        world.addParticle(LapisParticles.FLOATING_ENCHANT, x, y, z, vX, vY, vZ);
    }


    public ItemStack getDust() { return ((EnchBrewerInv)this.inv).dust; }
    public int getDustCount() { return getDust().isEmpty() ? 0 : getDust().getCount(); }
}

package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.api.casting.ParticleSpray;

import static com.luxof.lapisworks.Lapisworks.getPigmentFromDye;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// ion fw blockentities
public class ConjuredColorable extends Block {
    public static final IntProperty COLOR = IntProperty.of("color", 0, 15);
    public static final EnumProperty<DyeColor> PIGMENT = EnumProperty.of("pigment", DyeColor.class);
    public ConjuredColorable() {
        super(AbstractBlock.Settings.create()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .luminance((state) -> { return 2; })
                .dropsNothing()
                .allowsSpawning((any, any1, any2, any3) -> { return false; })
                .breakInstantly()
                .suffocates((any, any1, any2) -> { return false; })
                .blockVision((any, any1, any2) -> { return true; })
        );
        setDefaultState(getDefaultState().with(COLOR, 0).with(PIGMENT, DyeColor.BLACK));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR, PIGMENT);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) { return; }
        ParticleSpray.cloud(entity.getPos(), 1, 1)
            .sprayParticles(
                world.getServer().getWorld(world.getRegistryKey()),
                getPigmentFromDye(state.get(PIGMENT))
            );
    }
}

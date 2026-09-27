package com.luxof.lapisworks.blocks.entities;

import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.closeEnough;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class LiveJukeboxEntity extends BlockEntity {
    public List<Integer> notes = new ArrayList<>();
    public int frequency = 0;
    public List<Integer> playingNotes = new ArrayList<>(notes);
    public int hasBeenTimeBetweenNotes = 0;

    public LiveJukeboxEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE, pos, state);
    }

    public void startPlaying() {
        this.hasBeenTimeBetweenNotes = 0;
        this.playingNotes = new ArrayList<>(this.notes);
        this.markDirty();
    }

    public static SoundEvent getInstrument(World world, BlockPos pos, LiveJukeboxEntity blockEntity) {
        Instrument instr1 = world.getBlockState(pos.up()).getInstrument();
        if (instr1.isNotBaseBlock()) { return instr1.getSound().value(); }
        else {
            Instrument instr2 = world.getBlockState(pos.down()).getInstrument();
            return instr2.isNotBaseBlock() ? SoundEvents.BLOCK_NOTE_BLOCK_HARP.value() : instr2.getSound().value();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity bE) {
        if (world.isClient) { return; }
        LiveJukeboxEntity blockEntity = (LiveJukeboxEntity)bE;

        if (blockEntity.playingNotes.isEmpty()) {
            blockEntity.hasBeenTimeBetweenNotes = 0;
            return;
        }

        double timePeriod = 1.0 / blockEntity.frequency;

        if (!closeEnough(timePeriod * 20.0, (double)blockEntity.hasBeenTimeBetweenNotes, 0.001)) {
            blockEntity.hasBeenTimeBetweenNotes += 1;
            blockEntity.markDirty();
            return;
        }

        double latestNote = blockEntity.playingNotes.remove(0);
        blockEntity.hasBeenTimeBetweenNotes = 0;

        blockEntity.markDirty();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                ParticleTypes.NOTE,
                (double)pos.getX() + 0.5,
                (double)pos.getY() + 1.2,
                (double)pos.getZ() + 0.5,
                1,
                0.0,
                0.0,
                0.0,
                latestNote / 24.0
            );
        }
        world.playSound(
            null,
            pos,
            getInstrument(world, pos, blockEntity),
            SoundCategory.BLOCKS,
            1.0f,
            (float)(0.5 * Math.pow(2, latestNote / 12.0))
        );
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putIntArray("jukeboxNotes", this.notes);
        nbt.putInt("jukeboxFrequency", this.frequency);
        nbt.putIntArray("jukeboxPlaying", this.playingNotes);
        nbt.putInt("jukeboxTimeBetweenNotesATM", this.hasBeenTimeBetweenNotes);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        int[] notes = nbt.getIntArray("jukeboxNotes");
        this.notes = new ArrayList<>();
        for (int note : notes) { this.notes.add(note); }
        this.frequency = nbt.getInt("jukeboxFrequency");
        int[] playingNotes = nbt.getIntArray("jukeboxPlaying");
        for (int note : playingNotes) { this.playingNotes.add(note); }
        this.hasBeenTimeBetweenNotes = nbt.getInt("jukeboxTimeBetweenNotesATM");
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

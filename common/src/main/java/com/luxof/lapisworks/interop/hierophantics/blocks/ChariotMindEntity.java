package com.luxof.lapisworks.interop.hierophantics.blocks;

import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class ChariotMindEntity extends BlockEntity {
    public NbtCompound storedAmalgamationNbt;
    public ChariotMindEntity(BlockPos pos, BlockState state) {
        super(Chariot.CHARIOT_MIND_ENTITY_TYPE, pos, state);
        storedAmalgamationNbt = new Amalgamation(
            pos,
            state.get(ChariotMind.FOR_NOOBS),
            0,
            0.0,
            List.of()
        ).serialize();
    }

    public Amalgamation getAmalgamation(ServerWorld world) {
        return new Amalgamation(storedAmalgamationNbt, world);
    }
    /** basically yoinks the amalgamation but without the hex. */
    public Amalgamation getAmalgamationClient() {
        return new Amalgamation(storedAmalgamationNbt);
    }
    public NbtList getHexClient() {
        return storedAmalgamationNbt.getList("hex", NbtElement.COMPOUND_TYPE);
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("amalgam", storedAmalgamationNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedAmalgamationNbt = nbt.getCompound("amalgam");
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

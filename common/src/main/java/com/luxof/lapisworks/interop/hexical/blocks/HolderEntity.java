package com.luxof.lapisworks.interop.hexical.blocks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;

import com.luxof.lapisworks.interop.hexical.FullLapixical;

import static com.luxof.lapisworks.Lapisworks.intToHand;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class HolderEntity extends BlockEntity implements Inventory {
    public HeldItemInfo heldInfo = new HeldItemInfo(
        ItemStack.EMPTY.copy(),
        Hand.MAIN_HAND
    );

    public HolderEntity(BlockPos pos, BlockState state) {
        super(FullLapixical.HOLDER_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString(
            "item",
            Registries.ITEM.getId(this.heldInfo.stack().getItem()).toString()
        );
        nbt.putInt("amount", this.heldInfo.stack().getCount());
        nbt.putInt("hand", this.heldInfo.hand().ordinal());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.heldInfo = new HeldItemInfo(
            new ItemStack(
                Registries.ITEM.get(new Identifier(nbt.getString("item"))),
                nbt.getInt("amount")
            ),
            intToHand(nbt.getInt("hand"))
        );
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void clear() {
        this.heldInfo = new HeldItemInfo(ItemStack.EMPTY.copy(), this.heldInfo.hand());
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == 0 ? this.heldInfo.stack() : ItemStack.EMPTY.copy();
    }

    @Override
    public boolean isEmpty() { return this.heldInfo.stack().isEmpty(); }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) return ItemStack.EMPTY.copy();
        ItemStack removed = this.heldInfo.stack();
        this.heldInfo = new HeldItemInfo(ItemStack.EMPTY.copy(), this.heldInfo.hand());
        this.markDirty();
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0) return ItemStack.EMPTY.copy();
        ItemStack removed = this.heldInfo.stack().split(amount);
        this.markDirty();
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0) return;
        this.heldInfo = new HeldItemInfo(stack, this.heldInfo.hand());
        this.markDirty();
    }

    @Override
    public int size() { return 1; }
}

package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;

import com.mojang.authlib.GameProfile;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin2 extends PlayerEntity implements ChariotServerPlayer {
    public ServerPlayerEntityMixin2(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    ArrayList<Amalgamation> amalgamations = new ArrayList<>();
    int usedAmalgamsThisTick = 0;

    public int getUsedAmalgamsThisTick() { return usedAmalgamsThisTick; }
    public void incrementUsedAmalgamsThisTick() { usedAmalgamsThisTick++; }
    @Override @Unique
    public ArrayList<Amalgamation> getFusedAmalgamations() {
        return amalgamations;
    }

    @Shadow
    public abstract ServerWorld getServerWorld();
    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        amalgamations = new ArrayList<>(
            nbt.getList("amalgams", NbtElement.COMPOUND_TYPE)
                .stream()
                .map(compound -> new Amalgamation((NbtCompound)compound, getServerWorld()))
                .toList()
        );
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(
            "amalgams",
            nbtListOf(
                amalgamations.stream()
                    .map(amalgam -> amalgam.serialize())
                    .toList()
            )
        );
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        usedAmalgamsThisTick = 0;
    }
}

package com.luxof.lapisworks.interop.hexal.blocks;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.interop.hexal.Lapisal;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ram.talia.hexal.common.entities.WanderingWisp;

public class EnchSlipwayEntity extends BlockEntity {
    public EnchSlipwayEntity(BlockPos pos, BlockState state) {
        super(Lapisal.ENCH_SLIPWAY_ENTITY_TYPE, pos, state);
    }

    private long nextSpawnTick = 0;
    @Nullable
    protected Integer degrees = null;
    // for your @Override needs
    private Box pushBox = Box.of(this.pos.toCenterPos(), 20.0, 20.0, 20.0);
    protected Box getPushBox() { return pushBox; }
    protected double getPushStrength() { return 0.05; }

    public static void serverTick(ServerWorld sw, BlockPos bp, BlockState bs, EnchSlipwayEntity bE) {
        long tick = sw.getTime();
        Box aabb = Box.of(
            bp.toCenterPos(),
            64.0,
            64.0,
            64.0
        );

        if (tick >= bE.nextSpawnTick && sw.getEntitiesByClass(
            WanderingWisp.class,
            aabb,
            (any) -> true
        ).size() < 40) {
            Random random = bE.getWorld().random;
            bE.nextSpawnTick = tick + (int)Math.round(40 * (1 + random.nextGaussian()));
            WanderingWisp wisp = new WanderingWisp(sw, bp.toCenterPos());
            wisp.addVelocity(
                new Vec3d(0.5-random.nextDouble(), 0.5-random.nextDouble(), 0.5-random.nextDouble())
                    .normalize()
            );
            wisp.setPigment(Lapisworks.getRandomPigment(sw.random));
            sw.spawnEntity(wisp);
            bE.sync();
        }

        Vec3d riftPos = bp.toCenterPos();
        for (
            LivingEntity entity :
            sw.getEntitiesByClass(
                LivingEntity.class,
                bE.getPushBox(),
                ent -> !(ent instanceof AllayEntity)
            )
        ) {
            if (entity instanceof PlayerEntity plr && (plr.isCreative() || plr.isSpectator()))
                continue;
            Vec3d entPos = entity.getPos();
            Vec3d push = entPos.subtract(riftPos).normalize().multiply(
                bE.getPushStrength() * Math.max(1.0, 1.0 / riftPos.subtract(entPos).length())
            );
            entity.addVelocity(push);
            entity.velocityModified = true;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity bE) {
        EnchSlipwayEntity blockEntity = (EnchSlipwayEntity)bE;
        if (!world.isClient) {
            serverTick((ServerWorld)world, pos, state, blockEntity);
        }
    }

    public void sync() {
        this.markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("nextSpawnTick", nextSpawnTick);
        if (degrees != null) nbt.putInt("degrees", degrees);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        nextSpawnTick = nbt.getLong("nextSpawnTick");
        if (nbt.contains("degrees")) degrees = nbt.getInt("degrees");
        else degrees = null;
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

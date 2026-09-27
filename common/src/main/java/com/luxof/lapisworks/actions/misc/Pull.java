package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;

import com.luxof.lapisworks.mixinsupport.AcceleratableEntity;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.LapisworksIDs.APPLY_PULL_FOR_TIME;

import java.util.List;

import io.netty.buffer.Unpooled;

import com.luxof.lapisworks.platform.Networking;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Pull extends SpellActionNCT {
    public int argc = 3;

    public Result executeWithUserdata(
        HexIotaStack stack,
        CastingEnvironment ctx,
        NbtCompound userData
    ) {
        Entity entity = stack.getEntity(0);
        Vec3d pullIn = stack.getVec3(1);
        int timeInTicks = stack.getIntAbove(2, 0);

        NbtCompound alreadyPulled = userData.getCompound("alreadyPulled");
        long extraCost = alreadyPulled.getBoolean(entity.getUuidAsString()) ? dust(1) : 0L;

        return new Result(
            new Spell(timeInTicks, pullIn, entity),
            // 1 dust = 10,000 "media" internally
            extraCost + (long)(pullIn.length()*pullIn.length()*(double)timeInTicks*10000.0),
            List.of(
                ParticleSpray.cloud(ctx.mishapSprayPos(), 10, 10),
                ParticleSpray.burst(entity.getPos(), 3, 50)
            ),
            1L
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final int timeInTicks;
        public final Vec3d pullIn;
        public final Entity entity;

        public Spell(int time, Vec3d pull, Entity ent) {
            timeInTicks = time;
            pullIn = pull;
            entity = ent;
        }

        public CastingImage cast(CastingEnvironment ctx, CastingImage img) {
            img.getUserData()
                .getCompound("alreadyPulled")
                .putBoolean(entity.getUuidAsString(), true);

            if (entity instanceof ServerPlayerEntity splayer) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeDouble(pullIn.x);
                buf.writeDouble(pullIn.y);
                buf.writeDouble(pullIn.z);
                buf.writeInt(timeInTicks);
                Networking.INSTANCE.sendToPlayer(splayer, APPLY_PULL_FOR_TIME, buf);
                return img;
            }
            entity.addVelocity(pullIn);
            ((AcceleratableEntity)entity).applyLingeringAccel(
                pullIn,
                timeInTicks - 1
            );
            return img;
        }
    }
}

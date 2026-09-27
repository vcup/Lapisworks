package com.luxof.lapisworks.actions.wizard.enchsent;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;

import io.netty.buffer.Unpooled;

import com.luxof.lapisworks.platform.Networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;

public class BanishOtherSent extends SpellActionNCT {
    public int argc = 1;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos chosen = stack.getBlockPosInRange(0);

        return new Result(
            new Spell(chosen),
            dust(5),
            List.of(
                ParticleSpray.burst(ctx.mishapSprayPos(), 3, 20),
                ParticleSpray.cloud(chosen.toCenterPos(), 3, 20)
            ),
            1L
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BlockPos chosen;

        public Spell(BlockPos chosen) {
            this.chosen = chosen;
        }
        
        public void cast(CastingEnvironment ctx) {

            ctx.getWorld().getServer().getPlayerManager().getPlayerList().forEach(
                (ServerPlayerEntity ent) -> {
                    Vec3d currSentPos = ((EnchSentInterface)ent).getEnchantedSentinel();

                    if (
                        currSentPos == null ||
                        !BlockPos.ofFloored(currSentPos).equals(chosen)
                    )
                        return;

                    ((EnchSentInterface)ent).setEnchantedSentinel(null, null);

                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeBoolean(true);
                    buf.writeVector3f(new Vector3f());
                    buf.writeDouble(0.0);
                    Networking.INSTANCE.sendToPlayer(ent, SEND_SENT, buf);
                }
            );

        }
    }
}

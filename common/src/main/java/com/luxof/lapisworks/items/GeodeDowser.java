package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;

import com.luxof.lapisworks.Lapisworks;

import static com.luxof.lapisworks.LapisworksIDs.DOWSER_COULDNT_FIND;
import static com.luxof.lapisworks.LapisworksIDs.DOWSER_NOT_ENOUGH_MEDIA;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_TS;
import static com.luxof.lapisworks.LapisworksIDs.GEODE_DOWSER_REQUEST;

import com.mojang.datafixers.util.Pair;

import java.util.List;

import io.netty.buffer.Unpooled;

import com.luxof.lapisworks.platform.Networking;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GeodeDowser extends Item {
    public GeodeDowser(Settings settings) { super(settings); }

    public long getMediaCost() { return MediaConstants.DUST_UNIT * 1; }
    public Pair<BlockPos, BlockPos> getSearchAreaDimensions() {
        return new Pair<BlockPos, BlockPos>(
            new BlockPos(-100, -100, -100),
            new BlockPos(100, 100, 100)
        );
    }

    public void serverHandleDowseResult(ServerPlayerEntity plr, PacketByteBuf buf) {
        if (!buf.readBoolean()) {
            plr.sendMessage(DOWSER_COULDNT_FIND);
            return;
        }

        BlockPos closestBuddingPos = buf.readBlockPos();
        double closestBuddingDistance = buf.readDouble();

        FrozenPigment color = getColorBasedOnDistanceSqr(closestBuddingDistance);
        Vec3d end = closestBuddingPos.toCenterPos();
        Vec3d eyePos = plr.getEyePos();
        Vec3d point = eyePos.add(end.subtract(eyePos).normalize());
        ParticleSpray.burst(point, 5, 30).sprayParticles((ServerWorld)plr.getWorld(), color);
    }

    // i'd use POIs but budding amethyst that's already there won't be registered then (i think)
    // which is a problem for migrators
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.success(user.getStackInHand(hand));

        ServerPlayerEntity plr = (ServerPlayerEntity)user;

        List<ADMediaHolder> sources = MediaHelper.scanPlayerForMediaStuff(plr);
        long left = plr.isCreative() ? 0L : getMediaCost();
        for (ADMediaHolder source : sources) {
            if (left == 0) break;
            long found = MediaHelper.extractMedia(source, left, true, false);
            left -= found;
        }
        if (left > 0) {
            plr.sendMessage(
                Text.translatable(
                    DOWSER_NOT_ENOUGH_MEDIA,
                    getMediaCost() / MediaConstants.DUST_UNIT
                )
            );
            return TypedActionResult.fail(plr.getStackInHand(hand));
        }

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(GEODE_DOWSER_REQUEST);
        buf.writeIdentifier(Registries.BLOCK.getId(Blocks.BUDDING_AMETHYST));
        Networking.INSTANCE.sendToPlayer(plr, DOWSE_TS, buf);
        return TypedActionResult.success(plr.getStackInHand(hand));
    }

    public FrozenPigment getColorBasedOnDistanceSqr(double distanceSqr) {
        // i hope constant-rolling exists in the Java compiler
        if (distanceSqr < 16*16) return Lapisworks.getPigmentFromDye(DyeColor.BLUE);
        else if (distanceSqr < 32*32) return Lapisworks.getPigmentFromDye(DyeColor.LIGHT_BLUE);
        else if (distanceSqr < 48*48) return Lapisworks.getPigmentFromDye(DyeColor.PURPLE);
        else if (distanceSqr < 64*64) return Lapisworks.getPigmentFromDye(DyeColor.PINK);
        else if (distanceSqr < 80*80) return Lapisworks.getPigmentFromDye(DyeColor.ORANGE);
        else if (distanceSqr < 96*96) return Lapisworks.getPigmentFromDye(DyeColor.RED);
        else return Lapisworks.getPigmentFromDye(DyeColor.BLACK);
    }
}

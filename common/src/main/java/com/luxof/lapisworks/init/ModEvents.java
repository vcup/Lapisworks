package com.luxof.lapisworks.init;

import com.luxof.lapisworks.mixinsupport.CollarControllable;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.platform.PlatformEvents;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import com.luxof.lapisworks.collar.LapisCollarAdditions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;

public class ModEvents {
    public static void finallyBeUsed() {
        // Replaces Cardinal Components' PlayerCopyCallback (Fabric-only). The seam's third argument
        // keeps that callback's meaning -- true when the old player survived, i.e. a dimension
        // change -- and the NeoForge implementation inverts Forge's isWasDeath() to match it.
        PlatformEvents.INSTANCE.onPlayerClone((oldPlayer, newPlayer, alive) -> {
            if (!alive) ((LapisworksInterface)newPlayer).copyCrossDeath(oldPlayer);
            else ((LapisworksInterface)newPlayer).copyCrossDimensional(oldPlayer);
        });

        // Neither loader has a chunk-unload hook in common, so each platform registers it through
        // the seam (Fabric API's ServerChunkEvents, Forge's ChunkEvent.Unload).
        PlatformEvents.INSTANCE.onChunkUnload((world, chunk) -> {
            var state = PersistentStateCircleBlockCache.getState(world);
            state.stopCachingChunk(chunk.getPos());
        });

        /*UseEntityCallback.EVENT.register((plr, world, hand, entity, hitRes) -> {
            ItemStack stack = plr.getStackInHand(hand);
            if (
                plr.isSpectator() ||
                (!(entity instanceof LivingEntity living)) ||
                (hitRes != null && hitRes.getType() == HitResult.Type.MISS) ||
                !(entity instanceof CollarControllable collarable) ||
                (plr.isSneaking() ? !stack.isEmpty() : !stack.isOf(COLLAR)) ||
                (entity instanceof TameableEntity tameable && !tameable.isOwner(plr))
            ) return ActionResult.PASS;

            ItemStack alreadyThereCollar = collarable.getCollar();
            plr.setStackInHand(hand, collarable.setCollar(stack));
            if (!alreadyThereCollar.isEmpty())
                LapisCollarAdditions.toAllAdditions(
                    alreadyThereCollar,
                    (addition, id) -> addition.onUnequip(alreadyThereCollar, living)
                );
            if (!stack.isEmpty())
                LapisCollarAdditions.toAllAdditions(
                    alreadyThereCollar,
                    (addition, id) -> addition.onEquip(alreadyThereCollar, living)
                );

            return ActionResult.SUCCESS;
        });*/

        // i'll use events for it.. eventually...
        /*
        AttackEntityCallback.EVENT.register((plr, world, hand, entity, hitRes) -> {
            if (hitRes.getType() == HitResult.Type.MISS) return ActionResult.PASS;
        });
        */
    }
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import robotgiggle.hierophantics.HieroMindCastEnv;

/**
 * Lets a player's fused amalgamations cast along with a spell that refers to that player.
 *
 * <p>In hexcasting 0.11.3 {@link PatternIota#execute} contained the whole cast body, so this used to
 * hook {@code execute}'s HEAD (to remember the stack) plus the {@code new CastResult} instruction
 * (to read the resulting {@code OperationResult}). 0.11.4 moved that body into the new private
 * {@code lookupAndOperate(CastingVM, SpellContinuation, boolean)} and left {@code execute} as a
 * one-line delegate, so the "NEW CastResult" injection point no longer existed anywhere in
 * {@code execute} and the mixin failed to apply ("Critical injection failure ... Scanned 0
 * target(s)"), taking the whole game down with it whenever hierophantics was installed.
 *
 * <p>Both hooks therefore now live on {@code lookupAndOperate}, which is where 0.11.3's {@code
 * execute} body went: HEAD still remembers the pre-cast stack, and RETURN reads the image off the
 * {@link CastResult} that was just built (the same {@code CastingImage} the old injection read off
 * the {@code OperationResult}). Hooking it there - rather than at {@code execute}'s RETURN - keeps
 * parenthesised evaluations covered, exactly as the 0.11.3 code did.
 */
@Mixin(value = PatternIota.class, remap = false)
public class PatternIotaMixin2 {
    private ArrayList<Iota> oldStack = new ArrayList<>();

    @Inject(
        method = "lookupAndOperate",
        at = @At("HEAD")
    )
    public @NotNull void beforeExecute(
        CastingVM vm,
        SpellContinuation continuation,
        boolean inParens,
        CallbackInfoReturnable<CastResult> cir
    ) {
        if (
            vm.getEnv() instanceof HieroMindCastEnv &&
            vm.getImage().getUserData().getBoolean("counterspell_cast")
        ) {
            return;
        }
        oldStack = new ArrayList<>(vm.getImage().getStack());
    }

    @Unique
    private ArrayList<ChariotServerPlayer> getOnlyPlayers(Iterable<Iota> stack) {
        ArrayList<ChariotServerPlayer> players = new ArrayList<>();
        for (Iota iota : stack) {
            if (
                iota instanceof EntityIota entIota &&
                entIota.getEntity() instanceof ChariotServerPlayer player
            )
                players.add(player);

            Iterable<Iota> sub = iota.subIotas();
            if (sub != null)
                players.addAll(getOnlyPlayers(sub));
        }
        return players;
    }

    @Inject(
        method = "lookupAndOperate",
        at = @At("RETURN")
    )
    public @NotNull void afterExecute(
        CastingVM vm,
        SpellContinuation continuation,
        boolean inParens,
        CallbackInfoReturnable<CastResult> cir
    ) {
        if (
            vm.getEnv() instanceof HieroMindCastEnv &&
            vm.getImage().getUserData().getBoolean("counterspell_cast")
        ) {
            return;
        }

        CastingImage newImage = cir.getReturnValue().getNewData();
        // lookupAndOperate builds the CastResult of the mishap path with a null image
        if (newImage == null)
            return;

        var newStack = new ArrayList<>(newImage.getStack());
        newStack.removeAll(oldStack);

        Vec3d thisPos = vm.getEnv().mishapSprayPos();
        Set<ChariotServerPlayer> relevantPlayers = Set.copyOf(getOnlyPlayers(newStack));

        ServerPlayerEntity ohByTheWayThisIsTheCaster = vm.getEnv()
            .getCastingEntity() instanceof ServerPlayerEntity player
                ? player
                : null;
        ServerWorld comingFromWorld = vm.getEnv().getWorld();

        for (ChariotServerPlayer sp : relevantPlayers) {
            ServerPlayerEntity spe = (ServerPlayerEntity)sp;
            if (
                spe == ohByTheWayThisIsTheCaster ||
                spe.getServerWorld() != comingFromWorld
            )
                continue;

            List<Amalgamation> amalgams = sp.getFusedAmalgamations();
            int usedAmalgams = sp.getUsedAmalgamsThisTick();

            for (Amalgamation amalgam : amalgams.subList(usedAmalgams, amalgams.size())) {

                double distance = VALKYRIEN_SKIES_INTEROP
                    ? ValkyrienUtils.distance(comingFromWorld, thisPos, spe.getPos())
                    : thisPos.distanceTo(spe.getPos());

                if (distance > amalgam.range)
                    continue;
                else if (amalgam.willCast())
                    amalgam.cast(spe, thisPos);
                sp.incrementUsedAmalgamsThisTick();
            }
        }
    }
}

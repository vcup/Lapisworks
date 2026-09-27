package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.ConsumeMedia;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.ModPOIs;
import com.luxof.lapisworks.init.Patterns;

import static com.luxof.lapisworks.Lapisworks.exemptFromMediaConsumptionDecrease;
import static com.luxof.lapisworks.Lapisworks.getIdOf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets Simple Impeti respond to the pattern being cast, and scales a grand ritual's media cost.
 *
 * <p>Both hooks used to be {@code @Inject}s with {@code locals = LocalCapture.CAPTURE_FAILHARD}.
 * That makes Mixin build a local-variable table for the injection point, and NeoForge's Mixin/ASM
 * combination rejects the resulting stack frame ("VerifyError: Bad local variable type" at
 * {@code locals[11]}), which aborted Hex Casting's class initialization at startup. Neither hook
 * actually needs the surrounding frame, so both are rewritten against the <em>values</em> they care
 * about:
 * <ul>
 *   <li>the pattern lookup is rewritten where it is produced, and</li>
 *   <li>the media cost is rewritten on the finished {@link CastResult}, so the injection point is
 *       every return rather than the one {@code NEW} the original captured by position. That is
 *       strictly more robust, and equivalent because the side effects are only consumed by the
 *       caller after the method returns.</li>
 * </ul>
 * Verified in-game that both hooks fire and that the media scaling is exact: a
 * {@code ConsumeMedia(amount=1000)} becomes {@code ConsumeMedia(amount=500)} at the default
 * multiplier of 0.5, while non-media side effects pass through untouched.
 */
@Mixin(value = PatternIota.class, remap = false)
public abstract class PatternIotaMixin {
    @Unique private static RegistryKey<ActionRegistryEntry> doNothing = null;

    @Unique
    private static boolean triggerSimpleImpeti(
        HexPattern pat,
        boolean isValid,
        CastingEnvironment ctx
    ) {
        if (doNothing == null) {
            doNothing = Patterns.ARCHON_OF_MEANINGLESSNESS;
        }
        ServerWorld sw = ctx.getWorld();

        return sw.getPointOfInterestStorage().getInCircle(
                any -> any.matchesKey(ModPOIs.SIMP_IMPETUS_KEY),
                BlockPos.ofFloored(ctx.mishapSprayPos()),
                32,
                OccupationStatus.ANY
            ).filter(poi -> {
                BlockPos pos = poi.getPos();
                if (!(sw.getBlockEntity(pos) instanceof SimpleImpetusEntity simpleImpetus))
                    return false;

                return simpleImpetus.tryTrigger(
                    pat.anglesSignature(),
                    isValid,
                    // so it doesn't explode in my face one day
                    ctx.getCastingEntity() instanceof ServerPlayerEntity sp ? sp : null
                );
            })
            .count() > 0;
    }

    @Shadow
    public abstract HexPattern getPattern();

    /**
     * A Simple Impetus may replace the resolved pattern with the archon of meaninglessness, and a
     * big-chalk cast that is exempt from the media-cost decrease clears its marker.
     */
    @ModifyExpressionValue(
        method = "lookupAndOperate",
        at = @At(
            value = "INVOKE",
            // woah, being able to browse bytecode to just copy-paste is so fucking neat
            target = "at/petrak/hexcasting/common/casting/PatternRegistryManifest.matchPattern(Lat/petrak/hexcasting/api/casting/math/HexPattern;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)Lat/petrak/hexcasting/api/casting/PatternShapeMatch;"
        )
    )
    private PatternShapeMatch lapisworks$quickIsThisBigChalkable(
        PatternShapeMatch original,
        CastingVM vm,
        SpellContinuation continuation,
        boolean inParens
    ) {
        PatternShapeMatch lookup = original;
        if (triggerSimpleImpeti(
                getPattern(),
                !(lookup instanceof PatternShapeMatch.Nothing),
                vm.getEnv()
        )) {
            lookup = new PatternShapeMatch.Normal(doNothing);
        }

        NbtCompound userData = vm.getImage().getUserData();
        boolean marked = userData.getBoolean("lapisworks:big_chalk");
        if (marked && exemptFromMediaConsumptionDecrease(getIdOf(lookup)))
            userData.remove("lapisworks:big_chalk");

        return lookup;
    }

    /**
     * Multiplies every {@code ConsumeMedia} amount by the configured grand-ritual multiplier.
     * <p>
     * A fresh list is built rather than mutating the original in place: the side-effect list is
     * Kotlin-owned and may be immutable, so {@code set} on it is not guaranteed to work, whereas
     * producing a new {@link CastResult} through {@code copy} cannot fail that way.
     */
    @ModifyReturnValue(method = "lookupAndOperate", at = @At("RETURN"))
    private @NotNull CastResult lapisworks$doBigChalkIfYea(
        @NotNull CastResult original,
        CastingVM vm,
        SpellContinuation continuation,
        boolean inParens
    ) {
        if (!vm.getImage().getUserData().getBoolean("lapisworks:big_chalk")) return original;

        List<OperatorSideEffect> sideEffects = original.getSideEffects();
        List<OperatorSideEffect> scaled = new ArrayList<>(sideEffects.size());
        boolean changed = false;
        for (OperatorSideEffect sideEffect : sideEffects) {
            if (sideEffect instanceof ConsumeMedia fx) {
                scaled.add(new ConsumeMedia(
                    (long)(fx.getAmount() * LapisConfig.grand_ritual.cost_multiplier)
                ));
                changed = true;
            } else {
                scaled.add(sideEffect);
            }
        }
        if (!changed) return original;

        return original.copy(
            original.getCast(),
            original.getContinuation(),
            original.getNewData(),
            scaled,
            original.getResolutionType(),
            original.getSound()
        );
    }
}

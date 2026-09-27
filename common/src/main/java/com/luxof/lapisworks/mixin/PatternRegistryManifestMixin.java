package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import net.minecraft.util.Identifier;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.getIdOf;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PatternRegistryManifest.class, remap = false)
public abstract class PatternRegistryManifestMixin {

    @WrapMethod(method = {"matchPattern"})
    private static PatternShapeMatch matchPattern(
        HexPattern pat,
        CastingEnvironment environment,
        boolean checkForAlternateStrokeOrders,
        Operation<PatternShapeMatch> og
    ) {
        PatternShapeMatch shapeMatch = og.call(pat, environment, checkForAlternateStrokeOrders);

        Identifier _id = getIdOf(shapeMatch);
        String id = _id == null ? null : _id.toString();
        String sig = pat.anglesSignature();
        if (id == null) return shapeMatch;

        if (chosenFlags.values().contains(null)) {
            err("Why the fuck have the flags not been chosen yet?!");
            return shapeMatch;
        }

        for (String genericId : chosenFlags.keySet()) {
            if (!id.startsWith(genericId)) continue;
            int chosenIdx = chosenFlags.get(genericId);
            if (
                // rust method, anyone?
                !allPerWorldShapePatterns
                    .get(genericId)
                    .get(chosenIdx)
                    .equals(sig)
            )
                return new PatternShapeMatch.Nothing();
            break;
        }

        return shapeMatch;
    }
}

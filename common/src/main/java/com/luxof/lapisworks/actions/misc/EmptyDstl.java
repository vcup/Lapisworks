package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.castRay;

import java.util.List;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

public class EmptyDstl extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = dust(0.01);

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Vec3d start = stack.getVec3InRange(0);
        Vec3d end = stack.getVec3InRange(1);

        Vec3d dir = end.subtract(start).normalize();

        return List.of(new BooleanIota(
            !castRay(
                start.add(dir),
                end.subtract(dir),
                pos -> 
                    new Pair<>(pos, !ctx.getWorld().getBlockState(pos).isOpaque())
            ).getRight()
        ));
    }
}

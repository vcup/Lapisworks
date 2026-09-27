package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

import net.minecraft.util.math.Vec3d;

// where's that gif of kratos getting released from the chains?
public class Hadamard extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Vec3d A = stack.getVec3(0);
        Vec3d B = stack.getVec3(1);
        return List.of(new Vec3Iota(new Vec3d(A.x*B.x, A.y*B.y, A.z*B.z)));
    }
}

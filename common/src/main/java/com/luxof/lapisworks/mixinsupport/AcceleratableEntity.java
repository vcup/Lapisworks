package com.luxof.lapisworks.mixinsupport;

import java.util.List;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Pair;

public interface AcceleratableEntity {
    List<Pair<Vec3d, Integer>> getLingeringAccels();
    void applyLingeringAccel(Vec3d accel, int duration);
}

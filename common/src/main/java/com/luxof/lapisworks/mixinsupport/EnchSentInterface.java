package com.luxof.lapisworks.mixinsupport;

import net.minecraft.util.math.Vec3d;

public interface EnchSentInterface {
    Vec3d getEnchantedSentinel();
    Double getEnchantedSentinelAmbit();
    void setEnchantedSentinel(Vec3d pos, Double ambit);
    boolean shouldBreakSent();
    void breakSent();
}

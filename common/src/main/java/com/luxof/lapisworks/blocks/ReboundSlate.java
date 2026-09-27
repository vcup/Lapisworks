package com.luxof.lapisworks.blocks;

import net.minecraft.util.math.Direction;

public class ReboundSlate extends JumpSlate {
    @Override
    public Direction reverseDirIfNeeded(Direction dir, int strength) {
        return strength < 0 ? dir : dir.getOpposite();
    }
}

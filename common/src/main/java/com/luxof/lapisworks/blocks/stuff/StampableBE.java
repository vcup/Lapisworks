package com.luxof.lapisworks.blocks.stuff;

import at.petrak.hexcasting.api.casting.math.HexPattern;

import net.minecraft.util.math.Direction;

public interface StampableBE {
    public void stamp(HexPattern pattern, Direction horizontalPlayerFacing);
}

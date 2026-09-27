package com.luxof.lapisworks.interop.hexcessible;

import com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport;

import dev.tizu.hexcessible.entries.PatternEntries;

import net.minecraft.util.Identifier;

public class LapiscessibleInterface {
    public static void recalibratePWShapeUnlocksInHexcessible() {
        ((HexcessiblePWShapeSupport)PatternEntries.INSTANCE).calibratePWShapeUnlocks();
    }

    public static void unlockPWShapeInHexcessibleByAdvancement(Identifier advancementId) {
        ((HexcessiblePWShapeSupport)PatternEntries.INSTANCE).unlockPWShapeByAdvancement(advancementId);
    }
}

package com.luxof.lapisworks.interop.hexal;

import at.petrak.hexcasting.api.casting.math.HexDir;

import com.luxof.lapisworks.interop.hexal.actions.GibWispItem;
import com.luxof.lapisworks.interop.hexal.actions.RemoveWispItem;

import static com.luxof.lapisworks.init.Patterns.register;

public class LapisalPatterns {
    public static void imRunningOutOfMethodNames() {
        register("gib_wisp_item", "aqawewewaqaweqqaqqedeae", HexDir.NORTH_WEST, new GibWispItem());
        register("take_away_poor_baby_wisp_candy_like_evil_monster", "dedwqwqwdedwqeedeeqaqdq", HexDir.SOUTH_WEST, new RemoveWispItem());
    }
}

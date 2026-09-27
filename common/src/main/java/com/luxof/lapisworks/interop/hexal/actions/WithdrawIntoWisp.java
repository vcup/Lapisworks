package com.luxof.lapisworks.interop.hexal.actions;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.media.MediaTransferInterface;

import ram.talia.hexal.api.casting.eval.env.WispCastEnv;

// used by the actual Withdraw.java to not cuck up via lazy class loading
// (i prefer not testing lazy class loading with an IF statement of all things lmao)
// i asked if i could if (HEXAL_INTEORP && ctx instanceof WispCastEnv)
// "this is technically unspecified by the JVM spec,
// but it looks like something OpenJDK would be fine with" -pool
// yea nah
public class WithdrawIntoWisp {
    public static boolean isWisp(CastingEnvironment ctx) {
        return ctx instanceof WispCastEnv;
    }
    public static MediaTransferInterface getCastingWispAsMTI(CastingEnvironment ctx) {
        return (MediaTransferInterface)((WispCastEnv)ctx).getWisp();
    }
}

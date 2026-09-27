package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.math.HexDir;

import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamAlert;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamErr;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamGreater;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamHex;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamRange;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetAmalgamation;
import com.luxof.lapisworks.interop.hierophantics.patterns.GetSelfAmalgamsNum;
import com.luxof.lapisworks.interop.hierophantics.patterns.RemoveSelfAmalgam;
import com.luxof.lapisworks.interop.hierophantics.patterns.SetAmalgamAlert;
import com.luxof.lapisworks.interop.hierophantics.patterns.SetAmalgamHex;
import com.luxof.lapisworks.interop.hierophantics.patterns.SetAmalgamRange;

import static com.luxof.lapisworks.init.Patterns.register;

public class ChariotPatterns {
    public static void conjoinDasTwins() {
        register("get_amalgamation", "qaqqaqqa", HexDir.NORTH_EAST, new GetAmalgamation());
        register("get_amalgam_notiflevel", "waqaaqawqqwqqwq", HexDir.NORTH_WEST, new GetAmalgamAlert());
        //                                                    hehe weewee
        register("set_amalgam_notiflevel", "wdeddedweeweewe", HexDir.NORTH_EAST, new SetAmalgamAlert());
        register("get_amalgam_range", "qqqwqwqqqaqqqwqwqqqqaqqa", HexDir.NORTH_EAST, new GetAmalgamRange());
        register("set_amalgam_range", "eeeweweeedeeeweweeeedeed", HexDir.NORTH_WEST, new SetAmalgamRange());
        register("get_amalgam_greater", "qwewqaqwewqqaqqa", HexDir.NORTH_EAST, new GetAmalgamGreater());
        register("get_self_amalgams_num", "qwedewqqaqqa", HexDir.NORTH_EAST, new GetSelfAmalgamsNum());
        register("remove_self_amalgam", "edeedeed", HexDir.NORTH_WEST, new RemoveSelfAmalgam());
        register("get_amalgam_err", "eqaqqaqqadqeqaqqaqqad", HexDir.NORTH_WEST, new GetAmalgamErr());
        register("get_amalgam_hex", "qaqqaqqadaqdee", HexDir.NORTH_EAST, new GetAmalgamHex());
        register("set_amalgam_hex", "edeedeedadeaqq", HexDir.NORTH_WEST, new SetAmalgamHex());
    }
}

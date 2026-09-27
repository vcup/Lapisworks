package com.luxof.lapisworks.init;

/** Usually you don't fuck with this, as making a new GenericEnchant takes care of this for you. */
public class EnchantCountKeeper {
    private static int tally = -1;
    /** Doesn't "register" shit, just gives you your new enchantment's index. */
    public static int registerMyEnchantment() {
        tally++;
        return tally;
    }
}

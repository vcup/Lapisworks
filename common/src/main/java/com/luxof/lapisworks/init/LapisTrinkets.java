package com.luxof.lapisworks.init;

import com.luxof.lapisworks.platform.Accessories;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

/**
 * Declares Lapisworks' wearable item.
 * <p>
 * Only the collar is registered here: the necklaces implement Hex Casting's own
 * {@code HexBaubleItem}, and Hex Casting already registers every such item with whichever accessory
 * API the loader provides. The actual registration is loader-specific, so it goes through
 * {@link Accessories}; the slot name is loader-agnostic ("necklace" is Trinkets'
 * {@code chest/necklace} and Curios' {@code necklace}).
 */
public class LapisTrinkets {
    public static final String NECKLACE_SLOT = "necklace";

    public static void startFeelingCute() {
        Accessories.INSTANCE.registerWearable(COLLAR, NECKLACE_SLOT, COLLAR);
    }
}

package com.luxof.lapisworks.init.Mutables;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.VAULT.VAULT;

import java.util.List;

/** Kind of a singleton? You can make more instances but I REALLY don't recommend you do.
 * Use <code>setUp</code> before every usage of the recipe.
 * <p>See <code>EnhanceEnchantedBook.java</code> for an example on how to make your own BeegInfusion. */
public class BeegInfusion {
    protected List<HeldItemInfo> heldInfos = null;
    protected CastingEnvironment ctx = null;
    protected List<? extends Iota> hexStack = null;
    protected VAULT vault = null;
    public BeegInfusion() {}

    public BeegInfusion setUp(
        List<HeldItemInfo> heldInfos,
        CastingEnvironment ctx,
        List<? extends Iota> hexStack,
        VAULT vault
    ) {
        this.heldInfos = heldInfos;
        this.ctx = ctx;
        this.hexStack = hexStack;
        this.vault = vault;
        return this;
    }

    public boolean test() { return false; }
    public void mishapIfNeeded() {}
    public long getCost() { return 0L; }
    public void accept() {}
}

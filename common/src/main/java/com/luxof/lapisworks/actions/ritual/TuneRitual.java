package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.MishapThrowerJava.assertInOneTimeRitual;

import java.util.List;

public class TuneRitual extends SpellActionNCT {
    public int argc = 1;

    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        assertInOneTimeRitual(ctx);
        Iota iota = stack.get(0);

        return new Result(
            new Spell(iota),
            charged(1),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 3, 20)),
            1
        );
    }

    public static final class Spell implements RenderedSpellNCT {
        public final Iota iota;
        public Spell(Iota iota) { this.iota = iota; }

        public void cast(CastingEnvironment ctx) {
            ((RitualCastEnv)ctx).ritual().setTunedFrequency(iota);
        }
    }
}

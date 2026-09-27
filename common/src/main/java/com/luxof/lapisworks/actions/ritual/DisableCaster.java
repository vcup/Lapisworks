package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.MishapThrowerJava.assertInOneTimeRitual;

import java.util.List;

public class DisableCaster extends SpellActionNCT {
    public int argc = 0;

    // i feel like i have ADHD and am on Adderall
    // "is this how normal mfs feel?"
    // except instead of focus it's not having sore hands every time i make a pattern
    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment env) {
        return new Result(
            new Spell(assertInOneTimeRitual(env)),
            0L,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 5)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final OneTimeRitualExecutionState ritual;
        public Spell(OneTimeRitualExecutionState ritual) {
            this.ritual = ritual;
        }
        public void execute(CastingEnvironment env) {
            ritual.disableCaster();
        }
    }
}

package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;

// "too late" (lazy) to make this use EntityAttributeModifiers instead
public class MoarAttr extends SpellActionNCT {
    public int argc = 2;

    // i always keep my shit public in case someone needs to do something cursed
    public EntityAttribute modifyAttribute;
    public double limitModifier;
    public double limitOffset; // "only give mobs +60 +2xbase hp at max!"
    public double attrCompensateMult; // base plr speed is 0.1? set this to 10 or smth.
    public int expendedAmelModifier;
    public boolean playerOnly;

    public MoarAttr(
        EntityAttribute modifyAttribute,
        double limitModifier,
        double limitOffset,
        double attrCompensateMult,
        int expendedAmelModifier,
        boolean playerOnly
    ) {
        this.modifyAttribute = modifyAttribute;
        this.limitModifier = limitModifier;
        this.limitOffset = limitOffset;
        this.attrCompensateMult = attrCompensateMult;
        this.expendedAmelModifier = expendedAmelModifier;
        this.playerOnly = playerOnly;
    }

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        LivingEntity entity = playerOnly
            ? args.getPlayer(0)
            : args.getLivingEntityButNotArmorStand(0);
        double count = args.getPositiveDouble(1);


        double currentCombined = getCurrentAttrValue(entity);
        double currentJuiced = getCurrentJuiceValue(entity);

        double defaultVal = currentCombined - currentJuiced;
        // -1 since this is the limit on JUICING not TOTAL ATTRIBUTE VALUE
        double limit = defaultVal * (this.limitModifier - 1) + this.limitOffset;
        double setTo = Math.min(
            count / attrCompensateMult,
            limit
        );

        int diff = (int)Math.abs(Math.ceil((setTo - currentJuiced) * this.expendedAmelModifier));
        int expendedAmel = Math.max(diff, 0);
        assertItemAmount(ctx, Mutables::isAmel, AMEL, expendedAmel);

        return new SpellAction.Result(
            new Spell(entity, setTo, expendedAmel),
            MediaConstants.SHARD_UNIT * diff,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LivingEntity entity;
        public double setTo;
        public final int expendedAmel;

        public Spell(
            LivingEntity entity,
            double setTo,
            int expendedAmel
        ) {
            this.entity = entity;
            this.setTo = setTo;
            this.expendedAmel = expendedAmel;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ((GetVAULT)ctx).grabVAULT().drain(
                Mutables::isAmel,
                expendedAmel,
                false,
                Flags.PRESET_UpToHotbar
            );
            if (setTo < 0) {
                err("Lapisworks just shat it's pants and setTo was negative. Trying to fix this for you by abs()-ing setTo and clearing your juiced attribute! Hopefully this doesn't happpen in the future.");
                setTo = Math.abs(setTo);
                ((LapisworksInterface)this.entity).setJuicedAttrSpecifically(modifyAttribute, 0);
            }
            ((LapisworksInterface)this.entity).setAmountOfAttrJuicedUpByAmel(
                modifyAttribute,
                this.setTo
            );
		}
    }
    
    private double getCurrentAttrValue(LivingEntity entity) {
        return entity.getAttributes()
            .getCustomInstance(modifyAttribute)
            .getBaseValue();
    }

    private double getCurrentJuiceValue(LivingEntity entity) {
        return ((LapisworksInterface)entity).getAmountOfAttrJuicedUpByAmel(modifyAttribute);
    }
}

package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;

import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public abstract class PlayerBasedCastEnvMixin extends CastingEnvironment {
	protected PlayerBasedCastEnvMixin(ServerWorld world) { super(world); }

	@Shadow
	public abstract LivingEntity getCastingEntity();

	@Inject(at = @At("HEAD"), method = "canOvercast", cancellable = true)
	protected void canOvercast(CallbackInfoReturnable<Boolean> cir) {
		double amountOfMaxHPJuicedUp = ((LapisworksInterface)getCastingEntity())
			.getAmountOfAttrJuicedUpByAmel(EntityAttributes.GENERIC_MAX_HEALTH);

		if (amountOfMaxHPJuicedUp > 0) cir.setReturnValue(false);
	}

	@Unique
	private boolean isVecInRangeOfEnchSent(Vec3d vec) {
		if (this.getCastingEntity() == null) { return false; }
		else if (!(this.getCastingEntity() instanceof PlayerEntity)) { return false; }

		EnchSentInterface player = (EnchSentInterface)this.getCastingEntity();
		Vec3d sentPos = player.getEnchantedSentinel();
		Double sentAmbit = player.getEnchantedSentinelAmbit();

		if (sentPos == null) return false;
		if (vec.squaredDistanceTo(sentPos) > sentAmbit*sentAmbit) return false;

		return true;
	}

	// really should let this apply to spell circles too huh.
	@Unique
	public boolean isVecInRangeOfOrb(Vec3d vec) {
		List<HeldItemInfo> heldItems = ((GetStacks)this).getHeldStacks();
		for (HeldItemInfo heldItem : heldItems) {
			if (!(heldItem.stack().getItem() instanceof AmelOrb orb)) continue;

			Vec3d ambitOrigin = orb.getPlaceInAmbit(heldItem.stack());
			if (ambitOrigin == null) continue;

			double ambit = orb.ambitRadius;
			if (vec.distanceTo(ambitOrigin) <= ambit) return true;
		}
		return false;
	}

	@Inject(at = @At("HEAD"), method = "isVecInRangeEnvironment", cancellable = true)
	public void isVecInRangeEnvironment(Vec3d vec, CallbackInfoReturnable<Boolean> cir) {
		boolean res = isVecInRangeOfEnchSent(vec) || isVecInRangeOfOrb(vec);
		if (res) { cir.setReturnValue(res); }
	}
}

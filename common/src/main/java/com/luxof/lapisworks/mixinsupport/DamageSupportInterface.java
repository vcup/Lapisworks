package com.luxof.lapisworks.mixinsupport;

import java.util.List;

import com.luxof.lapisworks.items.GoldSword;
import com.luxof.lapisworks.items.IronSword;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface DamageSupportInterface {
	// i thought i needed this cuz i needed to mixin into PlayerEntity as well
	// but PlayerEntity's damage() function already calls LivingEntity's damage()
	// function
	// oh well i guess i'll keep it then
	// i could use events for this but fuuuuuuuuck
    default boolean damageHelper(
		DamageSource source,
		float amount,
		LivingEntity thisObject,
		List<Integer> enchantments
	) {
		if (source.getAttacker() == null) { return true; }
		else if (!(source.getAttacker() instanceof LivingEntity)) { return true; }
		Hand hand = thisObject.getActiveHand();
		ItemStack stack = thisObject.getStackInHand(hand);
		LivingEntity attacker = (LivingEntity)source.getAttacker();
		ItemStack attackerStack = attacker.getStackInHand(attacker.getActiveHand());
		boolean attackerHoldingAxe = attackerStack.getItem() instanceof AxeItem;
		
		
		if (attackerStack.getItem() instanceof GoldSword) {
			thisObject.addStatusEffect(
				new StatusEffectInstance(StatusEffects.POISON, 20 * 10) // 10 secs * 20 ticks (hehe secs)
			);
			stack.damage(9, attacker, (any) -> {});
			return true;
		}
		else if (!(stack.getItem() instanceof IronSword)) { return true; }
		else if (!thisObject.isUsingItem()) { return true; }


		if (thisObject.getItemUseTime() <= 5) {
			// PARRY
			attacker.addVelocity(
				attacker.getPos().subtract(thisObject.getPos()).normalize().multiply(1.5)
			);
			attacker.damage(
				thisObject.getWorld().getDamageSources().indirectMagic(thisObject, thisObject),
				((IronSword)stack.getItem()).getAttackDamage() * 0.5F
			);
			if (attackerStack.isEmpty()) { return true; }
			stack.damage(-20, attacker, (any) -> {});
			attackerStack.damage(attackerHoldingAxe ? 100 : 20, thisObject, (any) -> {
				stack.damage(-100, attacker, (any2) -> {});
				attacker.damage(
					thisObject.getWorld().getDamageSources().indirectMagic(thisObject, thisObject),
					((IronSword)stack.getItem()).getAttackDamage() * 1.0F
				);
			});
			if (attacker instanceof PlayerEntity) {
				((PlayerEntity)attacker).getItemCooldownManager().set(attackerStack.getItem(), 40);
			}
			return true;


		} else if (attackerHoldingAxe) {
			stack.damage(20, attacker, (any) -> {
				thisObject.damage(
					thisObject.getWorld().getDamageSources().mobAttack(attacker),
					((AxeItem)attackerStack.getItem()).getAttackDamage() * 1.5F
				);
			});
			thisObject.addVelocity(
				thisObject
					.getPos()
					.subtract(attacker.getPos())
					.normalize()
					.multiply(2)
			);
			if (thisObject instanceof PlayerEntity) {
				((PlayerEntity)thisObject).getItemCooldownManager().set(stack.getItem(), 20);
			}
			return true;
		} else {
			stack.damage(1, attacker, (any) -> {});
		}

		return true;
	}
}

package com.luxof.lapisworks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.luxof.lapisworks.mixinsupport.AcceleratableEntity;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface.AllEnchantments;
import com.luxof.lapisworks.interop.hexical.EntityDimensionsButTheHitboxIsDown;

import static com.luxof.lapisworks.Lapisworks.deserializeVec3d;
import static com.luxof.lapisworks.Lapisworks.isInCradle;
import static com.luxof.lapisworks.Lapisworks.nbtListOf;
import static com.luxof.lapisworks.Lapisworks.serializeVec3d;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.Pair;
import net.minecraft.registry.tag.DamageTypeTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements AcceleratableEntity {

    @Unique
    private ArrayList<Pair<Vec3d, Integer>> lingeringAccels = new ArrayList<>();

    @Override @Unique
    public List<Pair<Vec3d, Integer>> getLingeringAccels() {
        return List.copyOf(lingeringAccels);
    }

    @Override @Unique
    public void applyLingeringAccel(Vec3d accel, int duration) {
        lingeringAccels.add(new Pair<>(accel, duration));
    }

    @Shadow
    public abstract World getWorld();
    @Shadow
    public abstract void setVelocity(Vec3d velocity);
    @Shadow
    public abstract Vec3d getVelocity();
    @Shadow
    public abstract void addVelocity(double x, double y, double z);
    @Shadow
    public boolean velocityModified;

    @Inject(at = @At("TAIL"), method = "tick")
    public void lapisworks$tickPullSpellEffects(CallbackInfo ci) {

        if (
            (Object)this instanceof PlayerEntity player &&
            (
                player.isSpectator() ||
                (player.isCreative() && player.getAbilities().flying)
            )
        )
            return;

        ArrayList<Pair<Vec3d, Integer>> newLingeringAccels = new ArrayList<>(lingeringAccels);

        for (int i = lingeringAccels.size() - 1; i >= 0; i--) {
            Pair<Vec3d, Integer> lingeringAccel = lingeringAccels.get(i);

            Vec3d pull = lingeringAccel.getLeft();
            int newTicksLeft = lingeringAccel.getRight() - 1;

            if (newTicksLeft > 0)
                newLingeringAccels.set(i, new Pair<>(pull, newTicksLeft));
            else
                newLingeringAccels.remove(i);

            setVelocity(getVelocity().add(pull));
            if ((Object)this instanceof PlayerEntity)
                if (getWorld().isClient)
                    velocityModified = true;
            else
                velocityModified = true;
        }

        lingeringAccels = newLingeringAccels;
    }

    @Inject(at = @At("HEAD"), method = "readNbt")
    protected void lapisworks$readPullSpellFX(NbtCompound nbt, CallbackInfo ci) {
        ArrayList<Pair<Vec3d, Integer>> newLingeringAccels = new ArrayList<>();
        
        nbt.getList("pullSpellFX", NbtElement.COMPOUND_TYPE)
            .stream()
            .forEach(
                ele -> {
                    NbtCompound pair = (NbtCompound)ele;
                    newLingeringAccels.add(
                        new Pair<>(
                            deserializeVec3d(pair.getCompound("pull")),
                            nbt.getInt("ticksLeft")
                        )
                    );
                }
            );

        this.lingeringAccels = newLingeringAccels;
    }

    @Inject(at = @At("HEAD"), method = "writeNbt")
    protected void lapisworks$writePullSpellFX(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.put("pullSpellFX", nbtListOf(
            lingeringAccels.stream()
                .map(
                    pullSpellEffect -> {
                        NbtCompound pair = new NbtCompound();
                        pair.put("pull", serializeVec3d(pullSpellEffect.getLeft()));
                        pair.putInt("ticksLeft", pullSpellEffect.getRight());
                        return pair;
                    }
                )
                .toList()
        ));
    }

    @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	public void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof LivingEntity) {
            if (damageSource.isIn(DamageTypeTags.IS_FIRE) &&
                ((LapisworksInterface)this).getEnchant(AllEnchantments.fireResist) > 0) {
                cir.setReturnValue(true);
            }
        }
	}

    // why can't i just inject in ItemEntityMixin bruh
    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions lapisworks$getDimensions(EntityDimensions og) {
        if (
            (Object)this instanceof ItemEntity itemEntity &&
            isInCradle(itemEntity.getStack())
        )
            return new EntityDimensionsButTheHitboxIsDown(1.0F, 1.0F);

        return og;
    }

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private Box lapisworks$getBoundingBox(Box og) {
        if (
            (Object)this instanceof ItemEntity itemEntity &&
            isInCradle(itemEntity.getStack())
        )
            return itemEntity.getDimensions(itemEntity.getPose()).getBoxAt(itemEntity.getPos());

        return og;
    }
}

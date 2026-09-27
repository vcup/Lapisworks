package com.luxof.lapisworks.mixinsupport;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface CollarControllable {
    public ItemStack getCollar();
    public ItemStack setCollar(ItemStack collar);

    public static class CollarMindControlGoal extends Goal {

        private final MobEntity mob;
        private Vec3d previouslyGoingTo = null;
        private Vec3d goingTo = null;

        public CollarMindControlGoal(CollarControllable collarable) {
            this.mob = (MobEntity)collarable;
        }

        @Override
        public boolean canStart() {
            if (mob.getWorld().isClient) return false;
            ServerWorld sw = (ServerWorld)mob.getWorld();

            ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(mob);
            NbtCompound iotaTag = iotaHolder != null ? iotaHolder.readIotaTag() : null;
            if (iotaTag == null) return false;

            var iotaType = IotaType.getTypeFromTag(iotaTag);
            if (iotaType == HexIotaTypes.VEC3) {
                goingTo = ((Vec3Iota)IotaType.deserialize(iotaTag, sw)).getVec3();
                return false;
            }

            else if (iotaType == HexIotaTypes.ENTITY) {
                Entity entity = ((EntityIota)IotaType.deserialize(iotaTag, sw)).getEntity();
                if (!(entity instanceof LivingEntity living)) return false;
                mob.setTarget(living);
                return true;
            }

            return false;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (previouslyGoingTo == null) previouslyGoingTo = goingTo;
            if (goingTo == null) return;

            if (previouslyGoingTo.equals(goingTo)) return;
            else previouslyGoingTo = goingTo;

            if (mob instanceof TameableEntity tameable)
                tameable.setInSittingPose(false);
            mob.getNavigation().startMovingTo(goingTo.x, goingTo.y, goingTo.z, 1.1F);
        }
    }
}

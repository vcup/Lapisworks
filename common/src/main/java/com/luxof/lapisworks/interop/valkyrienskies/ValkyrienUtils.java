package com.luxof.lapisworks.interop.valkyrienskies;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.entity.handling.DefaultShipyardEntityHandler;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class ValkyrienUtils {
    public static double distance(World world, Vec3d start, Vec3d end) {
        return ValkyrienSkies.distance(world, start, end);
    }

    public static Vec3d toWorldspace(World world, Vec3d shipyardPosition) {
        Ship ship = ValkyrienSkies.getShipManagingBlock(world, shipyardPosition);
        if (ship == null) return shipyardPosition;
        return VectorConversionsMCKt.toMinecraft(ship.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(shipyardPosition)));
    }

    public static Vec3d getShipScale(World world, Vec3d shipyardPosition) {
        Ship ship = ValkyrienSkies.getShipManagingBlock(world, shipyardPosition);
        if (ship == null) return new Vec3d(1, 1, 1);
        return VectorConversionsMCKt.toMinecraft(ship.getTransform().getShipToWorldScaling());
    }

    public static void setEntityToShipyard(Entity entity, Vec3d shipyardPosition) {
        Ship ship = ValkyrienSkies.getShipManagingBlock(entity.getEntityWorld(), shipyardPosition);
        if (ship != null)
            DefaultShipyardEntityHandler.INSTANCE.moveEntityFromWorldToShipyard(entity, ship);
    }
}

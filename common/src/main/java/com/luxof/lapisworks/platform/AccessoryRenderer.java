package com.luxof.lapisworks.platform;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * The accessory-rendering callback an item's accessory uses, expressed without naming either
 * loader's renderer interface.
 * <p>
 * Fabric's {@code TrinketRenderer#render} and Curios' {@code ICurioRenderer#render} take the same
 * information in a different order, so the actual drawing lives once in shared code as one of these
 * and each platform module adapts its own interface onto it.
 */
@FunctionalInterface
public interface AccessoryRenderer {
    void render(
        ItemStack stack,
        EntityModel<? extends LivingEntity> contextModel,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        LivingEntity entity,
        float limbAngle,
        float limbDistance,
        float tickDelta,
        float animationProgress,
        float headYaw,
        float headPitch
    );
}

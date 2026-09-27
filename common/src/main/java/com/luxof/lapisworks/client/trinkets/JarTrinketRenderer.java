package com.luxof.lapisworks.client.trinkets;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.platform.AccessoryRenderMath;
import com.luxof.lapisworks.platform.AccessoryRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

/**
 * @author WireSegal
 * Created at 9:50 AM on 7/25/22.
 * <p>(added because LensTrinketRenderer also has it even though the code is different)
 */
public class JarTrinketRenderer implements AccessoryRenderer {
    // if i unravelled this many arguments it'd be even worse to read bruh wtf
    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {
        if (!(stack.isOf(ModItems.AMEL_JAR) &&
                contextModel instanceof PlayerEntityModel playerModel &&
                entity instanceof AbstractClientPlayerEntity player)) return;
        matrices.push();
        AccessoryRenderMath.followBodyRotations(entity, playerModel, matrices);
        // PRAYING this translates to the belt
        // this does NOT translate to the belt :sob: :pray:
        //AccessoryRenderMath.translateToRightLeg(matrices, playerModel, player);
        AccessoryRenderMath.translateToChest(matrices, playerModel, player);

        matrices.translate(-0.2, 0.1, 0.0275);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.35f, 0.35f, 0.35f);

        MinecraftClient instance = MinecraftClient.getInstance();
        // this ItemRenderer shit seems EXTREMELY! useful for my 4-armed project
        instance.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light,
            OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, instance.world, 0);
        matrices.pop();
    }
}

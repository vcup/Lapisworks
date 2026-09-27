package com.luxof.lapisworks.platform;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;

/**
 * Accessory-rendering transforms, shared by both platforms.
 * <p>
 * These are a faithful transcription of Trinkets' {@code TrinketRenderer} statics: they rotate the
 * model's own parts as well as the matrix stack, so they cannot be re-derived approximately without
 * visibly moving accessories on the player. Curios ships {@code followBodyRotations} but has no
 * equivalent of {@code translateToFace}/{@code translateToChest}, so transcribing them once here
 * keeps Fabric and NeoForge pixel-identical rather than "close enough" on one loader.
 * <p>
 * Because both loaders run this same code, it deliberately is not a seam.
 */
public final class AccessoryRenderMath {
    private AccessoryRenderMath() {}

    /** Copies the wearer's current body pose onto {@code model}. */
    @SuppressWarnings("unchecked")
    public static void followBodyRotations(LivingEntity entity, PlayerEntityModel<?> model, MatrixStack matrices) {
        var renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            var entityModel = livingRenderer.getModel();
            if (entityModel instanceof BipedEntityModel bipedModel) {
                bipedModel.copyBipedStateTo(model);
            }
        }
    }

    /** Moves the matrix stack to the centre of the wearer's face. */
    public static void translateToFace(MatrixStack matrices, PlayerEntityModel<?> model, PlayerEntity player,
            float headYaw, float headPitch) {
        // Swimming or gliding: tilt the head back rather than following the neck.
        if (player.isInSwimmingPose() || player.isFallFlying()) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(model.head.roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0F));
        } else {
            if (player.isInSneakingPose() && !model.riding) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
        }
        matrices.translate(0.0F, -0.25F, -0.3F);
    }

    /** Moves the matrix stack to the centre of the wearer's chest. */
    public static void translateToChest(MatrixStack matrices, PlayerEntityModel<?> model, PlayerEntity player) {
        if (player.isInSneakingPose() && !model.riding && !player.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.body.pitch));
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }
}

package com.luxof.lapisworks.interop.hexal.blocks;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class 
EnchSlipwayRenderer implements BlockEntityRenderer<EnchSlipwayEntity> {
    private static final Identifier TEXTURE = id("textures/block/amel_constructs/animated_enchslipway2.png");
    private static final int FRAME_COUNT = 32;
    private static final int FRAME_WIDTH = 64;
    private static final int SPRITESHEET_WIDTH = FRAME_WIDTH * FRAME_COUNT;
    private static final float STEP = (float)FRAME_WIDTH / (float)SPRITESHEET_WIDTH;
    private int currFrame = 0;

    public EnchSlipwayRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(EnchSlipwayEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Random random = entity.getWorld().getRandom();
        if (entity.degrees == null) {
            entity.degrees = random.nextBetween(0, 360);
            entity.sync();
        }
        
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.push();
        // magic numbers: fresh from my ass
        float dx = 0.03f - random.nextFloat() * 0.06f;
        float dy = 0.03f - random.nextFloat() * 0.06f;
        float dz = 0.03f - random.nextFloat() * 0.06f;
        matrices.translate(0.5 + dx, 0.5 + dy, 0.5 + dz);

        // face the camera
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        float scale = 10.0f;
        matrices.scale(scale, scale, scale);

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.degrees));
        Matrix4f posMat = matrices.peek().getPositionMatrix();
        Matrix3f normMat = matrices.peek().getNormalMatrix();

        currFrame = (currFrame + 1) % FRAME_COUNT;

        float u0 = STEP * currFrame;
        float u1 = STEP * (currFrame + 1);

        float v0 = 0f;
        float v1 = 1f;

        int bright = LightmapTextureManager.pack(15, 15);
        vc.vertex(posMat, -0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(u0, v1).overlay(overlay).light(bright).normal(normMat, 0, 1, 0).next();
        vc.vertex(posMat, -0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(u0, v0).overlay(overlay).light(bright).normal(normMat, 0, 1, 0).next();
        vc.vertex(posMat, 0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(u1, v0).overlay(overlay).light(bright).normal(normMat, 0, 1, 0).next();
        vc.vertex(posMat, 0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(u1, v1).overlay(overlay).light(bright).normal(normMat, 0, 1, 0).next();

        matrices.pop();
    }
}

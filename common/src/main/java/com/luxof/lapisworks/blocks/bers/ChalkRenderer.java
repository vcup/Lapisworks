package com.luxof.lapisworks.blocks.bers;

import com.luxof.lapisworks.blocks.entities.ChalkEntity;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.rotateToBeAttachedTo;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

/** this shit has given me a migraine */
public class ChalkRenderer implements BlockEntityRenderer<ChalkEntity> {
    private static final float y = 0.001f;

    private static final RenderLayer TEXTURE_POW = RenderLayer.getEntityCutout(id("textures/block/chalk/powered.png"));
    private static final int POWERED_LIGHT = LightmapTextureManager.pack(6, 9); // heehee

    private static final RenderLayer TEXTURE_UNPOW = RenderLayer.getEntityCutout(id("textures/block/chalk/unpowered.png"));

    public ChalkRenderer(BlockEntityRendererFactory.Context ctx) {}

    private int _toNum(boolean bool) { return bool ? 1 : 0; }
    private float[] getUV(boolean[] cardinals) {
        return getUV(cardinals[0], cardinals[1], cardinals[2], cardinals[3]);
    }
    private float[] getUV(boolean forward, boolean left, boolean down, boolean right) {
        int whole = (_toNum(forward) + _toNum(left) * 2 + _toNum(down) * 4 + _toNum(right) * 8) * 4;
        int x = whole % 32;
        int y = whole / 32 * 4; // notice intdiv floors
        return makeUV(x, y);
    }
    private float[] makeUV(int x, int y) {
        return new float[] {(float)x / 32.0f, (float)y / 32.0f};
    }
    private float[] addUV(float[] uv1, float[] uv2) {
        return new float[] {
            uv1[0] + uv2[0],
            uv1[1] + uv2[1]
        };
    }

    private void doVert(
        VertexConsumer vc,
        float u, float v,
        int overlay,
        int light,
        Matrix3f normMat
    ) {
        vc.color(255, 255, 255, 255).texture(u, v).overlay(overlay).light(light)
            .normal(normMat, 0f, 1f, 0f).next();
    }
    private void doSquare(
        VertexConsumer vc,
        MatrixStack matrices,
        float[] xyz1, float[] xyz2,
        float[] uv1, float[] uv2,
        int overlay,
        int light
    ) {
        Matrix3f normMat = matrices.peek().getNormalMatrix();
        Matrix4f posMat = matrices.peek().getPositionMatrix();

        doVert(vc.vertex(posMat, xyz1[0], xyz1[1], xyz1[2]), uv1[0], uv1[1], overlay, light, normMat);
        doVert(vc.vertex(posMat, xyz1[0], xyz1[1], xyz2[2]), uv1[0], uv2[1], overlay, light, normMat);
        doVert(vc.vertex(posMat, xyz2[0], xyz1[1], xyz2[2]), uv2[0], uv2[1], overlay, light, normMat);
        doVert(vc.vertex(posMat, xyz2[0], xyz1[1], xyz1[2]), uv2[0], uv1[1], overlay, light, normMat);
    }

    // +X is East and +Z is South in MC
    // it should be +Z = North bro wtf is that coordinate system eugh
    @Override
    public void render(ChalkEntity chalk, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int useLight = chalk.powered ? POWERED_LIGHT : light;
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(rotateToBeAttachedTo(chalk.attachedTo));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        float[] topLeft = {0f, y, 0f};
        float[] bottomRight = {1f, y, 1f};
        float[] dotTopLeft = {0.375f, y, 0.375f};
        float[] dotBottomRight = {0.625f, y, 0.625f};
        float[] rightmostUpperMiddle = {1f, y, 0.375f};
        float[] bottomMiddleLeft = {0.375f, y, 1f};
        float[] leftmostLowerMiddle = {0f, y, 0.625f};
        float[] topMiddleRight = {0.625f, y, 0f};

        VertexConsumer vc = vertexConsumers.getBuffer(chalk.powered ? TEXTURE_POW : TEXTURE_UNPOW);

        if (chalk.allSidesAreChalk()) {
            doSquare(
                vc, matrices,
                topLeft, bottomRight,
                new float[] {0.5f, 0.5f}, new float[] {1f, 1f},
                overlay, useLight
            );
            matrices.pop();
            return;
        }
        // center
        final float _uv1[] = getUV(chalk.sidesAreChalk);
        final float _uv2[] = addUV(_uv1, makeUV(4, 4));
        // from 13, 13 to 20, 20
        doSquare(
            vc, matrices,
            dotTopLeft, dotBottomRight,
            _uv1, _uv2,
            overlay, useLight
        );

        // forward
        if (chalk.sidesAreChalk[0]) {
            doSquare(
                vc, matrices,
                topLeft, rightmostUpperMiddle,
                new float[] {0.5f, 0.5f}, new float[] {1f, 0.65625f},
                overlay, useLight
            );
        }
        // left
        if (chalk.sidesAreChalk[1]) {
            doSquare(
                vc, matrices,
                topLeft, bottomMiddleLeft,
                new float[] {0.5f, 0.5f}, new float[] {0.6875f, 1f},
                overlay, useLight
            );
        }
        // backwards
        if (chalk.sidesAreChalk[2]) {
            doSquare(
                vc, matrices,
                leftmostLowerMiddle, bottomRight,
                new float[] {0.5f, 0.8125f}, new float[] {1f, 1f},
                overlay, useLight
            );
        }
        // right
        if (chalk.sidesAreChalk[3]) {
            doSquare(
                vc, matrices,
                topMiddleRight, bottomRight,
                new float[] {0.8125f, 0.5f}, new float[] {1f, 1f},
                overlay, useLight
            );
        }

        matrices.pop();
    }
}

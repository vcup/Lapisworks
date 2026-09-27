package com.luxof.lapisworks.blocks.bers;

import at.petrak.hexcasting.client.render.PatternColors;
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;

import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;

import static com.luxof.lapisworks.Lapisworks.getRotationForHorizontal;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.rotateToBeAttachedTo;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ChalkWithPatternRenderer implements BlockEntityRenderer<ChalkWithPatternEntity> {
    public ChalkWithPatternRenderer(BlockEntityRendererFactory.Context ctx) {}
    private static final RenderLayer TEXTURE = RenderLayer.getEntityCutout(
        id("textures/block/chalk/with_pattern.png")
    );
    private static final float[] uv_unpowered = new float[] {0.5f, 0.5f};
    private static final float[] uv_powered = new float[] {0.0f, 0.5f};
    private static final float y = 0.001f;

    public static final PatternColors UNPOWERED_PATTERN_COLORS = new PatternColors(0xFF_533888, 0xFF_6E4EA9);
    public static final PatternColors POWERED_PATTERN_COLORS = new PatternColors(0xFF_8B69CA, 0xFF_CD9EF0);

    @Override
    public void render(ChalkWithPatternEntity chalk, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(rotateToBeAttachedTo(chalk.attachedTo));
        if (chalk.rotated) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        VertexConsumer vc = vertexConsumers.getBuffer(TEXTURE);
        Matrix4f posMat = matrices.peek().getPositionMatrix();
        Matrix3f normMat = matrices.peek().getNormalMatrix();

        float[] uv = chalk.powered ? uv_powered : uv_unpowered;
        int useLight = chalk.powered ? LightmapTextureManager.pack(6, 9) : light;

        float x1 = 0f;
        float x2 = 1f;
        float u1Offset = 0f;
        float u2Offset = 0.5f;
        // ikik ? exists but i want less if
        if (!chalk.renderLeftBracket) {
            x1 = 0.5f;
            u1Offset = 0.25f;
        }
        if (!chalk.renderRightBracket) {
            x2 = 0.5f;
            u2Offset = 0.25f;
        }

        if (chalk.renderLeftBracket || chalk.renderRightBracket) {
            vc.vertex(posMat, x1, y, 0f).color(255, 255, 255, 255).texture(uv[0] + u1Offset, uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, x1, y, 1f).color(255, 255, 255, 255).texture(uv[0] + u1Offset, uv[1] + 0.5f).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, x2, y, 1f).color(255, 255, 255, 255).texture(uv[0] + u2Offset, uv[1] + 0.5f).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, x2, y, 0f).color(255, 255, 255, 255).texture(uv[0] + u2Offset, uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        }

        int patCount = chalk.pats.size();
        if (patCount == 0) {
            matrices.pop();
            return;
        }

        matrices.translate(0.5f, 0.5f, 0.5f);
        // cancel this rotation to not mess with any processing ahead
        if (chalk.rotated)
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
        matrices.multiply(getRotationForHorizontal(chalk.renderPatternsInDir, chalk.attachedTo));
        matrices.translate(-0.5f, -0.5f, -0.5f);
        // by default patterns render on the south side
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        long tick = chalk.getWorld().getTime();

        WorldlyPatternRenderHelpers.renderPattern(
            // magic numbers: every 5 seconds (20 ticks * 5 seconds = 100)
            chalk.pats.get((int)(tick / 100 % patCount)),
            WorldlyPatternRenderHelpers.WORLDLY_SETTINGS,
            chalk.powered ? POWERED_PATTERN_COLORS : UNPOWERED_PATTERN_COLORS,
            (double)chalk.getPos().hashCode(),
            matrices,
            vertexConsumers,
            null,
            -y,
            useLight,
            1
        );

        matrices.pop();
    }
}

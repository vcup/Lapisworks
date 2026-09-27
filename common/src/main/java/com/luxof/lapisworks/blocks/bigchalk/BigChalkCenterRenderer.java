package com.luxof.lapisworks.blocks.bigchalk;

import at.petrak.hexcasting.client.render.PatternColors;
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;

import com.luxof.lapisworks.blocks.bigchalk.BigChalkCenterEntity.RenderData;

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

public class BigChalkCenterRenderer implements BlockEntityRenderer<BigChalkCenterEntity> {
    private static final float y = 0.001f;

    private static final RenderLayer TEXTURE_main = RenderLayer.getEntityCutoutNoCull(
        id("textures/block/chalk_big.png")
    );
    private static final RenderLayer TEXTURE_alt = RenderLayer.getEntityCutoutNoCull(
        id("textures/block/chalk_big_alt.png")
    );
    private static final float[] TEXTURE_main_size = {960f, 384f};
    private static final float[] TEXTURE_main_sprite_size = {192f, 192f};

    public static final PatternColors UNPOWERED_PATTERN_COLORS = new PatternColors(0xFF_FCFAD2, 0xFF_8B69CA);
    public static final PatternColors POWERED_PATTERN_COLORS = new PatternColors(0xFF_FCFAD2, 0xFF_CD9EF0);

    /** provides both start and end in 4 items. */
    private static float[] getUVOfSprite(int spriteIdx) {
        int spritesPerRow = (int)(TEXTURE_main_size[0] / TEXTURE_main_sprite_size[0]);
        
        int x = (int)TEXTURE_main_sprite_size[0] * (spriteIdx % spritesPerRow);
        int y = (int)TEXTURE_main_sprite_size[1] * (spriteIdx / spritesPerRow);

        float u1 = (float)x / TEXTURE_main_size[0];
        float v1 = (float)y / TEXTURE_main_size[1];

        float u2 = u1 + TEXTURE_main_sprite_size[0] / TEXTURE_main_size[0];
        float v2 = v1 + TEXTURE_main_sprite_size[1] / TEXTURE_main_size[1];

        return new float[] { u1, v1, u2, v2 };
    }

    public BigChalkCenterRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BigChalkCenterEntity chalk, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int useLight = chalk.isPowered() ? LightmapTextureManager.pack(15, 15) : light;

        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(rotateToBeAttachedTo(chalk.attachedTo));
        matrices.multiply(getRotationForHorizontal(chalk.facing, chalk.attachedTo));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        RenderData renderData = chalk.renderData;
        Matrix4f posMat = matrices.peek().getPositionMatrix();
        Matrix3f normMat = matrices.peek().getNormalMatrix();

        VertexConsumer vc = vertexConsumers.getBuffer(chalk.altTexture ? TEXTURE_alt : TEXTURE_main);

        float fullT = renderData.getTranslation(tickDelta);
        float halfT = fullT * 0.5f;
        float quarTer = fullT * 0.25f;

        float[] uv;

        // main bg circle
        uv = getUVOfSprite(0);
        vc.vertex(posMat, -1f, y, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        matrices.translate(0f, halfT, 0f);
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(renderData.rotate(tickDelta));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        // 4 pronged arrows (connected together by circle in alt texture)
        // AKA inner circle AKA spiky circle AKA inner spiky circle
        uv = getUVOfSprite(1);
        vc.vertex(posMat, -1f, y*2, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y*2, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*2, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*2, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        matrices.translate(0f, halfT, 0f);
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(renderData.rotateInverse(tickDelta));
        matrices.multiply(renderData.rotateInverse(tickDelta));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        // 4-point star
        uv = getUVOfSprite(2);
        vc.vertex(posMat, -1f, y*3, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y*3, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*3, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*3, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        // text
        // it's rendered here despite being the (5+n)th sprite because it follows
        // 4-point star's rot, and the hexagon and 6-point star do not obstruct it
        uv = getUVOfSprite(5 + chalk.textVariant);
        vc.vertex(posMat, -1f, y*4, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y*4, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*4, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*4, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        matrices.translate(0f, halfT, 0f);
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(renderData.rotate(tickDelta));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        // hexagon
        uv = getUVOfSprite(3);
        vc.vertex(posMat, -1f, y*5, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y*5, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*5, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*5, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        // 6-point star
        uv = getUVOfSprite(4);
        vc.vertex(posMat, -1f, y*6, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, -1f, y*6, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*6, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();
        vc.vertex(posMat, 2f, y*6, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(useLight).normal(normMat, 0f, 1f, 0f).next();

        if (chalk.getPattern() == null) {
            matrices.pop();
            return;
        }

        // ??? why doesn't this work
        //matrices.translate(0.5f, 0.5f, 0.5f);
        //matrices.multiply(getReverseRotationForHorizontal(chalk.facing, chalk.attachedTo));
        //matrices.multiply(getRotationForHorizontal(chalk.patternFacing, chalk.attachedTo));
        //matrices.translate(-0.5f, 0.5f, 0.5f);
        matrices.translate(0f, quarTer, 0f);
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.scale(0.8f, 1f, 0.8f);
        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        WorldlyPatternRenderHelpers.renderPattern(
            chalk.getPattern(),
            WorldlyPatternRenderHelpers.WORLDLY_SETTINGS,
            chalk.isPowered() ? POWERED_PATTERN_COLORS : UNPOWERED_PATTERN_COLORS,
            (double)chalk.getPos().hashCode(),
            matrices,
            vertexConsumers,
            null,
            -(y*7),
            useLight,
            1
        );

        matrices.pop();
    }
}

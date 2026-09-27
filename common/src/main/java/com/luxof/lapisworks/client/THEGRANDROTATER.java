package com.luxof.lapisworks.client;

import at.petrak.hexcasting.client.ClientTickCounter;

import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class THEGRANDROTATER {
    public static Matrix4f makeMatrix4f(
        double m00, double m01, double m02, double m03,
        double m10, double m11, double m12, double m13,
        double m20, double m21, double m22, double m23,
        double m30, double m31, double m32, double m33
    ) {
        // WHO THE FUCK DECIDED COLUMN-MAJOR MATRICES SHOULD EXIST
        // I'LL KILL THEM
        // I'LL FUCKING KILL THEM
        // THE CODE SHOULD MATCH HOW IT FUCKING LOOKS
        // I DON'T WANT TO ROTATE 90 DEGREES IN MY FUCKING HEAD
        return new Matrix4f(
            new Matrix4d(
                m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32,
                m03, m13, m23, m33
            )
        );
    }

    public static Matrix4f COMPUTETHEGRANDMATRIX(float t) {
        // honestly i don't know, i just threw random numbers at it until it was slow
        // then divided by twenty to, i dunno, spread it out over 20 ticks ig
        double theta = 0.07 * t;
        double XYtheta = theta * 0.7;
        Matrix4f XY = makeMatrix4f(
            cos(XYtheta), -sin(XYtheta), 0, 0,
            sin(XYtheta), cos(XYtheta), 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        );
        double XWtheta = theta * 1.3;
        Matrix4f XW = makeMatrix4f(
            cos(XWtheta), 0, 0, -sin(XWtheta),
            0, 1, 0, 0,
            0, 0, 1, 0,
            sin(XWtheta), 0, 0, cos(XWtheta)
        );
        double ZWtheta = theta * 2.1;
        Matrix4f ZW = makeMatrix4f(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, cos(ZWtheta), -sin(ZWtheta),
            0, 0, sin(ZWtheta), cos(ZWtheta)
        );
        return XY.mul(ZW).mul(XW);
        //return XW;
    }

    public static RotationAxis allAxes = RotationAxis.of(new Vector3f(1, 1, 1));
    public static List<Vector4f> vertices = List.of(
        new Vector4f(-1, -1, -1, -1),
        new Vector4f(-1, -1, -1,  1),
        new Vector4f(-1, -1,  1, -1),
        new Vector4f(-1, -1,  1,  1),
        new Vector4f(-1,  1, -1, -1),
        new Vector4f(-1,  1, -1,  1),
        new Vector4f(-1,  1,  1, -1),
        new Vector4f(-1,  1,  1,  1),
        new Vector4f( 1, -1, -1, -1),
        new Vector4f( 1, -1, -1,  1),
        new Vector4f( 1, -1,  1, -1),
        new Vector4f( 1, -1,  1,  1),
        new Vector4f( 1,  1, -1, -1),
        new Vector4f( 1,  1, -1,  1),
        new Vector4f( 1,  1,  1, -1),
        new Vector4f( 1,  1,  1,  1)
    );
    public static List<Integer> drawOrder = List.of(
        // every pair is an edge connection
        // yeah i got chatgpt to make this for me
        // i'm not linking that shit up
            0,1, 0,2, 0,4, 0,8,
            1,3, 1,5, 1,9,
            2,3, 2,6, 2,10,
            3,7, 3,11,
            4,5, 4,6, 4,12,
            5,7, 5,13,
            6,7, 6,14,
            7,15,
            8,9, 8,10, 8,12,
            9,11, 9,13,
            10,11,10,14,
            11,15,
            12,13,12,14,
            13,15,
            14,15
    );
    public static Vector3f projectTo3D(Vector4f v) {
        // 4 - v.w. Why?
        // projection. v.w is also never above 1, so i can get away with something above that.
        return new Vector3f(v.x, v.y, v.z).div(4 - v.w);
    }
    public static void renderEnchantedSentinel(
        Vec3d sentinel,
        MatrixStack ms,
        float tickDelta
    ) {
        ms.push();

        MinecraftClient mc = MinecraftClient.getInstance();
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d camPos = cam.getPos();
        if (VALKYRIEN_SKIES_INTEROP) {
            Vec3d worldspace = ValkyrienUtils.toWorldspace(mc.world, sentinel);
            ms.translate(
                    worldspace.x - camPos.x,
                    worldspace.y - camPos.y,
                    worldspace.z - camPos.z
            );
        } else {
            ms.translate(
                    sentinel.x - camPos.x,
                    sentinel.y - camPos.y,
                    sentinel.z - camPos.z
            );
        }

        float time = ClientTickCounter.getTotal() / 2; // why not? it's perfectly useable
        Matrix4f rotationMatrix = COMPUTETHEGRANDMATRIX(time);

        if (VALKYRIEN_SKIES_INTEROP) {
            Vec3d shipScale = ValkyrienUtils.getShipScale(mc.world, sentinel);
            ms.scale((float) shipScale.x, (float) shipScale.y, (float) shipScale.z);
        }
        ms.scale(2.0f, 2.0f, 2.0f);


        Matrix4f matrix = ms.peek().getPositionMatrix();
        Matrix3f normalMatrix = ms.peek().getNormalMatrix();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.lineWidth(5f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(DrawMode.LINES, VertexFormats.LINES);

        drawOrder.forEach((Integer idx) -> {
            Vector3f v = projectTo3D(new Vector4f(vertices.get(idx)).mul(rotationMatrix));
            // vvvvvvvvvvvv makes almost every line have "good" normals so MC makes them be big
            Vector3f n = new Vector3f(v).cross(new Vector3f(0f, 0f, 1f)).normalize();
            buffer
                .vertex(matrix, v.x, v.y, v.z)
                .color(132, 62, 207, 255)
                .normal(normalMatrix, n.x, n.y, n.z)
                .next();
        });

        tessellator.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        ms.pop();
    }
}

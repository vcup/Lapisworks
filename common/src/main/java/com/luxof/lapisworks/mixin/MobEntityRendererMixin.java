package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.CollarControllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// mom i don't wanna do rendering...
// i don't fucking wanna...
@Mixin(MobEntityRenderer.class)
public class MobEntityRendererMixin {
    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    public void lapisworks$renderCollar(
        MobEntity mob,
        float f,
        float g,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumerProvider,
        int i,
        CallbackInfo ci
    ) {
        if (!(mob instanceof CollarControllable collarable)) return;

        if (mob instanceof CatEntity cat)
            renderCollarableCat(collarable, cat, f, g, matrices, vertexConsumerProvider, i);
    }

    @Unique
    private void renderCollarableCat(
        CollarControllable collarable,
        CatEntity cat,
        float f,
        float g,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumerProvider,
        int i
    ) {
        matrices.push();
        matrices.translate(0.0, 0.0, 0.35);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(135));
        matrices.scale(0.3f, 0.3f, 0.3f);
        //matrices.translate(0.0, 0.5, 0.0);

        ItemStack collar = collarable.getCollar();
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            collar,
            ModelTransformationMode.HEAD,
            i,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumerProvider,
            cat.getWorld(),
            0
        );

        matrices.pop();
    }
}

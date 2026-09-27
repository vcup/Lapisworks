package com.luxof.lapisworks.mixin;

import static com.luxof.lapisworks.Lapisworks.isInCradle;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// who up rendering they items rn
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderItem")
    public void render(
        ItemStack stack,
        ModelTransformationMode renderMode,
        boolean leftHanded,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        if (isInCradle(stack)) matrices.translate(0.0, -0.25, 0.0);
    }
}

package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.WispCanIntoItem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ram.talia.hexal.common.entities.BaseCastingWisp;
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(
        at = @At("HEAD"),
        method = "render"
    )
    public void render(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (!(entity instanceof BaseCastingWisp wisp)) return;
        matrices.push();

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.translate(0.0, -1.3, -0.4);

        //matrices.multiply(RotationAxis.POSITIVE_X.rotation(wisp.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));

        itemRenderer.renderItem(
            ((WispCanIntoItem)wisp).getStack(),
            ModelTransformationMode.HEAD,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumers,
            client.world,
            0
        );
        matrices.pop();
    }
}

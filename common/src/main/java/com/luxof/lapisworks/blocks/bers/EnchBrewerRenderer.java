package com.luxof.lapisworks.blocks.bers;

import com.luxof.lapisworks.blocks.entities.EnchBrewerEntity;
import com.luxof.lapisworks.inv.EnchBrewerInv;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class EnchBrewerRenderer implements BlockEntityRenderer<EnchBrewerEntity> {
    public EnchBrewerRenderer(BlockEntityRendererFactory.Context ctx) {}

    private void render(
        ItemStack stack,
        int light,
        MatrixStack matrices,
        VertexConsumerProvider vcp,
        double xOffset,
        double yOffset,
        double zOffset,
        int ordinal
    ) {
        matrices.push();
        matrices.scale(0.605F, 0.605F, 0.605F);
        matrices.translate(xOffset, yOffset, zOffset);
        // make the potions look into the center
        if (ordinal == 0) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45));
        } else if (ordinal == 1) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(45));
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            stack,
            ModelTransformationMode.FIXED,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vcp,
            MinecraftClient.getInstance().world,
            0
        );
        matrices.pop();
    }

    @Override
    public void render(EnchBrewerEntity brewer, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        EnchBrewerInv inv = (EnchBrewerInv)brewer.inv;

        matrices.push();

        ItemStack p1 = inv.getBrewingInto(0);
        ItemStack p2 = inv.getBrewingInto(1);
        ItemStack p3 = inv.getBrewingInto(2);
        // og_coord/scale, math can make trial and error vanish :face_holding_back_tears:
        if (!p1.isEmpty()) {
            render(p1, light, matrices, vertexConsumers, 0.425, 0.65, 0.4, 0);
        }
        if (!p2.isEmpty()) {
            render(p2, light, matrices, vertexConsumers, 0.425, 0.65, 1.25, 1);
        }
        if (!p3.isEmpty()) {
            render(p3, light, matrices, vertexConsumers, 1.227, 0.65, 0.826, 2);
        }

        matrices.pop();
    }
}

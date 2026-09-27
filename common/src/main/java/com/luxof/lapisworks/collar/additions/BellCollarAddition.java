package com.luxof.lapisworks.collar.additions;

import com.luxof.lapisworks.collar.LapisCollarAddition;
import com.luxof.lapisworks.init.LapisSounds;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR_BELL;

import com.luxof.lapisworks.platform.AccessorySlot;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class BellCollarAddition implements LapisCollarAddition {
    public static final Identifier ID = id("collar/bell");

    @Override public Text getName() {
        return Text.translatable("tooltips.lapisworks.collar.added_items.bell")
            .formatted(Formatting.YELLOW);
    }
    @Override public Text getName(ItemStack collarStack) { return getName(); }
    @Override public boolean testItem(Item item) { return item == Items.GOLD_INGOT; }

    @Override
    public boolean canAdd(ItemStack collarStack, List<Identifier> existingAdditions, Identifier yourId) {
        return true;
    }

    @Override
    public ItemStack craft(ItemStack collarStack, List<Identifier> existingAdditions, ItemStack yourStack,
            Identifier yourId) {
        ItemStack newCollar = collarStack.copy();
        COLLAR.addAddition(newCollar, yourId);
        return newCollar;
    }

    /**
     * Built lazily rather than in a field initializer.
     * <p>
     * Constructing an {@code ItemStack} resolves its item through the registry, which on NeoForge
     * throws "No delegate exists for value air" if it happens before the item registry is populated
     * -- which is where this class is first constructed during init.
     */
    private ItemStack bellStack = null;

    private ItemStack bellStack() {
        if (bellStack == null) bellStack = new ItemStack(COLLAR_BELL);
        return bellStack;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            entity,
            bellStack(),
            mode,
            false,
            matrices,
            vertexConsumers,
            entity != null ? entity.getWorld() : null,
            light,
            overlay,
            0
        );
    }

	@Override
	public void accessoryTick(
        ItemStack stack, AccessorySlot slot, LivingEntity entity
    ) {
		generalTick(stack, entity);
	}

    @Override
    public void generalTick(ItemStack stack, LivingEntity entity) {
        if (!entity.getWorld().isClient)
            return;
        double speed = entity.getVelocity().add(0.0, -entity.getVelocity().getY(), 0.0).length();

		if (
            speed < 0.1 ||
            entity.getRandom().nextDouble() < 0.25 ||
            Math.max(0, entity.age % 20 - entity.getRandom().nextBetween(0, 10)) != 0
        ) return;
        entity.getWorld().playSound(
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            LapisSounds.COLLAR_BELL,
            SoundCategory.AMBIENT,
            0.4f,
            0.8f + (float)(speed - 0.1) * 0.5f + entity.getRandom().nextFloat() * 0.1f,
            true
        );
    }
}

package com.luxof.lapisworks.neoforge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;
import com.luxof.lapisworks.platform.Accessories;
import com.luxof.lapisworks.platform.AccessoryRenderer;
import com.luxof.lapisworks.platform.AccessorySlot;
import com.luxof.lapisworks.platform.PlatformAccessoryItem;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

/**
 * NeoForge implementation of the accessory seam, backed by Curios.
 * <p>
 * The mirror image of the Fabric module's Trinkets adapter: items implement
 * {@link PlatformAccessoryItem}, and this class wraps them in an {@link ICurio} so Curios can drive
 * the equip/unequip/tick/modifier lifecycle, translating {@link AccessorySlot} to Curios'
 * {@code SlotContext}.
 * <p>
 * Curios decides which slot an item may occupy from the {@code data/curios/tags/items/<slot>.json}
 * item tags rather than from an API call, so {@link #registerWearable} only has to pass on the
 * item's behaviour; the tags in this module's resources do the slot assignment.
 */
public class NeoForgeAccessories implements Accessories {

    /** Adapts a loader-agnostic slot to Curios' SlotContext. */
    private static final class CurioSlot implements AccessorySlot {
        private final SlotContext ctx;
        private final ItemStack stack;

        CurioSlot(SlotContext ctx, ItemStack stack) {
            this.ctx = ctx;
            this.stack = stack;
        }

        @Override public ItemStack getStack() { return stack; }
        @Override public LivingEntity getEntity() { return ctx.entity(); }
        @Override public String getSlotName() { return ctx.identifier(); }
        @Override public int getIndex() { return ctx.index(); }
    }

    /** Every equipped curio, flattened out of Curios' slot -> stacks handler map. */
    private static List<SlotResult> allCurios(LivingEntity entity) {
        List<SlotResult> out = new ArrayList<>();
        CuriosApi.getCuriosInventory(entity).resolve().ifPresent(handler -> {
            for (var entry : handler.getCurios().entrySet()) {
                ICurioStacksHandler stacksHandler = entry.getValue();
                var stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    out.add(new SlotResult(
                        new SlotContext(entry.getKey(), entity, i, false, stacksHandler.isVisible()),
                        stack
                    ));
                }
            }
        });
        return out;
    }

    @Override
    public void registerWearable(Item item, String slot, PlatformAccessoryItem accessory) {
        if (accessory == null) return;

        // Curios resolves an item's curio behaviour from its ICurioItem capability, which it builds
        // from the registered instance; so register one that forwards to our seam callbacks.
        CuriosApi.registerCurio(item, new ICurioItem() {
            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
                accessory.onEquip(stack, new CurioSlot(slotContext, stack), slotContext.entity());
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
                accessory.onUnequip(stack, new CurioSlot(slotContext, stack), slotContext.entity());
            }

            @Override
            public void curioTick(SlotContext slotContext, ItemStack stack) {
                accessory.accessoryTick(stack, new CurioSlot(slotContext, stack), slotContext.entity());
            }

            @Override
            public boolean canEquip(SlotContext slotContext, ItemStack stack) {
                return accessory.canEquip(stack, new CurioSlot(slotContext, stack), slotContext.entity());
            }

            @Override
            public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(
                SlotContext slotContext, UUID uuid, ItemStack stack
            ) {
                return accessory.getAccessoryModifiers(
                    stack, new CurioSlot(slotContext, stack), slotContext.entity(), uuid
                );
            }
        });
    }

    @Override
    public List<Pair<AccessorySlot, ItemStack>> getAllEquipped(LivingEntity entity) {
        return allCurios(entity).stream()
            .map(r -> new Pair<AccessorySlot, ItemStack>(
                new CurioSlot(r.slotContext(), r.stack()),
                r.stack()
            ))
            .toList();
    }

    @Override
    public List<ItemStack> getEquippedIn(LivingEntity entity, String slot) {
        // Curios is flat (no "chest" group level), so the shared flat slot name matches directly.
        List<ItemStack> out = new ArrayList<>();
        CuriosApi.getCuriosInventory(entity).resolve()
            .flatMap(handler -> handler.getStacksHandler(slot))
            .ifPresent(stacksHandler -> {
                var stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    out.add(stacks.getStackInSlot(i));
                }
            });
        return out;
    }

    @Override
    public boolean isEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .map(handler -> handler.isEquipped(item))
            .orElse(false);
    }

    @Override
    public Pair<AccessorySlot, ItemStack> getFirstEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .flatMap(handler -> handler.findFirstCurio(item))
            .map(result -> new Pair<AccessorySlot, ItemStack>(
                new CurioSlot(result.slotContext(), result.stack()),
                result.stack()
            ))
            .orElse(null);
    }

    @Override
    public void registerRenderer(Item item, AccessoryRenderer renderer) {
        // Curios takes a Supplier and only supports the vanilla humanoid models, which is all
        // Lapisworks' accessories use.
        CuriosRendererRegistry.register(item, () -> new ICurioRenderer() {
            @Override
            public <T extends LivingEntity, M extends EntityModel<T>> void render(
                    ItemStack stack, SlotContext slotContext, MatrixStack matrices,
                    net.minecraft.client.render.entity.feature.FeatureRendererContext<T, M> renderLayerParent,
                    VertexConsumerProvider vertexConsumers, int light, float limbSwing,
                    float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                    float headPitch) {
                EntityModel<? extends LivingEntity> model = renderLayerParent.getModel();
                renderer.render(stack, model, matrices, vertexConsumers, light, slotContext.entity(),
                    limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        });
    }
}

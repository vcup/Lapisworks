package com.luxof.lapisworks.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.Multimap;
import com.luxof.lapisworks.platform.Accessories;
import com.luxof.lapisworks.platform.AccessoryRenderer;
import com.luxof.lapisworks.platform.AccessorySlot;
import com.luxof.lapisworks.platform.PlatformAccessoryItem;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

/**
 * Fabric implementation of the accessory seam, backed by Trinkets.
 * <p>
 * A shared item cannot implement Trinkets' {@code Trinket} directly (NeoForge has no Trinkets at
 * all), so items implement {@link PlatformAccessoryItem} and this class wraps them in an anonymous
 * {@code Trinket} that forwards each callback, translating {@link AccessorySlot} to Trinkets'
 * {@code SlotReference}. The NeoForge module does the mirror image against Curios.
 */
public class FabricAccessories implements Accessories {

    /** Adapts a loader-agnostic slot to Trinkets' SlotReference. */
    private static final class TrinketSlot implements AccessorySlot {
        private final SlotReference ref;

        TrinketSlot(SlotReference ref) {
            this.ref = ref;
        }

        @Override public ItemStack getStack() { return ref.inventory().getStack(ref.index()); }
        // TrinketInventory exposes its component, and the component knows the wearer; the inventory
        // itself has no getActor().
        @Override public LivingEntity getEntity() { return ref.inventory().getComponent().getEntity(); }
        @Override public String getSlotName() { return ref.inventory().getSlotType().getName(); }
        @Override public int getIndex() { return ref.index(); }
    }

    @Override
    public void registerWearable(Item item, String slot, PlatformAccessoryItem accessory) {
        // A null accessory means "wearable, but no item-specific behaviour" (Trinkets then uses
        // its own defaults, which is what Hex Casting's baubles rely on).
        Trinket trinket = accessory == null
            ? new Trinket() {}
            : new Trinket() {
                @Override
                public void onEquip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
                    accessory.onEquip(stack, new TrinketSlot(slotRef), entity);
                }

                @Override
                public void onUnequip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
                    accessory.onUnequip(stack, new TrinketSlot(slotRef), entity);
                }

                @Override
                public void tick(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
                    accessory.accessoryTick(stack, new TrinketSlot(slotRef), entity);
                }

                @Override
                public boolean canEquip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
                    return accessory.canEquip(stack, new TrinketSlot(slotRef), entity);
                }

                @Override
                public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(
                    ItemStack stack, SlotReference slotRef, LivingEntity entity, UUID uuid
                ) {
                    return accessory.getAccessoryModifiers(stack, new TrinketSlot(slotRef), entity, uuid);
                }
            };

        TrinketsApi.registerTrinket(item, trinket);
    }

    @Override
    public List<Pair<AccessorySlot, ItemStack>> getAllEquipped(LivingEntity entity) {
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(entity);
        if (opt.isEmpty()) return List.of();

        return opt.get().getAllEquipped().stream()
            .map(pair -> new Pair<AccessorySlot, ItemStack>(new TrinketSlot(pair.getLeft()), pair.getRight()))
            .toList();
    }

    @Override
    public List<ItemStack> getEquippedIn(LivingEntity entity, String slot) {
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(entity);
        if (opt.isEmpty()) return List.of();

        // Trinkets nests slots as group -> slot ("chest" -> "necklace"), while Curios is flat
        // ("necklace"). Shared code only knows the flat name, so search both levels for it.
        List<ItemStack> found = new ArrayList<>();
        for (var group : opt.get().getInventory().values()) {
            TrinketInventory inv = group.get(slot);
            if (inv == null) continue;
            for (int i = 0; i < inv.size(); i++) {
                found.add(inv.getStack(i));
            }
        }
        return found;
    }

    @Override
    public boolean isEquipped(LivingEntity entity, Item item) {
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(entity);
        return opt.isPresent() && opt.get().isEquipped(item);
    }

    @Override
    public Pair<AccessorySlot, ItemStack> getFirstEquipped(LivingEntity entity, Item item) {
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(entity);
        if (opt.isEmpty()) return null;
        try {
            List<Pair<SlotReference, ItemStack>> equipped =
                opt.get().getEquipped(stack -> stack.isOf(item));
            if (equipped.isEmpty()) return null;
            Pair<SlotReference, ItemStack> first = equipped.get(0);
            return new Pair<>(new TrinketSlot(first.getLeft()), first.getRight());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void registerRenderer(Item item, AccessoryRenderer renderer) {
        TrinketRendererRegistry.registerRenderer(item, new TrinketRenderer() {
            @Override
            public void render(ItemStack stack, SlotReference slotReference,
                    EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices,
                    VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
                    float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                    float headYaw, float headPitch) {
                renderer.render(stack, contextModel, matrices, vertexConsumers, light, entity,
                    limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            }
        });
    }
}

package com.luxof.lapisworks.neoforge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * NeoForge equivalent of Fabric's {@code BuiltinItemRendererRegistry}.
 * <p>
 * NeoForge has no such registry, and on 1.20.1 there is no registration event for one either: the
 * supported hook is {@link Item#initializeClient}, which an item's own class overrides so Forge can
 * ask it for an {@link IClientItemExtensions}. Forge calls it from the {@code Item} constructor and
 * rejects an extension that is the item itself ("Don't extend IItemRenderProperties in your item,
 * use an anonymous class instead"), so the renderer cannot be attached from outside the item class
 * the way Fabric allows.
 * <p>
 * This bridges the two: {@code Item.initializeClient} consults {@link #RENDERERS} (populated by
 * shared client init through {@link PlatformEvents#registerItemRenderer}) at <em>draw</em> time,
 * which is long after both the item's construction and that registration, so the lookup always
 * succeeds by then.
 */
public final class NeoForgeItemRendererExtensions {
    private NeoForgeItemRendererExtensions() {}

    private static final Map<Item, PlatformEvents.ItemRenderer> RENDERERS = new HashMap<>();

    static void register(Item item, PlatformEvents.ItemRenderer renderer) {
        RENDERERS.put(item, renderer);
    }

    /**
     * Called from an item's {@code initializeClient} override to hand Forge the extension that draws
     * that item.
     */
    public static void accept(Item item, Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BuiltinModelItemRenderer delegate = null;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (delegate == null) {
                    // Reuse the vanilla renderer instance rather than constructing one: its
                    // constructor eagerly builds chest/banner/bed block entities, so a fresh
                    // instance is wasteful and passing nulls would NPE on those fields.
                    BuiltinModelItemRenderer vanilla = MinecraftClient.getInstance()
                        .getItemRenderer()
                        .getBlockEntityRenderer();

                    delegate = new BuiltinModelItemRenderer(null, null) {
                        @Override
                        public void render(
                            ItemStack stack,
                            ModelTransformationMode mode,
                            MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers,
                            int light,
                            int overlay
                        ) {
                            PlatformEvents.ItemRenderer renderer = RENDERERS.get(item);
                            if (renderer == null) {
                                // Nothing registered yet: fall back to the vanilla behaviour so the
                                // item still draws instead of silently vanishing.
                                vanilla.render(stack, mode, matrices, vertexConsumers, light, overlay);
                                return;
                            }
                            renderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
                        }
                    };
                }
                return delegate;
            }
        });
    }
}

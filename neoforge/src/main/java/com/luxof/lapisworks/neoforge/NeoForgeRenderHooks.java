package com.luxof.lapisworks.neoforge;

import java.util.function.BiConsumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * NeoForge world-render hook that Architectury does not wrap.
 * <p>
 * Fabric's {@code WorldRenderEvents.AFTER_TRANSLUCENT} maps onto {@code RenderLevelStageEvent}
 * filtered to the translucent stage.
 * <p>
 * The item-renderer hook that also used to live here moved to
 * {@link NeoForgeItemRendererExtensions}: NeoForge has no builtin-item-renderer registry, so the
 * renderer has to be supplied through {@code Item#initializeClient} instead of a registration call.
 */
public final class NeoForgeRenderHooks {
    private NeoForgeRenderHooks() {}

    private static boolean levelStageRegistered = false;
    private static BiConsumer<MatrixStack, Float> afterTranslucent = null;

    static void onAfterTranslucent(BiConsumer<MatrixStack, Float> callback) {
        afterTranslucent = callback;
        if (levelStageRegistered) return;
        levelStageRegistered = true;

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderLevelStageEvent.class, event -> {
            // Fabric's AFTER_TRANSLUCENT runs once translucent geometry is done, which is what
            // AFTER_TRANSLUCENT_BLOCKS corresponds to in Forge's stage list.
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
            if (afterTranslucent == null) return;

            // The event's pose stack is the level renderer's current transform, which is the same
            // coordinate space Fabric's WorldRenderContext matrix stack provides.
            afterTranslucent.accept(event.getPoseStack(), event.getPartialTick());
        });
    }
}

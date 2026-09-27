package com.luxof.lapisworks.neoforge.mixin;

import java.util.function.Consumer;

import com.luxof.lapisworks.items.Collar;
import com.luxof.lapisworks.neoforge.NeoForgeItemRendererExtensions;

import net.minecraft.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Attaches the collar's custom item renderer on NeoForge.
 * <p>
 * The collar's model is {@code builtin/entity}, so it needs a {@code BuiltinModelItemRenderer}.
 * Fabric registers one through {@code BuiltinItemRendererRegistry}, but NeoForge has neither that
 * registry nor a registration event for it on 1.20.1: the only supported hook is
 * {@link Item#initializeClient}, which Forge invokes <em>from the Item constructor</em> and which
 * therefore has to be declared by the item class itself.
 * <p>
 * Rather than make the shared {@code Collar} depend on a Forge-only method, this mixin adds the
 * override to it. Forge's {@code Item.initClient()} only calls {@code initializeClient} when the
 * distribution is a client, so this is inert on a dedicated server; the mixin is additionally
 * declared client-only in the mixin config so the class is never applied there at all.
 */
@Mixin(Collar.class)
public abstract class CollarClientMixin extends Item {

    /**
     * Never called: the mixin only supplies the {@code initializeClient} override, and Mixin merges
     * that into {@code Collar} without running this constructor. It exists so the superclass link is
     * valid at compile time.
     */
    private CollarClientMixin(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        NeoForgeItemRendererExtensions.accept(this, consumer);
    }
}

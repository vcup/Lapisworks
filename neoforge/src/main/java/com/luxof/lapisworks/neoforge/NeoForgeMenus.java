package com.luxof.lapisworks.neoforge;

import com.luxof.lapisworks.platform.Menus;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkHooks;

/**
 * NeoForge implementation of the menu seam.
 * <p>
 * Both {@code ScreenHandlerType}'s constructor and {@code HandledScreens.register} are public here,
 * so no wrapper library is needed; a menu's extra opening data arrives through
 * {@code IContainerFactory} and is sent by {@code NetworkHooks.openScreen}.
 */
public class NeoForgeMenus implements Menus {

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> simpleType(SimpleFactory<T> factory) {
        return new ScreenHandlerType<>(
            (IContainerFactory<T>) (syncId, inventory, buf) -> factory.create(syncId, inventory),
            FeatureSet.empty()
        );
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> extendedType(ExtendedFactory<T> factory) {
        return new ScreenHandlerType<>(
            (IContainerFactory<T>) factory::create,
            FeatureSet.empty()
        );
    }

    @Override
    public <T extends ScreenHandler, S extends net.minecraft.client.gui.screen.ingame.HandledScreen<T>>
        void registerScreen(ScreenHandlerType<T> type, ScreenBinder<T, S> binder) {
        HandledScreens.register(type, binder::create);
    }

    @Override
    public void openExtended(ServerPlayerEntity player, ExtendedProvider provider) {
        // NetworkHooks wants a NamedScreenHandlerFactory; the provider already is one through its
        // createMenu/getDisplayName pair, so it is adapted rather than rewritten.
        NamedScreenHandlerFactory factory = new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, net.minecraft.entity.player.PlayerEntity p) {
                return provider.createMenu(syncId, inv, p);
            }
        };
        // Forge hands the writer the packet's buffer at this point, so the data is encoded once.
        NetworkHooks.openScreen(player, factory, provider::saveExtraData);
    }
}

package com.luxof.lapisworks.fabric;

import com.luxof.lapisworks.platform.Menus;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Fabric implementation of the menu seam, backed by the Fabric API.
 * <p>
 * Both {@code ScreenHandlerType}'s constructor and {@code HandledScreens.register} are private on
 * Fabric, so producing a type and binding a screen to one requires Fabric API's
 * {@code ExtendedScreenHandlerType} and {@code ScreenRegistry}.
 */
public class FabricMenus implements Menus {

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> simpleType(SimpleFactory<T> factory) {
        return new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> factory.create(syncId, inventory));
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> extendedType(ExtendedFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory::create);
    }

    @Override
    public <T extends ScreenHandler, S extends net.minecraft.client.gui.screen.ingame.HandledScreen<T>>
        void registerScreen(ScreenHandlerType<T> type, ScreenBinder<T, S> binder) {
        ScreenRegistry.register(type, binder::create);
    }

    @Override
    public void openExtended(ServerPlayerEntity player, ExtendedProvider provider) {
        // Fabric requires an ExtendedScreenHandlerFactory for an extended menu; opening one through
        // vanilla's openHandledScreen throws. The factory writes the data itself, at the point Fabric
        // asks for it, so the payload is encoded exactly once.
        ExtendedScreenHandlerFactory factory = new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                provider.saveExtraData(buf);
            }

            @Override
            public Text getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                return provider.createMenu(syncId, inv, p);
            }
        };
        player.openHandledScreen(factory);
    }
}

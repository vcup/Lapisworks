package com.luxof.lapisworks.platform;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * The seam over screen-handler registration and opening.
 * <p>
 * Replaces Architectury's {@code MenuRegistry}. This is the one place where the two loaders diverge
 * enough that shared code cannot express it:
 * <ul>
 *   <li>On Fabric, {@code ScreenHandlerType}'s constructor and {@code HandledScreens.register} are
 *       both <em>private</em>, so producing a type and binding a screen needs Fabric API's
 *       {@code ExtendedScreenHandlerType} and {@code ScreenRegistry}.</li>
 *   <li>On NeoForge both are <em>public</em>, and a menu's extra opening data arrives through
 *       {@code IContainerFactory} plus {@code NetworkHooks.openScreen}.</li>
 * </ul>
 * A menu that carries extra opening data must also be opened through the platform's extended path.
 * Opening one with vanilla's {@code openHandledScreen} makes Fabric throw
 * "Extended screen handler ... must be opened with an ExtendedScreenHandlerFactory".
 */
public interface Menus {
    Menus INSTANCE = Seams.load(Menus.class);

    /** Creates a menu type whose opening packet carries no extra data. */
    <T extends ScreenHandler> ScreenHandlerType<T> simpleType(SimpleFactory<T> factory);

    /** Creates a menu type whose opening packet carries extra data. */
    <T extends ScreenHandler> ScreenHandlerType<T> extendedType(ExtendedFactory<T> factory);

    /** Binds a screen to a menu type. Client only. */
    <T extends ScreenHandler, S extends net.minecraft.client.gui.screen.ingame.HandledScreen<T>>
        void registerScreen(ScreenHandlerType<T> type, ScreenBinder<T, S> binder);

    /**
     * Opens {@code provider}'s menu for {@code player}, letting the platform call
     * {@link ExtendedProvider#saveExtraData} at the moment its own API expects the data to be
     * written.
     * <p>
     * The data is deliberately not passed in already-encoded: both loaders hand the writer a buffer
     * at a specific point in the open sequence, and encoding it separately would either duplicate the
     * payload or write it at the wrong time.
     */
    void openExtended(ServerPlayerEntity player, ExtendedProvider provider);

    /**
     * What a block entity implements to be openable as a menu carrying extra data.
     * <p>
     * Stands in for Fabric's {@code ExtendedScreenHandlerFactory} and Architectury's
     * {@code ExtendedMenuProvider}, neither of which exists on the other loader.
     * <p>
     * Extends {@link net.minecraft.screen.NamedScreenHandlerFactory} deliberately, mirroring both of
     * those: {@code BlockWithEntity.createScreenHandlerFactory} returns the block entity <em>only</em>
     * when it is a {@code NamedScreenHandlerFactory}, so a provider that did not extend it would make
     * that method return null and the menu could never be opened by any route.
     */
    interface ExtendedProvider extends net.minecraft.screen.NamedScreenHandlerFactory {
        @Override
        ScreenHandler createMenu(int syncId, PlayerInventory inventory, net.minecraft.entity.player.PlayerEntity player);

        @Override
        Text getDisplayName();

        /** Writes the data the client needs to build the matching handler. */
        void saveExtraData(PacketByteBuf buf);
    }

    @FunctionalInterface
    interface SimpleFactory<T extends ScreenHandler> {
        T create(int syncId, PlayerInventory inventory);
    }

    @FunctionalInterface
    interface ExtendedFactory<T extends ScreenHandler> {
        T create(int syncId, PlayerInventory inventory, PacketByteBuf buf);
    }

    @FunctionalInterface
    interface ScreenBinder<T extends ScreenHandler, S extends net.minecraft.client.gui.screen.ingame.HandledScreen<T>> {
        S create(T handler, PlayerInventory inventory, Text title);
    }
}

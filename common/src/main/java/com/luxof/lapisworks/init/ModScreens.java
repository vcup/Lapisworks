package com.luxof.lapisworks.init;

import com.luxof.lapisworks.client.screens.ChalkWithPatternScreen;
import com.luxof.lapisworks.client.screens.ChalkWithPatternScreenHandler;
import com.luxof.lapisworks.client.screens.EnchBrewerScreen;
import com.luxof.lapisworks.client.screens.EnchBrewerScreenHandler;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import com.luxof.lapisworks.platform.Menus;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreens {
    /**
     * Registered in {@link #whatWasThatTF2CommentAboutMakingBadGUICodeSoYouDontHaveToTouchItAgain()}
     * rather than in the field initializers.
     * <p>
     * A {@code ScreenHandlerType} is a registry write, so it has to go through the platform registry
     * seam; doing it from a static initializer also made the write happen whenever this class was
     * first touched, which on NeoForge could be after the registries were frozen. The type itself
     * also cannot be constructed from shared code, because Fabric keeps its constructor private while
     * NeoForge makes it public, so {@link Menus} builds it per platform.
     */
    public static ScreenHandlerType<EnchBrewerScreenHandler> ENCH_BREWER_SCREEN_HANDLER;
    public static ScreenHandlerType<ChalkWithPatternScreenHandler> CHALK_WITH_PATTERN_SCREEN_HANDLER;

    /** Registers the screen handler types. Runs in the early (registry) init phase. */
    public static void whatWasThatTF2CommentAboutMakingBadGUICodeSoYouDontHaveToTouchItAgain() {
        ScreenHandlerType<EnchBrewerScreenHandler> brewer =
            Menus.INSTANCE.simpleType((syncId, plrInv) -> new EnchBrewerScreenHandler(syncId, plrInv));
        LapisworksRegistry.INSTANCE.register(Registries.SCREEN_HANDLER, id("ench_brewer"), brewer);
        ENCH_BREWER_SCREEN_HANDLER = brewer;

        ScreenHandlerType<ChalkWithPatternScreenHandler> chalk = Menus.INSTANCE.extendedType(
            (syncId, plrInv, buf) -> new ChalkWithPatternScreenHandler(syncId, plrInv, buf)
        );
        LapisworksRegistry.INSTANCE.register(Registries.SCREEN_HANDLER, id("chalk_with_pattern"), chalk);
        CHALK_WITH_PATTERN_SCREEN_HANDLER = chalk;
    }
    
    public static void registerOnClient() {
        Menus.INSTANCE.registerScreen(ENCH_BREWER_SCREEN_HANDLER, EnchBrewerScreen::new);
        Menus.INSTANCE.registerScreen(CHALK_WITH_PATTERN_SCREEN_HANDLER, ChalkWithPatternScreen::new);
    }
}

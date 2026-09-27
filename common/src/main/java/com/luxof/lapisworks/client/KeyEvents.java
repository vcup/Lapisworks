package com.luxof.lapisworks.client;

import at.petrak.hexcasting.common.lib.HexSounds;

import com.luxof.lapisworks.init.ModItems;

import static com.luxof.lapisworks.Lapisworks.accessoryEquipped;
import static com.luxof.lapisworks.LapisworksIDs.OPEN_CASTING_GRID;

import io.netty.buffer.Unpooled;

import com.luxof.lapisworks.platform.ClientRegistrations;

import com.luxof.lapisworks.platform.Networking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;

import org.lwjgl.glfw.GLFW;

public class KeyEvents {
    public static KeyBinding useCastingRing = new KeyBinding(
        "keys.lapisworks.use_casting_ring",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_G, // sorry hexical that keybind is mine now
        "key_category.lapisworks.lapisworks"
    );

    /**
     * Registers the key binding through the platform seam.
     * <p>
     * This must run from the loader's own key-binding registration point. Registering it from a
     * static initializer (or deferred through client setup) ran after the key-mapping window had
     * closed, which logged "Key mapping ... registered after event" and left the binding out of the
     * controls screen.
     */
    public static void registerKeyBinding() {
        ClientRegistrations.INSTANCE.registerKeyBinding(useCastingRing);
    }

    public static void endClientTick(MinecraftClient client) {
        if (useCastingRing.wasPressed()) { onPressUseCastingRing(client); }
    }
    
    public static void onPressUseCastingRing(MinecraftClient client) {
        if (client.player == null) return;
        else if (
            !(accessoryEquipped(client.player, (Item)ModItems.AMEL_RING) ||
            accessoryEquipped(client.player, (Item)ModItems.AMEL_RING2))
        ) return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        if (client.player.isSneaking())
            client.player.playSound(HexSounds.STAFF_RESET, 1f, 1f);

        buf.writeBoolean(client.player.isSneaking());
        Networking.INSTANCE.sendToServer(OPEN_CASTING_GRID, buf);
    }

    public static void staticInit() {}
}

package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.items.shit.ITotem;

import static com.luxof.lapisworks.Lapisworks.tryGetTotem;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(
        method = "getActiveTotemOfUndying",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void lapisworks$giveMyOwnTotemIfYouCantFindOne(
        PlayerEntity player,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        var totem = tryGetTotem(player);
        if (totem != null)
            cir.setReturnValue(
                totem.getLeft().getItem() instanceof ITotem iTotem
                    ? iTotem.getFloatingItemToShowClientPlayerOnRevive(
                        totem.getLeft(),
                        totem.getRight()
                    )
                    : totem.getLeft()
            );
    }
}

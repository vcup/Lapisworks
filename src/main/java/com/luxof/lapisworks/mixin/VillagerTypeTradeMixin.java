package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.init.ModEntities;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.village.VillagerType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Lapisworks registers its own villager type ({@code lapisworks:jack}, see
 * {@link ModEntities#JACK}), but vanilla's {@code TradeOffers.TypeAwareBuyForOneEmeraldFactory}
 * (SRG: {@code VillagerTrades.EmeraldsForVillagerTypeItem}) constructor refuses to build at all
 * unless *every* registered villager type has an entry in the trade table it is handed:
 *
 * <pre>BuiltInRegistries.VILLAGER_TYPE.stream().filter(type -&gt; !trades.containsKey(type))
 *     .findAny().ifPresent(type -&gt; { throw new IllegalStateException("Missing trade for villager type: " + type); });</pre>
 *
 * so without an entry the whole trade table fails to initialise ("Missing trade for villager type:
 * lapisworks:jack") and the game dies the first time a villager's trades are built - on Forge that
 * is during server startup. (An exemption instead of a real entry would only move the failure to
 * {@code getOffer}, which builds an ItemStack from the null item.)
 *
 * The target is package-private, hence the string target.
 */
@Mixin(targets = "net.minecraft.village.TradeOffers$TypeAwareBuyForOneEmeraldFactory")
public abstract class VillagerTypeTradeMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, index = 4)
    private static Map<VillagerType, Item> lapisworks$giveJackItsOwnTrade(
        Map<VillagerType, Item> trades
    ) {
        if (trades.containsKey(ModEntities.JACK))
            return trades;

        Map<VillagerType, Item> withJack = new HashMap<>(trades);
        withJack.put(ModEntities.JACK, Items.LAPIS_LAZULI);
        return withJack;
    }
}

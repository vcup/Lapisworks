package com.luxof.lapisworks.init;

import java.util.List;

import com.luxof.lapisworks.platform.PlatformEvents;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import static net.minecraft.loot.provider.number.ConstantLootNumberProvider.create;

public class LapisworksLoot {
    public static final Identifier STRONGHOLD_LIBRARY = idMC("chests/stronghold_library");
    public static final List<Identifier> lowChanceOnes = List.of(
        idMC("chests/buried_treasure"),
        idMC("chests/desert_pyramid"),
        //idMC("chest/shipwreck_treasure"), would pirates still exist? would they even dare to do this?
        idMC("chests/igloo_chest")
    );
    public static final List<Identifier> midChanceOnes = List.of(
        idMC("chests/stronghold_corridor"),
        idMC("chests/stronghold_crossing")
    );

    public static void gibLootexclamationmark() {
		PlatformEvents.INSTANCE.onModifyLootTable(
			(id, context, builtin) -> {

                // .with(ItemEntry.builder(...)) uses vanilla's with(LootPoolEntry.Builder) overload,
                // which calls .build() internally and appends to the same entries list, so no
                // explicit .build() is needed.
                LootPool.Builder lapisLoot = LootPool.builder()
                    .with(ItemEntry.builder(ModItems.WIZARD_DIARIES));
                
                float rolls = -1.0f;
                if (lowChanceOnes.indexOf(id) != -1) { rolls = 0.1f; }
                else if (midChanceOnes.indexOf(id) != -1) { rolls = 0.5f; }
                else if (id.equals(STRONGHOLD_LIBRARY)) { rolls = 1.5f; }

                if (rolls > -0.5f) { context.addPool(lapisLoot.rolls(create(rolls)).build()); }

            }
		);
    }
    // shorthand
    public static Identifier idMC(String any) { return new Identifier(any); }
}

package com.luxof.lapisworks.init;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.blocks.*;
import com.luxof.lapisworks.blocks.bigchalk.*;
import com.luxof.lapisworks.blocks.entities.*;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.registry.Registries;
import com.luxof.lapisworks.platform.LapisworksRegistry;
import net.minecraft.registry.Registry;

/** Hey! oh what'd you say? oh what'd you just say just now?
 * Watch what you say, 'cause baby you'll break my heart!
 * Oh, what'd you say? But baby know if you look inside, you'll find a heart of glass! */
public class ModBlocks {
    public static ConjuredColorable CONJURED_COLORABLE = new ConjuredColorable();
    public static Mind MIND_BLOCK = new Mind();
    public static LiveJukebox LIVE_JUKEBOX_BLOCK = new LiveJukebox();
    public static JumpSlate JUMP_SLATE_AM1 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AM2 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AMETH = new JumpSlate();
    public static JumpSlate JUMP_SLATE_LAPIS = new JumpSlate();
    public static ReboundSlate REBOUND_SLATE_1 = new ReboundSlate();
    public static ReboundSlate REBOUND_SLATE_2 = new ReboundSlate();
    public static SimpleImpetus SIMPLE_IMPETUS = new SimpleImpetus();
    public static EnchBrewer ENCH_BREWER = new EnchBrewer();
    public static UncraftedCondenser UNCRAFTED_CONDENSER = new UncraftedCondenser();
    public static MediaCondenser MEDIA_CONDENSER = new MediaCondenser();
    public static Chalk CHALK = new Chalk();
    public static ChalkWithPattern CHALK_WITH_PATTERN = new ChalkWithPattern();
    public static TuneableAmethyst TUNEABLE_AMETHYST = new TuneableAmethyst();
    public static Ritus RITUS = new Ritus(true);
    public static BigChalkPart BIG_CHALK_PART = new BigChalkPart();
    public static BigChalkCenter BIG_CHALK_CENTER = new BigChalkCenter();


    public static BlockEntityType<MindEntity> MIND_ENTITY_TYPE =
        meow(MindEntity::new, MIND_BLOCK);
    
    public static BlockEntityType<LiveJukeboxEntity> LIVE_JUKEBOX_ENTITY_TYPE =
        meow(LiveJukeboxEntity::new, LIVE_JUKEBOX_BLOCK);
    
    public static BlockEntityType<SimpleImpetusEntity> SIMPLE_IMPETUS_ENTITY_TYPE =
        meow(SimpleImpetusEntity::new, SIMPLE_IMPETUS);
    
    public static BlockEntityType<EnchBrewerEntity> ENCH_BREWER_ENTITY_TYPE =
        meow(EnchBrewerEntity::new, ENCH_BREWER);
    
    public static BlockEntityType<MediaCondenserEntity> MEDIA_CONDENSER_ENTITY_TYPE =
        meow(MediaCondenserEntity::new, MEDIA_CONDENSER);
    
    public static BlockEntityType<ChalkEntity> CHALK_ENTITY_TYPE =
        meow(ChalkEntity::new, CHALK);
    
    public static BlockEntityType<ChalkWithPatternEntity> CHALK_WITH_PATTERN_ENTITY_TYPE =
        meow(ChalkWithPatternEntity::new, CHALK_WITH_PATTERN);
    
    public static BlockEntityType<TuneableAmethystEntity> TUNEABLE_AMETHYST_ENTITY_TYPE =
        meow(TuneableAmethystEntity::new, TUNEABLE_AMETHYST);

    public static BlockEntityType<RitusEntity> RITUS_ENTITY_TYPE =
        meow(RitusEntity::new, RITUS);
    
    public static BlockEntityType<BigChalkCenterEntity> BIG_CHALK_CENTER_ENTITY_TYPE =
        meow(BigChalkCenterEntity::new, BIG_CHALK_CENTER);


    public static void wearASkirt() {
        pickACropTop("conjureable", CONJURED_COLORABLE);
        pickACropTop("mind", MIND_BLOCK);
        pickACropTop("amel_constructs/live_jukebox", LIVE_JUKEBOX_BLOCK);
        pickACropTop("amel_constructs/jumpslate/am1", JUMP_SLATE_AM1);
        pickACropTop("amel_constructs/jumpslate/am2", JUMP_SLATE_AM2);
        pickACropTop("amel_constructs/jumpslate/ameth", JUMP_SLATE_AMETH);
        pickACropTop("amel_constructs/jumpslate/lapis", JUMP_SLATE_LAPIS);
        pickACropTop("amel_constructs/jumpslate/rebound_1", REBOUND_SLATE_1);
        pickACropTop("amel_constructs/jumpslate/rebound_2", REBOUND_SLATE_2);
        pickACropTop("amel_constructs/simple_impetus", SIMPLE_IMPETUS);
        pickACropTop("amel_constructs/enchbrewer", ENCH_BREWER);
        pickACropTop("media_condenser_unit", MEDIA_CONDENSER);
        pickACropTop("uncrafted_condenser", UNCRAFTED_CONDENSER);
        pickACropTop("chalk", CHALK);
        pickACropTop("chalk_with_pattern", CHALK_WITH_PATTERN);
        pickACropTop("tuneable_amethyst", TUNEABLE_AMETHYST);
        pickACropTop("ritus", RITUS);
        pickACropTop("big_chalk/part", BIG_CHALK_PART);
        pickACropTop("big_chalk/center", BIG_CHALK_CENTER);
        dontForgetStockings("mind_entity_type", MIND_ENTITY_TYPE);
        dontForgetStockings("live_jukebox_entity_type", LIVE_JUKEBOX_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/simple_impetus", SIMPLE_IMPETUS_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/enchbrewer", ENCH_BREWER_ENTITY_TYPE);
        dontForgetStockings("media_condenser_unit", MEDIA_CONDENSER_ENTITY_TYPE);
        dontForgetStockings("chalk", CHALK_ENTITY_TYPE);
        dontForgetStockings("chalk_with_pattern", CHALK_WITH_PATTERN_ENTITY_TYPE);
        dontForgetStockings("tuneable_amethyst", TUNEABLE_AMETHYST_ENTITY_TYPE);
        dontForgetStockings("ritus", RITUS_ENTITY_TYPE);
        dontForgetStockings("big_chalk_center", BIG_CHALK_CENTER_ENTITY_TYPE);
    }

    public static void pickACropTop(String name, Block block) {
        LapisworksRegistry.INSTANCE.register(Registries.BLOCK, id(name), block);
    }

    public static <T extends BlockEntityType<?>> void dontForgetStockings(String name, T blockEntityType) {
        LapisworksRegistry.INSTANCE.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
    }

    @SuppressWarnings("null")
    public static <BE extends BlockEntity> BlockEntityType<BE> meow(
        BlockEntityFactory<BE> constructor,
        Block block
    ) {
        return new BlockEntityType<BE>(
            constructor,
            ImmutableSet.of(block),
            null
        );
    }
}

package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.lib.HexRegistries;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LapisworksIDs {
    /** shorthand */
    private static Text t(String str) { return Text.translatable(str); }

    // entity attribute modifiers
    public static final UUID REACH_ENHANCEMENT_UUID = UUID.fromString("53276a15-c1f6-4d56-ba0f-2b1d7312d679");
    public static final UUID ATK_RANGE_ENHANCEMENT_UUID = UUID.fromString("46519d02-6197-46a6-9df5-f80dd263adb2");

    // packets
    public static final Identifier OPEN_CASTING_GRID = id("open_casting_grid");
    public static final Identifier SEND_SENT = id("send_sent");
    public static final Identifier SEND_PWSHAPE_PATS = id("send_pwshape_pats");
    public static final Identifier DOWSE_TS = id("dowse_ts");
    public static final Identifier DOWSE_RESULT = id("dowse_result");
    public static final Identifier SET_PATTERNS_ON_CHALK = id("set_patterns_on_chalk");
    public static final Identifier GIB_DUST = id("gib_dust");
    public static final Identifier APPLY_PULL_FOR_TIME = id("apply_lingering_accel");
    public static final Identifier ROBBIES_EXALT_PACKET = id("do_robbies_exalt");
    public static final Identifier UNLOCK_SHIT_FOR_HEXCESSIBLE = id("unlock_pwshape_hexcessible");

    // items and shit
    public static final Identifier LAPIS_MAGIC_SHIT_GROUP = id("lapismagicshitgroup");
    public static final Identifier AMEL_TAG_ID = id("amel");
    public static final Identifier POTION_TAG_ID = id("potions");
    public static final Identifier TOTEM_TAG_ID = id("totem");
    // the tags themselves
    public static final TagKey<Item> AMEL_TAG = TagKey.of(RegistryKeys.ITEM, AMEL_TAG_ID);
    public static final TagKey<Item> POTION_TAG = TagKey.of(RegistryKeys.ITEM, POTION_TAG_ID);
    public static final TagKey<Item> TOTEM_TAG = TagKey.of(RegistryKeys.ITEM, TOTEM_TAG_ID);

    // block tags
    // ids
    public static final Identifier CHALK_CONNECTABLE_TAG_ID = id("chalk_connectable");
    public static final Identifier CANT_PLACE_CHALK_ON_TAG_ID = id("cant_place_chalk_on");
    public static final Identifier ONETIMERITUAL_BURN_BLACKLIST_TAG_ID = id("onetimeritual_burn_blacklist");
    // tags
    public static final TagKey<Block> CANT_PLACE_CHALK_ON_TAG = TagKey.of(RegistryKeys.BLOCK, CANT_PLACE_CHALK_ON_TAG_ID);
    public static final TagKey<Block> CHALK_CONNECTABLE_TAG = TagKey.of(RegistryKeys.BLOCK, CHALK_CONNECTABLE_TAG_ID);
    public static final TagKey<Block> ONETIMERITUAL_BURN_BLACKLIST_TAG = TagKey.of(RegistryKeys.BLOCK, ONETIMERITUAL_BURN_BLACKLIST_TAG_ID);

    // action tags
    // ids
    public static final Identifier GRAND_RITUAL_BLACKLIST_TAG_ID = id("grand_ritual_blacklist");
    public static final Identifier CANNOT_MODIFY_COST_TAG_ID = new Identifier("hexcasting", "cannot_modify_cost"); // futureproof
    // tags
    public static final TagKey<ActionRegistryEntry> GRAND_RITUAL_BLACKLIST_TAG = TagKey.of(HexRegistries.ACTION, GRAND_RITUAL_BLACKLIST_TAG_ID);
    public static final TagKey<ActionRegistryEntry> CANNOT_MODIFY_COST_TAG = TagKey.of(HexRegistries.ACTION, CANNOT_MODIFY_COST_TAG_ID); // futureproof

    // advancements
    public static final Identifier GOT_LAPIS = id("got_lapis");
    public static final Identifier CLUEDIN_ADVANCEMENT = id("cluedin");

    // V.A.U.L.T.
    public static final Identifier PLAYER_VAULT = id("player_vault");
    public static final Identifier CASTENV_VAULT = id("castenv_vault");

    // tooltips
    public static final Text DIARIES_TOOLTIP_1 = t("tooltips.lapisworks.wizard_diaries.1");
    public static final Text DIARIES_TOOLTIP_2 = t("tooltips.lapisworks.wizard_diaries.2");
    public static final Text DIARIES_TOOLTIP_3 = t("tooltips.lapisworks.wizard_diaries.3");
    public static final Text DIARIES_TOOLTIP_4 = t("tooltips.lapisworks.wizard_diaries.4");

    // item n blocks n shi text
    public static final Text AMEL = t("hexcasting.mishap.bad_item.amel");
    public static final Text MEDIA_CONDENSER = t("block.lapisworks.media_condenser_unit");

    public static final Text LINKABLE_MEDIA_BLOCK = t("mishaps.lapisworks.descs.linkable_media_block");
    public static final Text IMBUEABLE = t("mishaps.lapisworks.descs.imbueable");
    public static final Text INFUSEABLE_WITH_SMIND = t("mishaps.lapisworks.descs.smind_infuseable");
    public static final Text ENTITY_INFUSEABLE_WITH_SMIND = t("mishaps.lapisworks.descs.smind_infuseable_entity");
    public static final Text READABLE = t("mishaps.lapisworks.descs.readable");
    public static final Text WRITEABLE = t("mishaps.lapisworks.descs.writeable");
    public static final Text NON_IOTAHOLDER = t("mishaps.lapisworks.descs.noniotaholder");
    public static final Text READONLY_HOLDER = t("mishaps.lapisworks.descs.readonly");
    public static final Text SCROLL = t("mishaps.lapisworks.descs.scrolls");
    public static final Text PAT_SCROLL = t("mishaps.lapisworks.descs.scroll_with_pat");
    public static final Text GREAT_SCROLL = t("mishaps.lapisworks.descs.scroll_with_gs");
    public static final Text FULL_SIMPLE_MIND = t("mishaps.lapisworks.bad_block.full_mind");
    public static final Text MAINHAND = t("hands.lapisworks.main");
    public static final Text OFFHAND = t("hands.lapisworks.off");
    public static final Text GENERIC_BADITEM = t("mishaps.lapisworks.some_hand.bad_item.generic");
    public static final Text MIND_BLOCK = t("block.lapisworks.mind");
    public static final Text LIVE_JUKEBOX_BLOCK = t("block.lapisworks.amel_constructs.live_jukebox");
    public static final Text SIMP_IMP_BLOCK = t("block.lapisworks.amel_constructs.simple_impetus");
    public static final Text NOTELIST = t("mishaps.lapisworks.invalid_iota.need_notelist.intlist");
    public static final Text NOTELIST_MOFO = t("mishaps.lapisworks.invalid_iota.need_notelist.intlistmotherfucker");
    public static final Text NOTELIST_OUTOFRANGE = t("mishaps.lapisworks.invalid_iota.need_notelist.outofrange");
    public static final Text SCRYING_MIND_START = t("render.lapisworks.scryinglens.mind.start");
    public static final Text SCRYING_MIND_END = t("render.lapisworks.scryinglens.mind.end");
    public static final Text LAPISMAGICSHITGROUPTEXT = t("itemgroup.lapisworks.lapismagicshitgroup");
    public static final Text GOT_ALL_DIARIES = t("notif.lapisworks.wizard_diaries.all_gotten");
    public static final Text DIARY_UNREADABLE = t("notif.lapisworks.wizard_diaries.unreadable_diary");
    public static final Text ENCHBOOK_WITH_ONE_ENCH = t("mishaps.lapisworks.descs.oneench_enchbook");
    public static final Text ENCHBOOK_WITH_NOTONE_ENCH = t("mishaps.lapisworks.descs.moreench_enchbook");
    public static final Text DOWSER_COULDNT_FIND = t("notif.lapisworks.dowser.couldnt_find");

    // mishaps
    public static final String NOT_ENOUGH = "mishaps.lapisworks.not_enough_items";
    public static final String ALREADY_ENCHANTED = "mishaps.lapisworks.already_enchanted";
    public static final String SPECHAND_NOITEM = "mishaps.lapisworks.some_hand.no_item";
    public static final String SPECHAND_BADITEM = "mishaps.lapisworks.some_hand.bad_item";
    public static final String NOT_EQUIPPED = "mishaps.lapisworks.equipped.not";
    public static final String WRONG_EQUIPPED = "mishaps.lapisworks.equipped.bad";

    public static final String INFUSED_AMEL = "lapisworks:infused_amel";
    public static final String DOWSER_NOT_ENOUGH_MEDIA = "notif.lapisworks.dowser.not_enough_media";
    public static final String GEODE_DOWSER_REQUEST = id("request_from_geode_dowser_item").toString(); // doesn't work otherwise

    // lapixtended
    public static final Identifier MAKE_PARTAMEL_WAND = id("make_partamel_wand");
    // lexical
    public static final String RH_HOLDER = id("righthanded_holder").toString();
    public static final String HEXICAL_IMPETUS_HAND = "impetus_hand";
    public static final String IS_IN_CRADLE = "lapisworks:is_in_cradle";
    public static final Text HOLDER_MAINHAND = t("render.lapisworks.scryinglens.holder.mainhand");
    public static final Text HOLDER_OFFHAND = t("render.lapisworks.scryinglens.holder.offhand");
    // lapisal
    public static final Identifier OPEN_DIMENSIONAL_RIFT = id("open_dimensional_rift");
    public static final Identifier TURN_MIND_TO_WISP = id("turn_mind_to_wisp");
}

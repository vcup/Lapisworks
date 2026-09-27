package com.luxof.lapisworks.mishaps;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import static com.luxof.lapisworks.LapisworksIDs.ALREADY_ENCHANTED;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapAlreadyHasEnchantment extends Mishap {
    private final LivingEntity whatEntity;
    private final Text enchantmentName;
    private final int whatEnchant;
    private final int whatLevel;

    public MishapAlreadyHasEnchantment(LivingEntity whatEntity, Text enchantmentName, int whatEnchant, int whatLevel) {
        this.whatEntity = whatEntity;
        this.enchantmentName = enchantmentName;
        this.whatEnchant = whatEnchant;
        this.whatLevel = whatLevel;
    }

    @Override
    public FrozenPigment accentColor(CastingEnvironment ctx, Context errorCtx) {
        return switch (this.whatEnchant) {
            case 0 -> dyeColor(DyeColor.RED); // fireyfists
            case 1 -> dyeColor(DyeColor.WHITE); // lightningbending
            case 2 -> dyeColor(DyeColor.GRAY); // falldmgres
            case 3 -> dyeColor(DyeColor.LIGHT_BLUE); // longbreath
            case 4 -> dyeColor(DyeColor.PINK); // fireresist
            default -> dyeColor(DyeColor.BLACK); // any other stuff (e.g. unimpl by another mod)
        };
    }

    @Override
    public void execute(CastingEnvironment env, Context errorCtx, List<Iota> stack) {}

    @Override
    public Text errorMessage(CastingEnvironment ctx, Context errorCtx) {
        return Text.translatable(
            ALREADY_ENCHANTED,
            this.whatEntity.getName(),
            this.enchantmentName,
            this.enchantmentName,
            whatLevel
        );
    }
}

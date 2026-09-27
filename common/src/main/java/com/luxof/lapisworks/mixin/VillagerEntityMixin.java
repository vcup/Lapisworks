package com.luxof.lapisworks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.init.ModEntities;
import com.luxof.lapisworks.mixinsupport.ArtMindInterface;
import com.luxof.lapisworks.mixinsupport.JackMinterface;

import static com.luxof.lapisworks.Lapisworks.warn;
import static com.luxof.lapisworks.Lapisworks.hashMapof;
import static com.luxof.lapisworks.Lapisworks.nbtCompoundOf;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements ArtMindInterface, JackMinterface {
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private float usedMindPercentage = 0.0f;
    @Unique private int mindBeingUsedTicks = 0;
    @Unique private int dontUseAgainTicks = 0;
    @Override @Unique public float getUsedMindPercentage() { return this.usedMindPercentage; }
    @Override @Unique public void setUsedMindPercentage(float val) { this.usedMindPercentage = val; }
    @Override @Unique public void incUsedMindPercentage(float amount) { this.usedMindPercentage += amount; }
    @Override @Unique public int getMindBeingUsedTicks() { return this.mindBeingUsedTicks; }
    @Override @Unique public void setMindBeingUsedTicks(int val) { this.mindBeingUsedTicks = val; }
    @Override @Unique public void incMindBeingUsedTicks(int amount) { this.mindBeingUsedTicks += amount; }
    @Override @Unique public void setDontUseAgainTicks(int ticks) { this.dontUseAgainTicks = ticks; }
    @Override @Unique public void incDontUseAgainTicks(int ticks) { this.dontUseAgainTicks += ticks; }
    @Override @Unique public int getDontUseAgainTicks() { return this.dontUseAgainTicks; }

	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        setUsedMindPercentage(nbt.getFloat("LAPISWORKS_MIND_USED"));
        setMindBeingUsedTicks(nbt.getInt("LAPISWORKS_MIND_HEAL_COOLDOWN"));
        profToXpMap = hashMapof(
            nbt.getCompound("defaultXpForProfessionMap"),
            (str) -> Registries.VILLAGER_PROFESSION.get(new Identifier(str)),
            (ele) -> ((NbtInt)ele).intValue()
        );
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("LAPISWORKS_MIND_USED", getUsedMindPercentage());
        nbt.putInt("LAPISWORKS_MIND_HEAL_COOLDOWN", getMindBeingUsedTicks());
        nbt.put(
            "defaultXpForProfessionMap",
            nbtCompoundOf(
                profToXpMap.entrySet().stream()
                    .map(entry ->
                        new Pair<>(entry.getKey().toString(), NbtInt.of(entry.getValue())))
            )
        );
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        // must have cooldown or else heals at same rate as damaged
        if (this.getMindBeingUsedTicks() == 0) {
            if (this.getUsedMindPercentage() > 0.0f) {
                // magic numbers: goes down by 100% over a 2 minute period
                this.incUsedMindPercentage(-(100f / (120f * 20f)));
            }
        } else if (this.getMindBeingUsedTicks() < 0) {
            // if you have a use case for this, hmu and tell me to make it fuck off
            warn("why was mindBeingUsedTicks below 0?");
            this.setMindBeingUsedTicks(0);
        } else {
            this.incMindBeingUsedTicks(-1);
        }
        if (this.getDontUseAgainTicks() > 0) {
            this.incDontUseAgainTicks(-1);
        }
    }



    @Shadow public abstract VillagerData getVillagerData();
    @Shadow public abstract int getExperience();
    @Shadow public abstract void setExperience(int experience);
    @Shadow public abstract void reinitializeBrain(ServerWorld world);

    // mfw
    @Unique
    private void fillRecipesFor(VillagerProfession profession, int level) {
        var profToTradeFactories = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);
        if (profToTradeFactories == null || profToTradeFactories.isEmpty()) return;

        // which yarn remapper forgor the spelling of factories lmao
        TradeOffers.Factory[] offerFactories = profToTradeFactories.get(level);
        if (offerFactories == null) return;

        TradeOfferList offerList = getOffers();
        fillRecipesFromPool(offerList, offerFactories, 2);
    }

    @WrapMethod(method = "setVillagerData")
    public void setVillagerData(VillagerData VD, Operation<Void> og) {
        VillagerData oldVD = getVillagerData();
        if (
            VD.getType() != ModEntities.JACK ||
            VD.getProfession() == oldVD.getProfession()
        ) {
            og.call(VD);
            return;
        }

        setExperience(getDefaultExpForProfession(VD.getProfession()));
        int level = 1;
        while (
            VillagerData.canLevelUp(level) &&
            getExperience() >= VillagerData.getUpperLevelExperience(level)
        ) { level += 1; }

        og.call(VD.withLevel(level));

        for (int i = level - 1; i > 1; i--) {
            fillRecipesFor(VD.getProfession(), i);
        }
    }

    @Unique
    private HashMap<VillagerProfession, Integer> profToXpMap = new HashMap<>(Map.of(
        VillagerProfession.NONE, 0,
        VillagerProfession.NITWIT, 0
    ));
    @Override @Unique
    public int getDefaultExpForProfession(VillagerProfession profession) {
        if (!profToXpMap.containsKey(profession))
            // XP amount: around levels 2 to 3
            profToXpMap.put(profession, 60 + (int)(30.0 * Math.random() - 15.0));

        return profToXpMap.get(profession);
    }
}

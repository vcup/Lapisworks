package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.luxof.lapisworks.interop.hexical.blocks.HolderEntity;

import static com.luxof.lapisworks.LapisworksIDs.HEXICAL_IMPETUS_HAND;
import static com.luxof.lapisworks.LapisworksIDs.RH_HOLDER;
import static com.luxof.lapisworks.Lapisworks.HEXICAL_INTEROP;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import miyucomics.hexical.features.pedestal.PedestalBlockEntity;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// slightly higher prio than Miyu's mixin
// this should make my stuff run before hers
// also makes this run before Slate Works' i think
// so if Slate Works and Hexical disagreed on where the funcs should go first
// i decide that pedestals have the first turn (unintended but ok)
@Mixin(value = CircleCastEnv.class, remap = false, priority = 999)
public abstract class CircleCastEnvMixin extends CastingEnvironment {
    protected CircleCastEnvMixin(ServerWorld world) { super(world); }

    @Shadow
    public abstract CircleExecutionState circleState();

    @WrapMethod(method = {"getPrimaryStacks"})
    public List<HeldItemInfo> getPrimaryStacks(Operation<List<HeldItemInfo>> og) {
        List<HeldItemInfo> init = new ArrayList<>(og.call(new Object[0]));
        if (!HEXICAL_INTEROP) return init;

        NbtCompound userData = this.circleState().currentImage.getUserData();
        if (userData.contains(HEXICAL_IMPETUS_HAND)) {
            init.add(new HeldItemInfo(this.getPedestal().getStack(0), Hand.OFF_HAND));
        }
        if (userData.contains(RH_HOLDER)) {
            init.add(this.getRightHandedHolder().heldInfo);
        }
        return init;
    }

    @Inject(at = {@At("HEAD")}, method = {"replaceItem"}, cancellable = true)
    public void replaceItem(
        Predicate<ItemStack> stackOk,
        ItemStack replaceWith,
        @NotNull Hand hand,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!HEXICAL_INTEROP) return;
        NbtCompound userData = this.circleState().currentImage.getUserData();
        // null check because some monkey (me) may use 4 hands
        if (userData.contains(HEXICAL_IMPETUS_HAND) && (hand == Hand.OFF_HAND || hand == null)) {
            Inventory LHInv = (Inventory)this.getPedestal();
            if (stackOk.test(LHInv.getStack(0))) {
                LHInv.setStack(0, replaceWith);
                cir.setReturnValue(true);
            }
        }
        if (userData.contains(RH_HOLDER) && (hand == Hand.MAIN_HAND || hand == null)) {
            Inventory RHInv = (Inventory)this.getRightHandedHolder();
            if (stackOk.test(RHInv.getStack(0))) {
                RHInv.setStack(0, replaceWith);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private HolderEntity getRightHandedHolder() {
        if (!HEXICAL_INTEROP) return null;
        int[] p = this.circleState().currentImage.getUserData().getIntArray(RH_HOLDER);
        HolderEntity holder = (HolderEntity)this.getWorld().getBlockEntity(
            new BlockPos(p[0], p[1], p[2])
        );

        assert holder != null;

        return holder;
    }

    @Unique
    private PedestalBlockEntity getPedestal() {
        if (!HEXICAL_INTEROP) return null;
        int[] position = this.circleState().currentImage.getUserData().getIntArray("impetus_hand");
        ServerWorld world = this.getWorld();
        PedestalBlockEntity pedestal = (PedestalBlockEntity)world.getBlockEntity(
            new BlockPos(position[0], position[1], position[2])
        );

        assert pedestal != null;

        return pedestal;
    }
}

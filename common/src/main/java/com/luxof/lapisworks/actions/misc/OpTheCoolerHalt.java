package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import java.util.List;

import kotlin.Pair;

public class OpTheCoolerHalt implements Action {
    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> newStack = img.getStack();
        // no getIntAbove?
        int lastIdx = newStack.size() - 1;
        int done = OperatorUtils.getPositiveInt(newStack, lastIdx, newStack.size());
        newStack.remove(lastIdx);
        // wouldn't it be funny if i renamed cont to cunny
        SpellContinuation newCont = cont;

        while (done > 0 && newCont instanceof SpellContinuation.NotDone /* notDoneCont */) {
            // if i can't use this                                         ^^^^^^^^^^^
            // then why can't i define a variable with the same fucking name?
            //SpellContinuation.NotDone notDoneCont = (SpellContinuation.NotDone)newCont;
            SpellContinuation.NotDone notDone = (SpellContinuation.NotDone)newCont;
            Pair<Boolean, List<Iota>> newInfo = notDone.getFrame().breakDownwards(newStack);
            done -= newInfo.getFirst() ? 1 : 0;
            newStack = newInfo.getSecond();
            newCont = notDone.getNext();
        }

        if (done > 0) newStack = List.of();
        
        CastingImage newImage = img.withUsedOp().copy(
            newStack,
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
        return new OperationResult(newImage, List.of(), newCont, HexEvalSounds.NORMAL_EXECUTE);
    }
}

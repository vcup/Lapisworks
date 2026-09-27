package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import com.luxof.lapisworks.frames.FrameExecuteManyTimes;

import static com.luxof.lapisworks.Lapisworks.CastingImgWithStack;

import java.util.ArrayList;
import java.util.List;

public class OpForNInRange implements Action {
    private final boolean isThisKitkat;
    private final int argc;
    public OpForNInRange(boolean isThisKitkat) {
        this.isThisKitkat = isThisKitkat;
        this.argc = isThisKitkat ? 2 : 3;
    }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = img.getStack();
        if (stack.size() < argc)
            throw new MishapNotEnoughArgs(argc, stack.size());

        SpellList instrs = OperatorUtils.evaluatable(
            stack.get(stack.size() - 1),
            stack.size() - 1
        ).map(iota -> new SpellList.LList(List.of(iota)), list -> list);
        stack.remove(stack.size() - 1);

        ContinuationFrame newFrame;

        if (isThisKitkat) {
            int thisManyTimes = OperatorUtils.getInt(stack, stack.size() - 1, stack.size());
            stack.remove(stack.size() - 1);

            if (thisManyTimes == 0) return fuckOff(img, stack, cont);
            newFrame = new FrameExecuteManyTimes(instrs, stack, thisManyTimes);

        } else {
            int from = OperatorUtils.getInt(stack, stack.size() - 2, stack.size());
            int to = OperatorUtils.getInt(stack, stack.size() - 1, stack.size());
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);

            if (from == to) return fuckOff(img, stack, cont);
            List<Iota> data = new ArrayList<>();
            boolean traditional = from < to;
            int inc = traditional ? 1 : -1;

            // is this understandable? lmao
            for (int i = from; from < to ? i < to : i > to; i += inc) {
                data.add(new DoubleIota(i));
            }
            SpellList datum = new ListIota(data).getList();

            newFrame = new FrameForEach(datum, instrs, null, new ArrayList<>());
        }

        SpellContinuation newCont = cont instanceof SpellContinuation.NotDone notDone &&
            notDone.getFrame() instanceof FrameFinishEval
            ? cont
            : cont.pushFrame(FrameFinishEval.INSTANCE);

        return new OperationResult(
            CastingImgWithStack(img.withUsedOp(), stack),
            List.of(),
            newCont.pushFrame(newFrame),
            HexEvalSounds.THOTH
        );
    }

    private static OperationResult fuckOff(
        CastingImage img,
        List<Iota> stack,
        SpellContinuation cont
    ) {
        return new OperationResult(
            CastingImgWithStack(img.withUsedOp(), stack),
            List.of(),
            cont,
            HexEvalSounds.THOTH
        );
    }
}

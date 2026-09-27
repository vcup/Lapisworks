package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class EstrogenExalt extends ConstMediaActionNCT {
    public int argc = 3;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        List<Iota> list = stack.getJUSTAList(0);

        list.add(stack.getPositiveIntUnder(1, list.size()), stack.get(2));

        return List.of(new ListIota(list));
    }
}

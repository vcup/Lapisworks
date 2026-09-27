package com.luxof.lapisworks.mixinsupport;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;

import java.util.ArrayList;

public interface ChariotServerPlayer {
    public int getUsedAmalgamsThisTick();
    public void incrementUsedAmalgamsThisTick();
    public ArrayList<Amalgamation> getFusedAmalgamations();
}

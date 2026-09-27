package com.luxof.lapisworks.mixinsupport;

public interface ArtMindInterface {
    public float getUsedMindPercentage();
    public void setUsedMindPercentage(float val);
    public void incUsedMindPercentage(float amount);
    public int getMindBeingUsedTicks();
    public void setMindBeingUsedTicks(int val);
    public void incMindBeingUsedTicks(int amount);
    public void setDontUseAgainTicks(int ticks);
    public void incDontUseAgainTicks(int ticks);
    public int getDontUseAgainTicks();
}

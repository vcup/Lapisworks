package com.luxof.lapisworks.mixinsupport;

import org.jetbrains.annotations.Nullable;

public interface ControlCircleTickSpeed {
    /** Get the Ticks Per Tile forced on this <code>CircleExecutionState</code>. Can be null. */
    @Nullable public Integer getForcedTPT();
    /** Forces this <code>CircleExecutionState</code> to adhere to a fixed Ticks Per Tile. */
    public void setForcedTPT(int tpt);
    /** Stops forcing a Ticks Per Tile on this <code>CircleExecutionState</code>. */
    public void clearForcedTPT();
}

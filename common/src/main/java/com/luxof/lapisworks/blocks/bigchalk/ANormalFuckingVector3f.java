package com.luxof.lapisworks.blocks.bigchalk;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

// multiplies... and doesn't modify itself
public class ANormalFuckingVector3f {
    public static Vec3d sub(Vec3d a, ANormalFuckingVector3f b) {
        return new Vec3d(
            a.x - b.x,
            a.y - b.y,
            a.z - b.z
        );
    }
    public static Vec3d add(Vec3d a, ANormalFuckingVector3f b) {
        return new Vec3d(
            a.x + b.x,
            a.y + b.y,
            a.z + b.z
        );
    }

    public final float x;
    public final float y;
    public final float z;
    public ANormalFuckingVector3f(
        Direction dir
    ) {
        this((float)dir.getOffsetX(), (float)dir.getOffsetY(), (float)dir.getOffsetZ());
    }
    public ANormalFuckingVector3f(
        float x,
        float y,
        float z
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ANormalFuckingVector3f mul(float scalar) {
        return new ANormalFuckingVector3f(this.x*scalar, this.y*scalar, this.z*scalar);
    }
    public ANormalFuckingVector3f add(ANormalFuckingVector3f b) {
        return new ANormalFuckingVector3f(this.x+b.x, this.y+b.y, this.z+b.z);
    }
    public Vec3d toVec3d() {
        return new Vec3d(this.x, this.y, this.z);
    }
}

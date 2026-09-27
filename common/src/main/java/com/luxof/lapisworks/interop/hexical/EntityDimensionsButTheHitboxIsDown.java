package com.luxof.lapisworks.interop.hexical;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;

public class EntityDimensionsButTheHitboxIsDown extends EntityDimensions {
    public EntityDimensionsButTheHitboxIsDown(float width, float height) {
        super(width, height, true);
    }

    @Override
	public Box getBoxAt(double x, double y, double z) {
		float f = this.width / 2.0F;
		float g = this.height / 2.0F;
		return new Box(x - f, y - g, z - f, x + f, y + g, z + f);
	}
}

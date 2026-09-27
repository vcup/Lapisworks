package com.luxof.lapisworks.client.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

// i have 4 days
public class FloatingEnchant extends SpriteBillboardParticle {
    protected FloatingEnchant(
        ClientWorld clientWorld,
        double x, double y, double z,
        double vX, double vY, double vZ
    ) {
        // i don't know what the fuck kind of black fucking magic i was snorting
        // while multiplying the velocities
        super(clientWorld, x, y, z, vX * 0.05, Math.abs(vY * 0.05), vZ * 0.05);
        this.scale *= 0.6f;
        this.gravityStrength = -0.15F;
        this.velocityX *= 0.1f;
        this.velocityY *= 0.2f;
        this.velocityZ *= 0.1f;
        this.maxAge = 5 + this.random.nextInt(10);
    }

    @Override
    public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT; }

    public static class FloatingEnchantFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        public FloatingEnchantFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
            DefaultParticleType type,
            ClientWorld world,
            double x, double y, double z,
            double vX, double vY, double vZ
        ) {
            FloatingEnchant particle = new FloatingEnchant(world, x, y, z, vX, vY, vZ);
            particle.setSprite(this.spriteProvider.getSprite(world.random));
            return particle;
        }
    }
}

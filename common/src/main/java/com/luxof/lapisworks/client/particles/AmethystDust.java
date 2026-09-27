package com.luxof.lapisworks.client.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class AmethystDust extends SpriteBillboardParticle {
    protected AmethystDust(
        ClientWorld clientWorld,
        double x, double y, double z,
        double vX, double vY, double vZ
    ) {
        super(clientWorld, x, y, z, 0.0, 0.0, 0.0);
        this.scale *= 3f;
        this.gravityStrength = 0f;
        this.velocityMultiplier = 0.9f;
        this.velocityX = vX * 0.5f;
        this.velocityY = vY * 0.5f;
        this.velocityZ = vZ * 0.5f;
        this.maxAge = 15 + this.random.nextInt(20);
    }

    @Override
    public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT; }

    public static class AmethystDustFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        public AmethystDustFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
            DefaultParticleType type,
            ClientWorld world,
            double x, double y, double z,
            double vX, double vY, double vZ
        ) {
            AmethystDust particle = new AmethystDust(world, x, y, z, vX, vY, vZ);
            particle.setSprite(this.spriteProvider.getSprite(world.random));
            return particle;
        }
    }
}

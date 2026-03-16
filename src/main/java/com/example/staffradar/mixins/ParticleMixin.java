package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public class ParticleMixin {
    // Only score for particles that indicate a player action
    private static final Set<String> SUSPICIOUS_PARTICLES = Set.of(
        "minecraft:sweep_attack",
        "minecraft:crit",
        "minecraft:enchanted_hit",
        "minecraft:soul_fire_flame",
        "minecraft:large_smoke"
    );

    private static long lastParticleScore = 0;
    private static final long PARTICLE_GLOBAL_COOLDOWN_MS = 3000;

    @Inject(method = "onParticle", at = @At("HEAD"))
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().particleEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastParticleScore < PARTICLE_GLOBAL_COOLDOWN_MS) return;

        // Only check for suspicious, player-action particles
        ParticleType<?> type = packet.getParameters().getType();
        String particleId = Registries.PARTICLE_TYPE.getId(type).toString();
        if (!SUSPICIOUS_PARTICLES.contains(particleId)) return;

        double distSq = client.player.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ());
        if (distSq > 64) return; // Within 8 blocks

        boolean playerNearby = client.world.getPlayers().stream()
            .anyMatch(p -> !p.equals(client.player) && p.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 9);

        if (!playerNearby) {
            lastParticleScore = now;
            StaffRadarMod.LOGGER.info("[StaffRadar] Suspicious particle '{}' with no visible player nearby. Score +3", particleId);
            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("particle", 3);
        }
    }
}

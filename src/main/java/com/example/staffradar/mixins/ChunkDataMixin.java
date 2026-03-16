package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ClientPlayNetworkHandler.class)
public class ChunkDataMixin {
    // Track which chunks were already loaded
    private static final Set<Long> loadedChunks = ConcurrentHashMap.newKeySet();
    private static long joinTime = 0;
    private static final long GRACE_PERIOD_MS = 15000; // Ignore first 15s after join

    @Inject(method = "onChunkData", at = @At("HEAD"))
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().chunkEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        long now = System.currentTimeMillis();

        // Record join time on first chunk
        if (joinTime == 0) {
            joinTime = now;
        }

        // During grace period (initial world load), just record chunks and skip scoring
        if (now - joinTime < GRACE_PERIOD_MS) {
            loadedChunks.add(chunkKey(packet.getChunkX(), packet.getChunkZ()));
            return;
        }

        long key = chunkKey(packet.getChunkX(), packet.getChunkZ());

        // A RE-SEND of an already-loaded chunk is suspicious
        if (loadedChunks.contains(key)) {
            int playerChunkX = client.player.getChunkPos().x;
            int playerChunkZ = client.player.getChunkPos().z;
            int dx = Math.abs(packet.getChunkX() - playerChunkX);
            int dz = Math.abs(packet.getChunkZ() - playerChunkZ);

            // Only care about nearby chunks (within 4 chunk radius)
            if (dx <= 4 && dz <= 4) {
                double vel = client.player.getVelocity().lengthSquared();
                if (vel < 0.05) { // Player wasn't moving fast
                    StaffRadarMod.LOGGER.info("[StaffRadar] Nearby chunk {},{} reloaded while player was still. Score +3", packet.getChunkX(), packet.getChunkZ());
                    StaffRadarMod.getInstance().getSpectatorWatcher().addScore("chunk", 3);
                }
            }
        }

        loadedChunks.add(key);
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}

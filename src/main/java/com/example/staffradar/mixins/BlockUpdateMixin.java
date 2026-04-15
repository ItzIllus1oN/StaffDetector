package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ClientPlayNetworkHandler.class)
public class BlockUpdateMixin {
    private static final Map<Long, Long> blockCooldowns = new ConcurrentHashMap<>();
    private static final long BLOCK_COOLDOWN_MS = 5000;

    @Inject(method = "onBlockUpdate", at = @At("HEAD"))
    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().blockEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        BlockPos pos = packet.getPos();
        double distSq = client.player.getPos().squaredDistanceTo(pos.toCenterPos());
        if (distSq > 64) return;

        if (distSq < 9) return;
        long key = pos.asLong();
        long now = System.currentTimeMillis();
        if (blockCooldowns.getOrDefault(key, 0L) + BLOCK_COOLDOWN_MS > now) return;

        boolean otherPlayerNearby = client.world.getPlayers().stream()
            .anyMatch(p -> !p.equals(client.player) && p.getPos().squaredDistanceTo(pos.toCenterPos()) < 9);

        if (!otherPlayerNearby) {
            blockCooldowns.put(key, now);
            StaffRadarMod.LOGGER.info("[StaffRadar] Block update at {} with no visible player nearby. Score +2", pos);
            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("block", 2);
        }
    }
}

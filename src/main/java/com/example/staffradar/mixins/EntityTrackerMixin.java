package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ClientPlayNetworkHandler.class)
public class EntityTrackerMixin {

    private static final AtomicInteger selfUpdates = new AtomicInteger(0);
    private static final AtomicInteger otherUpdates = new AtomicInteger(0);
    private static long lastSpikeCheck = System.currentTimeMillis();
    private static final long SPIKE_WINDOW_MS = 5000;
    private static final double SPIKE_RATIO_THRESHOLD = 2.5;
    private static final int MIN_OTHER_PACKETS = 5;

    @Inject(method = "onEntityTrackerUpdate", at = @At("HEAD"))
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        boolean isSelf = packet.id() == client.player.getId();

        if (!isSelf && com.example.staffradar.config.ConfigManager.getConfig().invisibleEntityEnabled) {
            Entity entity = client.world.getEntityById(packet.id());
            if (entity instanceof net.minecraft.client.network.AbstractClientPlayerEntity playerEntity) {
                if (packet.trackedValues() != null) {
                    packet.trackedValues().forEach(entry -> {
                        if (entry.id() == 6 && entry.value() instanceof Byte flags) {
                            if ((flags & 0x20) != 0) {
                                StaffRadarMod.LOGGER.warn("[StaffRadar][INVISIBLE] Player {} has Invisible flag set!",
                                        playerEntity.getGameProfile().getName());
                                StaffRadarMod.getInstance().getSpectatorWatcher().addScore("invisible_entity", 8);
                            }
                        }
                    });
                }
            }
        }

        if (isSelf) {
            selfUpdates.incrementAndGet();
        } else {
            otherUpdates.incrementAndGet();
        }

        long now = System.currentTimeMillis();
        if (now - lastSpikeCheck >= SPIKE_WINDOW_MS) {
            int self = selfUpdates.getAndSet(0);
            int other = otherUpdates.getAndSet(0);
            lastSpikeCheck = now;

            if (other >= MIN_OTHER_PACKETS && self > other * SPIKE_RATIO_THRESHOLD) {
                StaffRadarMod.LOGGER.warn(
                        "[StaffRadar][SPIKE] Self entity got {} updates vs {} other updates in {}ms — possible spectator watching!",
                        self, other, SPIKE_WINDOW_MS);
                StaffRadarMod.getInstance().getSpectatorWatcher().addScore("entity_spike", 5);
            }
        }
    }
}

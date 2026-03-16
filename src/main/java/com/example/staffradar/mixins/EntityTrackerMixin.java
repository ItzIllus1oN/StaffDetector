package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class EntityTrackerMixin {

    @Inject(method = "onEntityTrackerUpdate", at = @At("HEAD"))
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().invisibleEntityEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Skip ourselves
        if (packet.id() == client.player.getId()) return;

        Entity entity = client.world.getEntityById(packet.id());
        if (entity == null) return;

        // We only care about entities that are visible players
        // If the entity is a player AND is now being tracked as Invisible, that's suspicious
        if (entity instanceof net.minecraft.client.network.AbstractClientPlayerEntity playerEntity) {
            if (packet.trackedValues() == null) return;

            packet.trackedValues().forEach(entry -> {
                // DataTracker entry ID 6 = flags byte for LivingEntity
                // Invisible flag = bit 5 (0x20)
                if (entry.id() == 6) {
                    Object value = entry.value();
                    if (value instanceof Byte flags) {
                        boolean invisible = (flags & 0x20) != 0;
                        if (invisible) {
                            StaffRadarMod.LOGGER.info("[StaffRadar][!] Entity {} ({}) has INVISIBLE flag set! Score +8", packet.id(), playerEntity.getGameProfile().getName());
                            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("invisible_entity", 8);
                        }
                    }
                }
            });
        }
    }
}

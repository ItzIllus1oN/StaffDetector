package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class SoundPacketMixin {
    @Inject(method = "onPlaySound", at = @At("HEAD"))
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().soundEnabled)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        double distSq = client.player.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ());
        if (distSq > 100)
            return;

        if (distSq < 16)
            return;

        boolean otherPlayerNearby = client.world.getPlayers().stream()
                .anyMatch(p -> !p.equals(client.player)
                        && p.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 9);

        if (!otherPlayerNearby) {
            String soundId = packet.getSound().value().id().toString();

            if (com.example.staffradar.config.ConfigManager.getConfig().soundBlacklist.contains(soundId)) {
                return;
            }

            StaffRadarMod.LOGGER.info("[StaffRadar] Suspicious sound '{}' with no visible player nearby. Score +1",
                    soundId);
            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("Suspicious Sound (" + soundId + ")", 1);
        }
    }
}

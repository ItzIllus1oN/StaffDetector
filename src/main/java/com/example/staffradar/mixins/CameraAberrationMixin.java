package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class CameraAberrationMixin {

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().cameraAberrationEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // If the player is essentially standing still and the server sends a forced position correction,
        // this can indicate a spectator teleporting to the player's position triggered a re-sync.
        double velocitySq = client.player.getVelocity().lengthSquared();
        if (velocitySq < 0.005) {
            StaffRadarMod.LOGGER.info("[StaffRadar] Camera Aberration detected! Server forced position while player was still. Score +5");
            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("camera", 5);
        }
    }
}

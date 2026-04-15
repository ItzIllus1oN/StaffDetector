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
    private static long lastCameraScore = 0;
    private static final long CAMERA_COOLDOWN_MS = 8000;

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().cameraAberrationEnabled)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        long now = System.currentTimeMillis();
        if (now - lastCameraScore < CAMERA_COOLDOWN_MS)
            return;

        double vx = client.player.getVelocity().x;
        double vy = client.player.getVelocity().y;
        double vz = client.player.getVelocity().z;

        boolean trulyStill = Math.abs(vx) < 0.005 && Math.abs(vy) < 0.05 && Math.abs(vz) < 0.005;

        if (trulyStill) {
            lastCameraScore = now;
            StaffRadarMod.LOGGER
                    .info("[StaffRadar] Camera Aberration: server forced position while player was still. Score +5");
            StaffRadarMod.getInstance().getSpectatorWatcher().addScore("camera", 5);
        }
    }
}

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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        double distSq = client.player.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ());
        if (distSq < 100) { // Within 10 blocks
            // Check if there's a visible player at that location
            boolean playerNearby = client.world.getPlayers().stream()
                .anyMatch(p -> p.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 4);
            
            if (!playerNearby) {
                String soundId = packet.getSound().value().id().toString();
                
                // Blacklist filter
                if (com.example.staffradar.config.ConfigManager.getConfig().soundBlacklist.contains(soundId)) {
                    return;
                }
                
                StaffRadarMod.getInstance().getSpectatorWatcher().addScore("Suspicious Sound (" + soundId + ")", 1);
            }
        }
    }
}

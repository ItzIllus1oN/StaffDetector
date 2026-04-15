package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import com.example.staffradar.detection.StaffPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ClientPlayNetworkHandler.class)
public class VanishTrackerMixin {

    private static final Map<UUID, String> tabListRegistry = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> vanishCooldowns = new ConcurrentHashMap<>();
    private static final long VANISH_COOLDOWN_MS = 15000;

    @Inject(method = "onPlayerList", at = @At("HEAD"))
    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().vanishTrackerEnabled)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
            UUID uuid = entry.profileId();
            if (uuid.equals(client.player.getUuid()))
                continue;

            if (entry.profile() != null && entry.profile().getName() != null) {
                tabListRegistry.put(uuid, entry.profile().getName());
            }

            if (packet.getActions().contains(PlayerListS2CPacket.Action.UPDATE_LISTED)) {
                if (!entry.listed()) {
                    checkVanishEvent(uuid, "UPDATE_LISTED=false (hidden from tab)", client);
                }
            }
        }
    }

    @Inject(method = "onPlayerRemove", at = @At("HEAD"))
    private void onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
        if (!com.example.staffradar.config.ConfigManager.getConfig().vanishTrackerEnabled)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        List<UUID> removedUuids = packet.profileIds();
        for (UUID uuid : removedUuids) {
            if (uuid.equals(client.player.getUuid()))
                continue;
            checkVanishEvent(uuid, "removed from session without disconnect", client);
        }
    }

    private void checkVanishEvent(UUID uuid, String reason, MinecraftClient client) {
        if (!StaffRadarMod.getInstance().getStaffDetector().isKnownStaff(uuid))
            return;

        long now = System.currentTimeMillis();
        if (vanishCooldowns.getOrDefault(uuid, 0L) + VANISH_COOLDOWN_MS > now)
            return;
        vanishCooldowns.put(uuid, now);

        StaffPlayer staff = StaffRadarMod.getInstance().getStaffDetector().getStaffByUuid(uuid);
        String staffName = staff != null ? staff.name() : tabListRegistry.getOrDefault(uuid, uuid.toString());

        String alert = "§4§l⚠ VANISH DETECTED! §r§c" + staffName + " §7(" + reason + ")";
        StaffRadarMod.LOGGER.warn("[StaffRadar][VANISH] {} - {}", staffName, reason);

        if (client.player != null) {
            client.player.sendMessage(net.minecraft.text.Text.literal(alert), false);
            client.player.sendMessage(net.minecraft.text.Text.literal(
                    "§7" + staffName + " §fwas a known staff and disappeared from the tab list."), false);
        }

        StaffRadarMod.getInstance().getSpectatorWatcher().addScore("vanish_detected", 999);
    }
}

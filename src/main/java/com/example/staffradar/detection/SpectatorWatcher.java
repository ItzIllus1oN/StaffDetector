package com.example.staffradar.detection;

import com.example.staffradar.StaffRadarMod;
import com.example.staffradar.gui.WatchAlertUI;
import net.minecraft.client.MinecraftClient;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SpectatorWatcher {
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();
    private final Map<String, Long> reasonCooldowns = new ConcurrentHashMap<>();
    private long lastAlertTime = 0;
    private static final long COOLDOWN_MS = 10000;
    // Per-reason cooldown to prevent stacking from noisy detectors
    private static final long REASON_COOLDOWN_MS = 4000;

    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        
        // Decay scores every few seconds to reduce false positives
        if (System.currentTimeMillis() % 5000 < 50) { 
            scores.entrySet().forEach(entry -> {
                if (entry.getValue() > 0) entry.setValue(entry.getValue() - 1);
            });
            scores.values().removeIf(score -> score <= 0);
        }
    }

    public void addScore(String reason, int amount) {
        com.example.staffradar.config.Config config = com.example.staffradar.config.ConfigManager.getConfig();
        // Route by reason prefix
        if (reason.contains("sound") && !config.soundEnabled) return;
        if (reason.contains("chunk") && !config.chunkEnabled) return;
        if (reason.contains("block") && !config.blockEnabled) return;
        if (reason.contains("particle") && !config.particleEnabled) return;
        if (reason.contains("invisible") && !config.invisibleEntityEnabled) return;
        if (reason.contains("camera") && !config.cameraAberrationEnabled) return;

        // Per-reason cooldown: avoid the same noisy signal from stacking repeatedly
        long now = System.currentTimeMillis();
        if (reasonCooldowns.getOrDefault(reason, 0L) + REASON_COOLDOWN_MS > now) return;
        reasonCooldowns.put(reason, now);

        scores.merge(reason, amount, Integer::sum);
        checkAlert();
    }

    private void checkAlert() {
        int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
        long now = System.currentTimeMillis();
        float threshold = com.example.staffradar.config.ConfigManager.getConfig().threshold;

        if (totalScore >= threshold && (now - lastAlertTime > COOLDOWN_MS)) {
            if (com.example.staffradar.config.ConfigManager.getConfig().spectatorAlertEnabled) {
                triggerAlert();
                lastAlertTime = now;
            }
            scores.clear(); // Reset after alert (even if disabled to avoid accumulation)
        }
    }

    private void triggerAlert() {
        MinecraftClient client = MinecraftClient.getInstance();
        StringBuilder debugInfo = new StringBuilder("⚠ STAFF IS WATCHING YOU ⚠ Details: ");
        scores.forEach((reason, score) -> debugInfo.append("[").append(reason).append(": ").append(score).append("] "));
        
        String logMsg = debugInfo.toString();
        
        if (client.player != null) {
            client.player.sendMessage(net.minecraft.text.Text.literal("§4§l⚠ STAFF IS WATCHING YOU ⚠"), true);
            // Send details to chat as well
            client.player.sendMessage(net.minecraft.text.Text.literal("§c" + logMsg), false);
        }
        
        StaffRadarMod.LOGGER.info(logMsg);
        WatchAlertUI.show();
    }

    public void resetScores() {
        scores.clear();
    }
}

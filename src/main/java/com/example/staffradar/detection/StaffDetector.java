package com.example.staffradar.detection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StaffDetector {
    private final Map<UUID, StaffPlayer> detectedStaff = new ConcurrentHashMap<>();
    private int tickCounter = 0;

    public void tick(MinecraftClient client) {
        if (tickCounter++ % 20 != 0) return; // Scan every 1 second

        if (client.getNetworkHandler() == null || !com.example.staffradar.config.ConfigManager.getConfig().keywordEnabled) {
            detectedStaff.clear();
            return;
        }

        List<String> keywords = com.example.staffradar.config.ConfigManager.getConfig().keywords;
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        Set<UUID> currentUuids = new HashSet<>();

        for (PlayerListEntry entry : playerList) {
            UUID uuid = entry.getProfile().getId();
            
            // Intelligence: Don't detect local player as staff
            if (client.player != null && uuid.equals(client.player.getUuid())) continue;

            String name = entry.getProfile().getName();
            if (name == null || name.trim().isEmpty()) continue; // Skip entries without valid names

            String displayName = entry.getDisplayName() != null ? entry.getDisplayName().getString() : "";
            
            // Keyword check in name and display name with "Strict Word Boundary" matching
            for (String keyword : keywords) {
                if (keyword == null || keyword.trim().isEmpty()) continue;
                // Use Regex for strict matching
                String k = keyword.trim();
                
                if (isStaffMatch(name, k) || isStaffMatch(displayName, k)) {
                    addStaff(uuid, name, "Keyword: " + keyword);
                    currentUuids.add(uuid);
                    break;
                }
            }
        }

        // Cleanup players who left or are no longer "staff"
        detectedStaff.keySet().removeIf(uuid -> !currentUuids.contains(uuid) && (System.currentTimeMillis() - detectedStaff.get(uuid).discoveryTime() > 10000));
        
        // Refresh UI
        com.example.staffradar.gui.StaffOverlayUI.updateList(detectedStaff.values());
    }

    public void addStaff(UUID uuid, String name, String reason) {
        if (!detectedStaff.containsKey(uuid)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(net.minecraft.text.Text.literal("§c[StaffRadar] §6" + name + " §fdetected in §eTab list!"), false);
            }
        }
        detectedStaff.put(uuid, new StaffPlayer(uuid, name, reason, System.currentTimeMillis()));
    }

    public Collection<StaffPlayer> getDetectedStaff() {
        return detectedStaff.values();
    }

    private boolean isStaffMatch(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) return false;
        
        String strippedText = stripFormatting(text);
        String strippedK = stripFormatting(keyword);
        
        if (strippedK.isEmpty()) return false;

        // Search for all occurrences of strippedK in strippedText
        String textLower = strippedText.toLowerCase();
        String kLower = strippedK.toLowerCase();
        
        int index = textLower.indexOf(kLower);
        while (index != -1) {
            // Check boundaries in the STRIPPED text
            // Before match
            boolean beforeOk = true;
            if (index > 0) {
                char before = strippedText.charAt(index - 1);
                if (Character.isLetterOrDigit(before)) beforeOk = false;
            }
            
            // After match
            boolean afterOk = true;
            if (index + strippedK.length() < strippedText.length()) {
                char after = strippedText.charAt(index + strippedK.length());
                if (Character.isLetterOrDigit(after)) afterOk = false;
            }
            
            if (beforeOk && afterOk) return true;
            
            index = textLower.indexOf(kLower, index + 1);
        }
        
        return false;
    }

    private String stripFormatting(String input) {
        if (input == null) return "";
        // 1. Strip hex colors: &x or §x followed by 6 pairs of §c or &c
        String stripped = input.replaceAll("(?i)[§&]x([§&][0-9a-f]){6}", "");
        // 2. Strip normal color codes and formatting
        return stripped.replaceAll("(?i)[§&][0-9a-fk-orx]", "");
    }

}

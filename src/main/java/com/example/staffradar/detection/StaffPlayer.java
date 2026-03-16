package com.example.staffradar.detection;

import java.util.UUID;

public record StaffPlayer(UUID uuid, String name, String reason, long discoveryTime) {
    public String getDisplayName() {
        return name + " (" + reason + ")";
    }
}

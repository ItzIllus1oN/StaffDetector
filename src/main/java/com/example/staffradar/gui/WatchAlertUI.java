package com.example.staffradar.gui;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class WatchAlertUI {
    private static long alertStartTime = 0;
    private static final long DURATION = 3000;

    public static void show() {
        alertStartTime = System.currentTimeMillis();
    }

    public static void renderHUD(DrawContext context, float delta) {
        long now = System.currentTimeMillis();
        if (now - alertStartTime > DURATION) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Pulsing opacity
        float progress = (now - alertStartTime) / (float) DURATION;
        float opacity = (float) Math.sin(progress * Math.PI * 4) * 0.5f + 0.5f;
        int color = ((int) (opacity * 150) << 24) | 0xFF0000;

        // Draw red aura (border)
        context.fill(0, 0, screenWidth, 5, color);
        context.fill(0, screenHeight - 5, screenWidth, screenHeight, color);
        context.fill(0, 5, 5, screenHeight - 5, color);
        context.fill(screenWidth - 5, 5, screenWidth, screenHeight - 5, color);

        // Draw alert text
        String text = "⚠ STAFF IS WATCHING YOU ⚠";
        int textWidth = client.textRenderer.getWidth(text);
        context.drawTextWithShadow(client.textRenderer, text, (screenWidth - textWidth) / 2, screenHeight / 2 - 20, 0xFF0000);
    }
}

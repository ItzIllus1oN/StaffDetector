package com.example.staffradar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class WatchAlertUI {

    private static long alertStartTime = 0;

    private static final long DURATION = 3000;

    public static void show() {
        alertStartTime = System.currentTimeMillis();
    }

    public static void renderHUD(
            GuiGraphicsExtractor context,
            Object deltaTracker
    ) {

        long now = System.currentTimeMillis();

        if (now - alertStartTime > DURATION) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        float progress =
                (now - alertStartTime) / (float) DURATION;

        float opacity =
                (float) Math.sin(progress * Math.PI * 4) * 0.5f + 0.5f;

        int color =
                ((int) (opacity * 150) << 24) | 0xFF0000;

        context.fill(
                0,
                0,
                screenWidth,
                5,
                color
        );

        context.fill(
                0,
                screenHeight - 5,
                screenWidth,
                screenHeight,
                color
        );

        context.fill(
                0,
                5,
                5,
                screenHeight - 5,
                color
        );

        context.fill(
                screenWidth - 5,
                5,
                screenWidth,
                screenHeight - 5,
                color
        );

        String text = "⚠ STAFF IS WATCHING YOU ⚠";

        int textWidth = client.font.width(text);

        context.text(
                client.font,
                text,
                (screenWidth - textWidth) / 2,
                screenHeight / 2 - 20,
                0xFFFF0000,
                true
        );
    }
}

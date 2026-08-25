package com.example.staffradar.gui;

import com.example.staffradar.detection.StaffPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StaffOverlayUI {

    private static boolean visible = true;
    private static List<StaffPlayer> cachedStaff = new ArrayList<>();

    public static void toggle() {
        visible = !visible;

        Minecraft client = Minecraft.getInstance();

        if (client.player != null) {
            client.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§7[StaffRadar] HUD Visibility: "
                                    + (visible ? "§aON" : "§cOFF")
                    ),
                    true
            );
        }
    }

    public static void updateList(Collection<StaffPlayer> staff) {
        cachedStaff = new ArrayList<>(staff);
    }

    public static void renderHUD(GuiGraphicsExtractor context, Object deltaTracker) {

        if (!visible) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.options.hideGui) {
            return;
        }

        if (cachedStaff.isEmpty()) {
            return;
        }

        int x = 5;
        int y = 5;

        int maxWidth = 120;

        for (StaffPlayer player : cachedStaff) {
            String text = player.name() + " (" + player.reason() + ")";
            maxWidth = Math.max(
                    maxWidth,
                    client.font.width(text) + 10
            );
        }

        int width = maxWidth;
        int height = 15 + cachedStaff.size() * 10;

        context.fill(
                x,
                y,
                x + width,
                y + height,
                0xCC000000
        );

        context.fill(
                x,
                y,
                x + width,
                y + 1,
                0xFFFFFF55
        );

        context.text(
                client.font,
                "§e§lStaff Detected (" + cachedStaff.size() + "):",
                x + 5,
                y + 5,
                0xFFFFFF55,
                true
        );

        int offset = 15;

        for (StaffPlayer player : cachedStaff) {

            String entry =
                    "§f" + player.name()
                            + " §7(" + player.reason() + ")";

            context.text(
                    client.font,
                    entry,
                    x + 5,
                    y + offset,
                    0xFFFFFFFF,
                    true
            );

            offset += 10;
        }
    }
}

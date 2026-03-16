/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.Text
 */
package io.github.itzispyder.improperui.util;

import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public final class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        if (message != null && ChatUtils.mc.player != null) {
            ChatUtils.mc.player.sendMessage((Text)Text.literal((String)message), false);
        }
    }

    public static void sendFormatted(String message, Object ... args) {
        if (message != null && ChatUtils.mc.player != null) {
            ChatUtils.mc.player.sendMessage((Text)Text.literal((String)StringUtils.color(String.format(message, args))), false);
        }
    }

    public static void sendRawText(Text text) {
        if (ChatUtils.mc.player != null && text != null) {
            ChatUtils.mc.player.sendMessage(text, false);
        }
    }

    public static void sendChatCommand(String cmd) {
        if (ChatUtils.mc.player != null) {
            ChatUtils.mc.player.networkHandler.sendChatCommand(cmd);
        }
    }

    public static void sendChatMessage(String msg) {
        if (ChatUtils.mc.player != null) {
            ChatUtils.mc.player.networkHandler.sendChatMessage(msg);
        }
    }

    public static void sendBlank(int lines) {
        for (int i = 0; i < lines; ++i) {
            ChatUtils.sendMessage("");
        }
    }

    public static void sendBlank() {
        ChatUtils.sendBlank(1);
    }

    public static void pingPlayer() {
        SoundManager sm = mc.getSoundManager();
        SoundEvent event = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        PositionedSoundInstance sound = PositionedSoundInstance.master((SoundEvent)event, (float)0.1f, (float)10.0f);
        sm.play((SoundInstance)sound);
    }
}


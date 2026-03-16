/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil$Type
 */
package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.ImproperUIAPI;
import io.github.itzispyder.improperui.script.callbacks.BuiltInCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ImproperUI
implements ModInitializer {
    public static final KeyBinding BIND = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("binds.improperui.menu", InputUtil.Type.KEYSYM, 344, "binds.improperui"));

    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class, "assets/improperui/improperui/homescreen.ui", "assets/improperui/improperui/example.ui");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BIND.wasPressed()) {
                ImproperUIAPI.parseAndRunFile("improperui", "homescreen.ui", new BuiltInCallbacks());
            }
        });
    }
}


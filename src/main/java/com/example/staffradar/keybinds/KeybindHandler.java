package com.example.staffradar.keybinds;

import com.example.staffradar.gui.StaffOverlayUI;
import com.example.staffradar.gui.ConfigScreenUI;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static KeyBinding toggleOverlayKey;
    private static KeyBinding openConfigKey;

    public static void register() {
        toggleOverlayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.staffradar.toggle_overlay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.staffradar.main"
        ));

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.staffradar.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.staffradar.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleOverlayKey.wasPressed()) {
                StaffOverlayUI.toggle();
            }
            while (openConfigKey.wasPressed()) {
                ConfigScreenUI.open();
            }
        });
    }
}

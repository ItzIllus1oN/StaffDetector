package com.example.staffradar.keybinds;

import com.example.staffradar.StaffRadarMod;
import com.example.staffradar.gui.ConfigScreenUI;
import com.example.staffradar.gui.StaffOverlayUI;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KeybindHandler {

    private static KeyMapping toggleOverlayKey;
    private static KeyMapping openConfigKey;

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            StaffRadarMod.MOD_ID,
                            "main"
                    )
            );

    public static void register() {

        toggleOverlayKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.staffradar.toggle_overlay",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_V,
                        CATEGORY
                )
        );

        openConfigKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.staffradar.open_config",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_P,
                        CATEGORY
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (toggleOverlayKey.consumeClick()) {
                StaffOverlayUI.toggle();
            }

            while (openConfigKey.consumeClick()) {
                ConfigScreenUI.open();
            }
        });
    }
}

package com.example.staffradar;

import com.example.staffradar.detection.StaffDetector;
import com.example.staffradar.detection.SpectatorWatcher;
import com.example.staffradar.keybinds.KeybindHandler;
import com.example.staffradar.gui.StaffOverlayUI;
import com.example.staffradar.gui.WatchAlertUI;
import com.example.staffradar.gui.ConfigScreenUI;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class StaffRadarMod implements ClientModInitializer {
    public static final String MOD_ID = "staffradar";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static StaffRadarMod instance;
    private StaffDetector staffDetector;
    private SpectatorWatcher spectatorWatcher;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("Initializing StaffRadar...");

        com.example.staffradar.config.ConfigManager.load();
        
        staffDetector = new StaffDetector();
        spectatorWatcher = new SpectatorWatcher();

        KeybindHandler.register();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            staffDetector.tick(client);
            spectatorWatcher.tick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            StaffOverlayUI.renderHUD(context, 0f);
            WatchAlertUI.renderHUD(context, 0f);
        });

        LOGGER.info("StaffRadar initialized successfully.");
    }

    public static StaffRadarMod getInstance() {
        return instance;
    }

    public StaffDetector getStaffDetector() {
        return staffDetector;
    }

    public SpectatorWatcher getSpectatorWatcher() {
        return spectatorWatcher;
    }
}

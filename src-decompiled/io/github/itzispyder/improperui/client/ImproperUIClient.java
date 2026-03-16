/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.minecraft.client.font.TextRenderer
 */
package io.github.itzispyder.improperui.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.font.TextRenderer;

public class ImproperUIClient
implements ClientModInitializer {
    private static final ImproperUIClient system = new ImproperUIClient();
    public TextRenderer codeRenderer;

    public static ImproperUIClient getInstance() {
        return system;
    }

    public void onInitializeClient() {
    }
}


package com.example.staffradar.gui;

import com.example.staffradar.config.Config;
import com.example.staffradar.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreenUI extends Screen {

    private EditBox keywordsField;

    private Checkbox keywordToggle;
    private Checkbox soundToggle;
    private Checkbox chunkToggle;
    private Checkbox alertToggle;
    private Checkbox blockToggle;
    private Checkbox particleToggle;
    private Checkbox invisibleEntityToggle;
    private Checkbox cameraToggle;
    private Checkbox vanishTrackerToggle;

    public ConfigScreenUI() {
        super(Component.literal("StaffRadar Configuration"));
    }

    @Override
    protected void init() {

        Config config = ConfigManager.getConfig();

        int leftX = this.width / 2 - 210;
        int rightX = this.width / 2 + 10;

        int startY = 35;
        int step = 22;

        this.keywordToggle = Checkbox.builder(
                        Component.literal("Keyword Detection"),
                        this.font
                )
                .pos(leftX, startY)
                .selected(config.keywordEnabled)
                .build();

        this.addRenderableWidget(this.keywordToggle);

        this.soundToggle = Checkbox.builder(
                        Component.literal("Sound Detection"),
                        this.font
                )
                .pos(leftX, startY + step)
                .selected(config.soundEnabled)
                .build();

        this.addRenderableWidget(this.soundToggle);

        this.chunkToggle = Checkbox.builder(
                        Component.literal("Chunk Resend Detection"),
                        this.font
                )
                .pos(leftX, startY + step * 2)
                .selected(config.chunkEnabled)
                .build();

        this.addRenderableWidget(this.chunkToggle);

        this.blockToggle = Checkbox.builder(
                        Component.literal("Block Interaction Detection"),
                        this.font
                )
                .pos(leftX, startY + step * 3)
                .selected(config.blockEnabled)
                .build();

        this.addRenderableWidget(this.blockToggle);

        this.particleToggle = Checkbox.builder(
                        Component.literal("Particle Detection"),
                        this.font
                )
                .pos(rightX, startY)
                .selected(config.particleEnabled)
                .build();

        this.addRenderableWidget(this.particleToggle);

        this.invisibleEntityToggle = Checkbox.builder(
                        Component.literal("Invisible Entity Detection"),
                        this.font
                )
                .pos(rightX, startY + step)
                .selected(config.invisibleEntityEnabled)
                .build();

        this.addRenderableWidget(this.invisibleEntityToggle);

        this.cameraToggle = Checkbox.builder(
                        Component.literal("Camera Aberration Detection"),
                        this.font
                )
                .pos(rightX, startY + step * 2)
                .selected(config.cameraAberrationEnabled)
                .build();

        this.addRenderableWidget(this.cameraToggle);

        this.vanishTrackerToggle = Checkbox.builder(
                        Component.literal("Vanish Tracker (Tab List)"),
                        this.font
                )
                .pos(rightX, startY + step * 3)
                .selected(config.vanishTrackerEnabled)
                .build();

        this.addRenderableWidget(this.vanishTrackerToggle);

        this.alertToggle = Checkbox.builder(
                        Component.literal("Staff Watch Alert"),
                        this.font
                )
                .pos(leftX, startY + step * 4)
                .selected(config.spectatorAlertEnabled)
                .build();

        this.addRenderableWidget(this.alertToggle);

        int fieldY = startY + step * 4 + 10;

        this.keywordsField = new EditBox(
                this.font,
                this.width / 2 - 150,
                fieldY,
                300,
                20,
                Component.literal("Keywords")
        );

        this.keywordsField.setMaxLength(2000);
        this.keywordsField.setValue(config.getKeywordsString());

        this.addRenderableWidget(this.keywordsField);

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Save & Close"),
                                button -> saveAndClose()
                        )
                        .bounds(
                                this.width / 2 - 60,
                                fieldY + 30,
                                120,
                                20
                        )
                        .build()
        );
    }

    private void saveAndClose() {

        Config config = ConfigManager.getConfig();

        config.keywordEnabled = this.keywordToggle.selected();
        config.soundEnabled = this.soundToggle.selected();
        config.chunkEnabled = this.chunkToggle.selected();
        config.blockEnabled = this.blockToggle.selected();
        config.particleEnabled = this.particleToggle.selected();
        config.invisibleEntityEnabled = this.invisibleEntityToggle.selected();
        config.cameraAberrationEnabled = this.cameraToggle.selected();
        config.vanishTrackerEnabled = this.vanishTrackerToggle.selected();
        config.spectatorAlertEnabled = this.alertToggle.selected();

        config.setKeywordsFromString(
                this.keywordsField.getValue()
        );

        ConfigManager.save();

        this.onClose();
    }

    @Override
    public void render(
            GuiGraphicsExtractor context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        context.fill(
                0,
                0,
                this.width,
                this.height,
                0xAA000000
        );

        context.centeredText(
                this.font,
                this.title,
                this.width / 2,
                15,
                0xFFFFFFFF
        );

        int fieldLabelY =
                35 + 22 * 4 + 10 - 12;

        context.text(
                this.font,
                "Staff Keywords (comma separated):",
                this.width / 2 - 150,
                fieldLabelY,
                0xFFAAAAAA,
                false
        );

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }

    public static void open() {

        Minecraft.getInstance().gui.setScreen(
                new ConfigScreenUI()
        );
    }
}

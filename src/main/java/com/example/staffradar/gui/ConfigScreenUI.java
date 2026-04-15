package com.example.staffradar.gui;

import com.example.staffradar.config.Config;
import com.example.staffradar.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreenUI extends Screen {
    private TextFieldWidget keywordsField;
    private CheckboxWidget keywordToggle;
    private CheckboxWidget soundToggle;
    private CheckboxWidget chunkToggle;
    private CheckboxWidget alertToggle;
    private CheckboxWidget blockToggle;
    private CheckboxWidget particleToggle;
    private CheckboxWidget invisibleEntityToggle;
    private CheckboxWidget cameraToggle;
    private CheckboxWidget vanishTrackerToggle;

    public ConfigScreenUI() {
        super(Text.of("StaffRadar Configuration"));
    }

    @Override
    protected void init() {
        Config config = ConfigManager.getConfig();
        int leftX = this.width / 2 - 210;
        int rightX = this.width / 2 + 10;
        int startY = 35;
        int step = 22;

        this.keywordToggle = CheckboxWidget.builder(Text.of("Keyword Detection"), this.textRenderer)
                .pos(leftX, startY).checked(config.keywordEnabled).build();
        this.addDrawableChild(this.keywordToggle);

        this.soundToggle = CheckboxWidget.builder(Text.of("Sound Detection"), this.textRenderer)
                .pos(leftX, startY + step).checked(config.soundEnabled).build();
        this.addDrawableChild(this.soundToggle);

        this.chunkToggle = CheckboxWidget.builder(Text.of("Chunk Resend Detection"), this.textRenderer)
                .pos(leftX, startY + step * 2).checked(config.chunkEnabled).build();
        this.addDrawableChild(this.chunkToggle);

        this.blockToggle = CheckboxWidget.builder(Text.of("Block Interaction Detection"), this.textRenderer)
                .pos(leftX, startY + step * 3).checked(config.blockEnabled).build();
        this.addDrawableChild(this.blockToggle);

        this.particleToggle = CheckboxWidget.builder(Text.of("Particle Detection"), this.textRenderer)
                .pos(rightX, startY).checked(config.particleEnabled).build();
        this.addDrawableChild(this.particleToggle);

        this.invisibleEntityToggle = CheckboxWidget.builder(Text.of("Invisible Entity Detection"), this.textRenderer)
                .pos(rightX, startY + step).checked(config.invisibleEntityEnabled).build();
        this.addDrawableChild(this.invisibleEntityToggle);

        this.cameraToggle = CheckboxWidget.builder(Text.of("Camera Aberration Detection"), this.textRenderer)
                .pos(rightX, startY + step * 2).checked(config.cameraAberrationEnabled).build();
        this.addDrawableChild(this.cameraToggle);

        this.vanishTrackerToggle = CheckboxWidget.builder(Text.of("Vanish Tracker (Tab List)"), this.textRenderer)
                .pos(rightX, startY + step * 3).checked(config.vanishTrackerEnabled).build();
        this.addDrawableChild(this.vanishTrackerToggle);

        this.alertToggle = CheckboxWidget.builder(Text.of("Staff Watch Alert"), this.textRenderer)
                .pos(leftX, startY + step * 4).checked(config.spectatorAlertEnabled).build();
        this.addDrawableChild(this.alertToggle);

        int fieldY = startY + step * 4 + 10;
        this.keywordsField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, fieldY, 300, 20,
                Text.of("Keywords"));
        this.keywordsField.setMaxLength(2000);
        this.keywordsField.setText(config.getKeywordsString());
        this.addDrawableChild(this.keywordsField);

        this.addDrawableChild(ButtonWidget.builder(Text.of("Save & Close"), button -> saveAndClose())
                .dimensions(this.width / 2 - 60, fieldY + 30, 120, 20).build());
    }

    private void saveAndClose() {
        Config config = ConfigManager.getConfig();
        config.keywordEnabled = this.keywordToggle.isChecked();
        config.soundEnabled = this.soundToggle.isChecked();
        config.chunkEnabled = this.chunkToggle.isChecked();
        config.blockEnabled = this.blockToggle.isChecked();
        config.particleEnabled = this.particleToggle.isChecked();
        config.invisibleEntityEnabled = this.invisibleEntityToggle.isChecked();
        config.cameraAberrationEnabled = this.cameraToggle.isChecked();
        config.vanishTrackerEnabled = this.vanishTrackerToggle.isChecked();
        config.spectatorAlertEnabled = this.alertToggle.isChecked();
        config.setKeywordsFromString(this.keywordsField.getText());
        ConfigManager.save();
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xAA000000);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        int fieldLabelY = 35 + 22 * 4 + 10 - 12;
        context.drawTextWithShadow(this.textRenderer, "Staff Keywords (comma separated):", this.width / 2 - 150,
                fieldLabelY, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    public static void open() {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ConfigScreenUI()));
    }
}

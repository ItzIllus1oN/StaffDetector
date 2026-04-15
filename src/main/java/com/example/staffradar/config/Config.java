package com.example.staffradar.config;

import java.util.Arrays;
import java.util.List;

public class Config {
    public boolean keywordEnabled = true;
    public boolean soundEnabled = true;
    public boolean chunkEnabled = true;
    public boolean spectatorAlertEnabled = true;
    public boolean blockEnabled = true;
    public boolean particleEnabled = true;
    public boolean invisibleEntityEnabled = true;
    public boolean cameraAberrationEnabled = true;
    public boolean vanishTrackerEnabled = true;
    public List<String> keywords = Arrays.asList(
            "Owner", "Co-Owner", "Admin", "Administrator", "Head-Admin", "Developer", "Dev", "Head-Developer",
            "Manager", "Maintainer", "Coordinator", "Moderator", "Mod", "SrMod", "Head-Mod", "Trial-Mod",
            "JrMod", "Helper", "SrHelper", "JrHelper", "Builder", "Designer", "Support", "SrStaff", "Staff",
            "Operator", "SuperOp", "Senior", "Lead", "Chief", "Director", "Resp", "Supervisor", "Grand-Mod",
            "Dueño", "Propietario", "Creador", "Fundador", "Co-Dueño", "Administrador", "Desarrollador",
            "Mod Senior", "Ayudante", "Soporte", "Diseñador", "Miembro del Staff", "Líder", "Jefe", "Encargado",
            "SR-ADMIN",
            "DUEÑO", "SR-MOD", "SR-HELPER", "SR-BUILDER", "SR-DESIGNER", "SR-SUPPORT", "SR-STAFF", "SR-OPERATOR",
            "SR-OP", "SR-SUPEROP", "SR-SENIOR", "SR-LEAD", "SR-CHIEF", "SR-DIRECTOR", "SR-RESP", "SR-SUPERVISOR",
            "SR-GRAND-MOD");
    public List<String> soundBlacklist = Arrays.asList(
            "minecraft:block.note_block.harp",
            "minecraft:block.note_block.bass",
            "minecraft:block.note_block.basedrum",
            "minecraft:block.note_block.snare",
            "minecraft:block.note_block.hat",
            "minecraft:block.note_block.flute",
            "minecraft:block.note_block.bell",
            "minecraft:block.note_block.chime",
            "minecraft:block.note_block.guitar",
            "minecraft:block.note_block.xylophone",
            "minecraft:block.note_block.iron_xylophone",
            "minecraft:block.note_block.cow_bell",
            "minecraft:block.note_block.didgeridoo",
            "minecraft:block.note_block.bit",
            "minecraft:block.note_block.banjo",
            "minecraft:block.note_block.pling");
    public float threshold = 20.0f;

    public String getKeywordsString() {
        return String.join(", ", keywords);
    }

    public void setKeywordsFromString(String str) {
        keywords = Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

package com.example.staffradar.detection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.example.staffradar.StaffRadarMod;

public class StaffDetector {
    private final Map<UUID, StaffPlayer> detectedStaff = new ConcurrentHashMap<>();
    private int tickCounter = 0;

    public void tick(MinecraftClient client) {
        if (tickCounter++ % 20 != 0)
            return;

        if (client.getNetworkHandler() == null
                || !com.example.staffradar.config.ConfigManager.getConfig().keywordEnabled) {
            detectedStaff.clear();
            return;
        }

        List<String> keywords = com.example.staffradar.config.ConfigManager.getConfig().keywords;
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        Set<UUID> currentUuids = new HashSet<>();

        for (PlayerListEntry entry : playerList) {
            UUID uuid = entry.getProfile().getId();

            if (client.player != null && uuid.equals(client.player.getUuid()))
                continue;

            String name = entry.getProfile().getName();
            if (name == null || name.trim().isEmpty())
                continue;

            String displayName = entry.getDisplayName() != null ? entry.getDisplayName().getString() : "";

            String prefix = "";
            String suffix = "";
            if (client.world != null) {
                net.minecraft.scoreboard.Team team = client.world.getScoreboard().getScoreHolderTeam(name);
                if (team != null) {
                    prefix = team.getPrefix().getString();
                    suffix = team.getSuffix().getString();
                }
            }

            for (String keyword : keywords) {
                if (keyword == null || keyword.trim().isEmpty())
                    continue;
                String k = keyword.trim();

                if (isStaffMatch(name, k) || isStaffMatch(displayName, k) ||
                        isStaffMatch(prefix, k) || isStaffMatch(suffix, k)) {
                    addStaff(uuid, name, "Keyword: " + keyword);
                    currentUuids.add(uuid);
                    break;
                }
            }
        }

        detectedStaff.keySet().removeIf(uuid -> !currentUuids.contains(uuid)
                && (System.currentTimeMillis() - detectedStaff.get(uuid).discoveryTime() > 10000));

        com.example.staffradar.gui.StaffOverlayUI.updateList(detectedStaff.values());
    }

    public void addStaff(UUID uuid, String name, String reason) {
        if (!detectedStaff.containsKey(uuid)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(
                        net.minecraft.text.Text.literal("§c[StaffRadar] §6" + name + " §fdetected in §eTab list!"),
                        false);
            }
        }
        detectedStaff.put(uuid, new StaffPlayer(uuid, name, reason, System.currentTimeMillis()));
    }

    public Collection<StaffPlayer> getDetectedStaff() {
        return detectedStaff.values();
    }

    public boolean isKnownStaff(UUID uuid) {
        return detectedStaff.containsKey(uuid);
    }

    public StaffPlayer getStaffByUuid(UUID uuid) {
        return detectedStaff.get(uuid);
    }

    private boolean isStaffMatch(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty())
            return false;

        String strippedText = stripFormatting(text);
        String strippedK = stripFormatting(keyword);

        if (strippedK.isEmpty())
            return false;

        String textLower = strippedText.toLowerCase();
        String kLower = strippedK.toLowerCase();

        int index = textLower.indexOf(kLower);
        while (index != -1) {
            boolean beforeOk = true;
            if (index > 0) {
                char before = strippedText.charAt(index - 1);
                if (Character.isLetterOrDigit(before))
                    beforeOk = false;
            }

            boolean afterOk = true;
            if (index + strippedK.length() < strippedText.length()) {
                char after = strippedText.charAt(index + strippedK.length());
                if (Character.isLetterOrDigit(after))
                    afterOk = false;
            }

            if (beforeOk && afterOk)
                return true;

            index = textLower.indexOf(kLower, index + 1);
        }

        return false;
    }

    private String stripFormatting(String input) {
        if (input == null)
            return "";
        String stripped = input.replaceAll("(?i)[§&]x([§&][0-9a-f]){6}", "");
        stripped = stripped.replaceAll("(?i)[§&][0-9a-fk-orx]", "");
        String normalized = normalizeUnicode(stripped);
        return normalized.replaceAll("[^a-zA-Z0-9 ]", " ");
    }

    private String normalizeUnicode(String input) {
        if (input == null || input.isEmpty())
            return input;

        String nfkd = Normalizer.normalize(input, Normalizer.Form.NFKD);
        StringBuilder sb = new StringBuilder(nfkd.length());
        for (int i = 0; i < nfkd.length();) {
            int cp = nfkd.codePointAt(i);
            i += Character.charCount(cp);
            if (cp >= 0x0300 && cp <= 0x036F)
                continue;
            String mapped = UNICODE_MAP.get(cp);
            if (mapped != null) {
                sb.append(mapped);
            } else {
                sb.appendCodePoint(cp);
            }
        }
        return sb.toString();
    }

    private static final Map<Integer, String> UNICODE_MAP = buildUnicodeMap();

    private static Map<Integer, String> buildUnicodeMap() {
        Map<Integer, String> m = new HashMap<>();
        m.put(0x1D00, "a");
        m.put(0x1D01, "ae");
        m.put(0x1D02, "ae");
        m.put(0x1D03, "b");
        m.put(0x1D04, "c");
        m.put(0x1D05, "d");
        m.put(0x1D06, "eth");
        m.put(0x1D07, "e");
        m.put(0x1D08, "e");
        m.put(0x1D09, "i");
        m.put(0x1D0A, "j");
        m.put(0x1D0B, "k");
        m.put(0x1D0C, "l");
        m.put(0x1D0D, "m");
        m.put(0x1D0E, "n");
        m.put(0x1D0F, "o");
        m.put(0x1D10, "o");
        m.put(0x1D11, "o");
        m.put(0x1D12, "o");
        m.put(0x1D13, "o");
        m.put(0x1D14, "oe");
        m.put(0x1D15, "ou");
        m.put(0x1D16, "o");
        m.put(0x1D17, "o");
        m.put(0x1D18, "p");
        m.put(0x1D19, "r");
        m.put(0x1D1A, "r");
        m.put(0x1D1B, "t");
        m.put(0x1D1C, "u");
        m.put(0x1D1D, "u");
        m.put(0x1D1E, "u");
        m.put(0x1D1F, "m");
        m.put(0x1D20, "v");
        m.put(0x1D21, "w");
        m.put(0x1D22, "z");
        m.put(0x0280, "r");
        m.put(0x0262, "g");
        m.put(0x029C, "h");
        m.put(0x0274, "n");
        m.put(0x029F, "l");
        m.put(0xA731, "s");
        m.put(0xA730, "f");
        m.put(0x0299, "b");
        for (int i = 0; i < 26; i++) {
            m.put(0x24B6 + i, String.valueOf((char) ('A' + i))); // uppercase circled
            m.put(0x24D0 + i, String.valueOf((char) ('a' + i))); // lowercase circled
        }
        addMathAlpha(m);
        return m;
    }

    private static void addMathAlpha(Map<Integer, String> m) {
        int[][] styles = {
                { 0x1D400, 0x1D41A },
                { 0x1D434, 0x1D44E },
                { 0x1D468, 0x1D482 },
                { 0x1D49C, 0x1D4B6 },
                { 0x1D4D0, 0x1D4EA },
                { 0x1D504, 0x1D51E },
                { 0x1D538, 0x1D552 },
                { 0x1D56C, 0x1D586 },
                { 0x1D5A0, 0x1D5BA },
                { 0x1D5D4, 0x1D5EE },
                { 0x1D608, 0x1D622 },
                { 0x1D63C, 0x1D656 },
                { 0x1D670, 0x1D68A },
        };
        for (int[] style : styles) {
            int cp = style[0];
            for (int i = 0; i < 26; i++) {
                m.put(cp + i, String.valueOf((char) ('A' + i)));
            }
            cp = style[1];
            for (int i = 0; i < 26; i++) {
                m.put(cp + i, String.valueOf((char) ('a' + i)));
            }
        }
        for (int i = 0; i < 10; i++)
            m.put(0x1D7CE + i, String.valueOf(i));
        for (int i = 0; i < 10; i++)
            m.put(0x1D7D8 + i, String.valueOf(i));
        for (int i = 0; i < 10; i++)
            m.put(0x1D7E2 + i, String.valueOf(i));
        for (int i = 0; i < 10; i++)
            m.put(0x1D7EC + i, String.valueOf(i));
        for (int i = 0; i < 10; i++)
            m.put(0x1D7F6 + i, String.valueOf(i));
    }
}

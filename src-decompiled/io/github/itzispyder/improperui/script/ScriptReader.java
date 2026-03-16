/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.util.misc.Pair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ScriptReader {
    public static List<String> getAllSections(String line, char openingChar, char closingChar) {
        ArrayList<String> lines = new ArrayList<String>();
        int index = 0;
        Pair<String, Integer> section = ScriptReader.firstSectionWithIndex(line, openingChar, closingChar);
        while (!((String)section.left).isEmpty()) {
            lines.add((String)section.left);
            section = ScriptReader.firstSectionWithIndex(line.substring(index += ((Integer)section.right).intValue()), openingChar, closingChar);
        }
        return lines;
    }

    public static String firstSection(String line, char enclosingChar) {
        return ScriptReader.firstSection(line, enclosingChar, enclosingChar);
    }

    public static String firstSection(String line, char openingChar, char closingChar) {
        return (String)ScriptReader.firstSectionWithIndex((String)line, (char)openingChar, (char)closingChar).left;
    }

    public static Pair<String, Integer> firstSectionWithIndex(String line, char openingChar, char closingChar) {
        line = line == null ? "" : line;
        StringBuilder result = new StringBuilder();
        if (line.isEmpty()) {
            return Pair.of(line, 0);
        }
        char[] chars = line.toCharArray();
        boolean began = false;
        int toIgnore = 0;
        for (int i = 0; i < chars.length; ++i) {
            boolean skip;
            char c = chars[i];
            boolean bl = skip = i > 0 && chars[i - 1] == '\\';
            if (c == openingChar && !skip) {
                if (began) {
                    if (openingChar != closingChar) {
                        ++toIgnore;
                    }
                } else {
                    began = true;
                    continue;
                }
            }
            if (c == closingChar && !skip && began) {
                if (toIgnore-- > 0) {
                    result.append(c);
                    continue;
                }
                return Pair.of(result.toString().trim(), i + 1);
            }
            if (c == '\\' && !skip || !began) continue;
            result.append(c);
        }
        String r = result.toString().trim();
        if (r.isEmpty()) {
            return Pair.of(r, chars.length);
        }
        String msg = "unclosed enclosing chars %s%s to mark string section".formatted(String.valueOf(openingChar), String.valueOf(closingChar));
        throw new IllegalArgumentException(msg);
    }

    public static String readFile(String path) {
        try {
            String line;
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                result.append(line);
                if (line.isEmpty()) continue;
                if (!(line.endsWith("{") || line.endsWith("}") || line.endsWith(";"))) {
                    result.append(";");
                }
                result.append(" ");
            }
            br.close();
            return result.toString().trim();
        }
        catch (Exception ex) {
            return "";
        }
    }

    public static String condenseLines(String string) {
        try {
            StringBuilder result = new StringBuilder();
            for (String line : (String[])string.lines().toArray(String[]::new)) {
                line = line.trim();
                result.append(line);
                if (line.isEmpty()) continue;
                if (!(line.endsWith("{") || line.endsWith("}") || line.endsWith(";"))) {
                    result.append(";");
                }
                result.append(" ");
            }
            return result.toString().trim();
        }
        catch (Exception ex) {
            return "";
        }
    }

    public static List<String> parse(String line) {
        line = line == null ? "" : line;
        ArrayList<String> lines = new ArrayList<String>();
        StringBuilder temp = new StringBuilder();
        char[] chars = line.toCharArray();
        boolean inQuote = false;
        int i = 0;
        while (i < chars.length) {
            String subLine;
            boolean skip;
            char c = chars[i];
            boolean bl = skip = i > 0 && chars[i - 1] == '\\';
            if (c == '\"' && !skip) {
                boolean bl2 = inQuote = !inQuote;
            }
            if (c == '{' && !skip && !inQuote) {
                subLine = line.substring(i);
                Pair<String, Integer> section = ScriptReader.firstSectionWithIndex(subLine, '{', '}');
                String lead = temp.toString().trim();
                lines.add((lead.isEmpty() ? "%s%s" : "%s {%s}").formatted(lead, section.left));
                temp = new StringBuilder();
                i += ((Integer)section.right).intValue();
                continue;
            }
            if (c == ';' && !skip && !inQuote) {
                subLine = temp.toString().trim();
                if (!subLine.isEmpty()) {
                    lines.add(subLine);
                }
                temp = new StringBuilder();
                ++i;
                continue;
            }
            temp.append(c);
            ++i;
        }
        String remaining = temp.toString().trim();
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        if (inQuote) {
            throw new IllegalArgumentException("unclosed quotation marks scanned in script");
        }
        return lines;
    }
}


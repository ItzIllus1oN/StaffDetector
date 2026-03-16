/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.elements.Button;
import io.github.itzispyder.improperui.render.elements.CheckBox;
import io.github.itzispyder.improperui.render.elements.Header;
import io.github.itzispyder.improperui.render.elements.HyperLink;
import io.github.itzispyder.improperui.render.elements.Label;
import io.github.itzispyder.improperui.render.elements.Positionable;
import io.github.itzispyder.improperui.render.elements.Radio;
import io.github.itzispyder.improperui.render.elements.Slider;
import io.github.itzispyder.improperui.render.elements.TextBox;
import io.github.itzispyder.improperui.render.elements.TextField;
import io.github.itzispyder.improperui.script.ScriptReader;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ScriptParser {
    private static final String ELEMENT = "[a-zA-Z0-9]+(\\s+[#-][a-zA-Z0-9:._-]+)*?\\s*\\{.*\\}";
    private static final String SECTION = "\\s*\\{.*\\}\\s*$";
    public static final Map<String, PropertyCache> CACHE = new HashMap<String, PropertyCache>();
    private static final Map<String, Supplier<? extends Element>> tagSuppliers = new HashMap<String, Supplier<? extends Element>>(){
        {
            this.put("checkbox", CheckBox::new);
            this.put("textfield", TextField::new);
            this.put("textarea", TextField::new);
            this.put("div", Element::new);
            this.put("e", Element::new);
            this.put("button", Button::new);
            this.put("link", HyperLink::new);
            this.put("a", HyperLink::new);
            this.put("slider", Slider::new);
            this.put("radio", Radio::new);
            this.put("textbox", TextBox::new);
            this.put("input", TextBox::new);
            this.put("textlabel", Label::new);
            this.put("label", Label::new);
            this.put("positionable", Positionable::new);
            this.put("header1", () -> new Header(1.8f));
            this.put("header2", () -> new Header(1.6f));
            this.put("header3", () -> new Header(1.4f));
            this.put("header4", () -> new Header(1.2f));
            this.put("header5", () -> new Header(1.0f));
            this.put("header6", () -> new Header(0.8f));
            this.put("h1", () -> new Header(1.8f));
            this.put("h2", () -> new Header(1.6f));
            this.put("h3", () -> new Header(1.4f));
            this.put("h4", () -> new Header(1.2f));
            this.put("h5", () -> new Header(1.0f));
            this.put("h6", () -> new Header(0.8f));
        }
    };

    public static PropertyCache getCache(String modId) {
        if (!CACHE.containsKey(modId)) {
            CACHE.put(modId, new PropertyCache(modId));
        }
        return CACHE.get(modId);
    }

    public static List<Element> parseFile(File file) {
        if (file == null || !file.exists()) {
            return new ArrayList<Element>();
        }
        String script = ScriptReader.readFile(file.getPath());
        return ScriptParser.parse(script);
    }

    public static List<Element> parse(String script) {
        ArrayList<Element> result = new ArrayList<Element>();
        try {
            script = ScriptReader.condenseLines(script);
            for (String section : ScriptReader.parse(script)) {
                result.add(ScriptParser.parseInternal(section));
            }
            for (Element element : result) {
                element.style();
            }
        }
        catch (Exception ex) {
            ChatUtils.sendMessage(StringUtils.color("&cError parsing script: " + ex.getMessage()));
        }
        return result;
    }

    private static Element parseInternal(String excerpt) {
        String[] attr = ScriptParser.getAttributes(excerpt);
        return ScriptParser.parseInternal0(attr[0], excerpt);
    }

    private static Element parseInternal0(String tag, String excerpt) {
        Element element = tagSuppliers.getOrDefault(tag, Element::new).get();
        if (excerpt.matches(ELEMENT)) {
            ScriptParser.callAttributes(element, ScriptParser.getAttributes(excerpt));
            excerpt = ScriptReader.firstSection(excerpt, '{', '}');
        }
        for (String line : ScriptReader.parse(excerpt)) {
            if (!line.matches(ELEMENT)) {
                element.queueProperty(line);
                continue;
            }
            String[] attr = ScriptParser.getAttributes(line);
            Element child = ScriptParser.parseInternal0(attr[0], ScriptReader.firstSection(line, '{', '}'));
            ScriptParser.callAttributes(child, attr);
            element.addChild(child);
        }
        return element;
    }

    private static String[] getAttributes(String scriptLine) {
        return scriptLine.replaceFirst(SECTION, "").split(" +");
    }

    private static void callAttributes(Element element, String[] attrs) {
        for (String attr : attrs) {
            element.callAttribute(attr);
        }
    }
}


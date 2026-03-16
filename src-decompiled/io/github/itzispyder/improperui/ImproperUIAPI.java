/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.InitContext;
import io.github.itzispyder.improperui.config.ConfigReader;
import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.ScriptParser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImproperUIAPI {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"ImproperUIAPI");
    private static final Map<String, InitContext> CONTEXTS = new HashMap<String, InitContext>();

    public static void init(String modId, Class<? extends ModInitializer> initializer, String ... scriptPaths) {
        InitContext context = CONTEXTS.get(modId);
        if (context == null) {
            context = new InitContext(modId, initializer, scriptPaths);
            CONTEXTS.put(modId, context);
        }
        context.init();
    }

    public static void reload() {
        CONTEXTS.values().forEach(InitContext::reload);
    }

    public static List<InitContext> collectContext() {
        return new ArrayList<InitContext>(CONTEXTS.values());
    }

    public static InitContext getContext(String modId) {
        return CONTEXTS.get(modId);
    }

    public static ConfigReader getConfigReader(String modId, String configFile) {
        return new ConfigReader(modId, configFile);
    }

    public static List<Element> parse(String script) {
        return ScriptParser.parse(script);
    }

    public static List<Element> parse(File file) {
        return ScriptParser.parseFile(file);
    }

    public static void parseAndRunScript(String script, CallbackListener ... callbackListeners) {
        new ImproperUIPanel(script, callbackListeners).open();
    }

    public static void parseAndRunFile(String modId, String fileName, CallbackListener ... callbackListeners) {
        File script = new File(Paths.getScripts(modId) + fileName);
        new ImproperUIPanel(script, callbackListeners).open();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.config;

import java.io.File;

public class Paths {
    public static final String FOLDER = ".improper-ui/";
    public static final String SCRIPTS = ".improper-ui/scripts/";
    public static final String CONFIGS = ".improper-ui/configs/";
    public static final String ASSETS = ".improper-ui/assets/";

    public static void init() {
        Paths.makeDirIfAbsent(FOLDER);
        Paths.makeDirIfAbsent(SCRIPTS);
        Paths.makeDirIfAbsent(CONFIGS);
        Paths.makeDirIfAbsent(ASSETS);
    }

    private static void makeDirIfAbsent(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getScripts(String modId) {
        return SCRIPTS + modId + "/";
    }

    public static String getConfigs(String modId) {
        return CONFIGS + modId + "/";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 */
package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.ImproperUIAPI;
import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.util.FileValidationUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ModInitializer;

public class InitContext {
    private final String modId;
    private final Class<? extends ModInitializer> initializer;
    private String[] scriptPaths;
    private boolean initialized = false;
    private final Set<String> paths = new HashSet<String>();

    public InitContext(String modId, Class<? extends ModInitializer> initializer, String ... scriptPaths) {
        this.modId = modId;
        this.initializer = initializer;
        this.scriptPaths = scriptPaths;
    }

    public void init() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.paths.clear();
        Paths.init();
        ImproperUIAPI.LOGGER.info("Initializing mod '{}' in '{}.class' with {} scripts:", new Object[]{this.modId, this.getName(), this.scriptPaths.length});
        ClassLoader loader = this.initializer.getClassLoader();
        for (String path : this.scriptPaths) {
            while (this.copyResource(loader, path) == -1) {
            }
        }
    }

    public void reload() {
        this.reInit(this.scriptPaths);
    }

    public void reInit(String ... scriptPaths) {
        this.initialized = false;
        this.scriptPaths = scriptPaths;
        this.paths.clear();
        this.init();
    }

    private int copyResource(ClassLoader loader, String path) {
        try {
            String name = path.trim().replaceAll(".*/", "");
            if (this.paths.contains(name)) {
                throw new IllegalArgumentException("path '%s' already exists".formatted(path));
            }
            InputStream is = loader.getResourceAsStream(path);
            if (is == null) {
                throw new IllegalArgumentException("resource not found!");
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String read = String.join((CharSequence)"\n", br.lines().toList());
            br.close();
            isr.close();
            is.close();
            File file = new File(Paths.getScripts(this.modId) + name);
            FileValidationUtils.validate(file);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(read);
            bw.close();
            fw.close();
            boolean success = file.exists();
            String filePath = file.getPath();
            if (success) {
                this.paths.add(name);
                ImproperUIAPI.LOGGER.info("-> Successfully cloned path '{}' to '{}'", (Object)path, (Object)filePath);
            } else {
                ImproperUIAPI.LOGGER.error("<- Path '{}' read, but was unable to be copied to '{}'", (Object)path, (Object)filePath);
            }
            return success ? 1 : -1;
        }
        catch (Exception ex) {
            ImproperUIAPI.LOGGER.error("<- Error copying resource '{}': {}\n", (Object)path, (Object)ex.getMessage());
            return 0;
        }
    }

    public String getId() {
        return this.modId;
    }

    public Class<? extends ModInitializer> getInitializer() {
        return this.initializer;
    }

    public String getName() {
        return this.initializer.getSimpleName();
    }

    public String[] getPaths() {
        return this.scriptPaths;
    }
}


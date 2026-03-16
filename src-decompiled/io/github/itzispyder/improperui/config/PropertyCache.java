/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import java.util.HashMap;
import java.util.Map;

public class PropertyCache {
    private final Map<String, Properties> cache = new HashMap<String, Properties>();
    private final String modId;

    public PropertyCache(String modId) {
        this.modId = modId;
    }

    public void upload(String path, Properties properties) {
        if (properties == null) {
            return;
        }
        this.cache.put(path, properties);
        properties.read(this.modId, path);
    }

    public Properties get(String path) {
        if (!this.cache.containsKey(path)) {
            this.upload(path, new Properties());
        }
        return this.cache.get(path);
    }

    public Properties.Value getProperty(ConfigKey key) {
        if (key == null) {
            return null;
        }
        return this.get(key.path).getProperty(key.key);
    }

    public void setProperty(ConfigKey key, Object value) {
        this.setProperty(key, value, false);
    }

    public void setProperty(ConfigKey key, Object value, boolean save) {
        if (key == null) {
            return;
        }
        this.get(key.path).setProperty(key.key, value.toString());
        if (save) {
            this.save(key);
        }
    }

    public void save(ConfigKey key) {
        if (key != null) {
            this.get(key.path).write(this.modId, key.path);
        }
    }

    public void clear() {
        for (Map.Entry<String, Properties> entry : this.cache.entrySet()) {
            entry.getValue().write(this.modId, entry.getKey());
        }
        this.cache.clear();
    }
}


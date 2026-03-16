/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.script.ScriptParser;

public record ConfigReader(String modId, String configFile) {
    public ConfigKey getKey(String property) {
        return new ConfigKey(this.modId, this.configFile, property);
    }

    public PropertyCache getPropertyCache() {
        return ScriptParser.getCache(this.modId);
    }

    public Properties.Value read(String property) {
        return this.getPropertyCache().getProperty(this.getKey(property));
    }

    public void write(String property, Object value) {
        ScriptParser.getCache(this.modId).setProperty(this.getKey(property), value, true);
    }

    public boolean readBool(String property, boolean def) {
        Properties.Value o = this.read(property);
        if (o == null) {
            this.write(property, def);
        }
        return o != null ? o.first().toBool() : def;
    }

    public int readInt(String property, int def) {
        return (int)this.readDouble(property, def);
    }

    public double readFloat(String property, float def) {
        return (float)this.readDouble(property, def);
    }

    public double readDouble(String property, double def) {
        Properties.Value o = this.read(property);
        if (o == null) {
            this.write(property, def);
        }
        return o != null ? o.first().toDouble() : def;
    }

    public String readStr(String property, String def) {
        Properties.Value o = this.read(property);
        if (o == null) {
            this.write(property, def);
        }
        return o != null ? o.first().toString() : def;
    }
}


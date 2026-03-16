/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.script.ScriptParser;

public class Radio
extends KeyHolderElement {
    private Color radioFill;

    public Radio() {
        this.queueProperty("size: 6");
        this.queueProperty("background-color: white");
        this.queueProperty("border: 2 360 black");
        this.queueProperty("shadow: 1 white");
        this.queueProperty("shadow-fade-color: white");
        this.queueProperty("margin: 2");
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("active", args -> this.setActive(args.get(0).toBool(), false));
        this.registerProperty("fill-color", args -> {
            this.fillColor = this.radioFill = args.get(0).toColor();
        });
        this.registerProperty("background-color", args -> {
            this.fillColor = this.radioFill = args.get(0).toColor();
        });
    }

    public boolean isActive() {
        return this.classList.contains("active");
    }

    public void setActive(boolean active) {
        this.setActive(active, true);
    }

    public void setActive(boolean active, boolean deep) {
        if (active) {
            if (this.parent != null && deep) {
                for (Element child : this.parent.getChildren()) {
                    if (!(child instanceof Radio)) continue;
                    Radio radio = (Radio)child;
                    radio.setActive(false);
                }
            }
            this.classList.add("active");
            this.fillColor = this.radioFill;
        } else {
            this.classList.remove("active");
            this.fillColor = this.borderColor;
        }
        ConfigKey key = this.getConfigKey();
        if (key != null) {
            this.onSaveKey(ScriptParser.getCache(key.modId), key);
        }
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        if (!release) {
            this.setActive(!this.isActive());
        }
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        Properties.Value property = cache.getProperty(key);
        this.setActive(property != null && property.get(0).toBool(), true);
    }

    @Override
    public void style() {
        super.style();
        if (this.getConfigKey() == null) {
            this.setActive(this.isActive(), false);
        }
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, this.isActive(), true);
    }
}


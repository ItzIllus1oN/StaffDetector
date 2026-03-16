/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;

public class CheckBox
extends KeyHolderElement {
    public CheckBox() {
        this.queueProperty("text-align: center");
        this.queueProperty("size: 10");
        this.queueProperty("border: 1 0 white");
        this.queueProperty("background-color: black");
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("active", args -> this.setActive(args.get(0).toBool()));
    }

    public boolean isActive() {
        return this.classList.contains("active");
    }

    public void setActive(boolean active) {
        if (active) {
            this.classList.add("active");
            this.innerText = "\u2714";
        } else {
            this.classList.remove("active");
            this.innerText = "";
        }
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        if (!release) {
            this.setActive(!this.isActive());
        }
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        Properties.Value property = cache.getProperty(key);
        if (property != null) {
            this.setActive(property.get(0).toBool());
        }
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, this.isActive(), true);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;

public class Positionable
extends KeyHolderElement {
    public Positionable() {
        this.queueProperty("size: 100 200");
        this.queueProperty("border-radius: 5");
        this.queueProperty("shadow-distance: 5");
        this.queueProperty("background-color: #80FFFFFF");
        this.queueProperty("draggable: true");
    }

    @Override
    public void style() {
        if (this.getId() == null) {
            throw new IllegalStateException("a Positionable element cannot have a null ID!");
        }
        if (super.getConfigKey() == null) {
            throw new IllegalStateException("a Positionable element needs to have a ConfigKey attribute \"-modid:config.properties:keyname\"");
        }
        super.style();
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        Properties.Value property = cache.getProperty(key);
        if (property != null) {
            this.moveTo(property.get(0).toInt(), property.get(1).toInt());
        }
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, "%s %s".formatted(this.x, this.y), true);
    }

    @Override
    public ConfigKey getConfigKey() {
        ConfigKey key = super.getConfigKey();
        return new ConfigKey(key.modId, key.path, "improperui.elements.positionable.%s.%s".formatted(this.getId(), key.key));
    }
}


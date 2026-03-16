/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.ConfigKeyHolder;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.script.ScriptParser;

public abstract class KeyHolderElement
extends Element
implements ConfigKeyHolder {
    public abstract void onLoadKey(PropertyCache var1, ConfigKey var2);

    public abstract void onSaveKey(PropertyCache var1, ConfigKey var2);

    protected KeyHolderElement(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    protected KeyHolderElement() {
        this(0, 0, 0, 0);
    }

    @Override
    public void style() {
        super.style();
        ConfigKey key = this.getConfigKey();
        if (key != null) {
            this.onLoadKey(ScriptParser.getCache(key.modId), key);
        }
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        ConfigKey key = this.getConfigKey();
        if (key != null && release) {
            this.onSaveKey(ScriptParser.getCache(key.modId), key);
        }
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        super.onKey(key, scan, release);
        ConfigKey configKey = this.getConfigKey();
        if (configKey != null && release) {
            this.onSaveKey(ScriptParser.getCache(configKey.modId), configKey);
        }
    }

    @Override
    public ConfigKey getConfigKey() {
        return (ConfigKey)ELEMENT_KEY_HOLDER.apply(this);
    }
}


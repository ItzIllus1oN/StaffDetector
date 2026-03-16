/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.render.Element;
import java.util.function.Function;

public interface ConfigKeyHolder {
    public static final Function<Element, ConfigKey> ELEMENT_KEY_HOLDER = element -> {
        String regex = "([a-zA-Z0-9_.-]+:)?[a-zA-Z0-9_.-]+\\.[a-zA-Z0-9_.-]+:[a-zA-Z0-9_.-]+";
        for (String s : element.classList) {
            if (!s.matches(regex)) continue;
            return new ConfigKey(s);
        }
        return null;
    };

    public ConfigKey getConfigKey();
}


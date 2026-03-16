/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.util.misc;

import io.github.itzispyder.improperui.util.misc.Pair;
import java.util.HashMap;
import java.util.Map;

public class ManualMap {
    @SafeVarargs
    public static <K, V> Map<K, V> fromEntries(Map.Entry<K, V> ... entries) {
        return Map.ofEntries(entries);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> fromPairs(Pair<K, V> ... pairs) {
        HashMap map = new HashMap();
        for (Pair<K, V> pair : pairs) {
            try {
                map.put(pair.left, pair.right);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> fromItems(Object ... items) {
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items amount must be even for each key to have a value!");
        }
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        for (int i = 0; i < items.length; i += 2) {
            try {
                map.put(items[i], items[i + 1]);
                continue;
            }
            catch (Exception ex) {
                break;
            }
        }
        return map;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.script.CallbackHandler;
import io.github.itzispyder.improperui.script.Event;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface CallbackListener {
    default public <E extends Event> void runCallbacks(String methodName, E target) {
        if (methodName == null || methodName.trim().isEmpty()) {
            return;
        }
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!methodName.equals(method.getName())) continue;
            try {
                Parameter[] params;
                if (method.getAnnotation(CallbackHandler.class) == null) {
                    this.error("specified callback method must have annotation: @io.github.itzispyder.improperui.script.CallbackHandler", new Object[0]);
                }
                if (method.getParameterCount() == 0) {
                    this.error("specified callback method must have one Event parameter", new Object[0]);
                }
                if ((params = method.getParameters())[0].getType() != target.getClass()) continue;
                method.setAccessible(true);
                method.invoke((Object)this, target);
            }
            catch (Exception ex) {
                this.error("encountered error invoking method: %s", ex.getMessage());
            }
            return;
        }
    }

    default public void error(String message, Object ... args) {
        throw new IllegalArgumentException(message.formatted(args));
    }
}


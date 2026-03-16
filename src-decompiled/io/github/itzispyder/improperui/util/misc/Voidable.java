/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.util.misc;

import java.util.function.Consumer;
import java.util.function.Function;

public class Voidable<T> {
    private final T value;

    private Voidable(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public <U> Voidable<U> map(Function<T, U> function) {
        return this.isPresent() ? Voidable.of(function.apply(this.value)) : Voidable.of(null);
    }

    public void accept(Consumer<T> action) {
        if (this.isPresent()) {
            action.accept(this.value);
        }
    }

    public void accept(Consumer<T> action, Runnable orElse) {
        if (this.isPresent()) {
            action.accept(this.value);
        } else {
            orElse.run();
        }
    }

    public T getOrDef(T fallback) {
        return this.isPresent() ? this.value : fallback;
    }

    public T getOrThrow(String msg, Object ... args) {
        if (this.isPresent()) {
            return this.value;
        }
        throw new IllegalArgumentException(msg.formatted(args));
    }

    public T getOrThrow() {
        return this.getOrThrow("value is not present.", new Object[0]);
    }

    public static <T> Voidable<T> of(T value) {
        return new Voidable<T>(value);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.constants;

public enum InputType {
    RELEASE,
    CLICK,
    HOLD,
    SCROLL,
    UNKNOWN;


    public static InputType of(int action) {
        return switch (action) {
            case 0 -> RELEASE;
            case 1 -> CLICK;
            case 2 -> HOLD;
            default -> UNKNOWN;
        };
    }

    public boolean isRelease() {
        return this == RELEASE;
    }

    public boolean isClick() {
        return this == CLICK;
    }

    public boolean isHold() {
        return this == HOLD;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isUp() {
        return this == RELEASE || this == UNKNOWN;
    }

    public boolean isDown() {
        return this == CLICK || this == HOLD;
    }
}


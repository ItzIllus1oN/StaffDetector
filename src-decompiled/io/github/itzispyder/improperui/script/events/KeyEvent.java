/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.script.events;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.constants.InputType;
import io.github.itzispyder.improperui.script.Event;

public class KeyEvent
extends Event {
    public final int key;
    public final int scan;
    public final InputType input;
    public final Element target;

    public KeyEvent(int key, int scan, InputType input, Element target) {
        this.key = key;
        this.scan = scan;
        this.input = input;
        this.target = target;
    }
}


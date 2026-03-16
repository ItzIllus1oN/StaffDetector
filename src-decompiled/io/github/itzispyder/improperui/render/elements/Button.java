/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.Element;

public class Button
extends Element {
    public Button() {
        this.queueProperty("size: 69 7");
        this.queueProperty("inner-text: \"Button\"");
        this.queueProperty("background-color: white");
        this.queueProperty("border-radius: 2");
        this.queueProperty("padding: 2");
        this.queueProperty("margin: 2");
        this.queueProperty("text-align: center");
        this.queueProperty("text-color: dark_gray");
        this.queueProperty("hovered => { border-thickness: 1; border-color: dark_gray; }");
        this.queueProperty("selected => { border-thickness: 0; background-color: gray; text-color: light_gray; }");
    }
}


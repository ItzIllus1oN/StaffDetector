/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.elements.Label;

public class Header
extends Label {
    public Header(float textScale) {
        this.queueProperty("inner-text-prefix: \"&l\"");
        this.queueProperty("text-scale: " + textScale);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Util
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.Element;
import net.minecraft.util.Util;

public class HyperLink
extends Element {
    public String link;

    public HyperLink() {
        this.queueProperty("text-color: aqua");
        this.queueProperty("height: 7");
        this.queueProperty("background-color: #00000000");
        this.queueProperty("hovered => { inner-text-prefix: \"&b&n\" }");
        this.queueProperty("selected => { inner-text-prefix: \"&3&n\" }");
        this.queueProperty("focused => { inner-text-prefix: \"&5&n\" }");
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("link", args -> {
            this.link = args.get(0).toString();
        });
        this.registerProperty("url", args -> {
            this.link = args.get(0).toString();
        });
        this.registerProperty("href", args -> {
            this.link = args.get(0).toString();
        });
    }

    @Override
    public void style() {
        super.style();
        this.innerText = this.innerText == null ? this.link : this.innerText;
        this.width = HyperLink.mc.textRenderer.getWidth(this.innerText == null ? "" : this.innerText);
        if (this.selectStyle != null) {
            this.selectStyle.innerText = this.innerText;
        }
        if (this.hoverStyle != null) {
            this.hoverStyle.innerText = this.innerText;
        }
        if (this.focusStyle != null) {
            this.focusStyle.innerText = this.innerText;
        }
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        if (this.link != null && !this.link.trim().isEmpty() && !release) {
            Util.getOperatingSystem().open(this.link);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.util.StringUtils;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;

public class Label
extends Element {
    public Label() {
        this.queueProperty("inner-text: \"Empty Label\"");
        this.queueProperty("padding: 2");
        this.queueProperty("background-color: none");
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("inner-text", args -> {
            this.innerText = StringUtils.color(args.getQuoteAndRemove());
            this.updateDimensions();
        });
        this.registerProperty("inner-text-prefix", args -> {
            this.innerTextPrefix = StringUtils.color(args.getQuoteAndRemove());
            this.updateDimensions();
        });
        this.registerProperty("inner-text-suffix", args -> {
            this.innerTextSuffix = StringUtils.color(args.getQuoteAndRemove());
            this.updateDimensions();
        });
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
        super.onRender(context, mx, my, delta);
        this.updateDimensions();
    }

    private void updateDimensions() {
        String text = this.getText();
        if (mc != null && Label.mc.textRenderer != null && text != null) {
            this.width = (int)((float)Label.mc.textRenderer.getWidth(text) * this.textScale);
            Objects.requireNonNull(Label.mc.textRenderer);
            this.height = (int)(9.0f * this.textScale);
        }
    }
}


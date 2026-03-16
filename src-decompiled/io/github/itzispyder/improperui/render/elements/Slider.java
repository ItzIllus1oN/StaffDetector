/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class Slider
extends KeyHolderElement {
    public double min;
    public double max;
    public double val;
    public int decimalPlaces;
    private int fillEnd;

    public Slider() {
        this.queueProperty("size: 100 17");
        this.queueProperty("range: 0 10");
        this.queueProperty("value: 10");
        this.queueProperty("decimal-places: 1");
        this.queueProperty("border-radius: 360");
        this.queueProperty("background-color: white");
        this.queueProperty("inner-text: \"Slider\"");
        this.fillEnd = this.x + this.width;
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("min", args -> {
            this.min = args.get(0).toDouble();
        });
        this.registerProperty("minimum", args -> {
            this.min = args.get(0).toDouble();
        });
        this.registerProperty("max", args -> {
            this.max = args.get(0).toDouble();
        });
        this.registerProperty("maximum", args -> {
            this.max = args.get(0).toDouble();
        });
        this.registerProperty("val", args -> {
            this.val = args.get(0).toDouble();
        });
        this.registerProperty("value", args -> {
            this.val = args.get(0).toDouble();
        });
        this.registerProperty("decimal-places", args -> {
            this.decimalPlaces = args.get(0).toInt();
        });
        this.registerProperty("range", args -> {
            this.min = args.get(0).toDouble();
            this.max = args.get(1).toDouble();
        });
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
        double range;
        int x = this.getPosX() + this.marginLeft - this.paddingLeft;
        int y = this.getPosY() + this.marginTop - this.paddingTop;
        if (this.parentPanel != null && this.parentPanel.selected == this) {
            this.fillEnd = MathUtils.clamp(mx, x, x + this.width);
            range = this.max - this.min;
            double ratio = (double)(this.fillEnd - x) / (double)this.width;
            double value = range * ratio;
            this.val = MathUtils.round(value + this.min, this.decimalPlaces);
            ConfigKey configKey = this.getConfigKey();
            if (configKey != null) {
                ScriptParser.getCache(configKey.modId).setProperty(configKey, this.val, true);
            }
        }
        range = this.max - this.min;
        double value = this.val - this.min;
        double ratio = value / range;
        int len = (int)((double)this.width * ratio);
        this.fillEnd = x + len;
        this.val = MathUtils.round(range * ratio + this.min, this.decimalPlaces);
        String text = "(%s)".formatted(this.val);
        RenderUtils.drawText(context, text, x + this.width + 10, y + (this.height - 7) / 2, 0.9f, false);
        RenderUtils.fillRect(context, x, y + this.height / 2 - 1, this.width, 2, this.borderColor.getHexCustomOpacity(this.opacity));
        RenderUtils.fillRect(context, x, y + this.height / 2 - 1, len, 2, this.fillColor.getHexCustomOpacity(this.opacity));
        x = this.fillEnd - (this.height / 2 + this.paddingLeft + this.paddingRight) / 2;
        RenderUtils.fillRoundShadow(context, x - this.borderThickness, (y += (this.height - (this.height / 2 + this.paddingTop + this.paddingBottom)) / 2) - this.borderThickness, this.height / 2 + this.paddingLeft + this.paddingRight + this.borderThickness * 2, this.height / 2 + this.paddingTop + this.paddingBottom + this.borderThickness * 2, this.borderRadius, this.shadowDistance, this.shadowColor.getHexCustomOpacity(this.opacity), this.shadowColor.getHexCustomAlpha(0));
        RenderUtils.fillRoundShadow(context, x, y, this.height / 2 + this.paddingLeft + this.paddingRight, this.height / 2 + this.paddingTop + this.paddingBottom, this.borderRadius, this.borderThickness, this.borderColor.getHexCustomOpacity(this.opacity), this.borderColor.getHexCustomOpacity(this.opacity));
        RenderUtils.fillRoundRect(context, x, y, this.height / 2 + this.paddingLeft + this.paddingRight, this.height / 2 + this.paddingTop + this.paddingBottom, this.borderRadius, this.fillColor.getHexCustomOpacity(this.opacity));
        if (this.backgroundImage != null) {
            RenderUtils.drawRoundTexture(context, this.backgroundImage, x, y, this.height / 2 + this.paddingLeft + this.paddingRight, this.height / 2 + this.paddingTop + this.paddingBottom, this.borderRadius);
        }
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        Properties.Value property = cache.getProperty(key);
        if (property != null) {
            this.val = property.get(0).toDouble();
        }
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, this.val, true);
    }
}


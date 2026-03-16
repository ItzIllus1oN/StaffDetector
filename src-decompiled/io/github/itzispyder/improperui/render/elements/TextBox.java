/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.text.Text
 *  org.lwjgl.glfw.GLFW
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.render.constants.BackgroundClip;
import io.github.itzispyder.improperui.render.constants.Visibility;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import java.util.function.Function;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TextBox
extends KeyHolderElement {
    public String defaultText;
    public String pattern;
    private boolean selectionBlinking;
    private int selectionBlink;

    public TextBox() {
        this.queueProperty("size: 90 12");
        this.queueProperty("border: 1 0 white");
        this.queueProperty("background-color: dark_gray");
        this.innerText = "";
    }

    @Override
    public void init() {
        super.init();
        this.registerProperty("default-text", args -> {
            this.defaultText = args.getQuote();
        });
        this.registerProperty("placeholder", args -> {
            this.defaultText = args.getQuote();
        });
        this.registerProperty("text-pattern", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("text-format", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("text-mask", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("input-pattern", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("input-format", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("input-mask", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("pattern", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("format", args -> {
            this.pattern = args.getQuote();
        });
        this.registerProperty("mask", args -> {
            this.pattern = args.getQuote();
        });
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
        int x = this.getPosX();
        int y = this.getPosY();
        if (this.visibility == Visibility.INVISIBLE) {
            return;
        }
        context.getMatrices().pushMatrix();
        int cx = x + this.width / 2;
        int cy = y + this.height / 2;
        context.getMatrices().rotateAbout((float)Math.toRadians(this.rotateZ), (float)cx, (float)cy);
        if (this.visibility != Visibility.ONLY_CHILDREN) {
            boolean focused = this.parentPanel != null && this.parentPanel.focused == this;
            RenderUtils.fillRoundShadow(context, x + this.marginLeft - this.paddingLeft - this.borderThickness, y + this.marginTop - this.paddingTop - this.borderThickness, this.width + this.paddingLeft + this.paddingRight + this.borderThickness * 2, this.height + this.paddingTop + this.paddingBottom + this.borderThickness * 2, this.borderRadius, this.shadowDistance, this.shadowColor.getHexCustomOpacity(this.opacity), this.shadowColor.getHexCustomAlpha(0));
            RenderUtils.fillRoundShadow(context, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius, this.borderThickness, focused ? this.borderColor.getHexCustomOpacity(this.opacity) : this.borderColor.darker().getHexCustomOpacity(this.opacity), focused ? this.borderColor.getHexCustomOpacity(this.opacity) : this.borderColor.darker().getHexCustomOpacity(this.opacity));
            RenderUtils.fillRoundRect(context, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius, focused ? this.fillColor.getHexCustomOpacity(this.opacity) : this.fillColor.darker().getHexCustomOpacity(this.opacity));
            if (this.backgroundImage != null) {
                RenderUtils.drawRoundTexture(context, this.backgroundImage, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius);
            }
            if (this.parentPanel != null) {
                String text;
                String string = text = this.innerText != null ? this.innerText : "";
                while (!text.isEmpty() && (float)TextBox.mc.textRenderer.getWidth(text) * 0.9f > (float)(this.width - this.height - 4)) {
                    text = text.substring(1);
                }
                Text display = Text.of((String)text);
                if (!this.queryMatchesPattern()) {
                    RenderUtils.drawDefaultScaledText(context, display, x + this.height / 2 + 2, y + this.height / 3, 0.9f, false, Color.ORANGE.getHexCustomOpacity(this.opacity));
                } else if (this.parentPanel.focused == this && !text.isEmpty()) {
                    RenderUtils.drawDefaultScaledText(context, display, x + this.height / 2 + 2, y + this.height / 3, 0.9f, false, this.textColor.getHexCustomOpacity(this.opacity));
                } else if (!text.isEmpty()) {
                    RenderUtils.drawDefaultScaledText(context, display, x + this.height / 2 + 2, y + this.height / 3, 0.9f, false, this.textColor.darker().darker().getHexCustomOpacity(this.opacity));
                } else {
                    RenderUtils.drawDefaultScaledText(context, Text.of((String)this.getDefaultText()), x + this.height / 2 + 2, y + this.height / 3, 0.9f, false, this.textColor.darker().darker().getHexCustomOpacity(this.opacity));
                }
                if (this.selectionBlinking) {
                    int tx = (int)((double)(x + this.height / 2 + 2) + (double)TextBox.mc.textRenderer.getWidth(text) * 0.9);
                    int ty = y + 2;
                    RenderUtils.drawVerLine(context, tx, ty, this.height - 4, -520093697);
                }
            }
        }
        if (this.visibility != Visibility.ONLY_SELF) {
            boolean shouldClip;
            boolean bl = shouldClip = this.backgroundClip != BackgroundClip.NONE;
            if (shouldClip) {
                Dimensions shape = switch (this.backgroundClip) {
                    case BackgroundClip.PADDING -> this.getPaddedDimensions();
                    case BackgroundClip.BORDER -> this.getBorderedDimensions();
                    case BackgroundClip.MARGIN -> this.getMarginalDimensions();
                    default -> this.getDimensions();
                };
                context.enableScissor(shape.x, shape.y, shape.x + shape.width, shape.y + shape.height);
            }
            this.onRenderChildren(context, mx, my, delta);
            if (shouldClip) {
                context.disableScissor();
            }
        }
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        if (this.parentPanel != null && !release) {
            String typed = GLFW.glfwGetKeyName((int)key, (int)scan);
            if (key == 256) {
                this.parentPanel.focused = null;
            } else if (key == 259) {
                this.onInput(input -> {
                    if (!input.isEmpty()) {
                        return input.substring(0, input.length() - 1);
                    }
                    return input;
                }, false);
            } else if (key == 32) {
                this.onInput(input -> input.concat(" "), true);
            } else if (key == 86 && this.parentPanel.ctrlKeyPressed) {
                this.onInput(input -> input.concat(TextBox.mc.keyboard.getClipboard()), true);
            } else if (typed != null) {
                this.onInput(input -> input.concat(this.parentPanel.shiftKeyPressed ? StringUtils.keyPressWithShift(typed) : typed), true);
            }
        }
    }

    public void onInput(Function<String, String> factory, boolean append) {
        this.innerText = factory.apply(this.innerText != null ? this.innerText : "");
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.parentPanel != null) {
            if (this.parentPanel.focused != this) {
                this.selectionBlinking = false;
                return;
            }
            if (this.selectionBlink++ >= 20) {
                this.selectionBlink = 0;
            }
            if (this.selectionBlink % 10 == 0 && this.selectionBlink > 0) {
                this.selectionBlinking = !this.selectionBlinking;
            }
        }
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        Properties.Value property = cache.getProperty(key);
        if (property != null) {
            this.innerText = property.getQuote();
        }
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, "\"%s\"".formatted(this.innerText), true);
    }

    public String getQuery() {
        return this.innerText;
    }

    public String getLowercaseQuery() {
        return this.innerText.toLowerCase();
    }

    public void setQuery(String query) {
        this.innerText = query;
    }

    public String getDefaultText() {
        return this.defaultText == null ? "" : this.defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean queryMatchesPattern() {
        return this.pattern == null || this.innerText.matches(this.pattern);
    }
}


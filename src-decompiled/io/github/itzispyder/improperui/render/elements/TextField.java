/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  org.lwjgl.glfw.GLFW
 */
package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.Properties;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.render.constants.BackgroundClip;
import io.github.itzispyder.improperui.render.constants.Visibility;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class TextField
extends KeyHolderElement {
    public static final int CHAR_W = 4;
    public static final int CHAR_H = 6;
    private int limitW;
    private int limitH;
    private HistoryQueue editHistory;
    private CharacterElement[][] chars;
    private Point selStart;
    private Point selEnd;
    private int cursor;
    private int selectionBlink;
    private boolean selectionBlinking;
    private boolean mouseDown;

    public TextField(String innerText, int x, int y, int w, int h) {
        super(x, y, Math.max(w, 50), Math.max(h, 18));
        boolean addW = w % 4 != 0;
        boolean addH = h % 6 != 0;
        this.limitW = (int)(Math.floor((double)w / 4.0) + (double)addW) - 2;
        this.limitH = (int)(Math.floor((double)h / 6.0) + (double)addH) - 2;
        this.width = (this.limitW + 2) * 4;
        this.height = (this.limitH + 2) * 6;
        this.innerText = innerText;
        this.chars = new CharacterElement[this.limitW][this.limitH];
        this.updateInnerText();
        this.selStart = new Point();
        this.selEnd = new Point();
        this.cursor = -1;
        this.mouseDown = false;
        this.editHistory = new HistoryQueue(100);
        this.queueProperty("inner-text: %s".formatted(innerText));
        this.queueProperty("size: %s %s".formatted(this.width, this.height));
        this.queueProperty("border: 1 0 white");
        this.queueProperty("background-color: dark_gray");
    }

    public TextField() {
        this("", 0, 0, 100, 18);
    }

    @Override
    public void style() {
        super.style();
        boolean addW = this.width % 4 != 0;
        boolean addH = this.height % 6 != 0;
        this.limitW = (int)(Math.floor((double)this.width / 4.0) + (double)addW) - 2;
        this.limitH = (int)(Math.floor((double)this.height / 6.0) + (double)addH) - 2;
        this.width = (this.limitW + 2) * 4;
        this.height = (this.limitH + 2) * 6;
        this.chars = new CharacterElement[this.limitW][this.limitH];
        this.updateInnerText();
        this.selStart = new Point();
        this.selEnd = new Point();
        this.cursor = -1;
        this.mouseDown = false;
        this.editHistory = new HistoryQueue(100);
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
        context.getMatrices().popMatrix();
        if (this.mouseDown) {
            this.pollMouseSelection(mx, my);
        }
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        ConfigKey configKey;
        if (this.parentPanel != null && !release) {
            String typed = GLFW.glfwGetKeyName((int)key, (int)scan);
            if (key == 256) {
                if (this.isSelecting()) {
                    this.updateCursor();
                } else {
                    this.parentPanel.focused = null;
                }
            } else if (key == 65 && this.parentPanel.ctrlKeyPressed) {
                this.cursor = -1;
                this.selStart.setLocation(0, 0);
                this.selEnd.setLocation(this.limitW, this.limitH);
            } else if (key == 259) {
                this.editHistory.push();
                if (this.isSelecting()) {
                    this.deleteSelectedText();
                    this.setCursor(this.cursor - 1);
                    return;
                }
                this.onInput(input -> StringUtils.insertString(this.innerText, this.cursor + 1, null), false);
            } else if (key == 261) {
                this.editHistory.push();
                if (this.isSelecting()) {
                    this.deleteSelectedText();
                    this.setCursor(this.cursor - 1);
                    return;
                }
                this.onInput(input -> StringUtils.insertString(this.innerText, this.cursor + 2, null), false);
                this.setCursor(this.cursor + 1);
            } else if (key == 32) {
                this.editHistory.push();
                this.onInput(input -> StringUtils.insertString(this.innerText, this.cursor + 1, " "), true);
            } else if (key == 86 && this.parentPanel.ctrlKeyPressed) {
                this.editHistory.push();
                String s = TextField.mc.keyboard.getClipboard().replace('\n', ' ');
                boolean sel = this.isSelecting();
                int len = s.length();
                if (sel) {
                    for (int i = len - 1; i >= 0; --i) {
                        char c = s.charAt(i);
                        this.onInput(input -> {
                            this.setCursor(this.cursor - 1);
                            return StringUtils.insertString(this.innerText, this.cursor + 1, String.valueOf(c));
                        }, true);
                    }
                } else {
                    for (char c : s.toCharArray()) {
                        this.onInput(input -> StringUtils.insertString(this.innerText, this.cursor + 1, String.valueOf(c)), true);
                    }
                }
            } else if (key == 67 && this.parentPanel.ctrlKeyPressed) {
                TextField.mc.keyboard.setClipboard(this.getSelectedText());
            } else if (key == 90 && this.parentPanel.ctrlKeyPressed) {
                this.editHistory.revertLastEdit();
            } else if (key == 263) {
                this.setCursor(this.cursor - 1);
                if (this.parentPanel.shiftKeyPressed) {
                    this.selEnd.setLocation(this.getCursor());
                } else {
                    this.updateCursor();
                }
            } else if (key == 262) {
                this.setCursor(this.cursor + 1);
                if (this.parentPanel.shiftKeyPressed) {
                    this.selEnd.setLocation(this.getCursor());
                } else {
                    this.updateCursor();
                }
            } else if (key == 265) {
                Point c = this.getCursor();
                this.setCursor(c.x - 1, c.y - 1);
                if (this.parentPanel.shiftKeyPressed) {
                    this.selEnd.setLocation(this.getCursor());
                } else {
                    this.updateCursor();
                }
            } else if (key == 264) {
                Point c = this.getCursor();
                this.setCursor(c.x - 1, c.y + 1);
                if (this.parentPanel.shiftKeyPressed) {
                    this.selEnd.setLocation(this.getCursor());
                } else {
                    this.updateCursor();
                }
            } else if (typed != null) {
                this.editHistory.push();
                String s = this.parentPanel.shiftKeyPressed ? StringUtils.keyPressWithShift(typed) : typed;
                boolean sel = this.isSelecting();
                this.onInput(input -> {
                    if (sel) {
                        this.setCursor(this.cursor - 1);
                    }
                    return StringUtils.insertString(this.innerText, this.cursor + 1, s);
                }, true);
            }
        }
        if ((configKey = this.getConfigKey()) != null && release) {
            this.onSaveKey(ScriptParser.getCache(configKey.modId), configKey);
        }
    }

    public void onInput(Function<String, String> factory, boolean append) {
        if (this.cursor + 1 >= this.limitW * this.limitH && append) {
            return;
        }
        this.deleteSelectedText();
        String typed = factory.apply(this.innerText);
        if (typed.length() > this.limitW * this.limitH && append) {
            return;
        }
        this.innerText = typed;
        this.updateInnerText();
        this.cursor += append ? 1 : -1;
        this.cursor = MathUtils.clamp(this.cursor, -1, this.limitW * this.limitH - 1);
        this.updateCursor();
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        this.mouseDown = !release;
        for (Element child : this.getChildren()) {
            if (!child.getHitboxDimensions().contains(mx, my)) continue;
            child.onLeftClick(mx, my, release);
            break;
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

    private void pollMouseSelection(int mx, int my) {
        CharacterElement ch = null;
        block0: for (int x = 0; x < this.limitW; ++x) {
            for (int y = 0; y < this.limitH; ++y) {
                CharacterElement c = this.chars[x][y];
                if (c == null || !c.getHitboxDimensions().contains(mx, my)) continue;
                ch = c;
                continue block0;
            }
        }
        if (ch != null) {
            this.selEnd.setLocation(ch.idx + (this.selEnd.x >= this.selStart.x ? 1 : 0), ch.idy);
            this.setCursor(this.selEnd.x - 1, this.selEnd.y);
        }
    }

    public String getInnerText() {
        return this.innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
        this.updateInnerText();
    }

    private void updateInnerText() {
        this.clearChars();
        int len = this.innerText.length();
        int cx = 0;
        int cy = 0;
        for (int i = 0; i < len; ++i) {
            CharacterElement che;
            char c = this.innerText.charAt(i);
            if (cx >= this.limitW) continue;
            this.chars[cx][cy] = che = new CharacterElement(i, cx, cy, String.valueOf(c), cx * 4 + 4, cy * 6 + 6);
            this.addChild(che);
            if (++cx < this.limitW) continue;
            cx = 0;
            ++cy;
        }
    }

    public Point getCursor() {
        int cx = 0;
        int cy = 0;
        for (int i = 0; i < this.cursor + 1; ++i) {
            if (cx >= this.limitW || ++cx < this.limitW) continue;
            cx = 0;
            ++cy;
        }
        return new Point(cx, cy);
    }

    public String getSelectedText() {
        StringBuilder b = new StringBuilder();
        for (int y = 0; y < this.limitH; ++y) {
            for (int x = 0; x < this.limitW; ++x) {
                CharacterElement c = this.chars[x][y];
                if (c == null || !c.isSelected()) continue;
                b.append(c.ch);
            }
        }
        return b.toString();
    }

    public void deleteSelectedText() {
        if (this.selStart.equals(this.selEnd)) {
            return;
        }
        int x = MathUtils.clamp(this.selStart.x, 0, this.limitW);
        int y = MathUtils.clamp(this.selStart.y, 0, this.limitH);
        int s1 = MathUtils.clamp(this.limitW * y + x, 0, this.innerText.length());
        x = MathUtils.clamp(this.selEnd.x, 0, this.limitW);
        y = MathUtils.clamp(this.selEnd.y, 0, this.limitH);
        int s2 = MathUtils.clamp(this.limitW * y + x, 0, this.innerText.length());
        if (s1 == s2) {
            return;
        }
        this.innerText = this.innerText.substring(0, Math.min(s1, s2)) + this.innerText.substring(Math.max(s1, s2));
        this.updateInnerText();
        this.setCursor(Math.min(s1, s2));
        this.updateCursor();
    }

    public boolean isSelecting() {
        return !this.selStart.equals(this.selEnd);
    }

    public void setCursor(int x, int y) {
        x = MathUtils.clamp(x, 0, this.limitW);
        y = MathUtils.clamp(y, 0, this.limitH);
        this.setCursor(this.limitW * y + x);
    }

    public void setCursor(int cursor) {
        this.cursor = MathUtils.clamp(cursor, -1, this.innerText.length() - 1);
    }

    public CharacterElement getCursorChar() {
        Point c = this.getCursor();
        return this.chars[c.x][c.y];
    }

    private void updateCursor() {
        this.selStart.setLocation(this.getCursor());
        this.selEnd.setLocation(this.selStart);
    }

    private void clearChars() {
        for (int x = 0; x < this.limitW; ++x) {
            for (int y = 0; y < this.limitH; ++y) {
                this.chars[x][y] = null;
            }
        }
        this.clearChildren();
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

    public class CharacterElement
    extends Element {
        private final String ch;
        public final int idc;
        public final int idx;
        public final int idy;

        public CharacterElement(int idc, int idx, int idy, String ch, int x, int y) {
            super(x, y, 4, 6);
            this.ch = ch;
            this.idc = idc;
            this.idx = idx;
            this.idy = idy;
        }

        @Override
        public void onRender(DrawContext context, int mx, int my, float delta) {
            int x = this.getPosX();
            int y = this.getPosY();
            boolean selected = this.isSelected();
            if (selected) {
                RenderUtils.fillRect(context, x, y, this.width, this.height, -805259265);
            }
            if (TextField.this.parentPanel != null) {
                int color;
                if (TextField.this.parentPanel.altKeyPressed) {
                    color = -1;
                    if (this.idx == TextField.this.selStart.x && this.idy == TextField.this.selStart.y) {
                        color = -16711936;
                    }
                    if (this.idx == TextField.this.selEnd.x && this.idy == TextField.this.selEnd.y) {
                        color = -65536;
                    }
                    RenderUtils.drawBox(context, x, y, this.width, this.height, color);
                }
                color = TextField.this.parentPanel.focused == TextField.this ? (selected ? -16777216 : TextField.this.textColor.brighter().brighter().getHexCustomOpacity(this.opacity)) : TextField.this.textColor.getHexCustomOpacity(this.opacity);
                RenderUtils.drawDefaultCode(context, this.ch, x, y - 2, false, color);
            }
            if (TextField.this.selectionBlinking && TextField.this.cursor == this.idc) {
                int tx = x + this.width;
                int ty = y - 2;
                RenderUtils.drawVerLine(context, tx, ty, this.height + 2, -1);
            }
        }

        @Override
        public void onLeftClick(int mx, int my, boolean release) {
            if (release) {
                return;
            }
            TextField.this.setCursor(this.idx, this.idy);
            TextField.this.updateCursor();
            if (TextField.this.parentPanel != null) {
                TextField.this.parentPanel.focused = TextField.this;
                TextField.this.parentPanel.selected = TextField.this;
            }
        }

        public boolean isSelected() {
            int len = TextField.this.innerText == null ? 0 : TextField.this.innerText.length();
            int s1 = MathUtils.clamp(TextField.this.limitW * TextField.this.selStart.y + TextField.this.selStart.x, 0, len);
            int s2 = MathUtils.clamp(TextField.this.limitW * TextField.this.selEnd.y + TextField.this.selEnd.x, 0, len);
            return this.idc >= Math.min(s1, s2) && this.idc < Math.max(s1, s2);
        }
    }

    public class HistoryQueue
    extends ArrayList<String> {
        private final int limit;

        public HistoryQueue(int limit) {
            this.limit = limit;
        }

        public void pop() {
            if (!this.isEmpty()) {
                this.remove(this.size() - 1);
            }
        }

        public void push() {
            if (TextField.this.innerText.isEmpty()) {
                return;
            }
            this.add(TextField.this.innerText);
            if (this.size() > this.limit) {
                this.remove(0);
            }
        }

        public String peek() {
            if (this.isEmpty()) {
                return null;
            }
            return (String)this.get(this.size() - 1);
        }

        public void revertLastEdit() {
            String peek = this.peek();
            if (peek == null) {
                return;
            }
            TextField.this.innerText = peek;
            TextField.this.updateInnerText();
            TextField.this.setCursor(TextField.this.innerText.length() - 1);
            TextField.this.updateCursor();
            this.pop();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.Text
 */
package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.Event;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImproperUIPanel
extends Screen {
    public Element selected;
    public Element focused;
    public Element hovered;
    public boolean shiftKeyPressed;
    public boolean altKeyPressed;
    public boolean ctrlKeyPressed;
    public final int[] cursor;
    private final List<Element> children = new ArrayList<Element>();
    private final List<CallbackListener> callbackListeners = new ArrayList<CallbackListener>();
    private File uiScriptAsFile;
    private String uiScriptAsString;

    public ImproperUIPanel() {
        super(Text.of((String)"Custom Scripted Panel Screen"));
        this.cursor = new int[2];
    }

    public ImproperUIPanel(List<Element> widgets, CallbackListener ... callbackListeners) {
        this();
        for (CallbackListener callback : callbackListeners) {
            this.registerCallback(callback);
        }
        for (Element child : widgets) {
            this.addChild(child);
        }
    }

    public ImproperUIPanel(String uiScript, CallbackListener ... callbackListeners) {
        this(ScriptParser.parse(uiScript), callbackListeners);
        this.uiScriptAsString = uiScript;
    }

    public ImproperUIPanel(File uiScript, CallbackListener ... callbackListeners) {
        this(ScriptParser.parseFile(uiScript), callbackListeners);
        this.uiScriptAsFile = uiScript;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void render(DrawContext context, int mx, int my, float delta) {
        if (this.selected != null && this.selected.draggable) {
            int dx = mx - this.cursor[0];
            int dy = my - this.cursor[1];
            this.selected.move(dx, dy);
            this.selected.boundInConstraints();
            this.cursor[0] = mx;
            this.cursor[1] = my;
        }
        this.getChildren().forEach(child -> child.onRender(context, mx, my, delta));
        boolean foundTarget = false;
        for (Element child2 : this.collectOrdered()) {
            if (!foundTarget && child2.getHitboxDimensions().contains(mx, my)) {
                this.hovered = child2;
                foundTarget = true;
            }
            if (!this.altKeyPressed) continue;
            Dimensions hit = child2.getHitboxDimensions();
            RenderUtils.drawBox(context, hit.x, hit.y, hit.width, hit.height, Color.RED.getHex());
            if (this.ctrlKeyPressed) {
                RenderUtils.drawLine(context, RenderUtils.width() / 2, RenderUtils.height() / 2, hit.x, hit.y, Color.BLUE.getHex());
            }
            if (this.selected == child2) {
                RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.RED.getHex());
                continue;
            }
            if (this.focused == child2) {
                RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.BLUE.getHex());
                continue;
            }
            if (this.hovered != child2) continue;
            RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.ORANGE.getHex());
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;
        for (Element child : this.collectOrdered()) {
            if (!child.pollClickable(button, mx, my, false)) continue;
            switch (button) {
                case 0: {
                    child.onLeftClick(mx, my, false);
                    break;
                }
                case 1: {
                    child.onRightClick(mx, my, false);
                    break;
                }
                case 2: {
                    child.onMiddleClick(mx, my, false);
                }
            }
            break;
        }
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;
        for (Element child : this.collectOrdered()) {
            if (!child.pollClickable(button, mx, my, true)) continue;
            switch (button) {
                case 0: {
                    child.onLeftClick(mx, my, true);
                    break;
                }
                case 1: {
                    child.onRightClick(mx, my, true);
                    break;
                }
                case 2: {
                    child.onMiddleClick(mx, my, true);
                }
            }
            break;
        }
        this.selected = null;
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0.0) {
            return false;
        }
        int mx = (int)mouseX;
        int my = (int)mouseY;
        for (Element child : this.collectOrdered()) {
            if (!child.pollScrollable(mx, my, verticalAmount > 0.0)) continue;
            child.onScroll(mx, my, true);
            break;
        }
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 340 || keyCode == 344) {
            this.shiftKeyPressed = true;
        } else if (keyCode == 342 || keyCode == 346) {
            this.altKeyPressed = true;
        } else if (keyCode == 341 || keyCode == 345) {
            this.ctrlKeyPressed = true;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
        if (this.focused != null) {
            this.focused.onKey(keyCode, scanCode, false);
        }
        return true;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 340 || keyCode == 344) {
            this.shiftKeyPressed = false;
        } else if (keyCode == 342 || keyCode == 346) {
            this.altKeyPressed = false;
        } else if (keyCode == 341 || keyCode == 345) {
            this.ctrlKeyPressed = false;
        }
        super.keyReleased(keyCode, scanCode, modifiers);
        if (this.focused != null) {
            this.focused.onKey(keyCode, scanCode, true);
        }
        return true;
    }

    public void tick() {
        this.children.forEach(Element::onTick);
    }

    public void resize(MinecraftClient client, int width, int height) {
        if (this.uiScriptAsFile != null) {
            new ImproperUIPanel(this.uiScriptAsFile, (CallbackListener[])this.callbackListeners.toArray(CallbackListener[]::new)).open();
        } else if (this.uiScriptAsString != null) {
            new ImproperUIPanel(this.uiScriptAsString, (CallbackListener[])this.callbackListeners.toArray(CallbackListener[]::new)).open();
        }
    }

    public void addChild(Element child) {
        if (child == null || child.parentPanel != null || child.parent != null || this.children.contains(child)) {
            return;
        }
        this.children.add(child);
        child.setParentPanel(this);
    }

    public void removeChild(Element child) {
        if (child == null) {
            return;
        }
        child.setParentPanel(null);
        this.children.remove(child);
    }

    public List<Element> getChildren() {
        return new ArrayList<Element>(this.children);
    }

    public List<Element> getChildrenOrdered() {
        return new ArrayList<Object>(this.getChildren().stream().sorted(Element.ORDER).toList());
    }

    public void clearChildren() {
        List<Element> children = this.getChildren();
        children.forEach(this::removeChild);
    }

    public Element getHoveredElement(int mx, int my) {
        for (Element child : this.collectOrdered()) {
            if (!child.getHitboxDimensions().contains(mx, my)) continue;
            return child;
        }
        return null;
    }

    public void registerCallback(CallbackListener listener) {
        if (listener != null) {
            this.callbackListeners.add(listener);
        }
    }

    public void runCallbacks(String methodName, Event event) {
        try {
            ArrayList<CallbackListener> callbacks = new ArrayList<CallbackListener>(this.callbackListeners);
            callbacks.forEach(callback -> callback.runCallbacks(methodName, event));
        }
        catch (Exception ex) {
            ChatUtils.sendMessage(StringUtils.color("&c" + ex.getMessage()));
        }
    }

    public void printAll() {
        this.children.forEach(Element::printAll);
    }

    public List<Element> collect() {
        ArrayList<Element> list = new ArrayList<Element>();
        for (Element child : this.children) {
            list.add(child);
            list.addAll(child.collect());
        }
        return list;
    }

    public List<Element> collectOrdered() {
        return new ArrayList<Object>(this.collect().stream().sorted(Element.ORDER).toList());
    }

    public List<Element> collectById(String id) {
        ArrayList<Element> list = new ArrayList<Element>();
        for (Element child : this.collect()) {
            if (!child.getId().equals(id)) continue;
            list.add(child);
        }
        return list;
    }

    public Element collectFirstById(String id) {
        for (Element child : this.collect()) {
            if (!child.getId().equals(id)) continue;
            return child;
        }
        return null;
    }

    public List<Element> collectByClassAttribute(String classAttribute) {
        ArrayList<Element> list = new ArrayList<Element>();
        for (Element child : this.collect()) {
            if (!child.classList.contains(classAttribute)) continue;
            list.add(child);
        }
        return list;
    }

    public Element collectFirstByClassAttribute(String classAttribute) {
        for (Element child : this.collect()) {
            if (!child.classList.contains(classAttribute)) continue;
            return child;
        }
        return null;
    }

    public void open() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != this) {
            mc.execute(() -> mc.setScreen((Screen)this));
        }
    }
}


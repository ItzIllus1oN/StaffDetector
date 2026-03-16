/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.joml.Matrix3x2fStack
 */
package io.github.itzispyder.improperui.util.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.util.render.states.ImproperUIQuadState;
import io.github.itzispyder.improperui.util.render.states.ImproperUIRoundRectState;
import io.github.itzispyder.improperui.util.render.states.ImproperUIRoundRectTexState;
import io.github.itzispyder.improperui.util.render.states.ImproperUIRoundRectWireframeState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

public final class RenderUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    public static void fillRect(DrawContext context, int x, int y, int w, int h, int color) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIQuadState(context, x, y, w, h, color));
    }

    public static void fillVerticalGradient(DrawContext context, int x, int y, int w, int h, int colorTop, int colorBottom) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIQuadState(context, x, y, w, h, colorTop, colorTop, colorBottom, colorBottom));
    }

    public static void fillCircle(DrawContext context, int cX, int cY, int radius, int color) {
        RenderUtils.fillRoundRect(context, cX - radius, cY - radius, radius * 2, radius * 2, radius, color);
    }

    public static void fillRoundRect(DrawContext context, int x, int y, int w, int h, int r, int color) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIRoundRectState(context, x, y, w, h, r, color));
    }

    public static void fillRoundRectGradient(DrawContext context, int x, int y, int w, int h, int r, int color1, int color2, int color3, int color4, int colorCenter) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIRoundRectState(context, x, y, w, h, r, color1, color2, color3, color4, colorCenter));
    }

    public static void fillRoundShadow(DrawContext context, int x, int y, int w, int h, int r, float thickness, int innerColor, int outerColor) {
        RenderUtils.fillRoundShadowGradient(context, x, y, w, h, r, thickness, innerColor, outerColor, innerColor, outerColor, innerColor, outerColor, innerColor, outerColor);
    }

    public static void fillRoundShadowGradient(DrawContext context, int x, int y, int w, int h, int r, float thickness, int inner1, int outer1, int inner2, int outer2, int inner3, int outer3, int inner4, int outer4) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIRoundRectWireframeState(context, x, y, w, h, r, thickness, inner1, outer1, inner2, outer2, inner3, outer3, inner4, outer4));
    }

    public static void fillRoundShadow(DrawContext context, int x, int y, int w, int h, int r, float thickness, int color) {
        RenderUtils.fillRoundShadow(context, x, y, w, h, r, thickness, color, new Color(color).getHexCustomAlpha(0.0));
    }

    public static void fillRoundTabTop(DrawContext context, int x, int y, int w, int h, int r, int color) {
        context.enableScissor(x, y, x + w, y + h);
        RenderUtils.fillRoundRect(context, x, y, w, h + r, r, color);
        context.disableScissor();
    }

    public static void fillRoundTabBottom(DrawContext context, int x, int y, int w, int h, int r, int color) {
        context.enableScissor(x, y, x + w, y + h);
        RenderUtils.fillRoundRect(context, x, y - r, w, h + r, r, color);
        context.disableScissor();
    }

    public static void fillRoundHoriLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        RenderUtils.fillRoundRect(context, x, y, length, thickness, thickness / 2, color);
    }

    public static void fillRoundVertLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        RenderUtils.fillRoundRect(context, x, y, thickness, length, thickness / 2, color);
    }

    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, float thickness, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float angle = (float)Math.atan2(dy, dx);
        float t = thickness / 2.0f;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        context.getMatrices().pushMatrix();
        context.getMatrices().rotateAbout(angle, (float)x1, (float)y1);
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIQuadState(context, (float)x1 - t, (float)y1 - t, (float)x1 + length + t, (float)y1 - t, (float)x1 + length + t, (float)y1 + t, (float)x1 - t, (float)y1 + t, color));
        context.getMatrices().popMatrix();
    }

    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        RenderUtils.drawLine(context, x1, y1, x2, y2, 0.5f, color);
    }

    public static void drawBox(DrawContext context, int x, int y, int w, int h, int color) {
        RenderUtils.drawLine(context, x, y, x + w, y, color);
        RenderUtils.drawLine(context, x, y + h, x + w, y + h, color);
        RenderUtils.drawLine(context, x, y, x, y + h, color);
        RenderUtils.drawLine(context, x + w, y, x + w, y + h, color);
    }

    public static void drawRect(DrawContext context, int x, int y, int w, int h, int color) {
        RenderUtils.drawHorLine(context, x, y, w, color);
        RenderUtils.drawVerLine(context, x, y + 1, h - 2, color);
        RenderUtils.drawVerLine(context, x + w - 1, y + 1, h - 2, color);
        RenderUtils.drawHorLine(context, x, y + h - 1, w, color);
    }

    public static void drawHorLine(DrawContext context, int x, int y, int length, int color) {
        RenderUtils.fillRect(context, x, y, length, 1, color);
    }

    public static void drawVerLine(DrawContext context, int x, int y, int length, int color) {
        RenderUtils.fillRect(context, x, y, 1, length, color);
    }

    public static void drawRoundRect(DrawContext context, int x, int y, int w, int h, int r, int color) {
        RenderUtils.fillRoundShadow(context, x, y, w, h, r, 0.5f, color);
    }

    public static void drawDefaultScaledText(DrawContext context, Text text, int x, int y, float scale, boolean shadow, int color) {
        Matrix3x2fStack m = context.getMatrices().pushMatrix();
        m.scale(scale);
        float rescale = 1.0f / scale;
        x = (int)((float)x * rescale);
        y = (int)((float)y * rescale);
        RenderUtils.drawDefaultText(context, text, x, y, shadow, color);
        context.getMatrices().popMatrix();
    }

    public static void drawDefaultCenteredScaledText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow, int color) {
        Matrix3x2fStack m = context.getMatrices().pushMatrix();
        m.scale(scale);
        float rescale = 1.0f / scale;
        centerX = (int)((float)centerX * rescale);
        y = (int)((float)y * rescale);
        RenderUtils.drawDefaultText(context, text, centerX -= RenderUtils.mc.textRenderer.getWidth((StringVisitable)text) / 2, y, shadow, color);
        context.getMatrices().popMatrix();
    }

    public static void drawDefaultRightScaledText(DrawContext context, Text text, int rightX, int y, float scale, boolean shadow, int color) {
        Matrix3x2fStack m = context.getMatrices().pushMatrix();
        m.scale(scale);
        float rescale = 1.0f / scale;
        rightX = (int)((float)rightX * rescale);
        y = (int)((float)y * rescale);
        RenderUtils.drawDefaultText(context, text, rightX -= RenderUtils.mc.textRenderer.getWidth((StringVisitable)text), y, shadow, color);
        context.getMatrices().popMatrix();
    }

    public static void drawDefaultScaledText(DrawContext context, Text text, int x, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultScaledText(context, text, x, y, scale, shadow, -1);
    }

    public static void drawDefaultCenteredScaledText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultCenteredScaledText(context, text, centerX, y, scale, shadow, -1);
    }

    public static void drawDefaultRightScaledText(DrawContext context, Text text, int rightX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultRightScaledText(context, text, rightX, y, scale, shadow, -1);
    }

    public static void drawDefaultText(DrawContext context, Text text, int x, int y, boolean shadow, int color) {
        context.drawText(RenderUtils.mc.textRenderer, text, x, y, color, shadow);
    }

    public static void drawDefaultCode(DrawContext context, String code, int x, int y, boolean shadow, int color) {
        context.drawText(RenderUtils.system.codeRenderer, code, x, y, color, shadow);
    }

    public static void drawText(DrawContext context, String text, int x, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultScaledText(context, (Text)Text.literal((String)text), x, y, scale, shadow);
    }

    public static void drawText(DrawContext context, String text, int x, int y, boolean shadow) {
        RenderUtils.drawDefaultScaledText(context, (Text)Text.literal((String)text), x, y, 1.0f, shadow);
    }

    public static void drawRightText(DrawContext context, String text, int leftX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultRightScaledText(context, (Text)Text.literal((String)text), leftX, y, scale, shadow);
    }

    public static void drawRightText(DrawContext context, String text, int leftX, int y, boolean shadow) {
        RenderUtils.drawDefaultRightScaledText(context, (Text)Text.literal((String)text), leftX, y, 1.0f, shadow);
    }

    public static void drawRightText(DrawContext context, Text text, int leftX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultRightScaledText(context, text, leftX, y, scale, shadow);
    }

    public static void drawRightText(DrawContext context, Text text, int leftX, int y, boolean shadow) {
        RenderUtils.drawDefaultRightScaledText(context, text, leftX, y, 1.0f, shadow);
    }

    public static void drawCenteredText(DrawContext context, String text, int centerX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultCenteredScaledText(context, (Text)Text.literal((String)text), centerX, y, scale, shadow);
    }

    public static void drawCenteredText(DrawContext context, String text, int centerX, int y, boolean shadow) {
        RenderUtils.drawDefaultCenteredScaledText(context, (Text)Text.literal((String)text), centerX, y, 1.0f, shadow);
    }

    public static void drawCenteredText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow) {
        RenderUtils.drawDefaultCenteredScaledText(context, text, centerX, y, scale, shadow);
    }

    public static void drawCenteredText(DrawContext context, Text text, int centerX, int y, boolean shadow) {
        RenderUtils.drawDefaultCenteredScaledText(context, text, centerX, y, 1.0f, shadow);
    }

    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int w, int h) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, w, h, w, h);
    }

    public static void drawRoundTexture(DrawContext context, Identifier texture, int x, int y, int w, int h, int r) {
        context.state.addSimpleElement((SimpleGuiElementRenderState)new ImproperUIRoundRectTexState(context, texture, x, y, w, h, r));
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, float scale) {
        x = (int)((float)x / scale);
        y = (int)((float)y / scale);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale);
        context.drawItem(item, x, y);
        context.drawStackOverlay(RenderUtils.mc.textRenderer, item, x, y);
        context.getMatrices().popMatrix();
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, float scale, String text) {
        x = (int)((float)x / scale);
        y = (int)((float)y / scale);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale);
        context.drawItem(item, x, y);
        context.drawStackOverlay(RenderUtils.mc.textRenderer, item, x, y, text);
        context.getMatrices().popMatrix();
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, int size) {
        RenderUtils.drawItem(context, item, x, y, (float)size / 16.0f);
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y) {
        RenderUtils.drawItem(context, item, x, y, 1.0f);
    }

    public static void drawBuffer(BufferBuilder buf, RenderLayer layer) {
        layer.draw(buf.end());
    }

    public static BufferBuilder getBuffer(VertexFormat.DrawMode drawMode, VertexFormat format) {
        return Tessellator.getInstance().begin(drawMode, format);
    }

    public static int width() {
        return mc.getWindow().getScaledWidth();
    }

    public static int height() {
        return mc.getWindow().getScaledHeight();
    }
}


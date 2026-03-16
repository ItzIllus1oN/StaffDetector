/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 */
package io.github.itzispyder.improperui.util.render.states;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.render.ImproperUIRenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public class ImproperUIRoundRectWireframeState
implements SimpleGuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup texture;
    private final Matrix3x2f pose;
    public float x;
    public float y;
    public float w;
    public float h;
    public float r;
    public float thickness;
    public int colorInner1;
    public int colorOuter1;
    public int colorInner2;
    public int colorOuter2;
    public int colorInner3;
    public int colorOuter3;
    public int colorInner4;
    public int colorOuter4;
    private final ScreenRect scissor;
    private final ScreenRect bounds;

    public ImproperUIRoundRectWireframeState(RenderPipeline pipeline, TextureSetup texture, Matrix3x2f pose, float x, float y, float w, float h, float r, float thickness, int colorInner1, int colorOuter1, int colorInner2, int colorOuter2, int colorInner3, int colorOuter3, int colorInner4, int colorOuter4, ScreenRect scissor, ScreenRect bounds) {
        this.pipeline = pipeline;
        this.texture = texture;
        this.pose = pose;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.thickness = thickness;
        this.colorInner1 = colorInner1;
        this.colorOuter1 = colorOuter1;
        this.colorInner2 = colorInner2;
        this.colorOuter2 = colorOuter2;
        this.colorInner3 = colorInner3;
        this.colorOuter3 = colorOuter3;
        this.colorInner4 = colorInner4;
        this.colorOuter4 = colorOuter4;
        this.scissor = scissor;
        this.bounds = bounds;
    }

    public ImproperUIRoundRectWireframeState(Matrix3x2f pose, float x, float y, float w, float h, float r, float thickness, int colorInner1, int colorOuter1, int colorInner2, int colorOuter2, int colorInner3, int colorOuter3, int colorInner4, int colorOuter4, ScreenRect scissor) {
        this(ImproperUIRenderPipelines.PIPELINE_QUADS, TextureSetup.empty(), pose, x, y, w, h, (float)MathUtils.clamp(r, 0.0, Math.min(w, h) / 2.0f), thickness, colorInner1, colorOuter1, colorInner2, colorOuter2, colorInner3, colorOuter3, colorInner4, colorOuter4, scissor, ImproperUIRoundRectWireframeState.createBounds(pose, scissor, x, y, w, h, thickness));
    }

    public ImproperUIRoundRectWireframeState(DrawContext context, float x, float y, float w, float h, float r, float thickness, int colorInner1, int colorOuter1, int colorInner2, int colorOuter2, int colorInner3, int colorOuter3, int colorInner4, int colorOuter4) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), x, y, w, h, r, thickness, colorInner1, colorOuter1, colorInner2, colorOuter2, colorInner3, colorOuter3, colorInner4, colorOuter4, context.scissorStack.peekLast());
    }

    public ImproperUIRoundRectWireframeState(DrawContext context, float x, float y, float w, float h, float r, float thickness, int colorInner, int colorOuter) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), x, y, w, h, r, thickness, colorInner, colorOuter, colorInner, colorOuter, colorInner, colorOuter, colorInner, colorOuter, context.scissorStack.peekLast());
    }

    public ImproperUIRoundRectWireframeState(DrawContext context, float x, float y, float w, float h, float r, float thickness, int color) {
        this(context, x, y, w, h, r, thickness, color, color);
    }

    public void setupVertices(VertexConsumer buf, float depth) {
        float[][] corners = new float[][]{{this.x + this.w - this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.r}, {this.x + this.w - this.r, this.y + this.r}};
        int[][] colors = new int[][]{{this.colorInner3, this.colorOuter3}, {this.colorInner4, this.colorOuter4}, {this.colorInner1, this.colorOuter1}, {this.colorInner2, this.colorOuter2}};
        for (int i = 0; i < 360; i += 10) {
            int corner = i / 90;
            float angle = (float)Math.toRadians(i);
            float x1 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y1 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            float x4 = corners[corner][0] + (float)(Math.cos(angle) * (double)(this.r + this.thickness));
            float y4 = corners[corner][1] + (float)(Math.sin(angle) * (double)(this.r + this.thickness));
            angle = (float)Math.toRadians(i + 10);
            float x2 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y2 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            float x3 = corners[corner][0] + (float)(Math.cos(angle) * (double)(this.r + this.thickness));
            float y3 = corners[corner][1] + (float)(Math.sin(angle) * (double)(this.r + this.thickness));
            buf.vertex(this.pose, x1, y1, depth).color(colors[corner][0]);
            buf.vertex(this.pose, x2, y2, depth).color(colors[corner][0]);
            buf.vertex(this.pose, x3, y3, depth).color(colors[corner][1]);
            buf.vertex(this.pose, x4, y4, depth).color(colors[corner][1]);
        }
        buf.vertex(this.pose, this.x + this.w - this.r, this.y + this.h, depth).color(colors[0][0]);
        buf.vertex(this.pose, this.x + this.r, this.y + this.h, depth).color(colors[0][0]);
        buf.vertex(this.pose, this.x + this.r, this.y + this.h + this.thickness, depth).color(colors[0][1]);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y + this.h + this.thickness, depth).color(colors[0][1]);
        buf.vertex(this.pose, this.x, this.y + this.h - this.r, depth).color(colors[1][0]);
        buf.vertex(this.pose, this.x, this.y + this.r, depth).color(colors[1][0]);
        buf.vertex(this.pose, this.x - this.thickness, this.y + this.r, depth).color(colors[1][1]);
        buf.vertex(this.pose, this.x - this.thickness, this.y + this.h - this.r, depth).color(colors[1][1]);
        buf.vertex(this.pose, this.x + this.r, this.y, depth).color(colors[2][0]);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y, depth).color(colors[2][0]);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y - this.thickness, depth).color(colors[2][1]);
        buf.vertex(this.pose, this.x + this.r, this.y - this.thickness, depth).color(colors[2][1]);
        buf.vertex(this.pose, this.x + this.w, this.y + this.r, depth).color(colors[3][0]);
        buf.vertex(this.pose, this.x + this.w, this.y + this.h - this.r, depth).color(colors[3][0]);
        buf.vertex(this.pose, this.x + this.w + this.thickness, this.y + this.h - this.r, depth).color(colors[3][1]);
        buf.vertex(this.pose, this.x + this.w + this.thickness, this.y + this.r, depth).color(colors[3][1]);
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public TextureSetup textureSetup() {
        return this.texture;
    }

    @Nullable
    public ScreenRect scissorArea() {
        return this.scissor;
    }

    @Nullable
    public ScreenRect bounds() {
        return this.bounds;
    }

    private static ScreenRect createBounds(Matrix3x2f pose, ScreenRect scissor, float x, float y, float w, float h, float thickness) {
        int a = (int)(x - thickness);
        int b = (int)(y - thickness);
        int c = (int)(w + thickness * 2.0f);
        int d = (int)(h + thickness * 2.0f);
        ScreenRect bounds = new ScreenRect(a, b, c, d).transformEachVertex(pose);
        return scissor == null ? bounds : scissor.intersection(bounds);
    }
}


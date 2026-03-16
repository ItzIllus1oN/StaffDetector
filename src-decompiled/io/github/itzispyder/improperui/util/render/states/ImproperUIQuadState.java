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
import io.github.itzispyder.improperui.util.render.ImproperUIRenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public class ImproperUIQuadState
implements SimpleGuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup texture;
    private final Matrix3x2f pose;
    public float x1;
    public float x2;
    public float x3;
    public float x4;
    public float y1;
    public float y2;
    public float y3;
    public float y4;
    public int color1;
    public int color2;
    public int color3;
    public int color4;
    private final ScreenRect scissor;
    private final ScreenRect bounds;

    public ImproperUIQuadState(RenderPipeline pipeline, TextureSetup texture, Matrix3x2f pose, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color1, int color2, int color3, int color4, ScreenRect scissor, ScreenRect bounds) {
        this.pipeline = pipeline;
        this.texture = texture;
        this.pose = pose;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.scissor = scissor;
        this.bounds = bounds;
    }

    public ImproperUIQuadState(Matrix3x2f pose, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color1, int color2, int color3, int color4, ScreenRect scissor) {
        this(ImproperUIRenderPipelines.PIPELINE_QUADS, TextureSetup.empty(), pose, x1, y1, x2, y2, x3, y3, x4, y4, color1, color2, color3, color4, scissor, ImproperUIQuadState.createBounds(pose, scissor, x1, y1, x2, y2, x3, y3, x4, y4));
    }

    public ImproperUIQuadState(DrawContext context, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color1, int color2, int color3, int color4) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), x1, y1, x2, y2, x3, y3, x4, y4, color1, color2, color3, color4, context.scissorStack.peekLast());
    }

    public ImproperUIQuadState(DrawContext context, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), x1, y1, x2, y2, x3, y3, x4, y4, color, color, color, color, context.scissorStack.peekLast());
    }

    public ImproperUIQuadState(DrawContext context, float x, float y, float w, float h, int color1, int color2, int color3, int color4) {
        this(context, x, y, x + w, y, x + w, y + h, x, y + h, color1, color2, color3, color4);
    }

    public ImproperUIQuadState(DrawContext context, float x, float y, float w, float h, int color) {
        this(context, x, y, x + w, y, x + w, y + h, x, y + h, color);
    }

    public void setupVertices(VertexConsumer buf, float depth) {
        buf.vertex(this.pose, this.x1, this.y1, depth).color(this.color1);
        buf.vertex(this.pose, this.x2, this.y2, depth).color(this.color2);
        buf.vertex(this.pose, this.x3, this.y3, depth).color(this.color3);
        buf.vertex(this.pose, this.x4, this.y4, depth).color(this.color4);
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

    private static ScreenRect createBounds(Matrix3x2f pose, ScreenRect scissor, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float minX = Math.min(x1, Math.min(x2, Math.min(x3, x4)));
        float maxX = Math.max(x1, Math.max(x2, Math.max(x3, x4)));
        float minY = Math.min(y1, Math.min(y2, Math.min(y3, y4)));
        float maxY = Math.max(y1, Math.max(y2, Math.max(y3, y4)));
        int x = (int)minX;
        int y = (int)minY;
        int w = (int)(maxX - minX);
        int h = (int)(maxY - minY);
        ScreenRect bounds = new ScreenRect(x, y, w, h).transformEachVertex(pose);
        return scissor == null ? bounds : scissor.intersection(bounds);
    }
}


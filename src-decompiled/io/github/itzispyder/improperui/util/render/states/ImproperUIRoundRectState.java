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

public class ImproperUIRoundRectState
implements SimpleGuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup texture;
    private final Matrix3x2f pose;
    public float x;
    public float y;
    public float w;
    public float h;
    public float r;
    public int color1;
    public int color2;
    public int color3;
    public int color4;
    public int colorCenter;
    private final ScreenRect scissor;
    private final ScreenRect bounds;

    public ImproperUIRoundRectState(RenderPipeline pipeline, TextureSetup texture, Matrix3x2f pose, float x, float y, float w, float h, float r, int color1, int color2, int color3, int color4, int colorCenter, ScreenRect scissor, ScreenRect bounds) {
        this.pipeline = pipeline;
        this.texture = texture;
        this.pose = pose;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.colorCenter = colorCenter;
        this.scissor = scissor;
        this.bounds = bounds;
    }

    public ImproperUIRoundRectState(Matrix3x2f pose, float x, float y, float w, float h, float r, int color1, int color2, int color3, int color4, int colorCenter, ScreenRect scissor) {
        this(ImproperUIRenderPipelines.PIPELINE_QUADS, TextureSetup.empty(), pose, x, y, w, h, (float)MathUtils.clamp(r, 0.0, Math.min(w, h) / 2.0f), color1, color2, color3, color4, colorCenter, scissor, ImproperUIRoundRectState.createBounds(pose, scissor, x, y, w, h));
    }

    public ImproperUIRoundRectState(DrawContext context, float x, float y, float w, float h, float r, int color1, int color2, int color3, int color4, int colorCenter) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), x, y, w, h, r, color1, color2, color3, color4, colorCenter, context.scissorStack.peekLast());
    }

    public ImproperUIRoundRectState(DrawContext context, float x, float y, float w, float h, float r, int color) {
        this(context, x, y, w, h, r, color, color, color, color, color);
    }

    public void setupVertices(VertexConsumer buf, float depth) {
        float[][] corners = new float[][]{{this.x + this.w - this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.r}, {this.x + this.w - this.r, this.y + this.r}};
        int[] colors = new int[]{this.color3, this.color4, this.color1, this.color2};
        float x1 = this.x + this.w / 2.0f;
        float y1 = this.y + this.h / 2.0f;
        for (int i = 0; i < 360; i += 10) {
            int corner = i / 90;
            float angle = (float)Math.toRadians(i);
            float x2 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y2 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            angle = (float)Math.toRadians(i + 10);
            float x3 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y3 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
            buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
            buf.vertex(this.pose, x2, y2, depth).color(colors[corner]);
            buf.vertex(this.pose, x3, y3, depth).color(colors[corner]);
        }
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y + this.h, depth).color(colors[0]);
        buf.vertex(this.pose, this.x + this.r, this.y + this.h, depth).color(colors[1]);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, this.x, this.y + this.h - this.r, depth).color(colors[1]);
        buf.vertex(this.pose, this.x, this.y + this.r, depth).color(colors[2]);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, this.x + this.r, this.y, depth).color(colors[2]);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y, depth).color(colors[3]);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, x1, y1, depth).color(this.colorCenter);
        buf.vertex(this.pose, this.x + this.w, this.y + this.r, depth).color(colors[3]);
        buf.vertex(this.pose, this.x + this.w, this.y + this.h - this.r, depth).color(colors[0]);
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

    private static ScreenRect createBounds(Matrix3x2f pose, ScreenRect scissor, float x, float y, float w, float h) {
        ScreenRect bounds = new ScreenRect((int)x, (int)y, (int)w, (int)h).transformEachVertex(pose);
        return scissor == null ? bounds : scissor.intersection(bounds);
    }
}


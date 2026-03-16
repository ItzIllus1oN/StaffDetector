/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  net.minecraft.util.Identifier
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 */
package io.github.itzispyder.improperui.util.render.states;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.render.ImproperUIRenderPipelines;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public class ImproperUIRoundRectTexState
implements SimpleGuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup texture;
    private final Matrix3x2f pose;
    public float x;
    public float y;
    public float w;
    public float h;
    public float r;
    private final ScreenRect scissor;
    private final ScreenRect bounds;

    public ImproperUIRoundRectTexState(RenderPipeline pipeline, TextureSetup texture, Matrix3x2f pose, float x, float y, float w, float h, float r, ScreenRect scissor, ScreenRect bounds) {
        this.pipeline = pipeline;
        this.texture = texture;
        this.pose = pose;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.scissor = scissor;
        this.bounds = bounds;
    }

    public ImproperUIRoundRectTexState(Matrix3x2f pose, Identifier texture, float x, float y, float w, float h, float r, ScreenRect scissor) {
        this(ImproperUIRenderPipelines.PIPELINE_TEX_QUADS, TextureSetup.withoutGlTexture((GpuTextureView)MinecraftClient.getInstance().getTextureManager().getTexture(texture).getGlTextureView()), pose, x, y, w, h, (float)MathUtils.clamp(r, 0.0, Math.min(w, h) / 2.0f), scissor, ImproperUIRoundRectTexState.createBounds(pose, scissor, x, y, w, h));
    }

    public ImproperUIRoundRectTexState(DrawContext context, Identifier texture, float x, float y, float w, float h, float r) {
        this(new Matrix3x2f((Matrix3x2fc)context.getMatrices()), texture, x, y, w, h, r, context.scissorStack.peekLast());
    }

    public void setupVertices(VertexConsumer buf, float depth) {
        float[][] corners = new float[][]{{this.x + this.w - this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.h - this.r}, {this.x + this.r, this.y + this.r}, {this.x + this.w - this.r, this.y + this.r}};
        float x1 = this.x + this.w / 2.0f;
        float y1 = this.y + this.h / 2.0f;
        float u1 = 0.5f;
        float v1 = 0.5f;
        for (int i = 0; i < 360; i += 10) {
            int corner = i / 90;
            float angle = (float)Math.toRadians(i);
            float x2 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y2 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            float u2 = (x2 - this.x) / this.w;
            float v2 = (y2 - this.y) / this.h;
            angle = (float)Math.toRadians(i + 10);
            float x3 = corners[corner][0] + (float)(Math.cos(angle) * (double)this.r);
            float y3 = corners[corner][1] + (float)(Math.sin(angle) * (double)this.r);
            float u3 = (x3 - this.x) / this.w;
            float v3 = (y3 - this.y) / this.h;
            buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
            buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
            buf.vertex(this.pose, x2, y2, depth).texture(u2, v2).color(-1);
            buf.vertex(this.pose, x3, y3, depth).texture(u3, v3).color(-1);
        }
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y + this.h, depth).texture((this.w - this.r) / this.w, 1.0f).color(-1);
        buf.vertex(this.pose, this.x + this.r, this.y + this.h, depth).texture(this.r / this.w, 1.0f).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, this.x, this.y + this.h - this.r, depth).texture(0.0f, (this.h - this.r) / this.h).color(-1);
        buf.vertex(this.pose, this.x, this.y + this.r, depth).texture(0.0f, this.r / this.h).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, this.x + this.r, this.y, depth).texture(this.r / this.w, 0.0f).color(-1);
        buf.vertex(this.pose, this.x + this.w - this.r, this.y, depth).texture((this.w - this.r) / this.w, 0.0f).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, x1, y1, depth).texture(u1, v1).color(-1);
        buf.vertex(this.pose, this.x + this.w, this.y + this.r, depth).texture(1.0f, this.r / this.h).color(-1);
        buf.vertex(this.pose, this.x + this.w, this.y + this.h - this.r, depth).texture(1.0f, (this.h - this.r) / this.h).color(-1);
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


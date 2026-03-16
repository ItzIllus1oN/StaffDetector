/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.render.VertexFormats
 */
package io.github.itzispyder.improperui.util.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

public class ImproperUIRenderPipelines {
    public static final RenderPipeline PIPELINE_QUADS = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET}).withLocation("pipeline/global_fill_pipeline").withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS).withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build();
    public static final RenderPipeline PIPELINE_TEX_QUADS = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET}).withLocation("pipeline/gui_textured").withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS).withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build();
}


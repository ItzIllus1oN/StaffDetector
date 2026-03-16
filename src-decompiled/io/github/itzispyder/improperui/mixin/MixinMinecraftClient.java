/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.FontManager
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.interfaces.FontManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MinecraftClient.class})
public abstract class MixinMinecraftClient {
    @Shadow
    @Final
    private FontManager field_1708;
    @Shadow
    @Final
    public static Identifier field_24211;

    @Inject(method={"onFontOptionsChanged"}, at={@At(value="TAIL")})
    public void initFont(CallbackInfo ci) {
        FontManagerAccessor fonts = (FontManagerAccessor)this.field_1708;
        ImproperUIClient.getInstance().codeRenderer = fonts.improperUI$createRenderer(field_24211);
    }
}


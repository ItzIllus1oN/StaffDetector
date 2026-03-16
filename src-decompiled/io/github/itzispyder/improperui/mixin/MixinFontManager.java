/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.font.FontManager
 *  net.minecraft.client.font.FontStorage
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.interfaces.FontManagerAccessor;
import java.util.Map;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={FontManager.class})
public abstract class MixinFontManager
implements FontManagerAccessor {
    @Shadow
    @Final
    private Map<Identifier, FontStorage> field_2259;
    @Shadow
    @Final
    private FontStorage field_24255;

    @Override
    public TextRenderer improperUI$createRenderer(Identifier fontId) {
        return new TextRenderer(id -> this.field_2259.getOrDefault(fontId, this.field_24255), false);
    }
}


package com.example.staffradar.mixins;

import com.example.staffradar.StaffRadarMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TabListMixin {
    @Inject(method = "onPlayerList", at = @At("HEAD"))
    private void onPlayerListUpdate(PlayerListS2CPacket packet, CallbackInfo ci) {
    }
}

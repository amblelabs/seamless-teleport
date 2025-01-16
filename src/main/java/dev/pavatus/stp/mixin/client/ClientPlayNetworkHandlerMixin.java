package dev.pavatus.stp.mixin.client;

import dev.pavatus.stp.STPMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        STPMod.LOGGER.info("Received multiversal block update: {}->{}", packet.getPos(), packet.getState());
    }
}

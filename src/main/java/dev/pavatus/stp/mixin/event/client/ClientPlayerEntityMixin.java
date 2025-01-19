package dev.pavatus.stp.mixin.event.client;

import dev.pavatus.stp.client.event.ClientPlayerInitEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow @Final protected MinecraftClient client;

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        ClientPlayerInitEvent.EVENT.invoker().onClientPlayerInit(
                this.client, (ClientPlayerEntity) (Object) this);
    }
}

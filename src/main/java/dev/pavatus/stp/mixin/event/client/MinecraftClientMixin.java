package dev.pavatus.stp.mixin.event.client;

import dev.pavatus.stp.client.event.ClientWorldEvents;
import org.spongepowered.asm.mixin.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "setWorld", at = @At("TAIL"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        ClientWorldEvents.CHANGE_WORLD.invoker().onChange((MinecraftClient) (Object) this, world);
    }
}

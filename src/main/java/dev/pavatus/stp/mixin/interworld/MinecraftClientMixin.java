package dev.pavatus.stp.mixin.interworld;

import dev.pavatus.stp.client.interworld.SMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements SMinecraftClient {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;

    @Unique private ClientPlayerEntity originalPlayer;
    @Unique private ClientWorld originalWorld;

    @Override
    public ClientPlayerEntity stp$player() {
        return originalPlayer;
    }

    @Override
    public ClientWorld stp$world() {
        return originalWorld;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    public void disconnect(Screen screen, CallbackInfo ci) {
        this.stp$update();
    }

    @Inject(method = "setWorld", at = @At("TAIL"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        this.stp$update();
    }

    @Override
    public void stp$update() {
        this.originalPlayer = this.player;
        this.originalWorld = this.world;
    }
}

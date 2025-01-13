package dev.pavatus.stp.mixin.client;

import java.io.File;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.pavatus.stp.client.ClientWorldEvents;
import dev.pavatus.stp.client.STPMinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ApiServices;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements STPMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @Shadow protected abstract void setWorld(@Nullable ClientWorld world);

    @Shadow private boolean integratedServerRunning;

    @Shadow @Final private YggdrasilAuthenticationService authenticationService;

    @Shadow @Final public File runDirectory;

    @Shadow @Final public ParticleManager particleManager;

    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Shadow public abstract void updateWindowTitle();

    @Mutable
    @Shadow @Final public WorldRenderer worldRenderer;

    @Unique
    private WorldRenderer vanillaRenderer;

    @Inject(method = "setWorld", at = @At("TAIL"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        ClientWorldEvents.CHANGE_WORLD.invoker().onChange((MinecraftClient) (Object) this, world);
    }

    @Override
    public void stp$joinWorld(ClientWorld world) {
        this.world = world;

        MinecraftClient client = (MinecraftClient) (Object) this;
        this.particleManager.setWorld(world);
        this.blockEntityRenderDispatcher.setWorld(world);
        this.updateWindowTitle();

        ClientWorldEvents.CHANGE_WORLD.invoker().onChange(client, world);

        if (!this.integratedServerRunning) {
            ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
            apiServices.userCache().setExecutor(client);

            SkullBlockEntity.setServices(apiServices, client);
            UserCache.setUseRemote(false);
        }
    }

    @Override
    public void stp$resetWorldRenderer() {
        if (this.vanillaRenderer != null)
            this.worldRenderer = this.vanillaRenderer;
    }

    @Override
    public void stp$setWorldRenderer(WorldRenderer renderer) {
        if (this.vanillaRenderer == null)
            this.vanillaRenderer = this.worldRenderer;

        this.worldRenderer = renderer;
    }
}

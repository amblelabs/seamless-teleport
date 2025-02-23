package dev.pavatus.stp.mixin.client;

import dev.pavatus.stp.client.world_rendering.STPChunkRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    STPChunkRenderer renderer = new STPChunkRenderer();
    @Inject(method = "renderWorld", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/" +
                    "client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/min" +
                    "ecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    private void ait$render(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        renderer.renderChunkFromTarget(tickDelta, limitTime, matrices, ci);
    }
}

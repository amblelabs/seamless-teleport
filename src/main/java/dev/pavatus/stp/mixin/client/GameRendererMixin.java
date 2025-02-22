package dev.pavatus.stp.mixin.client;

import dev.pavatus.stp.client.event.ClientWorldEvents;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.world_rendering.STPChunkRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    STPChunkRenderer renderer = new STPChunkRenderer();
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void ait$render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        renderer.renderChunkFromTarget(tickDelta, startTime, tick, ci);
    }

   /* @Inject(method = "onResized", at = @At("RETURN"))
    private void ait$onResized(CallbackInfo ci) {
        ClientWorldIndexer.getWorld()
    }*/
}

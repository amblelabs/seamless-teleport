package dev.pavatus.stp.mixin;

import dev.pavatus.stp.Capturable;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin implements Capturable {

    private boolean captured;

    @Inject(method = "stop", at = @At("HEAD"), cancellable = true)
    public void stop(CallbackInfo ci) {
        if (captured)
            ci.cancel();
    }

    @Override
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}

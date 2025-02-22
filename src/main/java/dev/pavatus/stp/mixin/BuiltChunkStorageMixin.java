package dev.pavatus.stp.mixin;

import dev.pavatus.stp.Capturable;
import net.minecraft.client.render.BuiltChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltChunkStorage.class)
public class BuiltChunkStorageMixin implements Capturable {

    private boolean captured;

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        if (this.captured)
            ci.cancel();
    }

    @Override
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}

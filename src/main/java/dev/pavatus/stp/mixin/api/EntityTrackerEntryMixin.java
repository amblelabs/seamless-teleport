package dev.pavatus.stp.mixin.api;

import dev.pavatus.stp.api.DoNotSyncMe;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {

    @Shadow @Final private Entity entity;

    @Inject(method = "startTracking", at = @At("HEAD"), cancellable = true)
    public void startTracking(ServerPlayerEntity player, CallbackInfo ci) {
        if (!(this.entity instanceof DoNotSyncMe))
            return;

        this.entity.onStartedTrackingBy(player);
        ci.cancel();
    }
}

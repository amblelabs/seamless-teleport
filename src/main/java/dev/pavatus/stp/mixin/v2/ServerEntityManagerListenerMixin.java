package dev.pavatus.stp.mixin.v2;

import dev.pavatus.stp.v2.GhostServerPlayerEntity;
import dev.pavatus.stp.v2.STPServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityManager.Listener.class)
public class ServerEntityManagerListenerMixin<T> {

    @Shadow @Final private T entity;

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!(this.entity instanceof ServerPlayerEntity player))
            return;

        STPServerPlayerEntity stpPlayer = (STPServerPlayerEntity) player;

        if (!stpPlayer.stp$isPortaled())
            return;

        ServerWorld world = player.getServerWorld();
        GhostServerPlayerEntity ghost = ((STPServerPlayerEntity) player).stp$createGhost();

        world.getPlayers().remove(player);
        world.getPlayers().add(ghost);

        ((EntityInvoker) player).stp$unsetRemoved();
        stpPlayer.stp$unsetPortaled();

        ((ServerWorldInvoker) world).stp$addPlayer(ghost);

        ci.cancel();
    }
}

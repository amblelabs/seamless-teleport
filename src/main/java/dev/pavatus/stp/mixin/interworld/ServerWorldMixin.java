package dev.pavatus.stp.mixin.interworld;

import dev.pavatus.stp.client.interworld.SServerPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "sendToPlayerIfNearby", at = @At("HEAD"), cancellable = true)
    public void sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet, CallbackInfoReturnable<Boolean> cir) {
        //noinspection RedundantCast
        if (player.getWorld() != (ServerWorld) (Object) this)
            return;

        Vec3d vec = new Vec3d(x, y, z);
        for (BlockPos pos : ((SServerPlayerEntity) player).stp$destPortals()) {
            if (pos.isWithinDistance(vec, force ? 512.0 : 32.0)) {
                player.networkHandler.sendPacket(packet);
                cir.setReturnValue(true);
                return;
            }

            cir.setReturnValue(false);
        }
    }
}

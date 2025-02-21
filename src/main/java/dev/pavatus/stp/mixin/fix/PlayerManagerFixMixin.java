package dev.pavatus.stp.mixin.fix;

import dev.pavatus.stp.ghost.GhostPlayerEntity;
import dev.pavatus.stp.ghost.GhostPlayerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerFixMixin {

    @Inject(method = "sendToDimension", at = @At("TAIL"))
    public void sendToDimension(Packet<?> packet, RegistryKey<World> dimension, CallbackInfo ci) {
        List<GhostPlayerEntity> ghosts = GhostPlayerManager.get().ghosts();

        for (ServerPlayerEntity ghost : ghosts) {
            if (ghost.getWorld().getRegistryKey() == dimension) {
                ghost.networkHandler.sendPacket(packet);
            }
        }
    }

    @Inject(method = "sendToAround", at = @At("TAIL"))
    public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet, CallbackInfo ci) {
        List<GhostPlayerEntity> ghosts = GhostPlayerManager.get().ghosts();

        for (ServerPlayerEntity ghost : ghosts) {
            if (ghost != player && ghost.getWorld().getRegistryKey() == worldKey) {
                double d = x - ghost.getX();
                double e = y - ghost.getY();
                double f = z - ghost.getZ();

                if (d * d + e * e + f * f < distance * distance)
                    ghost.networkHandler.sendPacket(packet);
            }
        }
    }
}

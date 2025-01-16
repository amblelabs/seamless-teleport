package dev.pavatus.stp.mixin.v2;

import dev.pavatus.stp.v2.STPServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ServerWorld.ServerEntityHandler.class)
public class ServerEntityHandlerMixin {

    /*@Redirect(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"))
    public boolean stopTracking(List<ServerPlayerEntity> players, Object o) {
        if (!(o instanceof ServerPlayerEntity player))
            return false;

        boolean result = players.remove(player);

        players.add(((STPServerPlayerEntity) player).stp$createGhost());
        return result;
    }*/
}

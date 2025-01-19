package dev.pavatus.stp.mixin.ghost;

import dev.pavatus.stp.ghost.GhostS2CPacketWrapper;
import net.minecraft.network.NetworkState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetworkState.class)
public class NetworkStateMixin {

    /*@Redirect(method = "getPacketId", at = @At(value = "INVOKE", target = "Ljava/lang/Object;getClass()Ljava/lang/Class;"))
    public Class<?> getPacketId(Object instance) {
        if (instance instanceof GhostS2CPacketWrapper<?> wrapper)
            instance = wrapper.getPacket();

        return instance.getClass();
    }*/
}

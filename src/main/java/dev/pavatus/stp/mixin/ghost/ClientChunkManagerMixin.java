package dev.pavatus.stp.mixin.ghost;

import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    // TODO: this just ensures that chunk packets received from the ghost are processed
    @Redirect(method = "loadChunkFromPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;isInRadius(II)Z"))
    public boolean isInRadius(ClientChunkManager.ClientChunkMap instance, int chunkX, int chunkZ) {
        return true;
    }
}

package dev.pavatus.stp.mixin.access;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerWorld.class)
public interface ServerWorldInvoker {

    @Invoker("addPlayer")
    void stp$addPlayer(ServerPlayerEntity player);
}

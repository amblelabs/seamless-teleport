package dev.pavatus.stp.mixin.interworld;

import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.CombinedDynamicRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {

    @Accessor
    CombinedDynamicRegistries<ClientDynamicRegistryType> getCombinedDynamicRegistries();

    @Accessor
    int getChunkLoadDistance();

    @Accessor
    int getSimulationDistance();
}

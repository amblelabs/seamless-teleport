package dev.pavatus.stp.mixin.world_indexing;

import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.indexing.SMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "onGameJoin", at = @At(value = "NEW", target = "(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/entry/RegistryEntry;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)Lnet/minecraft/client/world/ClientWorld;"))
    public ClientWorld onGameJoin(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> dimensionEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed) {
        return new SClientWorld(networkHandler, properties, worldKey, dimensionEntry,
                loadDistance, simulationDistance, profiler, worldRenderer, debugWorld,
                seed, ((SMinecraftClient) client).stp$getWorldIndex(worldKey));
    }

    @Redirect(method = "onPlayerRespawn", at = @At(value = "NEW", target = "(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/entry/RegistryEntry;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)Lnet/minecraft/client/world/ClientWorld;"))
    public ClientWorld onPlayerRespawn(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> dimensionEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed) {
        return new SClientWorld(networkHandler, properties, worldKey, dimensionEntry,
                loadDistance, simulationDistance, profiler, worldRenderer, debugWorld,
                seed, ((SMinecraftClient) client).stp$getWorldIndex(worldKey));
    }
}

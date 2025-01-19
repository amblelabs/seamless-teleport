package dev.pavatus.stp.client.indexing;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.Supplier;

public class SClientWorld extends ClientWorld {

    private final int index;

    public SClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties,
                        RegistryKey<World> worldKey, RegistryEntry<DimensionType> dimensionEntry,
                        int loadDistance, int simulationDistance, Supplier<Profiler> profiler,
                        WorldRenderer worldRenderer, boolean debugWorld, long seed, int index) {
        super(networkHandler, properties, worldKey, dimensionEntry, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed);

        this.index = index;
    }
}

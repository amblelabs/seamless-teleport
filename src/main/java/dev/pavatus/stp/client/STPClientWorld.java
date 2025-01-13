package dev.pavatus.stp.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;
import java.util.function.Supplier;

public class STPClientWorld extends ClientWorld {

    public STPClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties,
                          RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionTypeEntry,
                          int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer,
                          boolean debugWorld, long seed, Map<String, MapState> mapStates, Scoreboard scoreboard) {
        super(networkHandler, properties, registryRef, dimensionTypeEntry, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed);

        this.putMapStates(mapStates);
        this.setScoreboard(scoreboard);
    }
}

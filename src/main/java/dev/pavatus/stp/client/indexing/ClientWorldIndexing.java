package dev.pavatus.stp.client.indexing;

import dev.pavatus.stp.indexing.WorldIndexing;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapState;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Map;

public class ClientWorldIndexing {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(WorldIndexing.PACKET_ID, (client, handler, buf, responseSender) -> {
            int size = buf.readVarInt();

            for (int i = 0; i < size; i++) {

            }
        });
    }

    private static ClientWorld createWorld(MinecraftClient client, ClientPlayNetworkHandler handler) {
        Scoreboard scoreboard = client.world.getScoreboard();
        Map<String, MapState> map = client.world.getMapStates();

        boolean debugWorld = packet.isDebugWorld();
        boolean flatWorld = packet.isFlatWorld();

        ClientWorld.Properties properties = new ClientWorld.Properties(
                handler.worldProperties.getDifficulty(),
                handler.worldProperties.isHardcore(), flatWorld
        );

        this.worldProperties = properties;

        handler.world = new ClientWorld(
                this,
                properties,
                registryKey,
                registryEntry,
                this.chunkLoadDistance,
                this.simulationDistance,
                client::getProfiler,
                client.worldRenderer,
                debugWorld,
                packet.getSha256Seed()
        );

        this.world.setScoreboard(scoreboard);
    }
}

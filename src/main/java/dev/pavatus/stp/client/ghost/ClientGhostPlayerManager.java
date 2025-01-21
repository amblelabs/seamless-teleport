package dev.pavatus.stp.client.ghost;

import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.ghost.GhostPlayerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.thread.ThreadExecutor;

public class ClientGhostPlayerManager {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(GhostPlayerManager.PLAY_PACKET, (client, handler, buf, responseSender) -> {
            int packetId = buf.readVarInt();

            Packet<ClientPlayNetworkHandler> packet = (Packet<ClientPlayNetworkHandler>)
                    NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, packetId, buf);

            if (buf.readableBytes() < 4)
                return;

            int magic = buf.readInt();

            if (magic != 0xCAFEBABE)
                return;

            int worldIndex = buf.readVarInt();
            SClientWorld sworld = (SClientWorld) ClientWorldIndexer.getWorld(worldIndex);

            ClientPlayNetworkHandler networkHandler = sworld.stp$networkHandler();

            ClientWorld realWorld = client.world;
            ClientPlayerEntity realPlayer = client.player;

            // in theory, this should abuse mc's singlethreaded composure
            runOnThread(() -> {
                client.world = (ClientWorld) sworld;
                client.player = sworld.stp$player();
                packet.apply(networkHandler);

                client.world = realWorld;
                client.player = realPlayer;
            }, sworld.stp$networkHandler(), client);
        });
    }

    private static void runOnThread(Runnable runnable, ClientPlayNetworkHandler networkHandler, ThreadExecutor<?> engine) {
        // a simplified code of NetworkThreadUtils
        if (!engine.isOnThread()) {
            engine.executeSync(() -> {
                if (networkHandler.isConnectionOpen()) {
                    try {
                        runnable.run();
                    } catch (Exception ignored) { }
                }
            });

            return;
        }

        runnable.run();
    }
}

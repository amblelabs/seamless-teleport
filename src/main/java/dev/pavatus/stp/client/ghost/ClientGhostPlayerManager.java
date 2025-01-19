package dev.pavatus.stp.client.ghost;

import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.indexing.SMinecraftClient;
import dev.pavatus.stp.ghost.GhostPlayerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

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
            packet.apply(sworld.stp$networkHandler());

            System.out.println("Received remote packet: " + packet.getClass());
        });
    }
}

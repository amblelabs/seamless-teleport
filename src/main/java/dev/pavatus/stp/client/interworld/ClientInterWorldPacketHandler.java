package dev.pavatus.stp.client.interworld;

import dev.pavatus.stp.interworld.InterWorldPacketHandler;
import dev.pavatus.stp.mixin.interworld.ClientPlayNetworkHandlerAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ClientInterWorldPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(InterWorldPacketHandler.LOAD_PACKET, (client, handler, buf, responseSender) -> {
            ClientPlayNetworkHandlerAccessor acc = (ClientPlayNetworkHandlerAccessor) client.getNetworkHandler();

            int chunkLoadDistance = acc.getChunkLoadDistance();
            int simulationDistance = acc.getSimulationDistance();

            int index = buf.readVarInt();
            // TODO: implement index -> registry key for indexing
            RegistryKey<World> key = buf.readRegistryKey(RegistryKeys.WORLD);
            RegistryEntry<DimensionType> registryEntry =  acc.getCombinedDynamicRegistries()
                    .getCombinedRegistryManager()
                    .get(RegistryKeys.DIMENSION_TYPE)
                    .entryOf(buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE));

            long seed = buf.readLong();

            boolean debugWorld = buf.readBoolean();
            boolean flatWorld = buf.readBoolean();

            ClientWorld.Properties worldProperties = client.world.getLevelProperties();
            ClientWorld.Properties properties = new ClientWorld.Properties(worldProperties.getDifficulty(), worldProperties.isHardcore(), flatWorld);

            ClientWorld world = new ClientWorld(
                    client.getNetworkHandler(),
                    properties,
                    key,
                    registryEntry,
                    chunkLoadDistance,
                    simulationDistance,
                    client::getProfiler,
                    client.worldRenderer,
                    debugWorld,
                    seed
            );

            ((dev.pavatus.stp.client.indexing.SMinecraftClient) client).stp$worlds()
                    .set(index, world);
        });

        ClientPlayNetworking.registerGlobalReceiver(InterWorldPacketHandler.PLAY_PACKET, (client, handler, buf, responseSender) -> {
            Packet<ClientPlayNetworkHandler> packet = readPacket(buf);

            if (!shouldHandlePacket(packet))
                return;

            int worldIndex = readWorldIndex(buf);
            ClientInterWorldPacketEvent.EVENT.invoker().onPacket(worldIndex, packet);
        });

        ClientPlayNetworking.registerGlobalReceiver(InterWorldPacketHandler.PLAY_BUNDLE_PACKET, (client, handler, buf, responseSender) -> {
            int size = buf.readVarInt();
            Packet<ClientPlayNetworkHandler>[] packets = new Packet[size];

            for (int i = 0; i < size; i++) {
                Packet<ClientPlayNetworkHandler> packet = readPacket(buf);

                if (!shouldHandlePacket(packet))
                    continue;

                packets[i] = packet;
            }

            int worldIndex = readWorldIndex(buf);

            for (Packet<?> packet : packets) {
                ClientInterWorldPacketEvent.EVENT.invoker().onPacket(worldIndex, packet);
            }
        });
    }

    private static boolean shouldHandlePacket(Packet<?> packet) {
        return true;
    }

    private static Packet<ClientPlayNetworkHandler> readPacket(PacketByteBuf buf) {
        int packetId = buf.readVarInt();

        return (Packet<ClientPlayNetworkHandler>)
                NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, packetId, buf);
    }

    private static int readWorldIndex(PacketByteBuf buf) {
        if (buf.readableBytes() < 4)
            return -1;

        int magic = buf.readInt();

        if (magic != 0xCAFEBABE)
            return -1;

        return buf.readVarInt();
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

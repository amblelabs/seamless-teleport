package dev.pavatus.stp.client.ghost;

import dev.pavatus.stp.STPMod;
import dev.pavatus.stp.ghost.GhostPacketHandler;
import dev.pavatus.stp.util.WorldUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.thread.ThreadExecutor;

public class ClientGhostPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(GhostPacketHandler.PACKET_ID, (client, handler, buf, responseSender) -> {
            int worldIndex = buf.readVarInt();
            handle(worldIndex, readPacket(buf), handler, client);
        });

        ClientPlayNetworking.registerGlobalReceiver(GhostPacketHandler.PACKET_BUNDLE_ID, (client, handler, buf, responseSender) -> {
            int worldIndex = buf.readVarInt();
            int bundleSize = buf.readVarInt();

            for (int i = 0; i < bundleSize; i++) {
                Packet<ClientPlayPacketListener> packet = readPacket(buf);
                handle(worldIndex, packet, handler, client);
            }
        });
    }

    private static ClientWorld enterWorldPacket(int worldIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld result = client.world;

        client.world = WorldUtil.getWorld(MinecraftClient.getInstance(), worldIndex);
        return result;
    }

    private static void exitWorldPacket(ClientWorld world) {
        MinecraftClient.getInstance().world = world;
    }

    private static Packet<ClientPlayPacketListener> readPacket(PacketByteBuf buf) {
        int j = buf.readVarInt();
        Packet<?> packet = NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, j, buf);

        if (packet == null) {
            STPMod.LOGGER.error("Bad packet id {}", j);
            return null;
        }

        return (Packet<ClientPlayPacketListener>) packet;
    }

    public static <T extends PacketListener> void handle(int worldIndex, Packet<T> packet, T listener, ThreadExecutor<?> engine) throws OffThreadException {
        if (!engine.isOnThread()) {
            engine.executeSync(() -> {
                if (listener.isConnectionOpen()) {
                    try {
                        ClientWorld world = enterWorldPacket(worldIndex);
                        packet.apply(listener);
                        exitWorldPacket(world);
                    } catch (Exception var3) {
                        if (listener.shouldCrashOnException()) {
                            throw var3;
                        }

                        STPMod.LOGGER.error("Failed to handle packet {}, suppressing error", packet, var3);
                    }
                } else {
                    STPMod.LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
                }
            });

            throw OffThreadException.INSTANCE;
        }
    }
}

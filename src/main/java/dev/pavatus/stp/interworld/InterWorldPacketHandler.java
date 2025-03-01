package dev.pavatus.stp.interworld;

import dev.pavatus.stp.STPMod;
import dev.pavatus.stp.indexing.ServerWorldIndexer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class InterWorldPacketHandler {

    public static final Identifier PLAY_PACKET = new Identifier(STPMod.MOD_ID, "play_packet");
    public static final Identifier PLAY_BUNDLE_PACKET = new Identifier(STPMod.MOD_ID, "play_bundle_packet");

    public static void init() {

    }

    public static boolean shouldProcessPacket(Packet<?> packet) {
        return !(packet instanceof CustomPayloadS2CPacket);
    }

    public static void sendPlayPacket(ServerPlayerEntity player, Packet<?> packet, PacketCallbacks callbacks) {
        if (packet instanceof BundleS2CPacket bundle) {
            sendBundlePacket(player, bundle, callbacks);
            return;
        }

        handlePacket(player, callbacks,
                buf -> writePacket(packet, buf));
    }

    private static void sendBundlePacket(ServerPlayerEntity player, BundleS2CPacket bundle, PacketCallbacks callbacks) {
        handlePacket(player, callbacks, buf -> {
            List<Packet<ClientPlayPacketListener>> packets = (List<Packet<ClientPlayPacketListener>>) bundle.getPackets();

            buf.writeVarInt(packets.size());

            for (Packet<?> packet : packets) {
                writePacket(packet, buf);
            }
        });
    }

    private static void handlePacket(ServerPlayerEntity player, PacketCallbacks callbacks, Consumer<PacketByteBuf> consumer) {
        PacketByteBuf buf = PacketByteBufs.create();

        consumer.accept(buf);
        writeWorld(player.getServerWorld(), buf);

        player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(PLAY_PACKET, buf), callbacks);
    }

    public static void writeWorld(ServerWorld world, PacketByteBuf buf) {
        //buf.writeInt(0xCAFEBABE);

        int index = ServerWorldIndexer.getWorldIndex(world);
        buf.writeVarInt(index);
    }

    private static void writePacket(Packet<?> packet, PacketByteBuf buf) {
        int packetId = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet);

        if (packetId == -1) {
            STPMod.LOGGER.error("Failed to serialize an unregistered packet {}", packet);
            return;
        }

        buf.writeVarInt(packetId);
        packet.write(buf);
    }
}
